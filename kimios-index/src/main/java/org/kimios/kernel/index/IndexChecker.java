/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kimios.kernel.dms.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: udate index checker

public class IndexChecker
{
    Logger log = LoggerFactory.getLogger(IndexChecker.class);

    public enum IndexError
    {

        NOT_IN_INDEX,
        MORE_THAN_ONCE_IN_INDEX,
        PATH,
        ACL_COUNT,
        ACL_DATA,
        EXTENSION,
        NAME,
        OWNER,
        NO_DOCUMENT_VERSION,
        DOCUMENT_TYPE;
    }

    private static IndexChecker instance;

    private IndexChecker()
    {
    }

    public static IndexChecker getInstance()
    {
        if (instance == null) {
            instance = new IndexChecker();
        }
        return instance;
    }

    private boolean inProcess = false;

    private long totalDoc = 0;

    private long ind = 0;

    private final Map<Document, List<IndexError>> errors = new HashMap<Document, List<IndexError>>();

    public void checkIndex()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                log.info("Starting index check");
                try {
                    inProcess = true;
                    /*IndexManager manager = IndexManager.getInstance();
             IndexReader reader = manager.getIndexReader();
             DataTransactionManager dtm = HTransactionManager.newOperation();
             InitSessionFactory.setAutoCommitSession();
             Session s = InitSessionFactory.getInstance().getCurrentSession();
             Query q = s.createQuery("from Document");
             List<Document> res = (List<Document>)q.list();

             totalDoc = res.size();

             log.info("Document(s) count in database: " + res.size());
             log.info("Document(s) count in index: " + reader.numDocs());
             log.info("Deleted document(s) count in index: " + reader.numDeletedDocs());
             IndexSearcher searcher = new IndexSearcher(reader);
             for(Document item: res){
               ind++;
               List<IndexError> errList = new ArrayList<IndexError>();
               BooleanQuery lQuery = new BooleanQuery();
               lQuery.add(new BooleanClause(new TermQuery(new Term("DocumentUid",NumberUtils.pad(item.getUid()))), Occur.MUST));
               final List<Integer> list = new Vector<Integer>();
               TopScoreDocCollector dcColl = TopScoreDocCollector.create(Integer.MAX_VALUE, true);
               searcher.search(lQuery, dcColl);
               ScoreDoc[] items = dcColl.topDocs().scoreDocs;
               for(int it = 0; it < items.length; it++){
                 list.add(items[it].doc);
               }
               if(list.size() == 0){
                 log.error("Document " + item.getUid() + " at " + item.getPath() + " NOT INDEXED");
                 errList.add(IndexError.NOT_IN_INDEX);
               }else{
                 if(list.size() > 1){
                   log.error("Document " + item.getUid() + " at " + item.getPath() + " INDEXED MORE THAN ONCE");
                   errList.add(IndexError.MORE_THAN_ONCE_IN_INDEX);
                 }else{
                   log.info("Starting ACL Check");
                   org.apache.lucene.document.Document lDoc = searcher.doc(list.get(0));
                   /*
                    *  Check ACL
                    */
                    /* List<DMEntityACL> acls = new HDMEntitySecurityFactory().getDMEntityACL(item);
                   List<String> lACLs = Arrays.asList(lDoc.getValues("DocumentACL"));
                   if(lACLs.size() != acls.size()){
                     log.error("Invalid ACLs Count for " + item.getUid() + " at " + item.getPath());
                     errList.add(IndexError.ACL_COUNT);
                   }
                   for(DMEntityACL acItem: acls){
                     if(!lACLs.contains(acItem.getRuleHash())){
                       log.error("Invalid ACLs data for " + item.getUid() + " at " + item.getPath());
                       errList.add(IndexError.ACL_DATA);
                       break;
                     }
                   }
                   /*
                    * Check Extension and path
                    */
                    /* if(item.getExtension() != null){
                     String extension = lDoc.get("DocumentExtension");
                     if(!item.getExtension().equalsIgnoreCase(extension)){
                       log.error("Invalid Extension");
                       errList.add(IndexError.EXTENSION);
                     }
                   }
                   String parentPath = lDoc.get("DocumentParent");
                   if(!item.getPath().substring(0,item.getPath().lastIndexOf("/") + 1)
                       .equalsIgnoreCase(parentPath)){
                     log.error("Invalid document parent path");
                     errList.add(IndexError.PATH);
                   }
                   /*
                    * Check Version, Document Type and metas
                    */
                    /*   String documentTypeUid = lDoc.get("DocumentTypeUid");
                       DocumentVersion dv = new HDocumentVersionFactory().getLastDocumentVersion(item);
                       if(dv != null){
                         DocumentType dt = dv.getDocumentType();
                         if(dt != null){
                           if(!(Long.parseLong(documentTypeUid) == dt.getUid())){
                             errList.add(IndexError.DOCUMENT_TYPE);
                             log.error("Invalid document type");
                           }
                         }else{
                           if(!(documentTypeUid == null)){
                             log.error("Invalid document type");
                             errList.add(IndexError.DOCUMENT_TYPE);
                           }
                         }
                       }else{
                         errList.add(IndexError.NO_DOCUMENT_VERSION);
                       }
                     }
                   }
                   /*
                    *  Record errors
                    */
                    /*if(errList.size() > 0)
                       errors.put(item, errList);
                   }
                   dtm.end();
                   searcher.close();
                   reader.close(); */
                } catch (Exception e) {
                    log.error("Exception during check", e);
                }
                inProcess = false;
                log.info("Ending index check");
            }
        }).start();
    }

    public boolean isInProcess()
    {
        return inProcess;
    }

    public void setInProcess(boolean inProcess)
    {
        this.inProcess = inProcess;
    }

    public long getTotalDoc()
    {
        return totalDoc;
    }

    public void setTotalDoc(long totalDoc)
    {
        this.totalDoc = totalDoc;
    }

    public long getInd()
    {
        return ind;
    }

    public void setInd(long ind)
    {
        this.ind = ind;
    }

    public Map<Document, List<IndexError>> getErrors()
    {
        return errors;
    }
}

