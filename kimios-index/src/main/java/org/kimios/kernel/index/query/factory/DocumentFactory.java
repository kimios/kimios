package org.kimios.kernel.index.query.factory;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.events.impl.AddonDataHandler;
import org.kimios.kernel.ws.pojo.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DocumentFactory
{
    public List<Document> getPojosFromSolrInputDocument(List<SolrDocument> docs){

        ArrayList<Document> documentArrayList = new ArrayList<Document>(  );

        for( SolrDocument doc: docs){


            Document pojo = new Document(  );

            pojo.setUid( (Long)doc.get( "DocumentUid" ) );
            pojo.setName( doc.get( "DocumentNameDisplayed" ).toString() );
            pojo.setPath( doc.get( "DocumentPath" ).toString() );
            Calendar creationDate = Calendar.getInstance();
            creationDate.setTime( ( Date)doc.get( "DocumentCreationDate" ) );
            pojo.setCreationDate(creationDate);
            Calendar updateDate = Calendar.getInstance();
            updateDate.setTime( ( Date)doc.get( "DocumentUpdateDate" ) );
            pojo.setUpdateDate(updateDate);
            Calendar versionCreationDate = Calendar.getInstance();
            versionCreationDate.setTime( ( Date)doc.get( "DocumentVersionCreationDate" ) );
            pojo.setVersionCreationDate(versionCreationDate);
            Calendar versionUpdateDate = Calendar.getInstance();
            versionUpdateDate.setTime( ( Date)doc.get( "DocumentVersionUpdateDate" ) );
            pojo.setVersionUpdateDate(versionUpdateDate);


            String documentTypeName = doc.get( "DocumentTypeName" ) != null ? doc.get("DocumentTypeName").toString() : "";
            pojo.setDocumentTypeName( documentTypeName );



            ArrayList<Long> docTypes = (ArrayList<Long>)doc.get("DocumentTypeUid");

            if(docTypes != null && docTypes.size() > 0)
                pojo.setDocumentTypeUid( docTypes.get( 0 ) );


            String ownerId = doc.get( "DocumentOwnerId" ) != null ? doc.get("DocumentOwnerId").toString() : "";
            pojo.setOwner( ownerId );
            String ownerSource = doc.get( "DocumentOwnerSource" ) != null ? doc.get("DocumentOwnerSource").toString() : "";
            pojo.setOwnerSource( ownerSource );

            String extension = doc.get( "DocumentExtension" ) != null ? doc.get("DocumentExtension").toString() : "";
            pojo.setExtension( extension );

            pojo.setFolderUid( (Long)doc.get( "DocumentParentId" ) );

            pojo.setLength( (Long)doc.get( "DocumentVersionLength" ) );

            pojo.setCheckedOut( (Boolean)(doc.get("DocumentCheckout") != null ? doc.get( "DocumentCheckout") : false ) );
            if(pojo.getCheckedOut()){
                Calendar checkoutDate = Calendar.getInstance();
                checkoutDate.setTime( ( Date)doc.get( "DocumentCheckoutDate" ) );
                pojo.setCheckoutDate(checkoutDate);
                String checkoutOwnerId = doc.get( "DocumentCheckoutOwnerId" ) != null ? doc.get("DocumentCheckoutOwnerId").toString() : "";
                pojo.setCheckoutUser( checkoutOwnerId );
                String checkoutOwnerSource = doc.get( "DocumentCheckoutOwnerSource" ) != null ?
                    doc.get("DocumentCheckoutOwnerSource").toString() : "";
                pojo.setCheckoutUserSource( checkoutOwnerSource );
            }
            pojo.setWorkflowStatusName( doc.get( "DocumentWorkflowStatusName" ) != null ? doc.get( "DocumentWorkflowStatusName" ).toString() : "" );
            pojo.setWorkflowStatusUid((Long) (doc.get( "DocumentWorkflowStatusUid" ) != null ? doc.get( "DocumentWorkflowStatusUid" ) : null) );
            pojo.setOutOfWorkflow( (Boolean) doc.get( "DocumentOutWorkflow" ) );

            pojo.setAddonDatas((String) doc.get("DocumentRawAddonDatas"));

            documentArrayList.add( pojo );

        }

        return documentArrayList;






    }



}
