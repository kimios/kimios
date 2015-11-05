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

package org.kimios.kernel.index;

/**
 * Created by farf on 6/16/14.
 */


//import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
        import org.kimios.kernel.controller.IPathController;
        import org.kimios.kernel.events.IEventHandlerManager;
import org.kimios.kernel.events.impl.AddonDataHandler;
        import org.kimios.kernel.index.query.factory.DocumentFactory;
import org.kimios.kernel.index.query.factory.DocumentIndexStatusFactory;
import org.kimios.kernel.index.solr.SolrIndexer;
import org.kimios.kernel.index.solr.utils.SolrServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory to instantiate the @see SolrIndexManager.
 *
 * @author Fabien Alin
 * @version 1.0
 * @see org.kimios.kernel.index.SolrIndexManager
 */
public class SolrIndexFactory {

    private static Logger log = LoggerFactory.getLogger(SolrIndexFactory.class);

    private static SolrServer solrServer;

    private String solrUrl;

    private String solrHome;

    private String coreName;

    private IPathController pathController;

    private DocumentFactory solrDocumentFactory;

    private DocumentIndexStatusFactory documentIndexStatusFactory;

    private boolean serverMode = false;

    private AddonDataHandler addonDataHandler;

    private SolrIndexer solrIndexer;

    private IEventHandlerManager eventHandlerManager;

    private SessionEndHandler sessionEndHandler;

    public IEventHandlerManager getEventHandlerManager() {
        return eventHandlerManager;
    }

    public void setEventHandlerManager(IEventHandlerManager eventHandlerManager) {
        this.eventHandlerManager = eventHandlerManager;
    }

    public SessionEndHandler getSessionEndHandler() {
        return sessionEndHandler;
    }

    public void setSessionEndHandler(SessionEndHandler sessionEndHandler) {
        this.sessionEndHandler = sessionEndHandler;
    }

    public AddonDataHandler getAddonDataHandler() {
        return addonDataHandler;
    }

    public void setAddonDataHandler(AddonDataHandler addonDataHandler) {
        this.addonDataHandler = addonDataHandler;
    }

    public SolrIndexer getSolrIndexer() {
        return solrIndexer;
    }

    public void setSolrIndexer(SolrIndexer solrIndexer) {
        this.solrIndexer = solrIndexer;
    }

    public DocumentFactory getSolrDocumentFactory() {
        return solrDocumentFactory;
    }

    public void setSolrDocumentFactory(DocumentFactory solrDocumentFactory) {
        this.solrDocumentFactory = solrDocumentFactory;
    }

    public DocumentIndexStatusFactory getDocumentIndexStatusFactory() {
        return documentIndexStatusFactory;
    }

    public void setDocumentIndexStatusFactory(DocumentIndexStatusFactory documentIndexStatusFactory) {
        this.documentIndexStatusFactory = documentIndexStatusFactory;
    }

    public IPathController getPathController() {
        return pathController;
    }

    public void setPathController(IPathController pathController) {
        this.pathController = pathController;
    }

    public String getCoreName() {
        return coreName;
    }

    public void setCoreName(String coreName) {
        this.coreName = coreName;
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
    }

    public String getSolrHome() {
        return solrHome;
    }

    public void setSolrHome(String solrHome) {
        this.solrHome = solrHome;
    }

    public String getSolrUrl() {
        return solrUrl;
    }

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl;
    }

    public static void shutdownSolr() {
        if(solrServer instanceof EmbeddedSolrServer){
            ((EmbeddedSolrServer)solrServer).getCoreContainer().shutdown();
        } else {
            solrServer.shutdown();
        }
    }

    public SolrIndexManager createInstance() throws Exception {

        if (solrServer == null) {
            if (serverMode) {
                solrServer = SolrServerBuilder.initHttpServer(solrUrl);
            } else {
                solrServer = SolrServerBuilder.initLocalServer(solrHome, coreName);
            }
        }

        SolrIndexManager manager = new SolrIndexManager(solrServer);
        manager.setPathController(pathController);
        manager.setSolrDocumentFactory(solrDocumentFactory);
        manager.setDocumentIndexStatusFactory(documentIndexStatusFactory);

        eventHandlerManager.addHandler(addonDataHandler);
        SolrIndexer si = new SolrIndexer();
        si.setIndexManager(manager);
        eventHandlerManager.addHandler(si);
        log.debug("adding session event handler {}", sessionEndHandler);
        eventHandlerManager.addHandler(sessionEndHandler);
        return manager;
    }

}
