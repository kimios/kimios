/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2012  DevLib'
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
package org.kimios.kernel.deployment;

import java.util.List;

import org.kimios.kernel.dms.Meta;
import org.kimios.kernel.dms.MetaFeed;
import org.kimios.kernel.security.Role;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.FactoryInstantiator;
import org.kimios.kernel.user.User;
import org.kimios.kernel.user.impl.HAuthenticationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/***
 *
 *
 *
 */

public class DataInitializerCtrl
{
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
            /*
            Should fix meta feed
             */
        }

        /*
            Fix new data
         */


        log.info("Database Initialized.");
    }

    private void createMinimalSettings() throws Exception
    {

        AuthenticationSource authenticationSource = new HAuthenticationSource();
        authenticationSource.setName("kimios");
        authenticationSource.setEnableSSOCheck(false);
        authenticationSource.setEnableAuthByEmail(false);
        userFactoryInstantiator.getAuthenticationSourceFactory()
                .saveAuthenticationSource(authenticationSource);

        authenticationSource = userFactoryInstantiator.getAuthenticationSourceFactory()
                .getAuthenticationSource(authenticationSource.getName());

        createAdminUser(authenticationSource);
    }

    private void createAdminUser(AuthenticationSource authenticationSource) throws Exception
    {

        User adminUser = new User();
        adminUser.setAuthenticationSourceName(authenticationSource.getName());
        adminUser.setMail("kimios@kimios.org");
        adminUser.setName("Kimios Administrator");
        adminUser.setFirstName("Kimios");
        adminUser.setLastName("Administrator");
        adminUser.setUid("admin");
        adminUser.setPassword("kimios");

        authenticationSource.getUserFactory().saveUser(adminUser, "kimios");


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
