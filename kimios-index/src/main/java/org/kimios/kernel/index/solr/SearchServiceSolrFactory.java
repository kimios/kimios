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

package org.kimios.kernel.index.solr;

//import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.CoreContainer;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.index.SolrIndexManager;
import org.kimios.kernel.index.query.factory.DocumentFactory;
import org.kimios.kernel.index.query.factory.DocumentIndexStatusFactory;
import org.kimios.kernel.index.solr.utils.SolrServerBuilder;
import org.springframework.beans.factory.FactoryBean;

import java.net.URL;

/**
 * Spring factory to instantiate the @see SolrIndexManager
 *
 * @author Fabien Alin
 * @version 1.0
 * @see SolrIndexManager
 */
public class SearchServiceSolrFactory implements FactoryBean<SolrIndexManager>
{
    private static SolrServer solrServer;

    private static SolrServer contentSolrServer;

    private String solrUrl;

    private String solrHome;

    private String coreName;

    private IPathController pathController;

    private DocumentFactory solrDocumentFactory;

    private DocumentIndexStatusFactory documentIndexStatusFactory;

    private boolean serverMode = false;

    public DocumentIndexStatusFactory getDocumentIndexStatusFactory() {
        return documentIndexStatusFactory;
    }

    public void setDocumentIndexStatusFactory(DocumentIndexStatusFactory documentIndexStatusFactory) {
        this.documentIndexStatusFactory = documentIndexStatusFactory;
    }

    public DocumentFactory getSolrDocumentFactory()
    {
        return solrDocumentFactory;
    }

    public void setSolrDocumentFactory( DocumentFactory solrDocumentFactory )
    {
        this.solrDocumentFactory = solrDocumentFactory;
    }

    public IPathController getPathController()
    {
        return pathController;
    }

    public void setPathController(IPathController pathController)
    {
        this.pathController = pathController;
    }

    public String getCoreName()
    {
        return coreName;
    }

    public void setCoreName(String coreName)
    {
        this.coreName = coreName;
    }

    public boolean isServerMode()
    {
        return serverMode;
    }

    public void setServerMode(boolean serverMode)
    {
        this.serverMode = serverMode;
    }

    public String getSolrHome()
    {
        return solrHome;
    }

    public void setSolrHome(String solrHome)
    {
        this.solrHome = solrHome;
    }

    public String getSolrUrl()
    {
        return solrUrl;
    }

    public void setSolrUrl(String solrUrl)
    {
        this.solrUrl = solrUrl;
    }


    public synchronized static void shutdownSolr()
    {
       if(solrServer instanceof EmbeddedSolrServer){
           ((EmbeddedSolrServer)solrServer).getCoreContainer().shutdown();
       } else {
           solrServer.shutdown();
           contentSolrServer.shutdown();
       }
    }

    public SolrIndexManager getObject() throws Exception
    {

        if (solrServer == null) {
            if (serverMode) {
                solrServer = SolrServerBuilder.initHttpServer(solrUrl);
            } else {
                SolrServerBuilder.initLocalServer(solrHome, coreName, "/schema.xml");
                URL solrHomeUrl= SolrServerBuilder.initLocalServer(solrHome, coreName + "-body", "/schema-body.xml");
                SolrServer[] items = SolrServerBuilder.buidServers(solrHomeUrl, solrHome, coreName);

                solrServer = items[0];
                contentSolrServer = items[1];
            }

        }
        SolrIndexManager manager = new SolrIndexManager(solrServer, contentSolrServer);
        manager.setPathController(pathController);
        manager.setSolrDocumentFactory( solrDocumentFactory );
        manager.setDocumentIndexStatusFactory( documentIndexStatusFactory );
        return manager;
    }

    public Class<SolrIndexManager> getObjectType()
    {
        return SolrIndexManager.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
