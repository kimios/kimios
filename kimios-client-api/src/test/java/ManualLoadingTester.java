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

import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.kimios.client.controller.SecurityController;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchResponse;
import org.kimios.webservices.DateParamConverter;
import org.kimios.webservices.NotificationService;
import org.kimios.webservices.SearchService;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 *
 *
 *
 *
 */
public class ManualLoadingTester
{


    public static void main( String[] args )
        throws Exception
    {
        /*Properties prop = new Properties();
        prop.setProperty( "server.url", "http://localhost:8080" );
        prop.setProperty( "service.context", "/services" );
        prop.setProperty( "temp.directory", "/tmp" );
        prop.setProperty( "transfer.chunksize", "10240" );
        PropertyPlaceholderConfigurer cfgHolder = new PropertyPlaceholderConfigurer();
        cfgHolder.setIgnoreUnresolvablePlaceholders( true );
        cfgHolder.setBeanName( "propResolver" );
        cfgHolder.setProperties( prop );
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();
        ctx.addBeanFactoryPostProcessor( cfgHolder );
        ctx.setConfigLocations( new String[]{ "kimios-ctx-client-rest.xml" } );
        ctx.refresh();
        SecurityController secCtrl = ctx.getBean( SecurityController.class );

        String sessionId = secCtrl.startSession( "farfou", "loufarf", "testing" );

        System.out.println( " >> Session Id " + sessionId );

        SearchService searchService = ctx.getBean( SearchService.class );





        /*List<Object> obj = new ArrayList<Object>();
        obj.add(new DateParamConverter());
        NotificationService proxy = JAXRSClientFactory.create("http://localhost:8080/services/rest/", NotificationService.class, obj);
*/
        /*

        sessionId=NCEQLZQNP95HEVFCWWXJ&documentId=17434&workflowStatusId=1&userName=farfou&userSource=testing&statusDate=Fri+Feb+08+17%3A36%3A54+CET+2013

         */
        //proxy.acceptRequest(sessionId, 17434, 1, "farfou", "testing", new Date(), "");
           /*
        List<Criteria> criteriaList = new ArrayList<Criteria>();

        Criteria q = new Criteria();
        /*List<String> fq = new ArrayList<String>();
         /*
        fq.add( "Facture_teclib" );
        q.setFiltersValues( fq );
        q.setLevel( 0 );
        q.setFaceted( true );
        q.setFieldName( "DocumentOwner" );
        criteriaList.add( q );
        q = new Criteria();
        q.setQuery( null );
        q.setLevel( 1 );
        q.setFaceted( true );
        q.setFieldName( "DocumentCreationDate" );
        q.setRangeMin( "2010-01-01'T'00:00:00Z" );
        q.setRangeMax( "2013-12-31'T'00:00:00Z" );
        q.setDateFacetGapRange( "1" );
        q.setDateFacetGapType( "YEAR" );

        criteriaList.add( q );
        //"Facture_Fournisseur/2010-04-01T00:00:00Z TO 2010-05-01T00:00:00Z"

        searchService.saveSearchQuery( sessionId, null, "FacetedOwner", criteriaList, null, null);
        SearchResponse response =
            searchService.advancedSearchDocuments( sessionId, criteriaList, 0, 10, null, null, "flegastelois@teclib/2011-01-01T00:00:00Z TO 2011-01-01T00:00:00Z+1MONTH");

        if ( response.getFacetsData() != null )
        {
            for ( Object o : response.getFacetsData().keySet() )
            {

                Date endDate = null;
                try
                {
                    DateMathParser mp = new DateMathParser( TimeZone.getTimeZone( "UTC" ), Locale.getDefault() );
                    mp.setNow( new SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss'Z'" ).parse( o.toString() ) );
                    endDate = mp.parseMath( "+" + q.getDateFacetGapRange() + q.getDateFacetGapType() );
                }
                catch ( Exception e )
                {

                }

                System.out.println( " > " + o + " / " + endDate + "      =====> " + response.getFacetsData().get( o ) );
            }

        }   */

       /* searchService.saveSearchQuery( sessionId, null, "Search Request Name", criteriaList, null, null );
        List<SearchRequest> srList = searchService.listSearchQueries( sessionId );
        for ( SearchRequest sr : srList )
        {
            System.out.println( " >" + sr );
            System.out.println( "Removing: " );
            searchService.deleteSearchQuery( sessionId, sr.getId() );
        }
        /*System.out.println( " > " + searchService.listSearchQueries( sessionId ).size() );*/

    }
}
