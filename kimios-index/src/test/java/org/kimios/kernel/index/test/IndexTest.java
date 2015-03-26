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

package org.kimios.kernel.index.test;

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
