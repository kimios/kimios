package org.kimios.kernel.index.test;

import static org.junit.Assert.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kimios.kernel.index.SolrIndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 7/6/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/test-conf/ctx-kimios.xml",
    "classpath*:/META-INF/spring/ctx-kimios*.xml"})
public class IndexTest
{

    private static Logger logger = LoggerFactory.getLogger( IndexTest.class );

    @Autowired
    private SolrIndexManager solrIndexManager;

    @Test
    public void checkFieldList(){

        logger.info( "Starting test" );
        List<String> fieldList = solrIndexManager.filterFields();
        for(String u: fieldList)
            logger.info( "Filed Available for search: " + u );

    }

    @Test
    public void searchOnField(){

        SolrQuery q;


    }
}
