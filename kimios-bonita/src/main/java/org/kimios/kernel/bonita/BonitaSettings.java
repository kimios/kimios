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

package org.kimios.kernel.bonita;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.util.APITypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BonitaSettings {

    private static Logger logger = LoggerFactory.getLogger(BonitaSettings.class);

    private String bonitaUserName;
    private String bonitaUserPassword;
    private String bonitaHome;
    private String bonitaApplicationName;
    private String bonitaServerUrl;
    private String bonitaPublicServerUrl;
    private String bonitaKimiosRoleName;
    private String bonitaProfileUsers;
    private Set<String> validDomainsToSynchronize;

    private String commasSeparatedListDomains;

    public String getCommasSeparatedListDomains() {
        return commasSeparatedListDomains;
    }

    public void setCommasSeparatedListDomains(String commasSeparatedListDomains) {
        this.commasSeparatedListDomains = commasSeparatedListDomains;
    }

    private boolean bonitaEnabled = false;

    public void init() throws IOException {
        File homeFolder = null;
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("server.url", bonitaServerUrl);
        settings.put("application.name", bonitaApplicationName);
        APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, settings);
        //set domain list
        if(commasSeparatedListDomains != null && commasSeparatedListDomains.length() > 0){
            String[] domainsList = commasSeparatedListDomains.split(",");
            validDomainsToSynchronize = new HashSet<String>();
            for(String d: domainsList) {
                validDomainsToSynchronize.add(d);
                logger.info("{} domain added for sync", d);
            }
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

    public boolean isBonitaEnabled() {
        return bonitaEnabled;
    }

    public void setBonitaEnabled(boolean bonitaEnabled) {
        this.bonitaEnabled = bonitaEnabled;
    }
}
