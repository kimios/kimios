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

package org.kimios.kernel.index.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.ConfigSolr;
import org.apache.solr.core.ConfigSolrXmlOld;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrResourceLoader;
import org.kimios.kernel.controller.IPathController;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.index.SolrIndexManager;
import org.kimios.kernel.index.query.factory.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Spring factory to instantiate the @see SolrIndexManager
 *
 * @author Fabien Alin
 * @version 1.0
 * @see SolrIndexManager
 */
public class SearchServiceSolrFactory implements FactoryBean<SolrIndexManager>
{
    private static Logger log = LoggerFactory.getLogger(SearchServiceSolrFactory.class);

    private static SolrServer solrServer;

    private static CoreContainer coreContainer;

    private String solrUrl;

    private String solrHome;

    private String coreName;

    private IPathController pathController;

    private DocumentFactory solrDocumentFactory;

    private boolean serverMode = false;

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

    private static SolrServer initLocalServer(String solrHome, String coreName)
    {
        try {

            log.info("Kimios Solr Home " + solrHome);
            URL sorlHomeUrl = new URL("file://" + solrHome);
            File home = new File(sorlHomeUrl.getFile());
            checkSolrXmlFile(home, coreName);
            /*
                Check solr.xml existence. If not exist (create it)
                */
            File f = new File(home, "solr.xml");
            coreContainer = CoreContainer.createAndLoad(solrHome, f);;
            EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, coreName);
            return server;
        } catch (Exception ex) {
            log.error("Error initializing SOLR server", ex);
            return null;
        }
    }

    private static void checkSolrXmlFile(File solrHome, String coreName) throws IOException, IndexException
    {
        if (!solrHome.exists()) {
            log.debug("Solr home doesn't exist. Path " + solrHome.getAbsolutePath() + " not found");
            if (!solrHome.mkdirs()) {
                log.error("Unable to create solr Home");
                throw new IndexException("Unable to create solr home " + solrHome.getAbsolutePath());
            }
        }

        File solrConfFile = new File(solrHome, "solr.xml");
        if (!solrConfFile.exists()) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(SearchServiceSolrFactory.class.getClassLoader().getResourceAsStream(
                            "solr.xml")));

            StringBuffer content = new StringBuffer();
            String item = null;
            while ((item = reader.readLine()) != null) {
                content.append(item);
            }
            /*
               Replace with coreName
            */
            String solrSettings = content.toString().replaceAll("\\{core-name\\}", coreName);
            if (solrConfFile.createNewFile()) {
                FileWriter fw = new FileWriter(solrConfFile);
                fw.append(solrSettings);
                fw.flush();
                fw.close();
            } else {
                throw new IndexException("Unable to create solr conf file");
            }

            File fCore = new File(solrHome, coreName);
            fCore.mkdir();

            File f = new File(fCore, "conf");
            f.mkdir();

            FileOutputStream fos = new FileOutputStream(f.getAbsolutePath() + "/schema.xml");
            InputStream schemaStream = SearchServiceSolrFactory.class.getResourceAsStream("/schema.xml");
            /*
            InputStream cfgStream = SearchServiceSolrFactory.class.getResourceAsStream("/solrconfig.xml");
            InputStream mappingAccent = SearchServiceSolrFactory.class.getResourceAsStream("/mapping-ISOLatin1Accent.txt");*/

            int readBytes;
            byte[] bArray = new byte[2048];
            while ((readBytes = schemaStream.read(bArray)) != -1) {
                fos.write(bArray, 0, readBytes);
            }
            fos.flush();
            fos.close();

            /*fos = new FileOutputStream(f.getAbsolutePath() + "/solrconfig.xml");
            while ((readBytes = cfgStream.read(bArray)) != -1) {
                fos.write(bArray, 0, readBytes);
            }
            fos.flush();
            fos.close();  */

            List<String> items = new ArrayList<String>();
            items.add("protwords.txt");
            items.add("synonyms.txt");
            items.add("spellings.txt");
            items.add("stopwords.txt");
            items.add("misspelled_words.txt");
            items.add("spellingAdditions.txt");

            for (String fileToTouch : items) {
                File fConf = new File(f.getAbsolutePath() + "/" + fileToTouch);
                fConf.createNewFile();
            }
        }
    }

    private static SolrServer initSolrServer(String serverUrl)
    {
        try {
            HttpSolrServer server = new HttpSolrServer(serverUrl);
            return server;
        } catch (Exception ex) {
            log.error("Error initializing SOLR server", ex);
            return null;
        }
    }

    public static void shutdownSolr()
    {
        if (coreContainer != null) {
            coreContainer.shutdown();
        }
    }

    public SolrIndexManager getObject() throws Exception
    {

        if (solrServer == null) {
            if (serverMode) {
                solrServer = initSolrServer(solrUrl);
            } else {
                solrServer = initLocalServer(solrHome, coreName);
            }
        }

        SolrIndexManager manager = new SolrIndexManager(solrServer);
        manager.setPathController(pathController);
        manager.setSolrDocumentFactory( solrDocumentFactory );

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
