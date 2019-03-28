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
package org.kimios.services.impl;

import org.kimios.kernel.jobs.model.TaskInfo;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.kernel.user.model.Group;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.webservices.SecurityService;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "SecurityService", name = "SecurityService")
public class SecurityServiceImpl extends CoreService implements SecurityService
{
    public DMEntitySecurity[] getDMEntitySecurities(String sessionId, long dmEntityId) throws
            DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.security.model.DMEntitySecurity> v =
                    securityController.getDMEntitySecurityies(session, dmEntityId);
            DMEntitySecurity[] r = new DMEntitySecurity[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.get(i).toPojo();
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public TaskInfo updateDMEntitySecurities(UpdateSecurityWithXmlCommand updateSecurityWithXmlCommand)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(updateSecurityWithXmlCommand.getSessionId());
            TaskInfo info =
                    securityController.updateDMEntitySecurities(session,
                            updateSecurityWithXmlCommand.getDmEntityId(),
                            updateSecurityWithXmlCommand.getXmlStream(),
                            updateSecurityWithXmlCommand.isRecursive(),
                            updateSecurityWithXmlCommand.isAppendMode());
            return info;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public TaskInfo updateDMEntitySecurities(UpdateSecurityCommand updateSecurityCommand)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(updateSecurityCommand.getSessionId());
            List<org.kimios.kernel.security.model.DMEntitySecurity> items =
                    new ArrayList<org.kimios.kernel.security.model.DMEntitySecurity>();
            for(DMEntitySecurity pojo: updateSecurityCommand.getSecurities())
                items.add(org.kimios.kernel.security.model.DMEntitySecurity.fromPojo(pojo));
            TaskInfo info =
                    securityController.updateDMEntitySecurities(session,
                            updateSecurityCommand.getDmEntityId(), items,
                            updateSecurityCommand.isRecursive(),
                            updateSecurityCommand.isAppendMode());
            return info;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public DMEntitySecurity[] getDefaultDMEntitySecurities(String sessionId, String objectType) throws
            DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.security.model.DMEntitySecurity> v =
                    securityController.getDefaultDMSecurityEntities(session, objectType);
            DMEntitySecurity[] r = new DMEntitySecurity[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.get(i).toPojo();
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void updateDefaultDMEntitySecurities(String sessionId, String xmlStream, String objectType)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            securityController.saveDefaultDMSecurityEntities(session, xmlStream, objectType, null);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean canRead(String sessionId, long dmEntityId) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            boolean val = securityController.canRead(session, dmEntityId);
            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean canWrite(String sessionId, long dmEntityId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            boolean val = securityController.canWrite(session, dmEntityId);

            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean hasFullAccess(String sessionId, long dmEntityId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            boolean val = securityController.hasFullAccess(session, dmEntityId);

            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.AuthenticationSource[] getAuthenticationSources() throws DMServiceException
    {

        try {

            List<AuthenticationSource> v = securityController.getAuthenticationSources();
            org.kimios.kernel.ws.pojo.AuthenticationSource[] r =
                    new org.kimios.kernel.ws.pojo.AuthenticationSource[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.get(i).toPojo();
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public String startSession(String userName, String userSource, String password) throws DMServiceException
    {

        try {

            Session session = securityController.startSession(userName, userSource, password);

            if (session != null) {
                return session.getUid();
            } else {
                throw new Exception("Error 01 : Invalid session");
            }
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void endSession(String sessionId) throws DMServiceException
    {

        try {

            securityController.endSession(sessionId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean isSessionAlive(String sessionId) throws DMServiceException
    {

        try {

            boolean val = securityController.isSessionAlive(sessionId);

            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public User getUser(String sessionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            User user = securityController.getUser(session).toPojo();

            return user;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public User[] getUsers(String sessionId, String userSource) throws DMServiceException
    {

        try {

            getHelper().getSession(sessionId);

            List<org.kimios.kernel.user.model.User> v = securityController.getUsers(userSource);
            User[] r = new User[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.get(i).toPojo();
            }

            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public SecurityEntity[] searchSecurityEntities(String sessionId, String searchText, String userSource, int securityEntityType) throws DMServiceException {
        try {
            getHelper().getSession(sessionId);

            List<org.kimios.kernel.security.model.SecurityEntity> v = this.administrationController.searchSecurityEntities(searchText, userSource, securityEntityType);
            org.kimios.kernel.ws.pojo.SecurityEntity[] r = new org.kimios.kernel.ws.pojo.SecurityEntity[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.get(i).toPojo();
            }

            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.Group getGroup(String sessionId, String groupId, String userSource)
            throws DMServiceException
    {

        try {

            getHelper().getSession(sessionId);

            org.kimios.kernel.ws.pojo.Group group = securityController.getGroup(groupId, userSource).toPojo();

            return group;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.Group[] getGroups(String sessionId, String userSource) throws DMServiceException
    {

        try {

            getHelper().getSession(sessionId);

            List<Group> v = securityController.getGroups(userSource);

            org.kimios.kernel.ws.pojo.Group[] r = new org.kimios.kernel.ws.pojo.Group[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.get(i).toPojo();
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean canCreateWorkspace(String sessionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            boolean val = securityController.canCreateWorkspace(session);

            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean hasStudioAccess(String sessionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            boolean val = securityController.hasStudioAccess(session);

            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean hasReportingAccess(String sessionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            boolean val = securityController.hasReportingAccess(session);

            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public boolean isAdmin(String sessionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            boolean val = securityController.isAdmin(session);

            return val;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public String startSessionWithToken(String externalToken) throws DMServiceException {
        try {

            Session session = securityController.startSession(externalToken);
            return session.getUid();
        }   catch (Exception e){
            throw getHelper().convertException(e);
        }
    }
}

