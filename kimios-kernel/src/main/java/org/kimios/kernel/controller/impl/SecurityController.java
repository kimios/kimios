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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.XMLException;
import org.kimios.kernel.jobs.ThreadManager;
import org.kimios.kernel.jobs.security.ACLUpdateJob;
import org.kimios.kernel.security.*;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Transactional
public class SecurityController extends AKimiosController implements ISecurityController
{
    private static Logger log = LoggerFactory.getLogger(ISecurityController.class);

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#getDMEntitySecurityies(org.kimios.kernel.security.Session, long, int)
    */
    public List<DMEntitySecurity> getDMEntitySecurityies(Session session, long dmEntityUid)
            throws ConfigException,
            DataSourceException
    {
        Vector<DMEntitySecurity> v = new Vector<DMEntitySecurity>();
        DMEntity entity = this.getDMEntity(dmEntityUid);

        if (entity != null) {
            v = securityFactoryInstantiator.getDMEntitySecurityFactory().getDMEntitySecurities(entity);
        }
        return v;
    }

    public List<DMEntitySecurity> getDefaultDMSecurityEntities(Session session, String objectType)
            throws ConfigException,
            DataSourceException
    {

        return securityFactoryInstantiator.getDMEntitySecurityFactory().getDefaultDMEntitySecurity(objectType, null);
    }

