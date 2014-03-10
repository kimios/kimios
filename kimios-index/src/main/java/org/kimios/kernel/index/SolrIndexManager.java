/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

import org.apache.lucene.search.Query;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.DocumentWorkflowStatusRequest;
import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.dms.WorkflowStatus;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.filters.impl.GlobalFilter;
import org.kimios.kernel.index.query.factory.DocumentFactory;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.kernel.security.DMEntityACL;
import org.kimios.kernel.ws.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class SolrIndexManager
    implements ISolrIndexManager
{
    private static Logger log = LoggerFactory.getLogger( SolrIndexManager.class );

    private Reindexer reindexer = null;

    private Thread reindexThread = null;

    private IPathController pathController;

    private org.kimios.kernel.index.query.factory.DocumentFactory solrDocumentFactory;

    public DocumentFactory getSolrDocumentFactory()
    {
        return solrDocumentFactory;
    }

    public void setSolrDocumentFactory( DocumentFactory solrDocumentFactory )
    {
        this.solrDocumentFactory = solrDocumentFactory;
    }

    public SolrIndexManager( SolrServer solr )
    {
        this.solr = solr;
    }

    private final SolrServer solr;

    public synchronized void reindex( String path )
        throws DataSourceException, ConfigException, IndexException
    {
        final String finalPath = path;
        if ( this.reindexThread == null || !this.reindexThread.isAlive() )
        {

            this.reindexer = new Reindexer( this, pathController, finalPath );
            this.reindexThread = new Thread( this.reindexer );
            this.reindexThread.setName( "ReindexThread" );
            this.reindexThread.start();
        }
        else
        {
            throw new IndexException( null, "A reindex process is already running." );
        }
    }

    public int getReindexProgression()
    {
        if ( this.reindexThread != null && this.reindexer != null )
        {
            return this.reindexer.getReindexProgression();
        }
        else
        {
            return -1;
        }
    }

    public boolean deleteDirectory( File path )
    {
        if ( path.exists() )
        {
            File[] files = path.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i].isDirectory() )
                {
                    deleteDirectory( files[i] );
                }
                else
                {
                    files[i].delete();
                }
            }
        }
        return ( path.delete() );
    }

    public void deleteDocument( Document document )
        throws IndexException
    {
        try
        {
            this.solr.deleteById( String.valueOf( document.getUid() ) );
            log.debug( "Commited deletion of document " + document.getUid() );
            this.solr.commit();
        }
        catch ( IOException e )
        {
            throw new IndexException( e, "An exception occured while deleting document " + document.getUid() + " : "
                + e.getMessage() );
        }
        catch ( SolrServerException e )
        {
            throw new IndexException( e, "An exception occured while deleting document " + document.getUid() + " : "
                + e.getMessage() );
        }
    }


    private SolrInputDocument toSolrInputDocument( Document document, SolrDocument previousSolrDocument )
        throws DataSourceException, ConfigException
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField( "DocumentUid", document.getUid() );
        doc.addField( "DocumentName", document.getName().toLowerCase() );
        doc.addField( "DocumentNameDisplayed", document.getName() );
        doc.addField( "DocumentNameAnalysed", document.getName() );
        if ( document.getExtension() != null )
        {
            doc.addField( "DocumentExtension", document.getExtension().toLowerCase() );
        }
        doc.addField( "DocumentOwner", document.getOwner() + "@" + document.getOwnerSource() );
        doc.addField( "DocumentOwnerId", document.getOwner() );
        doc.addField( "DocumentOwnerSource", document.getOwnerSource() );
        doc.addField( "DocumentPath", document.getPath() );
        doc.addField( "DocumentParent", document.getFolder().getPath() + "/" );
        doc.addField( "DocumentParentId", document.getFolder().getUid() );
        DocumentVersion version =
            FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion( document );

        if ( version == null )
        {
            log.error( "Document {} has no version", document.getUid() );
            return null;
        }
        //standard datas
        doc.addField( "DocumentCreationDate", document.getCreationDate() );
        doc.addField( "DocumentUpdateDate", document.getUpdateDate() );
        doc.addField( "DocumentVersionId", version.getUid() );
        doc.addField( "DocumentVersionCreationDate", version.getCreationDate() );
        doc.addField( "DocumentVersionUpdateDate", version.getModificationDate() );
        doc.addField( "DocumentVersionOwner", version.getAuthor() + "@" + version.getAuthorSource() );
        doc.addField( "DocumentVersionOwnerId", version.getAuthor() );
        doc.addField( "DocumentVersionOwnerSource", version.getAuthorSource() );
        doc.addField( "DocumentVersionLength", version.getLength() );
        doc.addField( "DocumentVersionHash", version.getHashMD5() + ":" + version.getHashSHA1() );

        Lock lock = document.getCheckoutLock();

        doc.addField( "DocumentCheckout", lock != null );

        if ( lock != null )
        {
            doc.addField( "DocumentCheckoutOwnerId", lock.getUser() );
            doc.addField( "DocumentCheckoutOwnerSource", lock.getUserSource() );
            doc.addField( "DocumentCheckoutDate", lock.getDate() );
        }
        // DocumentOutWorkflow

        DocumentWorkflowStatusRequest req =
            FactoryInstantiator.getInstance().getDocumentWorkflowStatusRequestFactory().getLastPendingRequest(
                document );
        DocumentWorkflowStatus st =
            FactoryInstantiator.getInstance().getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(
                document.getUid() );
        boolean outOfWorkflow = true;
        if ( req != null )
        {
            outOfWorkflow = false;
        }
        if ( st != null )
        {

            WorkflowStatus stOrg = FactoryInstantiator.getInstance().getWorkflowStatusFactory().getWorkflowStatus(
                st.getWorkflowStatusUid() );
            doc.addField( "DocumentWorkflowStatusName", stOrg.getName() );
            doc.addField( "DocumentWorkflowStatusUid", st.getWorkflowStatusUid() );
            if ( stOrg.getSuccessorUid() == null )
            {
                outOfWorkflow = true;
            }
        }
        doc.addField( "DocumentOutWorkflow", outOfWorkflow );
        if ( version.getDocumentType() != null )
        {
            log.info( "Document Type Found for version" );
            doc.addField( "DocumentTypeUid", version.getDocumentType().getUid() );
            doc.addField( "DocumentTypeName", version.getDocumentType().getName() );
            List<MetaValue> values = FactoryInstantiator.getInstance().getMetaValueFactory().getMetaValues( version );
            log.info( "Meta Values Found for version " + values.size() + " / " + values );
            for ( MetaValue value : values )
            {
                switch ( value.getMeta().getMetaType() )
                {
                    case MetaType.STRING:
                        doc.addField( "MetaDataString_" + value.getMetaUid(),
                                      ( (String) value.getValue() ).toLowerCase() );
                        break;
                    case MetaType.BOOLEAN:
                        doc.addField( "MetaDataBoolean_" + value.getMetaUid(), value.getValue() );
                        break;
                    case MetaType.NUMBER:
                        doc.addField( "MetaDataNumber_" + value.getMetaUid(), value.getValue() );
                        break;
                    case MetaType.DATE:

                        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
                        if(value != null && value.getValue() != null){
                            cal.setTime( (Date) value.getValue() );
                            doc.addField( "MetaDataDate_" + value.getMetaUid(), cal.getTime() );
                        }

                        break;
                    default:
                        doc.addField( "MetaData_" + value.getMetaUid(), value.getValue() );
                        break;
                }
            }
        }
        for ( String attribute : document.getAttributes().keySet() )
        {
            doc.addField( "Attribute_" + attribute.toUpperCase(),
                          document.getAttributes().get( attribute ).getValue() );
        }
        if(document.getAddOnDatas() != null)
            doc.addField("DocumentRawAddonDatas", document.getAddOnDatas());
        Object body = null;
        Map<String, Object> metaDatas = null;

        /*
            check old pojo for hash
         */
        boolean shouldReindexBody = true;
        if(previousSolrDocument != null){
            log.debug("matching " + doc.getFieldValue("DocumentVersionHash") + " against " + previousSolrDocument.getFieldValue("DocumentVersionHash"));
            String oldCombinedHash = previousSolrDocument.getFieldValue("DocumentVersionHash").toString();
            if(doc.getFieldValue("DocumentVersionHash").equals(oldCombinedHash)){
                log.debug("old version matched on " + oldCombinedHash);
                /*
                    get old datas
                 */
                doc.addField("DocumentBody", previousSolrDocument.getFieldValue("DocumentBody"));
                for(String fileMetaFieldName: previousSolrDocument.getFieldNames()){
                    if(fileMetaFieldName.startsWith("FileMetaData_")){
                        doc.addField(fileMetaFieldName, previousSolrDocument.getFieldValue(fileMetaFieldName));
                        log.debug("added previous field " + fileMetaFieldName + " ==> " + previousSolrDocument.getFieldValue(fileMetaFieldName));
                    }
                }

                shouldReindexBody = false;
            }
        }

        if(shouldReindexBody) {
            log.debug("previous document version unavailable in index. will process document body");
            try
            {
                GlobalFilter globalFilter = new GlobalFilter();
                body = globalFilter.getFileBody( document, version.getInputStream() );
                metaDatas = globalFilter.getMetaDatas();
            }
            catch ( Throwable ex )
            {
                log.debug( "Error while getting body", ex );
            }
            if ( body == null )
            {
                body = IndexHelper.EMPTY_STRING;
            }
            if ( body instanceof String )
            {
                doc.addField( "DocumentBody", (String) body );
            }
            if ( metaDatas != null )
            {
                for ( String mKey : metaDatas.keySet() )
                {
                    doc.addField( "FileMetaData_" + mKey, metaDatas.get( mKey ) );
                }
            }
        }
        List<DMEntityACL> acls =
            org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory().getDMEntityACL(
                document );

        for ( int i = 0; i < acls.size(); i++ )
        {
            doc.addField( "DocumentACL", acls.get( i ).getRuleHash() );
        }

        return doc;
    }

    public void indexDocument( DMEntity documentEntity )
        throws IndexException, DataSourceException, ConfigException
    {
        try
        {

            Document document = (Document) documentEntity;

            /*
                check if document must be reindexed
             */


            String docQuery = "DocumentUid:" + documentEntity.getUid();



            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(docQuery);
            SolrDocumentList searchResponse = this.loadDocuments(solrQuery);

            SolrDocument previousRecord = null;
            if(searchResponse.getNumFound() == 1){
                log.debug("found one document for uid #" + documentEntity.getUid());
                previousRecord = searchResponse.get(0);
            }
            this.deleteDocument( document );
            SolrInputDocument solrInputDocument = toSolrInputDocument( document, previousRecord );
            this.solr.add( solrInputDocument );

            this.solr.commit();
        }
        catch ( IOException io )
        {
            throw new IndexException( io,
                                      "An exception occured while indexing document " + documentEntity.getUid() + " : "
                                          +
                                          io.getMessage() );
        }
        catch ( SolrServerException ex )
        {
            throw new IndexException( ex,
                                      "An exception occured while indexing document " + documentEntity.getUid() + " : "
                                          +
                                          ex.getMessage() );
        }
    }

    public void indexDocumentList( List<DMEntity> documentEntities )
        throws IndexException, DataSourceException, ConfigException
    {
        try
        {
            List<SolrInputDocument> updatedDocument = new ArrayList<SolrInputDocument>();
            List<String> updatedDocumentIds = new ArrayList<String>();
            for ( DMEntity doc : documentEntities )
            {
                SolrInputDocument solrInputDocument = toSolrInputDocument( (Document) doc, null );
                if ( solrInputDocument != null )
                {
                    updatedDocumentIds.add( String.valueOf( doc.getUid() ) );
                    updatedDocument.add( solrInputDocument );
                    log.debug( "Doc added to solr Query " + doc + " / " + solrInputDocument );
                }
            }

            this.solr.deleteById( updatedDocumentIds );
            this.solr.add( updatedDocument );
            this.solr.commit();
        }
        catch ( IOException io )
        {
            throw new IndexException( io, "An exception occured while indexing document list " + io.getMessage() );
        }
        catch ( SolrServerException ex )
        {
            throw new IndexException( ex, "An exception occured while indexing document " + ex.getMessage() );
        }
    }

    public List<? extends Number> executeQuery( Query query )
        throws IndexException
    {
        QueryResponse rsp;
        try
        {
            rsp = solr.query( new SolrQuery( query.toString() ) );
            final List<Long> list = new Vector<Long>();
            SolrDocumentList documentList = rsp.getResults();
            for ( SolrDocument dc : documentList )
            {
                list.add( (Long) dc.getFieldValue( "DocumentUid" ) );
            }
            return list;
        }
        catch ( SolrServerException ex )
        {
            throw new IndexException( ex, ex.getMessage() );
        }
    }


    private SolrDocumentList loadDocuments( SolrQuery query )
            throws IndexException
    {
        QueryResponse rsp;
        try{

            rsp = solr.query( query );
            SolrDocumentList documentList = rsp.getResults();
            return documentList;

        }   catch (SolrServerException ex )
        {
            throw new IndexException( ex, ex.getMessage() );
        }
    }


    public SearchResponse executeSolrQuery( SolrQuery query )
        throws IndexException
    {
        QueryResponse rsp;
        try
        {
            rsp = solr.query( query );
            final List<Long> list = new Vector<Long>();
            SolrDocumentList documentList = rsp.getResults();
            for ( SolrDocument dc : documentList )
            {

                if(log.isDebugEnabled())
                    log.debug( "Solr result doc: " + dc );

                list.add( (Long) dc.getFieldValue( "DocumentUid" ) );
            }
            SearchResponse searchResponse = new SearchResponse( list );

            if(log.isDebugEnabled())
                log.debug(" Solr Num found " + documentList.getNumFound());

            searchResponse.setResults( new Long( documentList.getNumFound() ).intValue() );
            searchResponse.setRows( solrDocumentFactory.getPojosFromSolrInputDocument( rsp.getResults() ) );
            HashMap facetsData = new HashMap();
            if ( rsp.getFacetRanges() != null )
            {
                for ( RangeFacet facet : rsp.getFacetRanges() )
                {;
                    List<RangeFacet.Count> items = facet.getCounts();
                    for ( RangeFacet.Count fc : items )
                    {
                        if ( facet.getStart() instanceof Date )
                        {
                            facetsData.put( fc.getValue() + " TO " + fc.getValue() + facet.getGap().toString(),
                                            new Object[]{ fc.getValue(), fc.getCount() } );
                        }
                        else
                        {
                            facetsData.put( ( fc.getValue() + " TO " + ( Long.parseLong( fc.getValue() )
                                + ( (Number) facet.getGap() ).longValue() ) ),
                                            new Object[]{ fc.getValue(), fc.getCount() } );
                        }
                    }
                    break;
                }
            }
            if ( rsp.getFacetFields() != null )
            {
                for ( FacetField facet : rsp.getFacetFields() )
                {
                    if(log.isDebugEnabled())
                        log.debug("Returned Facet " + facet.getName() + " --> " + facet.getValueCount());
                    List<FacetField.Count> items = facet.getValues();
                    if(items != null){
                        for ( FacetField.Count fc : items )
                        {
                            facetsData.put( fc.getName(), new Object[]{ fc.getName(), fc.getCount() } );
                        }
                        break;
                    }

                }
            }
            searchResponse.setFacetsData( facetsData );
            return searchResponse;
        }
        catch ( SolrServerException ex )
        {
            throw new IndexException( ex, ex.getMessage() );
        }
    }

    public void updateAcls( long docUid, List<DMEntityACL> acls, boolean commit )
        throws IndexException
    {
        try
        {
            log.trace( "Updating ACL for document #" + docUid );
            QueryResponse rsp = this.solr.query( new SolrQuery( "DocumentUid:" + docUid ) );
            if ( rsp.getResults().getNumFound() > 0 )
            {
                SolrDocument doc = rsp.getResults().get( 0 );
                this.solr.deleteById( String.valueOf( docUid ) );
                doc.removeFields( "DocumentACL" );
                SolrInputDocument docUpdate = ClientUtils.toSolrInputDocument( doc );
                for ( int j = 0; j < acls.size(); j++ )
                {
                    docUpdate.addField( "DocumentACL", acls.get( j ).getRuleHash() );
                }
                this.solr.add( docUpdate );
            }
        }
        catch ( Exception e )
        {
            throw new IndexException( e, e.getMessage() );
        }
        finally
        {

            if(commit){
                try
                {
                    this.solr.commit();
                }
                catch ( Exception e )
                {
                    throw new IndexException( e, e.getMessage() );
                }
            }

        }
    }

    public void deletePath( String path )
        throws IndexException
    {
        try
        {

            log.debug( "Path delete: " + path );
            if ( path.endsWith( "/" ) )
            {
                path = path.substring( 0, path.lastIndexOf( "/" ) );
            }
            Query q = new DocumentParentClause( path ).getLuceneQuery();
            this.solr.deleteByQuery( "DocumentParent:" + path + "/*" );
            this.solr.commit();
        }
        catch ( Exception ex )
        {
            throw new IndexException( ex, ex.getMessage() );
        }
    }

    public void deleteByQuery( String query )
        throws IndexException
    {
        try
        {
            this.solr.deleteByQuery( query );
            this.solr.commit();
        }
        catch ( Exception ex )
        {
            throw new IndexException( ex, ex.getMessage() );
        }
    }

    public void updatePath( String oldPath, String newPath )
        throws IndexException
    {
        try
        {
            if ( oldPath.endsWith( "/" ) )
            {
                oldPath = oldPath.substring( 0, oldPath.lastIndexOf( "/" ) );
            }
            if ( !newPath.endsWith( "/" ) )
            {
                newPath += "/";
            }
            Query q = new DocumentParentClause( oldPath ).getLuceneQuery();
            SolrDocumentList items = this.solr.query( new SolrQuery( q.toString() ) ).getResults();

            if ( items.getNumFound() > 0 )
            {
                List<SolrInputDocument> documentList = new ArrayList<SolrInputDocument>();
                this.solr.deleteByQuery( q.toString() );
                for ( SolrDocument doc : items )
                {
                    String path = doc.getFieldValue( "DocumentParent" ).toString();
                    path = newPath + path.substring( oldPath.length() + 1 );
                    doc.removeFields( "DocumentParent" );
                    SolrInputDocument updatedDocument = ClientUtils.toSolrInputDocument( doc );
                    updatedDocument.addField( "DocumentParent", path );
                    documentList.add( updatedDocument );
                }
                this.solr.add( documentList );
                this.solr.commit();
            }
        }
        catch ( Exception ex )
        {
            throw new IndexException( ex, ex.getMessage() );
        }
    }

    public List<String> filterFields()
    {

        try
        {
            SolrQuery query = new SolrQuery();
            query.setRequestHandler( "luke" );
            query.setQuery( "numTerms=0" );
            SolrResponse response = solr.query( query );
            SimpleOrderedMap items = (SimpleOrderedMap) response.getResponse().get( "fields" );
            List<String> fieldList = new ArrayList<String>();
            for ( int u = 0; u < items.size(); u++ )
            {
                fieldList.add( items.getName( u ) );
            }

            return fieldList;
        }
        catch ( Exception e )
        {
            throw new IndexException( e, e.getMessage() );
        }


    }


    public void commit() throws IndexException {

        try{
            this.solr.commit();
        }   catch (Exception e){
            throw new IndexException(e);
        }
    }

    public void closeSolr(){
        try{
            this.solr.shutdown();
        }   catch (Exception e){
            log.error("Error while closing Solr", e);
        }
    }

    public IPathController getPathController()
    {
        return pathController;
    }

    public void setPathController( IPathController pathController )
    {
        this.pathController = pathController;
    }
}
