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

import org.kimios.kernel.ws.pojo.AuthenticationSource;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Role;
import org.kimios.kernel.ws.pojo.Session;
import org.kimios.kernel.ws.pojo.User;
import org.kimios.utils.logging.LoggerManager;
import org.kimios.webservices.AdministrationService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.services.utils.KimiosBusServiceManager;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@WebService(targetNamespace = "http://kimios.org", serviceName = "AdministrationService", name = "AdministrationService")
public class AdministrationServiceImpl extends CoreService implements AdministrationService
{

    public AdministrationServiceImpl(){

    }

    public KimiosBusServiceManager getKimiosBusServiceManager() {
        return kimiosBusServiceManager;
    }

    public void setKimiosBusServiceManager(KimiosBusServiceManager kimiosBusServiceManager) {
        this.kimiosBusServiceManager = kimiosBusServiceManager;
    }

    private KimiosBusServiceManager kimiosBusServiceManager;

    public AdministrationServiceImpl(KimiosBusServiceManager serviceManager){
        this.kimiosBusServiceManager = serviceManager;
    }

    public Role[] getRoles(String sessionUid, int role) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            Vector<org.kimios.kernel.security.model.Role> v = administrationController.getRoles(session, role);
            Role[] r = new Role[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.elementAt(i).toPojo();
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public Role[] getUserRoles(String sessionUid, String userName, String userSource) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            Vector<org.kimios.kernel.security.model.Role> v =
                    administrationController.getRoles(session, userName, userSource);
            Role[] r = new Role[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.elementAt(i).toPojo();
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void createRole(String sessionUid, int role, String userName, String userSource) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.createRole(session, role, userName, userSource);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void deleteRole(String sessionUid, int role, String userName, String userSource) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.deleteRole(session, role, userName, userSource);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Return the Authentication Source specified by name
     */
    public AuthenticationSource getAuthenticationSource(String sessionUid, String name) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            AuthenticationSource a = administrationController.getAuthenticationSource(session, name).toPojo();
            return a;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Return connection parameters XML stream for the authentication sources specified by name
     */
    public String getAuthenticationSourceParamsXml(String sessionUid, String name, String className)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            return administrationController.getAuthenticationSourceParamsXml(session, name, className);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Return connection parameters XML stream for the authentication sources specified by name
     */
    public Map<String, String> getAuthenticationSourceParams(String sessionUid, String name, String className)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            return administrationController.getAuthenticationSourceParams(session, name, className);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Create new authentication source
     */
    public void createAuthenticationSource(String sessionUid, String name, String className, boolean enableSsoCheck,
                                           boolean enableMailCheck, String xmlParameters)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.createAuthenticationSource(session, name, className, enableSsoCheck, enableMailCheck, xmlParameters);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void createAuthenticationSource(String sessionUid, String name, String className, boolean enableSsoCheck,
                                           boolean enableMailCheck, Map<String, String> items)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.createAuthenticationSource(session, name, className, enableSsoCheck, enableMailCheck, items);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Update authentication source
     */
    public void updateAuthenticationSource(String sessionUid, String authenticationSourceName,
            String className, boolean enableSsoCheck, boolean enableMailCheck,String xmlParameters) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController
                    .updateAuthenticationSource(session, authenticationSourceName, className, enableSsoCheck, enableMailCheck, xmlParameters);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Update authentication source
     */
    public void updateAuthenticationSource(String sessionUid, String authenticationSourceName,
                                                  String className, boolean enableSsoCheck, boolean enableMailCheck,Map<String, String> params) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController
                    .updateAuthenticationSource(session, authenticationSourceName, className, enableSsoCheck, enableMailCheck, params);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Delete authentication source
     */
    public void deleteAuthenticationSource(String sessionUid, String name) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.deleteAuthenticationSource(session, name);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * Get a class names list of all implemented authentication sources
     */
    public String getAvailableAuthenticationSourceXml(String sessionUid) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            String xmlClassName = administrationController.getAvailableAuthenticationSourceXml(session);
            return xmlClassName;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public List<String> getAvailableAuthenticationSources(String sessionUid) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            return administrationController.getAvailableAuthenticationSource(session);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }


    public String getAvailableAuthenticationSourceParamsXml(String sessionUid, String className) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            String xmlClassName = administrationController.getAvailableAuthenticationSourceParamsXml(session, className);
            return xmlClassName;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public List<String> getAvailableAuthenticationSourceParams(String sessionUid, String className) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            return administrationController.getAvailableAuthenticationSourceParams(session, className);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void createUser(String sessionUid, String uid, String firstName, String lastName, String phoneNumber,
                           String mail, String password, String authenticationSourceName, boolean enabled)
            throws DMServiceException
    {

        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.createUser(session, uid, firstName, lastName, phoneNumber, mail, password, authenticationSourceName,
                    enabled);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void updateUser(String sessionUid, String uid, String firstName, String lastName, String phoneNumber,
                           String mail, String password, String authenticationSourceName, boolean enabled)
            throws DMServiceException
    {

        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.updateUser(session, uid, firstName, lastName, phoneNumber,
                    mail, password, authenticationSourceName, enabled);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void updateUserEmails(String sessionId,
                           String uid,
                           String authenticationSourceName,
                           List<String> emails)
            throws DMServiceException{
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionId);
            administrationController.updateUserEmails(session, uid, authenticationSourceName, emails);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void deleteUser(String sessionUid, String uid, String authenticationSourceName) throws DMServiceException
    {

        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.deleteUser(session, uid, authenticationSourceName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void createGroup(String sessionUid, String gid, String name, String authenticationSourceName)
            throws DMServiceException
    {

        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.createGroup(session, gid, name, authenticationSourceName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void updateGroup(String sessionUid, String gid, String name, String authenticationSourceName)
            throws DMServiceException
    {

        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.updateGroup(session, gid, name, authenticationSourceName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void deleteGroup(String sessionUid, String gid, String authenticationSourceName) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.deleteGroup(session, gid, authenticationSourceName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void addUserToGroup(String sessionUid, String uid, String gid, String authenticationSourceName)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.addUserToGroup(session, uid, gid, authenticationSourceName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void removeUserFromGroup(String sessionUid, String uid, String gid, String authenticationSourceName)
            throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            administrationController.removeUserFromGroup(session, uid, gid, authenticationSourceName);
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public User getManageableUser(String sessionUid, String uid, String authenticationSourceName)
            throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            User user = administrationController.getUser(session, uid, authenticationSourceName).toPojo();

            return user;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public User[] getManageableUsers(String sessionUid, String gid, String authenticationSourceName)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            Vector<org.kimios.kernel.user.model.User> v =
                    administrationController.getUsers(session, gid, authenticationSourceName);
            User[] r = new User[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.elementAt(i).toPojo();
            }
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.Group getManageableGroup(String sessionUid, String gid,
            String authenticationSourceName) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            org.kimios.kernel.ws.pojo.Group group =
                    administrationController.getGroup(session, gid, authenticationSourceName).toPojo();
            return group;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    //  public Group[] getManageableUsers(String sessionUid,String gid, String authenticationSourceName) throws DMServiceException {
//    
//    try {
//      
////      Session session = getHelper().getSession(sessionUid);
////      
////      Vector<org.kimios.kernel.user.Group> v = controller.getGroups(session, userUid, authenticationSourceName);
////      Group[] r = new Group[v.size()];
////      for (int i = 0; i < v.size(); i++)
////        r[i] = v.elementAt(i).toPojo();
//
//      
//      return u;
//    } catch (Exception e) {
//      
//      throw getHelper().convertException(e);
//    }
//  }
    public org.kimios.kernel.ws.pojo.Group[] getManageableGroups(String sessionUid, String userUid,
            String authenticationSourceName) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            Vector<org.kimios.kernel.user.model.Group> v = administrationController.getGroups(session, userUid, authenticationSourceName);
            org.kimios.kernel.ws.pojo.Group[] r = new org.kimios.kernel.ws.pojo.Group[v.size()];
            for (int i = 0; i < v.size(); i++) {
                r[i] = v.elementAt(i).toPojo();
            }
            return r;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    public void reindex(String sessionUid, String path) throws DMServiceException
    {

        org.kimios.kernel.security.model.Session session = null;
        try {

            session = getHelper().getSession(sessionUid);

            searchManagementController.reindex(session, path);
        } catch (Exception ex) {
            throw getHelper().convertException(ex);
        }
    }

    public int getReindexProgress(String sessionUid) throws DMServiceException
    {

        org.kimios.kernel.security.model.Session session = null;
        try {

            session = getHelper().getSession(sessionUid);

            int progress = searchManagementController.getReindexProgress(session);

            return progress;
        } catch (Exception ex) {
            throw getHelper().convertException(ex);
        }
    }

    /**
     *
     * @param sessionUid
     * @return
     * @throws DMServiceException
     */
    public Document[] getCheckedOutDocuments(String sessionUid) throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            List<org.kimios.kernel.dms.model.Document> documents = administrationController.getCheckedOutDocuments(session);
            List<Document> docs = documentController.convertToPojos(session, documents);
            return docs.toArray(new Document[]{ });
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    /**
     *
     * @param sessionUid
     * @param documentUid
     * @throws DMServiceException
     */
    public void clearLock(String sessionUid, long documentUid) throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            administrationController.clearLock(session, documentUid);
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    /**
     *
     * @param sessionUid
     * @param dmEntityUid
     * @param userName
     * @param userSource
     * @throws DMServiceException
     */
    public void changeOwnership(String sessionUid, long dmEntityUid, String userName,
            String userSource) throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            administrationController.changeOwnership(session, dmEntityUid, userName, userSource);
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    /**
     *
     * @param sessionUid
     * @return
     * @throws DMServiceException
     */
    public User[] getConnectedUsers(String sessionUid) throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            User[] users = administrationController.getConnectedUsers(session);

            return users;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    /**
     *
     * @param sessionUid
     * @return
     * @throws DMServiceException
     */
    public Session[] getAllEnabledSessions(String sessionUid) throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            Session[] sessions = administrationController.getEnabledSessions(session);

            return sessions;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    /**
     *
     * @param sessionUid
     * @param userName
     * @param userSource
     * @return
     * @throws DMServiceException
     */
    public Session[] getEnabledSessions(String sessionUid, String userName, String userSource) throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            Session[] sessions = administrationController.getEnabledSessions(session, userName, userSource);

            return sessions;
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    /**
     *
     * @param sessionUid
     * @param sessionUidToRemove
     * @throws DMServiceException
     */
    public void removeEnabledSession(String sessionUid, String sessionUidToRemove) throws DMServiceException
    {

        try {

            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);

            administrationController.removeEnabledSession(session, sessionUidToRemove);
        } catch (Exception e) {

            throw getHelper().convertException(e);
        }
    }

    /**
     *
     * @param sessionUid
     * @param userName
     * @param userSource
     * @throws DMServiceException
     */
    public void removeEnabledSessions(String sessionUid, String userName, String userSource) throws DMServiceException
    {

        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionUid);
            administrationController.removeEnabledSessions(session, userName, userSource);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void setUserAttribute(String sessionId, String userId,
            String userSource, String attributeName, String attributeValue)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionId);
            administrationController.setUserAttribute(session, userId, userSource, attributeName, attributeValue);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public String getUserAttribute(String sessionId, String userId, String userSource, String attributeName)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionId);
            return administrationController.getUserAttribute(session, userId, userSource, attributeName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public User getUserByAttribute(String sessionId, String userSource, String attributeName, String attributeValue)
            throws DMServiceException
    {
        try {
            org.kimios.kernel.security.model.Session session = getHelper().getSession(sessionId);
            return administrationController.getUserByAttributeValue(session, userSource, attributeName, attributeValue)
                    .toPojo();
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public void disableServiceLogging() throws DMServiceException {
        this.kimiosBusServiceManager.disableLogging();
    }

    @Override
    public void enableServiceLogging() throws DMServiceException {
        this.kimiosBusServiceManager.enableLogging();
    }

    public HashMap<String,String> listLoggers() throws DMServiceException {
        return (HashMap)LoggerManager.listLoggers();
    }

    @Override
    public void setLoggerLevel(String loggerName, String loggerLevel) throws DMServiceException {
        LoggerManager.setLevel(loggerName, loggerLevel);
    }
}

