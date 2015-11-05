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
package org.kimios.kernel.index.query;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.kimios.kernel.security.model.DMSecurityRule;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Build Solr Queries
 */
public class QueryBuilder
{
    private static Logger log = LoggerFactory.getLogger( QueryBuilder.class );

    public static String buildAclQuery( Session session )
    {

        List<String> aclQueriesList = new ArrayList<String>();

        StringBuilder builder = new StringBuilder();

        /*
            Owner Query
         */

        builder.append( "+(DocumentOwner:" );
        builder.append( session.getUserName() );
        builder.append( "@" );
        builder.append( session.getUserSource() );
        builder.append( " OR " );
        /*
            Build list of possible ACL
         */
        List<DMSecurityRule> rules = new ArrayList<DMSecurityRule>();
        rules.add( DMSecurityRule.getInstance( session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                                               DMSecurityRule.READRULE ) );
        rules.add( DMSecurityRule.getInstance( session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                                               DMSecurityRule.WRITERULE ) );
        rules.add( DMSecurityRule.getInstance( session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                                               DMSecurityRule.FULLRULE ) );

        for ( Group g : session.getGroups() )
        {
            rules.add( DMSecurityRule.getInstance( g.getGid(), session.getUserSource(), SecurityEntityType.GROUP,
                                                   DMSecurityRule.READRULE ) );
            rules.add( DMSecurityRule.getInstance( g.getGid(), session.getUserSource(), SecurityEntityType.GROUP,
                                                   DMSecurityRule.WRITERULE ) );
            rules.add( DMSecurityRule.getInstance( g.getGid(), session.getUserSource(), SecurityEntityType.GROUP,
                                                   DMSecurityRule.FULLRULE ) );
        }

        String or = " OR ";

        for ( int u = 0; u < rules.size(); u++ )
        {
            DMSecurityRule rule = rules.get( u );
            builder.append( "DocumentACL:" );
            builder.append( ClientUtils.escapeQueryChars( rule.getRuleHash() ) );
            if ( u < ( rules.size() - 1 ) )
            {
                builder.append( or );
            }
        }
        builder.append( ") " );

        String bldNot = "-(DocumentACL:" + ClientUtils.escapeQueryChars(
            DMSecurityRule.getInstance( session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                                        DMSecurityRule.NOACCESS ).getRuleHash() );
        bldNot += ")";

        builder.append( bldNot );

        log.debug( "SOLR ACL Query: " + builder.toString() );

        return builder.toString();
    }

    public static String documentNameQuery( String query )
    {

        String documentNameQuery = "";
        boolean fileExtSearch = query.toLowerCase().contains( "." );
        if ( fileExtSearch )
        {
            String docName = null;
            String extension = null;
            extension = query.toLowerCase().substring( query.indexOf( "." ) + 1 );
            docName = query.toLowerCase().substring( 0, query.indexOf( "." ) );

            String[] items = docName.split("\\*");

            StringBuffer bf = new StringBuffer();

            if(items.length == 0){
                bf.append("*");
            } else{
                for(String t: items){
                    bf.append("*");
                    bf.append(ClientUtils.escapeQueryChars(t.toLowerCase()));
                }
            }

            //documentNameQuery = "DocumentName:*" + ClientUtils.escapeQueryChars( docName.toLowerCase() ) + "*";
            documentNameQuery += "DocumentName:" + bf.toString();
            documentNameQuery += " AND DocumentExtension:" + ClientUtils.escapeQueryChars( extension ) + "*";

            //documentNameQuery = documentNameQuery.replaceAll(ClientUtils.escapeQueryChars("*"), "*");


        }
        else
        {
            documentNameQuery = "DocumentName:*" + ClientUtils.escapeQueryChars( query.toLowerCase() ) + "*";
        }

        log.debug( "SOLR DocumentName Query: " + documentNameQuery );
        return documentNameQuery;
    }

    public static String documentParentQuery( String query )
    {
        String documentPathQuery = "DocumentParent:" + ClientUtils.escapeQueryChars( query ) + "/*";
        log.debug( "SOLR DocumentParent Query: " + documentPathQuery );
        return documentPathQuery;
    }

    public static String numberQuery( String fieldName, String rangeMin, String rangeMax )
    {
        String numbeQuery =
            fieldName + ":[" + ( rangeMin != null ? rangeMin : "*" ) + " TO " + ( rangeMax != null ? rangeMax : "*" )
                + "]";
        return numbeQuery;
    }

    public static String dateQuery( String dateFieldName, String min, String max )
        throws ParseException
    {

        SimpleDateFormat localFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        localFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        SimpleDateFormat solrFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'");
        solrFormat.setTimeZone(TimeZone.getTimeZone("UTC") );

        Date rangeMin = null;
        Date rangeMax = null;


        rangeMin = min != null && min.trim().length() > 0 ? localFormat.parse(min) : null;
        rangeMax = max != null && max.trim().length() > 0 ? localFormat.parse(max) : null;

        String minParam =  ( rangeMin != null ? solrFormat.format( rangeMin ) : "*" );

        String maxParam = (
                rangeMax != null ? solrFormat.format( rangeMax ) : "*" );
        if(rangeMin != null && rangeMax != null){
            if(rangeMin.compareTo(rangeMax) == 0){
                //add 1 day to rangemax

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(rangeMax.getTime());
                calendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.MILLISECOND, -1);
                maxParam =   solrFormat.format( calendar.getTime() );
            }
        }

        String documentPathQuery =
            dateFieldName + ":[" + minParam + " TO " + maxParam + "]";
        log.debug( "SOLR {} Query: {}", dateFieldName, documentPathQuery );
        return documentPathQuery;
    }

}
