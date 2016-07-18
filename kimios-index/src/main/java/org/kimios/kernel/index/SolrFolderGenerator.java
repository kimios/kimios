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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.common.SolrInputDocument;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.dms.model.MetaType;
import org.kimios.kernel.dms.model.VirtualFolderMetaData;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.security.model.DMEntityACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by farf on 28/10/15.
 */
public class SolrFolderGenerator {


    private static Logger log = LoggerFactory.getLogger(SolrFolderGenerator.class);


    private Folder folder;
    private List<VirtualFolderMetaData> metaValues;
    private ObjectMapper mp;
    private List<DMEntityACL> acls;

    public SolrFolderGenerator(Folder folder, List<VirtualFolderMetaData> metaValues, ObjectMapper mp){
        this.folder = folder;
        this.mp = mp;
        this.metaValues = metaValues;
        loadDocumentData(folder);
    }

    private void loadDocumentData(Folder folder){
        acls = org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory().getDMEntityACL(
                        folder);
    }

    protected SolrInputDocument toSolrInputDocument(Folder folder, List<VirtualFolderMetaData> metaValues)
            throws DataSourceException, ConfigException {

        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat utcDateParser = new SimpleDateFormat("dd-MM-yyyy");
        utcDateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat utcDateTimeParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        utcDateTimeParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        log.debug("processing folder {} for path {}", folder.getUid(), folder.getPath());
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("DocumentUid", folder.getUid());
        doc.addField("DocumentName", folder.getName().toLowerCase());
        doc.addField("DocumentNameDisplayed", folder.getName());
        doc.addField("DocumentNameAnalysed", folder.getName());

        doc.addField("DocumentOwner", folder.getOwner() + "@" + folder.getOwnerSource());
        doc.addField("DocumentOwnerId", folder.getOwner());
        doc.addField("DocumentOwnerSource", folder.getOwnerSource());
        doc.addField("DocumentPath", folder.getPath());
        doc.addField("DocumentParent", folder.getParent().getPath() + "/");
        doc.addField("DocumentParentId", folder.getParent().getUid());

        //standard datas
        doc.addField("DocumentCreationDate", folder.getCreationDate());
        doc.addField("DocumentUpdateDate", folder.getUpdateDate());
        doc.addField("DocumentVersionId", -1);
        doc.addField("DocumentVersionCreationDate", folder.getCreationDate());
        doc.addField("DocumentVersionUpdateDate", folder.getUpdateDate());
        doc.addField("DocumentVersionOwner", folder.getOwner() + "@" + folder.getOwnerSource());
        doc.addField("DocumentVersionOwnerId", folder.getOwner());
        doc.addField("DocumentVersionOwnerSource", folder.getOwnerSource());
        doc.addField("DocumentVersionLength", -10);
        doc.addField("DocumentVersionHash", "folder");
        doc.addField("DocumentOutWorkflow", true);
        if (metaValues.size() > 0) {
            log.debug("Document Type Found for version");


            String documentTypeName = null;
            Long documentTypeUid = null;
            for (VirtualFolderMetaData folderMetaData : metaValues) {


                if (documentTypeName == null && documentTypeUid == null) {
                    documentTypeName = folderMetaData.getMeta().getDocumentType().getName();
                    documentTypeUid = folderMetaData.getMeta().getDocumentTypeUid();
                    doc.addField("DocumentTypeUid", documentTypeUid);
                    doc.addField("DocumentTypeName", documentTypeName);
                }
                switch (folderMetaData.getMeta().getMetaType()) {

                    case MetaType.STRING:
                        doc.addField("MetaDataString_" + folderMetaData.getMetaId(),
                                folderMetaData.getStringValue());
                        break;
                    case MetaType.DATE:

                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        if (folderMetaData.getDateValue() != null) {
                            //reparse date for local
                            String dateString = dateParser.format(folderMetaData.getDateValue());
                            log.debug("meta parsed on re-index: " + dateString);
                            try {
                                cal.setTime(utcDateParser.parse(dateString));
                                log.debug("meta parsed on re-index: " + dateString + " ==> Cal:" + cal.getTime());
                                doc.addField("MetaDataDate_" + folderMetaData.getMetaId(), cal.getTime());
                            } catch (Exception ex) {
                                log.error("error while reparsing meta data date {} {} {}", folderMetaData.getMeta().getName(), folderMetaData.getDateValue(), dateString);
                            }
                        }
                        break;
                    default:
                        doc.addField("MetaData_" + folderMetaData.getMetaId(), folderMetaData.getStringValue());
                        break;
                }
            }
        }
        doc.addField("Attribute_VirtualFolder", "folder");
        if (folder.getAddOnDatas() != null && folder.getAddOnDatas().length() > 0) {
            doc.addField("DocumentRawAddonDatas", folder.getAddOnDatas());
        }
        doc.addField("DocumentBody", IndexHelper.EMPTY_STRING);
        for (int i = 0; i < acls.size(); i++) {
            doc.addField("DocumentACL", acls.get(i).getRuleHash());
        }
        return doc;
    }
}
