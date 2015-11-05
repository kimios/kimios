/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.kernel.index;

import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jackson.map.ObjectMapper;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.VirtualFolderMetaData;
import org.kimios.kernel.index.query.model.DocumentIndexStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by farf on 28/10/15.
 */
public class SolrDocCallable implements Callable<SolrInputDocument> {

    private static Logger log = LoggerFactory.getLogger(SolrDocCallable.class);

    private SolrIndexManager solrIndexManager;

    private boolean updateDocsMetaWrapper;
    private boolean flush;
    private long readVersionTimeOut;
    private TimeUnit readVersionTimeoutTimeUnit;
    private ObjectMapper mp;
    private Map<String, Object> fileData;
    private DocumentIndexStatus documentIndexStatus;

    public SolrDocCallable(DocumentIndexStatus documentIndexStatus,
                           SolrIndexManager solrIndexManager,
                           boolean updateDocsMetaWrapper,
                           boolean flush,
                           long readVersionTimeOut,
                           TimeUnit readVersionTimeoutTimeUnit,
                           ObjectMapper mp
        ) {
        this.documentIndexStatus = documentIndexStatus;
        this.flush = flush;
        this.solrIndexManager = solrIndexManager;
        this.updateDocsMetaWrapper = updateDocsMetaWrapper;
        this.readVersionTimeOut = readVersionTimeOut;
        this.readVersionTimeoutTimeUnit = readVersionTimeoutTimeUnit;
        this.mp = mp;
    }


    public void setFileData(Map<String, Object> fileData){
        this.fileData = fileData;
    }

    @Override
    public SolrInputDocument call() throws Exception {
        if (documentIndexStatus.getDmEntity() instanceof Document) {
            Document document = (Document)documentIndexStatus.getDmEntity();
            log.debug("started solr input document for doc #" + document.getUid()
                    + " (" + document.getPath() + ")");
            SolrInputDocument solrInputDocument = new SolrDocGenerator(document, this.mp)
                    .toSolrInputDocument(flush, updateDocsMetaWrapper, null, documentIndexStatus);
            if(fileData != null){
                //sync data !
                for(String solrInputField: fileData.keySet())
                    solrInputDocument.addField(solrInputField, fileData.get(solrInputField));
            }
            return solrInputDocument;
        } else if (documentIndexStatus.getDmEntity() instanceof Folder) {
            Folder folder = (Folder)documentIndexStatus.getDmEntity();
            List<VirtualFolderMetaData> metaDataList = FactoryInstantiator.getInstance()
                    .getVirtualFolderFactory()
                    .virtualFolderMetaDataList(folder);
            for(VirtualFolderMetaData metaData: metaDataList)
                metaData.getMeta().getDocumentType().getName();
            SolrFolderGenerator solrFolderGenerator = new SolrFolderGenerator(folder,
                    metaDataList,
                    this.mp);
            SolrInputDocument solrInputDocument = solrFolderGenerator.toSolrInputDocument(folder, metaDataList);
            return solrInputDocument;
        } else {
            return null;
        }
    }
}
