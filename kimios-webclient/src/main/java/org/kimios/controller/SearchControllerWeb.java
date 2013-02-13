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
package org.kimios.controller;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import org.kimios.core.wrappers.DMEntity;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.ws.pojo.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author Fabien Alin
 */
public class SearchControllerWeb
    extends Controller
{

    public SearchControllerWeb( Map<String, String> parameters )
    {
        super( parameters );
    }

    public String execute()
        throws Exception
    {
        List<Document> res = new ArrayList<Document>();
        if ( action.equalsIgnoreCase( "Quick" ) )
        {

            long dmEntityUid = -1;
            try
            {
                dmEntityUid = Long.parseLong( parameters.get( "dmEntityUid" ) );
            }
            catch ( Exception e )
            {
            }
            int dmEntityType = -1;
            try
            {
                dmEntityType = Integer.parseInt( parameters.get( "dmEntityType" ) );
            }
            catch ( Exception e )
            {
                dmEntityType = -1;
            }
            if ( dmEntityUid <= 0 )
            {
                dmEntityUid = -1;
                dmEntityType = -1;
            }

            res = searchController.quickSearch( sessionUid, dmEntityType, dmEntityUid, parameters.get( "name" ) );
            log.debug(
                "Quick search in uid: " + dmEntityUid + " [Type: " + dmEntityType + "]: " + res.size() + " results" );
        }
        else if ( action.equalsIgnoreCase( "Advanced" ) )
        {
            String positionUidS = parameters.get( "dmEntityUid" );
            String positionTypeS = parameters.get( "dmEntityType" );
            long positionUid = -1;
            try
            {
                positionUid = Long.parseLong( positionUidS );
            }
            catch ( Exception e )
            {
            }
            int positionType = -1;
            try
            {
                positionType = Integer.parseInt( positionTypeS );
            }
            catch ( Exception e )
            {
            }

            List<Long> alreayParsedMeta = new ArrayList<Long>();
            List<Criteria> criteriaList = new ArrayList<Criteria>();
            for ( String k : parameters.keySet() )
            {

                if ( parameters.get( k ) != null )
                {
                    Criteria c = new Criteria();
                    c.setFieldName( k );
                    c.setLevel( 0 );
                    c.setPosition( 0 );
                    if ( k.startsWith( "MetaData" ) && parameters.get( k ) != null
                        && parameters.get( k ).trim().length() > 0 )
                    {
                        String metaUid = k.split( "_" )[1];
                        if ( !alreayParsedMeta.contains( Long.parseLong( metaUid ) ) )
                        {

                            if ( k.contains( "String" ) )
                            {
                                c.setMetaType( new Long( 1 ) );
                            }
                            if ( k.contains( "Number" ) )
                            {
                                c.setMetaType( new Long( 2 ) );
                            }
                            if ( k.contains( "Date" ) )
                            {
                                c.setMetaType( new Long( 3 ) );
                            }
                            if ( k.contains( "Boolean" ) )
                            {
                                c.setMetaType( new Long( 4 ) );
                            }
                            c.setMetaId( Long.parseLong( metaUid ) );

                            if ( k.contains( "Date" ) || k.contains( "Number" ) )
                            {

                                String fromKey = k.split( "_" )[0] + "_" + metaUid + "_from";
                                String toKey = k.split( "_" )[0] + "_" + metaUid + "_to";
                                if ( parameters.get( fromKey ) != null )
                                {
                                    c.setRangeMin( parameters.get( fromKey ) );
                                }
                                if ( parameters.get( toKey ) != null )
                                {
                                    c.setRangeMax( parameters.get( toKey ) );
                                }
                        /*try
                        {
                            dFrom = new java.text.SimpleDateFormat( "yyyy-MM-dd" ).parse(
                                parameters.get( "" ) ).getTime();
                        }
                        catch ( Exception e )
                        {
                            dFrom = -1;
                        }
                        try
                        {
                            dTo = new java.text.SimpleDateFormat( "yyyy-MM-dd" ).parse(
                                parameters.get( "meta_value_" + metaUid + "_3_dto" ) ).getTime();
                        }
                        catch ( Exception e )
                        {
                            dTo = -1;
                        }*/
                            }
                            else
                            {
                                c.setQuery( parameters.get( k ) );
                            }
                            alreayParsedMeta.add( Long.parseLong( metaUid ) );
                        }
                        else
                        {
                        /* In case of meta already parsed: continue to next criteria */
                            continue;
                        }
                    }
                    else
                    {
                        c.setQuery( parameters.get( k ) );
                    }
                    criteriaList.add( c );
                }

            }

            int page = parameters.get( "page" ) != null ? Integer.parseInt( parameters.get( "page" ) ) : -1;
            int pageSize = parameters.get( "pageSize" ) != null ? Integer.parseInt( parameters.get( "pageSize" ) ) : -1;

            res = searchController.advancedSearchDocument( sessionUid, page, pageSize, positionUid, positionType,
                                                           criteriaList );
            log.debug( "Advanced search in uid: " + positionUid + " [Type: " + positionType + "]: " + res.size()
                           + " results" );
        }
        Vector<DMEntity> it = new Vector<DMEntity>();
        for ( Document d : res )
        {
            it.add( new DMEntity( d ) );
        }
        String jsonResp =
            new JSONSerializer().exclude( "class" ).transform( new DateTransformer( "MM/dd/yyyy hh:mm:ss" ),
                                                               "creationDate" ).transform(
                new DateTransformer( "MM/dd/yyyy hh:mm:ss" ), "checkoutDate" ).serialize( it );
        String fullResp = "{list:" + jsonResp + "}";
        return fullResp;
    }
}

