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
import org.kimios.kernel.dms.*;
import org.kimios.kernel.index.filters.impl.GlobalFilter;
import org.kimios.kernel.index.query.model.DocumentIndexStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by farf on 28/10/15.
 */
public class SolrDocFileReaderCallable implements Callable<Map<Long,Map<String,Object>>> {

    private static Logger log = LoggerFactory.getLogger(SolrDocFileReaderCallable.class);

    private long readVersionTimeOut;
    private TimeUnit readVersionTimeoutTimeUnit;
    private Map<DocumentIndexStatus, DocumentVersion> docFiles;
    private BlockingQueue blockingQueue;

    public SolrDocFileReaderCallable(Map<DocumentIndexStatus, DocumentVersion> docFiles,
                                     long readVersionTimeOut, TimeUnit readVersionTimeoutTimeUnit,
                                     BlockingQueue blockingQueue) {
        this.readVersionTimeOut = readVersionTimeOut;
        this.readVersionTimeoutTimeUnit = readVersionTimeoutTimeUnit;
        this.docFiles = docFiles;
        this.blockingQueue = blockingQueue;
    }

    @Override
    public Map<Long,Map<String,Object>> call() throws Exception {

        Map<Long, Map<String,Object>> globalResults = new HashMap<Long, Map<String, Object>>();
        for(final DocumentIndexStatus d: docFiles.keySet()){
            final DocumentVersion v = docFiles.get(d);
            //start thread with timeout
            Callable<Map<String, Object>> rn = new Callable<Map<String, Object>>() {
                @Override
                public Map<String, Object> call() {
                    return readVersionFileToData((Document)d.getDmEntity(), v, d);
                }
            };
            Future<Map<String,Object>> result = Executors.newSingleThreadExecutor().submit(rn);
            try {
                Map<String, Object> item = result.get(readVersionTimeOut, readVersionTimeoutTimeUnit);
                globalResults.put(d.getDmEntity().getUid(), item);
                d.setReadFileDatas(item);
            }catch (InterruptedException ex){
                result.cancel(true);
                d.setBodyIndexed(false);
            }
            catch (TimeoutException ex){
                //Important to kill running thread !!
                 boolean cancelled = result.cancel(true);
                log.debug("cancelled read thread for #" + d.getDmEntity().getUid() + "(" + cancelled + ")");
                d.setBodyIndexed(false);
            }
            catch (ExecutionException ex){
                result.cancel(true);
                d.setBodyIndexed(false);
            }
            //put in queue !
            this.blockingQueue.offer(d);
        }
        return globalResults;
    }


    protected Map<String, Object> readVersionFileToData(Document document, DocumentVersion version,
                                                        DocumentIndexStatus documentIndexStatus){
        Object body = IndexHelper.EMPTY_STRING;;
        Map<String, Object> metaDatas = null;
        try {
            GlobalFilter globalFilter = new GlobalFilter();
            body = globalFilter.getFileBody(document, version.getInputStream());
            metaDatas = globalFilter.getMetaDatas();
        } catch (Throwable ex) {
            log.debug("Error while getting body", ex);
            documentIndexStatus.setBodyIndexed(false);
            StringWriter stringWriterStackTrace = new StringWriter();
            PrintWriter prw = new PrintWriter(stringWriterStackTrace);
            ex.printStackTrace(prw);
            documentIndexStatus.setError(ex.getMessage() + " ==> "
                    + stringWriterStackTrace);
        }
        if(metaDatas == null){
            metaDatas = new HashMap<String, Object>();
        } else {
            for (String mKey : metaDatas.keySet()) {
                metaDatas.put("FileMetaData_" + mKey, metaDatas.get(mKey));
            }
        }
        if (body instanceof String) {
            metaDatas.put("DocumentBody", body);
            documentIndexStatus.setBodyIndexed(true);
        }
        return metaDatas;
    }
}