    public void saveDefaultDMSecurityEntities(Session session, String xmlStream, String objectType, String entityPath)
            throws ConfigException,
            DataSourceException
    {
        if(securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {

            List<DMEntitySecurity> des = DMEntitySecurityUtil.getDMentitySecuritesFromXml(xmlStream, null);
            for(DMEntitySecurity security: des){
                securityFactoryInstantiator.getDMEntitySecurityFactory()
                        .saveDefaultDMEntitySecurity(security, objectType, entityPath);
            }

        }else {
            throw new AccessDeniedException();
        }
    }

    public void saveDefaultDMSecurityEntities(Session session, List<DMEntitySecurity> des, String objectType, String entityPath)
            throws ConfigException,
            DataSourceException
    {
        if(securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null) {
            for(DMEntitySecurity security: des){
                securityFactoryInstantiator.getDMEntitySecurityFactory()
                        .saveDefaultDMEntitySecurity(security, objectType, entityPath);
            }

        }else {
            throw new AccessDeniedException();
        }
    }


    private void processDMEntitySecurityUpdate(Session session, long dmEntityUid, String xmlStream,
                                               boolean isRecursive, boolean appendMode){
        DMEntity entity = this.getDMEntity(dmEntityUid);

        if (entity != null) {
            DMEntitySecurityFactory fact = FactoryInstantiator.getInstance().getDMEntitySecurityFactory();
            if (getSecurityAgent()
                    .isFullAccess(entity, session.getUserName(), session.getUserSource(), session.getGroups()))
            {
                if (entity.getType() == 1 || entity.getType() == 2) {
                    if (isRecursive && getSecurityAgent()
                            .hasAnyChildNotFullAccess(entity, session.getUserName(), session.getUserSource(),
                                    session.getGroups()))
                    {
                        throw new AccessDeniedException();
                    }
                }
                List<DMEntitySecurity> submittedSecurities =
                        DMEntitySecurityUtil.getDMentitySecuritesFromXml(xmlStream, entity);

                if (isRecursive) {
                    List<DMEntitySecurity> newSubmittedSecurities = null;
                    List<DMEntityACL> removedAcls = null;
                    if(appendMode){
                        List<DMEntityACL> acls = fact.generateDMEntityAclsFromSecuritiesObject(submittedSecurities, entity);
                        List<DMEntityACL> currentAcls = fact.getDMEntityACL(entity);


                        //identify removed acls  and isolate in a list
                        List<DMEntityACL> aclsCopys = new ArrayList<DMEntityACL>(acls);
                        removedAcls = new ArrayList<DMEntityACL>(currentAcls);
                        removedAcls.removeAll(aclsCopys);

                        log.info("submitted securities count: {} - existing securities {}",
                                acls.size(), currentAcls.size());
                        acls.removeAll(currentAcls);
                        //generate securities from acls
                        newSubmittedSecurities = fact.generateDMEntitySecuritiesFromAcls(acls, entity);

                        log.info("after clean, new submitted acls / securities count {} - {}",
                                acls.size(), newSubmittedSecurities.size());

                    } else {
                        newSubmittedSecurities = submittedSecurities;
                    }
                    ThreadManager.getInstance()
                            .startJob(session, new ACLUpdateJob(aclUpdater, session, entity, newSubmittedSecurities, removedAcls, appendMode));
                } else {
                    fact.cleanACL(entity);
                    List<DMEntityACL> nAcls = new ArrayList<DMEntityACL>();
                    for (DMEntitySecurity acl : submittedSecurities) {
                        nAcls.addAll(fact.saveDMEntitySecurity(acl));
                    }
                    //set acl in the context for event handler
                    EventContext.addParameter("acls", nAcls);
                }
            } else {
                throw new AccessDeniedException();
            }
        } else {
            throw new AccessDeniedException();
        }
    }


    private void processDMEntitySecurityUpdate(Session session, long dmEntityUid, List<DMEntitySecurity> submittedSecurities,
                                               boolean isRecursive, boolean appendMode){
        DMEntity entity = this.getDMEntity(dmEntityUid);

        if (entity != null) {
            DMEntitySecurityFactory fact = FactoryInstantiator.getInstance().getDMEntitySecurityFactory();
            if (getSecurityAgent()
                    .isFullAccess(entity, session.getUserName(), session.getUserSource(), session.getGroups()))
            {
                if (entity.getType() == 1 || entity.getType() == 2) {
                    if (isRecursive && getSecurityAgent()
                            .hasAnyChildNotFullAccess(entity, session.getUserName(), session.getUserSource(),
                                    session.getGroups()))
                    {
                        throw new AccessDeniedException();
                    }
                }


                for(DMEntitySecurity sec: submittedSecurities)
                    sec.setDmEntity(entity);


                if (isRecursive) {
                    List<DMEntityACL> removedAcls = null;
                    List<DMEntitySecurity> newSubmittedSecurities = null;
                    if(appendMode){

                        List<DMEntityACL> currentAcls = fact.getDMEntityACL(entity);
                        List<DMEntityACL> acls = fact.generateDMEntityAclsFromSecuritiesObject(submittedSecurities, entity);

                        //identify removed acls  and isolate in a list
                        List<DMEntityACL> aclsCopys = new ArrayList<DMEntityACL>(acls);
                        removedAcls = new ArrayList<DMEntityACL>(currentAcls);
                        removedAcls.removeAll(aclsCopys);

                        log.info("submitted securities count: {} - existing securities {}",
                                acls.size(), currentAcls.size());
                        acls.removeAll(currentAcls);
                        //generate securities from acls
                        newSubmittedSecurities = fact.generateDMEntitySecuritiesFromAcls(acls, entity);

                        log.info("after clean, new submitted acls / securities count / removed acls {} - {} - {}",
                                acls.size(), newSubmittedSecurities.size(), removedAcls.size());

                    } else {
                        newSubmittedSecurities = submittedSecurities;
                        removedAcls = new ArrayList<DMEntityACL>();
                    }
                    ThreadManager.getInstance()
                            .startJob(session, new ACLUpdateJob(aclUpdater, session, entity, newSubmittedSecurities, removedAcls, appendMode));
                } else {
                    fact.cleanACL(entity);
                    List<DMEntityACL> nAcls = new ArrayList<DMEntityACL>();
                    for (DMEntitySecurity acl : submittedSecurities) {
                        nAcls.addAll(fact.saveDMEntitySecurity(acl));
                    }
                    //set acl in the context for event handler
                    EventContext.addParameter("acls", nAcls);
                }
            } else {
                throw new AccessDeniedException();
            }
        } else {
            throw new AccessDeniedException();
        }
    }


    @DmsEvent(eventName = { DmsEventName.ENTITY_ACL_UPDATE })
    public void updateDMEntitySecurities(Session session, long dmEntityUid, String xmlStream,
                                         boolean isRecursive, boolean appendMode) throws AccessDeniedException, ConfigException, DataSourceException,
            XMLException
    {
        processDMEntitySecurityUpdate(session, dmEntityUid, xmlStream, isRecursive, appendMode);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#updateDMEntitySecurities(org.kimios.kernel.security.Session, long, int, java.lang.String, boolean)
    */
    @DmsEvent(eventName = { DmsEventName.ENTITY_ACL_UPDATE })
    public void updateDMEntitySecurities(Session session, long dmEntityUid, List<DMEntitySecurity> items,
                                         boolean isRecursive, boolean appendMode)
                        throws AccessDeniedException, ConfigException, DataSourceException,
            XMLException
    {
        processDMEntitySecurityUpdate(session, dmEntityUid, items, isRecursive, appendMode);
    }

    /**
     * Convenience method to get dmEntity from dmEntityUid and dmEntityType (workspace, folder and document)
     */
    private DMEntity getDMEntity(long dmEntityUid) throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityUid);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#canRead(org.kimios.kernel.security.Session, long, int)
    */
    public boolean canRead(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException
    {
        DMEntity entity = this.getDMEntity(dmEntityUid);
        return getSecurityAgent()
                .isReadable(entity, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#canWrite(org.kimios.kernel.security.Session, long, int)
    */
    public boolean canWrite(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException
    {
        DMEntity entity = this.getDMEntity(dmEntityUid);
        return getSecurityAgent()
                .isWritable(entity, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#hasFullAccess(org.kimios.kernel.security.Session, long, int)
    */
    public boolean hasFullAccess(Session session, long dmEntityUid)
            throws ConfigException, DataSourceException
    {
        DMEntity entity = this.getDMEntity(dmEntityUid);
        return getSecurityAgent()
                .isFullAccess(entity, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#getAuthenticationSources()
    */
    public List<AuthenticationSource> getAuthenticationSources() throws ConfigException, DataSourceException
    {
        return authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSources();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#startSession(java.lang.String, java.lang.String, java.lang.String)
    */
    public Session startSession(String userName, String userSource, String password)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        Session s = SessionManager.getInstance().startSession(userName, password, userSource);
        return s;
    }

    public void endSession(String sessionId) throws ConfigException, DataSourceException, AccessDeniedException{
        SessionManager.getInstance().removeSession(sessionId);
    }


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#isSessionAlive(java.lang.String)
    */
    public boolean isSessionAlive(String sessionUid) throws ConfigException, DataSourceException
    {
        Session s = SessionManager.getInstance().getSession(sessionUid);
        return s != null;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#getUser(org.kimios.kernel.security.Session)
    */
    public User getUser(Session session) throws ConfigException, DataSourceException
    {
      AuthenticationSource source =
              authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(session.getUserSource());
        if(source == null){
            throw new AccessDeniedException();
        }else {
            return source.getUserFactory().getUser(session.getUserName());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#getUsers(java.lang.String)
    */
    public Vector<User> getUsers(String userSource) throws ConfigException, DataSourceException
    {
        return authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(userSource)
                .getUserFactory().getUsers();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#getGroup(java.lang.String, java.lang.String)
    */
    public Group getGroup(String groupUid, String userSource) throws ConfigException, DataSourceException
    {
        return authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(userSource)
                .getGroupFactory().getGroup(groupUid);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#getGroups(java.lang.String)
    */
    public List<Group> getGroups(String userSource) throws ConfigException, DataSourceException
    {
        return authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(userSource)
                .getGroupFactory().getGroups();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#canCreateWorkspace(org.kimios.kernel.security.Session)
    */
    public boolean canCreateWorkspace(Session session) throws ConfigException, DataSourceException
    {
        Role createWorkspaceRole = securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.WORKSPACE, session.getUserName(), session.getUserSource());
        return (createWorkspaceRole != null);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#hasStudioAccess(org.kimios.kernel.security.Session)
    */
    public boolean hasStudioAccess(Session session) throws ConfigException, DataSourceException
    {
        return (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) != null);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#hasReportingAccess(org.kimios.kernel.security.Session)
    */
    public boolean hasReportingAccess(Session session) throws ConfigException, DataSourceException
    {
        return (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) != null);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#isAdmin(org.kimios.kernel.security.Session)
    */
    public boolean isAdmin(Session session) throws ConfigException, DataSourceException
    {
        return (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#isAdmin(String userName, String userSource)
    */
    public boolean isAdmin(String userName, String userSource) throws ConfigException, DataSourceException
    {
        return (securityFactoryInstantiator.getRoleFactory().getRole(Role.ADMIN, userName, userSource) != null);
    }

    public User getUser(String userName, String userSource) throws ConfigException, DataSourceException
    {
        return authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(userSource)
                .getUserFactory().getUser(userName);
    }

    public Session impersonnate(Session session, String userName, String userSource)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.ADMIN, session.getUserName(), session.getUserSource()) != null)
        {
            return SessionManager.getInstance().startSession(userName, userSource);
        } else {
            throw new AccessDeniedException();
        }
    }

    public Session startSession(String externalToken) throws ConfigException, AccessDeniedException {
        return SessionManager.getInstance().startSession(externalToken);
    }

}
