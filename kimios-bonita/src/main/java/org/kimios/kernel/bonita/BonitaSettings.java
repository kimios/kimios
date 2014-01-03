package org.kimios.kernel.bonita;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class BonitaSettings {

    private String bonitaUserName;
    private String bonitaUserPassword;
    private String bonitaHome;
    private String bonitaApplicationName;
    private String bonitaServerUrl;
    private String bonitaPublicServerUrl;
    private String bonitaKimiosRoleName;
    private String bonitaProfileUsers;
    private Set<String> validDomainsToSynchronize;

    public void init() throws IOException {
        File homeFolder = null;
        if (System.getProperty("bonita.home") == null) {
            // create a bonita home that is for application 'myClientExample' and on localhost:8080
            homeFolder = new File(bonitaHome);
            homeFolder.mkdirs();
            File file = new File(homeFolder, "client");
            file.mkdir();
            file = new File(file, "conf");
            file.mkdir();
            file = new File(file, "bonita-client.properties");
            file.createNewFile();
            final Properties properties = new Properties();
            properties.put("application.name", bonitaApplicationName);
            properties.put("org.bonitasoft.engine.api-type", "HTTP");
            properties.put("server.url", bonitaServerUrl);
            properties.put("org.bonitasoft.engine.api-type.parameters", "server.url,application.name");

            final FileWriter writer = new FileWriter(file);
            try {
                properties.store(writer, "Server configuration");
            } finally {
                writer.close();
            }
            System.out.println("Using server configuration " + properties);
            System.setProperty("bonita.home", homeFolder.getAbsolutePath());
        }
    }

    public String getBonitaUserName() {
        return bonitaUserName;
    }

    public void setBonitaUserName(String bonitaUserName) {
        this.bonitaUserName = bonitaUserName;
    }

    public String getBonitaUserPassword() {
        return bonitaUserPassword;
    }

    public void setBonitaUserPassword(String bonitaUserPassword) {
        this.bonitaUserPassword = bonitaUserPassword;
    }

    public String getBonitaHome() {
        return bonitaHome;
    }

    public void setBonitaHome(String bonitaHome) {
        this.bonitaHome = bonitaHome;
    }

    public String getBonitaApplicationName() {
        return bonitaApplicationName;
    }

    public void setBonitaApplicationName(String bonitaApplicationName) {
        this.bonitaApplicationName = bonitaApplicationName;
    }

    public String getBonitaServerUrl() {
        return bonitaServerUrl;
    }

    public void setBonitaServerUrl(String bonitaServerUrl) {
        this.bonitaServerUrl = bonitaServerUrl;
    }

    public String getBonitaKimiosRoleName() {
        return bonitaKimiosRoleName;
    }

    public void setBonitaKimiosRoleName(String bonitaKimiosRoleName) {
        this.bonitaKimiosRoleName = bonitaKimiosRoleName;
    }

    public String getBonitaProfileUsers() {
        return bonitaProfileUsers;
    }

    public void setBonitaProfileUsers(String bonitaProfileUsers) {
        this.bonitaProfileUsers = bonitaProfileUsers;
    }

    public Set<String> getValidDomainsToSynchronize() {
        return validDomainsToSynchronize;
    }

    public void setValidDomainsToSynchronize(Set<String> validDomainsToSynchronize) {
        this.validDomainsToSynchronize = validDomainsToSynchronize;
    }

    public String getBonitaPublicServerUrl() {
        return bonitaPublicServerUrl;
    }

    public void setBonitaPublicServerUrl(String bonitaPublicServerUrl) {
        this.bonitaPublicServerUrl = bonitaPublicServerUrl;
    }
}
