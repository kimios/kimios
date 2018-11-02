/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
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
package org.kimios.deployer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InstallerCore
{
    // Path to the resources directory
    private String installerPathUrl;

    private String tmpDirectory;

    private Properties lnType;

    private Properties allConf;

    private Properties serverProperties;

    private Properties clientProperties;

    public Properties getAllConf()
    {
        return allConf;
    }

    public void setAllConf(Properties allConf)
    {
        this.allConf = allConf;
    }

    private static JarFile jf;

    private static String basePath;

    public static InputStream getInputStream(String relativePath)
            throws Exception
    {
        if (jf != null) {
            JarEntry je = jf.getJarEntry(relativePath);
            return jf.getInputStream(je);
        } else {

            if (new File(new URI(basePath + relativePath)).exists()) {
                return new FileInputStream(new File(
                        new URI(basePath + relativePath)));
            } else {
                return InstallerCore.class.getResourceAsStream(relativePath);
            }
        }
    }

    public InstallerCore(String installerPath, String _tmpDirectory,
            Boolean fromJar, Properties _serverProperties, Properties _clientProperties) throws Exception
    {
        if (fromJar != null && fromJar) {
            System.out.println(" >>>> " + installerPath);
            JarURLConnection conn = (JarURLConnection) new URL(installerPath
                    + "/").openConnection();
            JarFile jarfile = conn.getJarFile();
            jf = jarfile;
        } else {
            basePath = installerPath;
        }
        this.installerProgress = InstallerProgress.getInstance();
        installerPathUrl = installerPath;
        File f = new File(_tmpDirectory);
        this.tmpDirectory = f.toURI().toString();
        allConf = new Properties();
        allConf.load(getInputStream("/driver-list.properties"));
        lnType = new Properties();
        lnType.load(getInputStream("/index-languages.properties"));
    }

    public void setServerProperties(Properties serverProperties)
    {
        this.serverProperties = serverProperties;
    }

    public void setClientProperties(Properties clientProperties)
    {
        this.clientProperties = clientProperties;
    }

    /* process field */
    private InstallerProgress installerProgress;

    /* given conf field */
    private String serverUrl;

    private String catalinaHome;

    private String catalinaBase;

    private String catalinaWebappsDirectory;

    private boolean isJboss;

    private String jbossVersion;

    private String serverAppName = "kimios-server";

    private String clientAppName = "ROOT";

    /* kimios server name (logical) */
    private String serverName = "kimios DMS Server";

    public String getServerAppName()
    {
        return serverAppName;
    }

    public void setServerAppName(String serverAppName)
    {
        this.serverAppName = serverAppName;
    }

    public String getClientAppName()
    {
        return clientAppName;
    }

    public void setClientAppName(String clientAppName)
    {
        this.clientAppName = clientAppName;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public String getCatalinaHome()
    {
        return catalinaHome;
    }

    public void setCatalinaHome(String catalinaHome)
    {
        this.catalinaHome = catalinaHome;
    }

    public boolean isJboss()
    {
        return isJboss;
    }

    public void setJboss(boolean isJboss)
    {
        this.isJboss = isJboss;
    }

    public String getJbossVersion()
    {
        return jbossVersion;
    }

    public void setJbossVersion(String jbossVersion)
    {
        this.jbossVersion = jbossVersion;
    }

    public String getCatalinaBase()
    {
        return catalinaBase;
    }

    public void setCatalinaBase(String catalinaBase)
    {
        this.catalinaBase = catalinaBase;
    }

    public String getCatalinaWebappsDirectory()
    {
        return catalinaWebappsDirectory;
    }

    public void setCatalinaWebappsDirectory(String catalinaWebappsDirectory)
    {
        this.catalinaWebappsDirectory = catalinaWebappsDirectory;
    }

    /* database settings */
    private String host;

    private String databaseUser;

    private String databasePass;

    private String name;

    private String type;

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getDatabaseUser()
    {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser)
    {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePass()
    {
        return databasePass;
    }

    public void setDatabasePass(String databasePass)
    {
        this.databasePass = databasePass;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    /* data paths settings */
    private String repository;

    private String index;

    private String tmpfiles;

    public String getRepository()
    {
        return repository;
    }

    public void setRepository(String repository)
    {
        this.repository = repository;
    }

    public String getIndex()
    {
        return index;
    }

    public void setIndex(String index)
    {
        this.index = index;
    }

    public String getTmpfiles()
    {
        return tmpfiles;
    }

    public void setTmpfiles(String tmpfiles)
    {
        this.tmpfiles = tmpfiles;
    }

    /* index language */
    private String languageIndex;

    public String getLanguageIndex()
    {
        return languageIndex;
    }

    public void setLanguageIndex(String languageIndex)
    {
        this.languageIndex = languageIndex;
    }

    /* workflow mailer settings */
    private String wfAddress;

    private String wfHost;

    private String wfUser;

    private String wfPass;

    private String wfTlsAuth;

    private String wfPort;

    public String getWfAddress()
    {
        return wfAddress;
    }

    public void setWfAddress(String wfAddress)
    {
        this.wfAddress = wfAddress;
    }

    public String getWfHost()
    {
        return wfHost;
    }

    public void setWfHost(String wfHost)
    {
        this.wfHost = wfHost;
    }

    public String getWfUser()
    {
        return wfUser;
    }

    public void setWfUser(String wfUser)
    {
        this.wfUser = wfUser;
    }

    public String getWfPass()
    {
        return wfPass;
    }

    public void setWfPass(String wfPass)
    {
        this.wfPass = wfPass;
    }

    public String getWfTlsAuth()
    {
        return wfTlsAuth;
    }

    public void setWfTlsAuth(String wfTlsAuth)
    {
        this.wfTlsAuth = wfTlsAuth;
    }

    public String getWfPort()
    {
        return wfPort;
    }

    public void setWfPort(String wfPort) throws Exception
    {
        isValidPort(Integer.parseInt(wfPort));
        this.wfPort = wfPort;
    }

    private void isValidPort(int port) throws Exception
    {
        if (port < 1 || port > 65535) {
            throw new Exception("Invalid port range: " + port);
        }
    }

    /* calculated field */
    private String compressedWarPath;

    private String dbJarFile;

    private String analyserClassName;

    private String warPath;

    private String xmlPath;

    private String databaseDriverClass;

    public String checkDatabase() throws Exception
    {
        // load driver
        String driverDb = allConf.getProperty(type + ".driver");
        String jdbcUrl = allConf.getProperty(type + ".jdbcurl");
        dbJarFile = allConf.getProperty(type + ".jar");
        // String host, String login, String dbName, String dbPassword,
        // String _driver, String jdbcUrl
        DBManager.init(host, databaseUser, name, databasePass, driverDb,
                jdbcUrl);
        DBManager dbm = DBManager.getInstance();
        if (!dbm.isConnected()) {
            throw new Exception();
        }
        dbm.disconnect();
        return "Database connection successfully established";
    }

    public String checkRepository() throws Exception
    {
        if (new File(repository).exists() && !new File(repository).canWrite()) {
            throw new Exception("You don't have permission to write: "
                    + repository);
        }
        if (new File(index).exists() && !new File(index).canWrite()) {
            throw new Exception("You don't have permission to write: " + index);
        }
        if (new File(tmpfiles).exists() && !new File(tmpfiles).canWrite()) {
            throw new Exception("You don't have permission to write: "
                    + tmpfiles);
        }
        return "Data paths successfully validated";
    }

    public String defineIndexLanguage() throws Exception
    {
        analyserClassName = lnType.getProperty(languageIndex + ".analyser");
        return "Index language successfully set to " + analyserClassName;
    }

    public void installDMS() throws Exception
    {
        try {

            type = serverProperties.get("jdbc.databasetype").toString();
            databaseDriverClass = allConf.get(type + ".driver").toString();
            if (DBManager.getInstance().isConnected()) {
                DBManager.getInstance().disconnect();
            }

            createDatabase();
            configure();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            this.installerProgress.setMaxProgression(-2);
            this.installerProgress.setStatus("initializing", e);
            throw new Exception(e);
        }
    }

    private void createDatabase() throws Exception
    {
        this.installerProgress.init();
        this.installerProgress.setStatus("Initializing database...", null);
        DatabaseCreator dbc = new DatabaseCreator();
        String sqlPath = allConf.getProperty(type + ".script");
        dbc.createDatabase(host, name, databaseUser, databasePass, type,
                allConf.getProperty(type + ".jdbcurl"),
                allConf.getProperty(type + ".driver"),
                allConf.getProperty(type + ".createdb"), sqlPath);
    }

    private void configure() throws Exception
    {
        try {
            this.installerProgress.init();
            this.installerProgress.setStatus("Setting configuration...", null);
            String databaseTypePrefix = type;
            String appServerConfPath = "appservers/"
                    + (isJboss ? "jboss/" + jbossVersion : "tomcat");
            String jndiDSName = "jdbc/dms";
            String appServerJndiPath = !isJboss ? "java:/comp/env/" : "java:/";
            // Server configuration
            String serverPropertiesFilePath = tmpDirectory
                    + warPath + serverAppName + "/WEB-INF/conf/kimios.properties";
            serverProperties.load(getInputStream("" + appServerConfPath
                    + "/kimios.properties"));
            serverProperties.put("jdbc.databasetype", databaseTypePrefix);
            serverProperties.put("jdbc.dialect",
                    allConf.getProperty(databaseTypePrefix + ".dialect"));
            serverProperties.put("jdbc.schema",
                    allConf.getProperty(databaseTypePrefix + ".defaultschema"));
            serverProperties.put("jdbc.jndids", appServerJndiPath + jndiDSName);
            FileOutputStream fos = new FileOutputStream(new File(new URI(serverPropertiesFilePath)));
            serverProperties.store(fos, "");
            fos.close();
            // Context.xml tomcat
            String clientPropertiesFilePath = tmpDirectory
                    + warPath + clientAppName + "/WEB-INF/kimios.properties";
            clientProperties.put("TemporaryFilesPath", tmpfiles);
            String clientServerUrl = serverUrl + "/" + serverAppName;
            clientProperties.put("ServerUrl", clientServerUrl);
            clientProperties.store(new FileOutputStream(clientPropertiesFilePath), "");

            // Client configuration
            new File(warPath + serverAppName).mkdirs();
            new File(tmpfiles).mkdirs();
            new File(repository).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
            this.installerProgress.setMaxProgression(-2);
            this.installerProgress.setStatus("setting configuration", e);
            throw new Exception(e);
        }
    }

    private void finish()
    {
        if (isJboss) {
            installerProgress.setMaxProgression(-3);
            installerProgress
                    .setStatus(
                            "kimios DMS is installed.<br/>",
                            null);
        } else {
            installerProgress.setMaxProgression(0);
            installerProgress.setStatus("Waiting for kimios to Start...", null);
        }
    }
}
