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
package org.kimios.webservices.impl;

import java.util.List;
import java.util.Vector;

import javax.jws.WebService;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.ws.pojo.DMEntitySecurity;
import org.kimios.kernel.ws.pojo.User;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.SecurityService;

@WebService(targetNamespace = "http://kimios.org", serviceName = "SecurityService", name = "SecurityService")
public class SecurityServiceImpl extends CoreService implements SecurityService
{
    public DMEntitySecurity[] getDMEntitySecurities(String sessionId, long dmEntityId) throws
            DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.security.DMEntitySecurity> v =
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

    public void updateDMEntitySecurities(String sessionId, long dmEntityId, String xmlStream, boolean isRecursive)
            throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            securityController.updateDMEntitySecurities(session, dmEntityId, xmlStream, isRecursive);
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

            List<org.kimios.kernel.user.User> v = securityController.getUsers(userSource);
            User[] r = new User[v.size()];
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
}

