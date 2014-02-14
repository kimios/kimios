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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 *
 *
 *
 *
 */
public class ManualLoadingTester
{


    public static void pause()
        throws Exception
    {
        BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.print( "Paused: \n" );
        String t = br.readLine();
    }


    public static void overrideBean( ApplicationContext ctx, ConfigurableListableBeanFactory bf, String beanId )
    {
        BeanDefinition bdf = bf.getBeanDefinition( beanId );
        BeanDefinitionRegistry br = (BeanDefinitionRegistry) bf;
        br.removeBeanDefinition( beanId );
        System.out.println( " > Contains bean after remove ? " + ctx.containsBean( beanId ) );
        br.registerBeanDefinition( beanId, bdf );
        System.out.println( " > Contains bean after register ? " + ctx.containsBean( beanId ) );
    }


    public static void main( String[] args )
        throws Exception
    {
        /*Properties prop = new Properties();
        prop.setProperty( "server.url", "http://localhost:8080" );
        Properties prop = new Properties();
        prop.setProperty( "server.url", "http://kimios.devlib.infra/kimios" );
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
        String sessionId = null;

        for ( int i = 0; i < 4; i++ )
        {

         /*
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
<<<<<<< HEAD
        /*System.out.println( " > " + searchService.listSearchQueries( sessionId ).size() );*/

    }
}
