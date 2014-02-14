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

package org.kimios.kernel.index.query;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.util.DateMathParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Fabien ALIN <fabien.alin@gmail.com>
 */
public class FacetQueryBuilder
{


    public static SolrQuery dateFacetBuiler( SolrQuery query, String facetField, String rangeMin, String rangeMax,
                                                String facetGapType, String facetGapNb )
        throws Exception
    {

        query.setFacet( true );
        SimpleDateFormat localFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        localFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ));
        Date rangeMinDt = null;
        Date rangeMaxDt = null;
        if(StringUtils.isNotBlank(rangeMin)){

            try{
                rangeMinDt = localFormat.parse( rangeMin );

            } catch (Exception e){
                e.printStackTrace();
                try{
                    rangeMinDt = new DateMathParser(localFormat.getTimeZone(), Locale.getDefault()).parseMath(rangeMin);
                }   catch (Exception ex){
                    ex.printStackTrace();
                }
            }


        }

        if(StringUtils.isNotBlank(rangeMax)){
            try{
                    rangeMaxDt = localFormat.parse( rangeMax );

            } catch (Exception e){
                e.printStackTrace();
                try{
                    rangeMaxDt = new DateMathParser(localFormat.getTimeZone(), Locale.getDefault()).parseMath(rangeMax);
                }   catch (Exception ex){
                     ex.printStackTrace();
                }
            }


        }
        System.out.println("Facet date start " + rangeMinDt + " / ==> end: " + rangeMaxDt);
        query.addDateRangeFacet( facetField, rangeMinDt, rangeMaxDt, facetGapNb + (StringUtils.isNotBlank(facetGapType) ? facetGapType : ""));

        return query;
    }


    public static SolrQuery numberFacetBuiler( SolrQuery query, String facetField, String rangeMin, String rangeMax,
                                             String facetGap )
        throws Exception
    {

        query.setFacet( true );
        Number rangeMinN = rangeMin != null ? Integer.parseInt( rangeMin ) : null;
        Number rangeMaxN = rangeMax != null ? Integer.parseInt( rangeMax ) : null;
        Number facetGapN = facetGap != null ? Integer.parseInt( facetGap ) : null;
        query.addNumericRangeFacet( facetField, rangeMinN, rangeMaxN, facetGapN );

        return query;
    }


    public static SolrQuery stringFacetBuilder( SolrQuery query, String facetField, String facetQuery )
        throws Exception
    {

        query.setFacet( true );
        if(facetQuery == null)
            query.addFacetField( facetField );
        else
            query.addFacetQuery( facetField + ":" + ClientUtils.escapeQueryChars(facetQuery));


        return query;
    }


}
