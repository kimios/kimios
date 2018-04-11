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
package org.kimios.kernel.deployment;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.security.model.Role;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.kernel.user.FactoryInstantiator;
import org.kimios.kernel.user.model.User;
import org.kimios.kernel.user.impl.HAuthenticationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;


public class DataInitializerCtrl implements IDataInitializerCtrl {

    public static final String KIMIOS_DEFAULT_DOMAIN = "KIMIOS_DEFAULT_DOMAIN";
    public static final String KIMIOS_ADMIN_PASSWORD = "KIMIOS_ADMIN_PASSWORD";
    public static final String KIMIOS_ADMIN_USERID = "KIMIOS_ADMIN_USERID";
    public static final String KIMIOS_ADMIN_FIRSTNAME = "KIMIOS_ADMIN_FIRSTNAME";
    public static final String KIMIOS_ADMIN_LASTNAME = "KIMIOS_ADMIN_LASTNAME";
    public static final String KIMIOS_ADMIN_EMAIL = "KIMIOS_ADMIN_EMAIL";

    private static Logger log = LoggerFactory.getLogger(DataInitializerCtrl.class);

    private FactoryInstantiator userFactoryInstantiator;

    private org.kimios.kernel.dms.FactoryInstantiator dmsFactoryInstantiator;

    private org.kimios.kernel.security.FactoryInstantiator securityFactoryInstantior;

    public org.kimios.kernel.security.FactoryInstantiator getSecurityFactoryInstantior()
    {
        return securityFactoryInstantior;
    }

    public void setSecurityFactoryInstantior(org.kimios.kernel.security.FactoryInstantiator securityFactoryInstantior)
    {
        this.securityFactoryInstantior = securityFactoryInstantior;
    }

    public FactoryInstantiator getUserFactoryInstantiator()
    {
        return userFactoryInstantiator;
    }

    public void setUserFactoryInstantiator(FactoryInstantiator userFactoryInstantiator)
    {
        this.userFactoryInstantiator = userFactoryInstantiator;
    }

    public org.kimios.kernel.dms.FactoryInstantiator getDmsFactoryInstantiator() {
        return dmsFactoryInstantiator;
    }

    public void setDmsFactoryInstantiator(org.kimios.kernel.dms.FactoryInstantiator dmsFactoryInstantiator) {
        this.dmsFactoryInstantiator = dmsFactoryInstantiator;
    }

    @Override
    @Transactional
    public void checkSettings() throws Exception
    {

        List<AuthenticationSource> authenticationSourceList =
                userFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSources();

        if (authenticationSourceList.size() == 0) {
            log.info("Inserting Authentication datas");
            createMinimalSettings();
        } else {

            for(AuthenticationSource s: authenticationSourceList){

                if(s.getEnableAuthByEmail() == null){
                    s.setEnableAuthByEmail(false);
                    userFactoryInstantiator.getAuthenticationSourceFactory()
                            .saveAuthenticationSource(s);
                }
                if(s.getEnableSSOCheck() == null){
                    userFactoryInstantiator.getAuthenticationSourceFactory()
                            .saveAuthenticationSource(s);
                }
            }
        }
        log.info("Database Initialized.");
    }

    private void createMinimalSettings() throws Exception
    {


        /** Generate Password if necessary */
        String defaultDomain =
                StringUtils.isEmpty(System.getenv(KIMIOS_DEFAULT_DOMAIN)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.default.domain")) ? "kimios" : System.getProperty("kimios.default.domain")) :
                        System.getenv(KIMIOS_DEFAULT_DOMAIN);
        String adminPassword =
                StringUtils.isEmpty(System.getenv(KIMIOS_ADMIN_PASSWORD)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.password")) ?
                                "kimios" + Calendar.getInstance().get(Calendar.YEAR) : System.getProperty("kimios.admin.password")) :
                        System.getenv(KIMIOS_ADMIN_PASSWORD);
        String adminLogin =
                StringUtils.isEmpty(System.getenv(KIMIOS_ADMIN_USERID)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.userid")) ? "admin" : System.getProperty("kimios.admin.userid")) :
                        System.getenv(KIMIOS_ADMIN_USERID);

        String adminFirstName =
                StringUtils.isEmpty(System.getenv(KIMIOS_ADMIN_FIRSTNAME)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.firstname")) ? "Kimios" : System.getProperty("kimios.admin.firstname")) :
                        System.getenv(KIMIOS_ADMIN_FIRSTNAME);

        String adminLastName =
                StringUtils.isEmpty(System.getenv(KIMIOS_ADMIN_LASTNAME)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.lastname")) ? "Administrator" : System.getProperty("kimios.admin.lastname")) :
                        System.getenv(KIMIOS_ADMIN_LASTNAME);

        String adminEmail=
                StringUtils.isEmpty(System.getenv(KIMIOS_ADMIN_EMAIL)) ?
                        (StringUtils.isEmpty(System.getProperty("kimios.admin.email")) ? "default@kimios-instance.org" : System.getProperty("kimios.admin.email")) :
                        System.getenv(KIMIOS_ADMIN_EMAIL);


        AuthenticationSource authenticationSource = new HAuthenticationSource();
        authenticationSource.setName(defaultDomain);
        authenticationSource.setEnableSSOCheck(false);
        authenticationSource.setEnableAuthByEmail(false);
        userFactoryInstantiator.getAuthenticationSourceFactory()
                .saveAuthenticationSource(authenticationSource);


        AuthenticationSource backupSource = authenticationSource;

        log.info("Creating Authentication source " + authenticationSource.getName());
        authenticationSource = userFactoryInstantiator.getAuthenticationSourceFactory()
                .getAuthenticationSource(authenticationSource.getName());

        log.info("Creating Authentication source " + authenticationSource);
        if(authenticationSource == null){
            //throw new Exception("Kimios Init Process Error. Unable to create minmal Data");
            authenticationSource = backupSource;
        }
        createAdminUser(authenticationSource, adminLogin, adminPassword, adminFirstName, adminLastName, adminEmail);
    }

    private void createAdminUser(AuthenticationSource authenticationSource, String login, String password, String firstname, String lastname, String email) throws Exception
    {

        User adminUser = new User();
        adminUser.setAuthenticationSourceName(authenticationSource.getName());
        adminUser.setMail(email);
        adminUser.setName(firstname.trim() + " " + lastname.trim());
        adminUser.setFirstName(firstname);
        adminUser.setLastName(lastname);
        adminUser.setUid(login);
        adminUser.setPassword(password);

        authenticationSource.getUserFactory().saveUser(adminUser, password);


        Role adminRole = new Role();
        adminRole.setRole(Role.ADMIN);
        adminRole.setUserName(adminUser.getID());
        adminRole.setUserSource(authenticationSource.getName());

        Role studioRole = new Role();
        studioRole.setRole(Role.STUDIO);
        studioRole.setUserName(adminUser.getID());
        studioRole.setUserSource(authenticationSource.getName());

        Role workspaceCreatorRole = new Role();
        workspaceCreatorRole.setRole(Role.WORKSPACE);
        workspaceCreatorRole.setUserName(adminUser.getID());
        workspaceCreatorRole.setUserSource(authenticationSource.getName());

        securityFactoryInstantior
                .getRoleFactory()
                .saveRole(adminRole);

        securityFactoryInstantior
                .getRoleFactory()
                .saveRole(studioRole);

        securityFactoryInstantior
                .getRoleFactory()
                .saveRole(workspaceCreatorRole);
    }
}
