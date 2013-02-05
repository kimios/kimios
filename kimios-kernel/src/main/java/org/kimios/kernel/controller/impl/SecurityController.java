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
package org.kimios.kernel.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.XMLException;
import org.kimios.kernel.jobs.ThreadManager;
import org.kimios.kernel.jobs.security.ACLUpdateJob;
import org.kimios.kernel.security.DMEntityACL;
import org.kimios.kernel.security.DMEntitySecurity;
import org.kimios.kernel.security.DMEntitySecurityFactory;
import org.kimios.kernel.security.DMEntitySecurityUtil;
import org.kimios.kernel.security.FactoryInstantiator;
import org.kimios.kernel.security.Role;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.security.SessionManager;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityController extends AKimiosController implements ISecurityController
{
    private static Logger log = LoggerFactory.getLogger(ISecurityController.class);

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#getDMEntitySecurityies(org.kimios.kernel.security.Session, long, int)
    */
    public List<DMEntitySecurity> getDMEntitySecurityies(Session session, long dmEntityUid, int dmEntityType)
            throws ConfigException,
            DataSourceException
    {
        Vector<DMEntitySecurity> v = new Vector<DMEntitySecurity>();
        DMEntity entity = this.getDMEntity(dmEntityUid, dmEntityType);
        if (entity != null) {
            v = securityFactoryInstantiator.getDMEntitySecurityFactory().getDMEntitySecurities(entity);
        }
        return v;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#updateDMEntitySecurities(org.kimios.kernel.security.Session, long, int, java.lang.String, boolean)
    */
    public void updateDMEntitySecurities(Session session, long dmEntityUid, int dmEntityType, String xmlStream,
            boolean isRecursive) throws AccessDeniedException, ConfigException, DataSourceException,
            XMLException
    {
        DMEntity entity = this.getDMEntity(dmEntityUid, dmEntityType);
        if (entity != null) {
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
                if (isRecursive) {
                    /*ACLUpdateJob job =
                            ApplicationContextProvider.prototypeBean("aclUpdaterThreadJob", ACLUpdateJob.class, null);*/
                    ThreadManager.getInstance()
                            .startJob(session, new ACLUpdateJob(aclUpdater), "UpdateACL", xmlStream, entity);
                } else {
                    DMEntitySecurityFactory fact = FactoryInstantiator.getInstance().getDMEntitySecurityFactory();
                    Vector<DMEntitySecurity> des = DMEntitySecurityUtil.getDMentitySecuritesFromXml(xmlStream, entity);
                    fact.cleanACL(entity);
                    List<DMEntityACL> nAcls = new ArrayList<DMEntityACL>();
                    for (DMEntitySecurity acl : des) {
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

    /**
     * Convenience method to get dmEntity from dmEntityUid and dmEntityType (workspace, folder and document)
     */
    private DMEntity getDMEntity(long dmEntityUid, int dmEntityType) throws ConfigException, DataSourceException
    {
        DMEntity entity = null;
        switch (dmEntityType) {
            case DMEntityType.WORKSPACE:
                entity = dmsFactoryInstantiator.getWorkspaceFactory().getWorkspace(dmEntityUid);
                break;
            case DMEntityType.FOLDER:
                entity = dmsFactoryInstantiator.getFolderFactory().getFolder(dmEntityUid);
                break;
            case DMEntityType.DOCUMENT:
                entity = dmsFactoryInstantiator.getDocumentFactory().getDocument(dmEntityUid);
                break;
        }
        return entity;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#canRead(org.kimios.kernel.security.Session, long, int)
    */
    public boolean canRead(Session session, long dmEntityUid, int dmEntityType)
            throws ConfigException, DataSourceException
    {
        DMEntity entity = this.getDMEntity(dmEntityUid, dmEntityType);
        return getSecurityAgent()
                .isReadable(entity, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#canWrite(org.kimios.kernel.security.Session, long, int)
    */
    public boolean canWrite(Session session, long dmEntityUid, int dmEntityType)
            throws ConfigException, DataSourceException
    {
        DMEntity entity = this.getDMEntity(dmEntityUid, dmEntityType);
        return getSecurityAgent()
                .isWritable(entity, session.getUserName(), session.getUserSource(), session.getGroups());
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.ISecurityController#hasFullAccess(org.kimios.kernel.security.Session, long, int)
    */
    public boolean hasFullAccess(Session session, long dmEntityUid, int dmEntityType)
            throws ConfigException, DataSourceException
    {
        DMEntity entity = this.getDMEntity(dmEntityUid, dmEntityType);
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
        return authFactoryInstantiator.getAuthenticationSourceFactory().getAuthenticationSource(session.getUserSource())
                .getUserFactory().getUser(session.getUserName());
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
}
