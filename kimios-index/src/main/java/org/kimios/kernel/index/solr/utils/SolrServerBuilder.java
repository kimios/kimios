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

package org.kimios.kernel.index.solr.utils;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.CoreContainer;
import org.kimios.exceptions.IndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by farf on 13/07/15.
 */
public class SolrServerBuilder {


    private static Logger log = LoggerFactory.getLogger(SolrServerBuilder.class);

    public static SolrServer initHttpServer(String serverUrl)
    {
        try {
            HttpSolrServer server = new HttpSolrServer(serverUrl);
            return server;
        } catch (Exception ex) {
            log.error("Error initializing SOLR server", ex);
            return null;
        }
    }

    public static URL initLocalServer(String solrHome, String coreName, String schemaName)
    {
        try {

            log.info("Kimios Solr Home " + solrHome);
            String os = System.getProperty("os.name").toLowerCase();
            URL solrHomeUrl = null;
            if(os.contains("win") && !os.contains("darwin")){
                //windos url
                solrHomeUrl = new URL("file:///" + solrHome);
            } else {
                if(solrHome.startsWith("/")){
                    solrHomeUrl = new URL("file://" + solrHome);
                } else {
                    solrHomeUrl = new URL("file://" + System.getProperty("user.dir").toString() + "/" + solrHome);
                }

            }

            File home = new File(solrHomeUrl.getFile());
            File f = new File(home, "solr.xml");
            checkSolrXmlFile(home, coreName, schemaName);
            /*
                Check solr.xml existence. If not exist (create it)

            */

            return solrHomeUrl;

        } catch (Exception ex) {
            log.error("Error initializing SOLR server", ex);
            return null;
        }
    }


    public static SolrServer[] buidServers(URL solrHomeUrl, String solrHome, String coreName){

        File home = new File(solrHomeUrl.getFile());
        File solrXml = new File(home, "solr.xml");
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(SolrServerBuilder.class.getClassLoader());
        CoreContainer coreContainer = CoreContainer.createAndLoad(solrHome, solrXml);
        EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, coreName);
        EmbeddedSolrServer contentServer = new EmbeddedSolrServer(coreContainer, coreName + "-body");

        Thread.currentThread().setContextClassLoader(cl);

        return new SolrServer[]{server, contentServer};

    }

    private static void checkSolrXmlFile(File solrHome, String coreName, String schemaName) throws IOException, IndexException
    {
        /*
            Check Solr Version. Below is for >= 5

        if (!solrHome.exists()) {
            log.debug("Solr home doesn't exist. Path " + solrHome.getAbsolutePath() + " not found");
            if (!solrHome.mkdirs()) {
                log.error("Unable to create solr Home");
                throw new IndexException("Unable to create solr home " + solrHome.getAbsolutePath());
            }
        }



        File fCore = new File(solrHome, coreName);
        if(!fCore.exists()){
            fCore.mkdir();

            //creare core.properties

            File fCoreDef = new File(fCore, "core.properties");
            fCoreDef.createNewFile();

            Properties properties = new Properties();
            properties.setProperty("name", coreName);
            properties.store(new FileOutputStream(fCoreDef),"");

            File f = new File(fCore, "conf");
            f.mkdir();




            if (!solrHome.exists()) {
            log.debug("Solr home doesn't exist. Path " + solrHome.getAbsolutePath() + " not found");
            if (!solrHome.mkdirs()) {
                log.error("Unable to create solr Home");
                throw new IndexException("Unable to create solr home " + solrHome.getAbsolutePath());
            }
        }   */

        log.info("checking solr home {}", solrHome.getPath());
        if(!solrHome.exists()){
            log.info("initialisze empty directory for solr instance");
            solrHome.mkdirs();
        }
        File solrConfFile = new File(solrHome, "solr.xml");
        if (!solrConfFile.exists()) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(SolrServerBuilder.class.getClassLoader().getResourceAsStream(
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
        }


        File fCore = new File(solrHome, coreName);
        fCore.mkdir();

        File f = new File(fCore, "conf");
        if(!f.exists()){
            f.mkdir();
            createEmptySettingsFile(f, schemaName);
        }

    }


    private static void createEmptySettingsFile(File indexDirectory, String customSchema) throws IOException {
        InputStream schemaStream = SolrServerBuilder.class.getResourceAsStream(customSchema);
        InputStream cfgStream = SolrServerBuilder.class.getResourceAsStream("/solrconfig.xml");
        InputStream mappingAccent = SolrServerBuilder.class.getResourceAsStream("/mapping-ISOLatin1Accent.txt");


        IOUtils.copy(schemaStream, new FileOutputStream(new File(indexDirectory, "schema.xml")));
        IOUtils.copy(cfgStream, new FileOutputStream(new File(indexDirectory, "solrconfig.xml")));
        IOUtils.copy(mappingAccent, new FileOutputStream(new File(indexDirectory, "mapping-ISOLatin1Accent.txt")));

        List<String> items = new ArrayList<String>();
        items.add("protwords.txt");
        items.add("synonyms.txt");
        items.add("spellings.txt");
        items.add("stopwords.txt");
        items.add("misspelled_words.txt");
        items.add("spellingAdditions.txt");

        for (String fileToTouch : items) {
            File fConf = new File(indexDirectory.getAbsolutePath() + "/" + fileToTouch);
            fConf.createNewFile();
        }
    }


}
