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

package org.kimios.kernel.index.query.engine;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
