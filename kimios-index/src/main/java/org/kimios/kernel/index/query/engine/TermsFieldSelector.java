package org.kimios.kernel.index.query.engine;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 7/6/13
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class TermsFieldSelector
{


    private static Logger log = LoggerFactory.getLogger( TermsFieldSelector.class );

    public List<String> filterValuesForField( String fieldName )
    {
         return null;
    }

    public List<String> filterFields( String fieldList )
    {

        try
        {
            SolrQuery query = new SolrQuery();
            query.setQueryType( "luke" );
            query.setQuery( "numTerms=0" );

            SolrResponse response = solrServer.query( query );


            log.info( response.toString() );



        }
        catch ( Exception e )
        {

        }

        return null;
    }


    private SolrServer solrServer;

    public SolrServer getSolrServer()
    {
        return solrServer;
    }

    public void setSolrServer( SolrServer solrServer )
    {
        this.solrServer = solrServer;
    }
}
