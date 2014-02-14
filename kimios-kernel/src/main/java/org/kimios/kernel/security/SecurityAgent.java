/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.security;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.Group;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class SecurityAgent
{

    private static org.slf4j.Logger log = LoggerFactory.getLogger( SecurityAgent.class );
    private static SecurityAgent instance;

    synchronized public static SecurityAgent getInstance()
    {
        if (instance == null) {
            instance = new SecurityAgent();
        }
        return instance;
    }

    private FactoryInstantiator securityFactoryInstantiator;

    private org.kimios.kernel.dms.FactoryInstantiator dmsFactoryInstantiator;

    public void setSecurityFactoryInstantiator(
            FactoryInstantiator securityFactoryInstantiator)
    {
        this.securityFactoryInstantiator = securityFactoryInstantiator;
    }

    public void setDmsFactoryInstantiator(
            org.kimios.kernel.dms.FactoryInstantiator dmsFactoryInstantiator)
    {
        this.dmsFactoryInstantiator = dmsFactoryInstantiator;
    }

    public <T extends DMEntityImpl> List<T> areReadable(List<T> entities, String userName, String userSource,
            Vector<Group> groups) throws ConfigException,
            DataSourceException
    {

        if (isAdmin(userName, userSource)) {
            return entities;
        }

        Vector<T> readables = new Vector<T>();
//    for(T dm: entities){
//      if(dm.getOwner().equalsIgnoreCase(userName) && dm.getOwnerSource().equalsIgnoreCase(userSource)){
//        readables.add(dm);
//      }
//    }
//    entities.removeAll(readables);
        if (entities.size() > 0) {
            /* Generate read hash */
            Vector<String> hashs = new Vector<String>();
            Vector<String> noAccessHash = new Vector<String>();
            noAccessHash.add(DMSecurityRule
                    .getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS).getRuleHash());
            hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.READRULE)
                    .getRuleHash());
            hashs.add(
                    DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                            .getRuleHash());
            hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                    .getRuleHash());
            for (Group g : groups) {
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.READRULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.WRITERULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.FULLRULE).getRuleHash());
            }
            readables.addAll(securityFactoryInstantiator.getDMEntitySecurityFactory()
                    .authorizedEntities(entities, userName, userSource, hashs, noAccessHash));
            Collections.sort(readables);
        }
        return readables;
    }

    public <T extends DMEntityImpl> List<T> areWritable(List<T> entities, String userName, String userSource,
            Vector<Group> groups) throws ConfigException, DataSourceException
    {

        if (isAdmin(userName, userSource)) {
            return entities;
        }

        Vector<T> writables = new Vector<T>();
//    for(T dm: entities){
//      if(dm.getOwner().equalsIgnoreCase(userName) && dm.getOwnerSource().equalsIgnoreCase(userSource)){
//        writables.add(dm);
//      }
//    }
        entities.removeAll(writables);
        if (entities.size() > 0) {
            /* Generate read hash */
            Vector<String> hashs = new Vector<String>();
            Vector<String> noAccessHash = new Vector<String>();
            noAccessHash.add(DMSecurityRule
                    .getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS).getRuleHash());
            hashs.add(
                    DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                            .getRuleHash());
            hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                    .getRuleHash());
            for (Group g : groups) {
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.WRITERULE).getRuleHash());
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.FULLRULE).getRuleHash());
            }
            writables.addAll(securityFactoryInstantiator.getDMEntitySecurityFactory()
                    .authorizedEntities(entities, userName, userSource, hashs, noAccessHash));
            Collections.sort(writables);
        }
        return writables;
    }

    public <T extends DMEntityImpl> List<T> areFullAccess(List<T> entities, String userName, String userSource,
            Vector<Group> groups) throws ConfigException, DataSourceException
    {

        if (isAdmin(userName, userSource)) {
            return entities;
        }

        Vector<T> fullAccessAble = new Vector<T>();
//    for(T dm: entities){
//      if(dm.getOwner().equalsIgnoreCase(userName) && dm.getOwnerSource().equalsIgnoreCase(userSource)){
//        fullAccessAble.add(dm);
//      }
//    }
        entities.removeAll(fullAccessAble);
        if (entities.size() > 0) {
            /* Generate read hash */
            Vector<String> hashs = new Vector<String>();
            Vector<String> noAccessHash = new Vector<String>();
            noAccessHash.add(DMSecurityRule
                    .getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS).getRuleHash());
            hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                    .getRuleHash());
            for (Group g : groups) {
                hashs.add(DMSecurityRule
                        .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                                DMSecurityRule.FULLRULE).getRuleHash());
            }
            fullAccessAble.addAll(securityFactoryInstantiator.getDMEntitySecurityFactory()
                    .authorizedEntities(entities, userName, userSource, hashs, noAccessHash));
            Collections.sort(fullAccessAble);
        }
        return fullAccessAble;
    }

    public boolean isReadable(DMEntity dm, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException
    {

        if (dm == null || userName == null || userSource == null) {
            return false;
        }

        if (isAdmin(userName, userSource)) {
            return true;
        }

        String noAccessUserHash =
                DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS)
                        .getRuleHash();
        /* Generate read hash */
        Vector<String> hashs = new Vector<String>();
        Vector<String> noAccessHash = new Vector<String>();
        noAccessHash.add(noAccessUserHash);
        hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.READRULE)
                .getRuleHash());
        hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                .getRuleHash());
        hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                .getRuleHash());
        for (Group g : groups) {
            hashs.add(DMSecurityRule.getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                    DMSecurityRule.READRULE).getRuleHash());
            hashs.add(DMSecurityRule.getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                    DMSecurityRule.WRITERULE).getRuleHash());
            hashs.add(DMSecurityRule.getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                    DMSecurityRule.FULLRULE).getRuleHash());
        }
        boolean secReturn = securityFactoryInstantiator.getDMEntitySecurityFactory()
                .ruleExists(dm, userName, userSource, hashs, noAccessHash);
        return secReturn;
    }

    public boolean isWritable(DMEntity dm, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException
    {
        if (dm == null || userName == null || userSource == null) {
            return false;
        }

        if (dm.getType() == DMEntityType.DOCUMENT) {
            Document doc = (Document) dm;
            if (doc.isCheckedOut() && (!doc.getCheckoutLock().getUser().equals(userName) ||
                    !doc.getCheckoutLock().getUserSource().equals(userSource)))
            {
                return false;
            }
            if (!canWriteDuringWorkflow(doc, userName, userSource, groups)) {
                return false;
            }
        }

        if (isAdmin(userName, userSource)) {
            return true;
        }

        String noAccessUserHash =
                DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS)
                        .getRuleHash();

        /*
        *  if user is owner and hasn't limitation
        */
        /* Generate read hash */
        Vector<String> hashs = new Vector<String>();
        Vector<String> noAccessHash = new Vector<String>();
        noAccessHash.add(noAccessUserHash);
        hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                .getRuleHash());
        hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                .getRuleHash());
        for (Group g : groups) {
            hashs.add(DMSecurityRule.getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                    DMSecurityRule.WRITERULE).getRuleHash());
            hashs.add(DMSecurityRule.getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                    DMSecurityRule.FULLRULE).getRuleHash());
        }
        return securityFactoryInstantiator.getDMEntitySecurityFactory()
                .ruleExists(dm, userName, userSource, hashs, noAccessHash);
    }

    /*
    *  Check if one child element is not writable (this method don't check the checked out documents)
    */
    public boolean hasAnyChildNotWritable(DMEntity dm, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException
    {
        if (dm == null || userName == null || userSource == null) {
            return true;
        }

        if (isAdmin(userName, userSource)) {
            return false;
        }

        String noAccessUserHash =
                DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS)
                        .getRuleHash();
        Vector<String> writeHash = new Vector<String>();
        writeHash
                .add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.WRITERULE)
                        .getRuleHash());
        writeHash.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                .getRuleHash());
        for (Group g : groups) {
            writeHash.add(DMSecurityRule
                    .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                            DMSecurityRule.WRITERULE).getRuleHash());
            writeHash.add(DMSecurityRule
                    .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                            DMSecurityRule.FULLRULE).getRuleHash());
        }
        return securityFactoryInstantiator.getDMEntitySecurityFactory()
                .hasAnyChildNotWritable(dm, userName, userSource, writeHash, noAccessUserHash);
    }

    public boolean hasAnyChildNotFullAccess(DMEntity dm, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException
    {
        if (dm == null || userName == null || userSource == null) {
            return true;
        }

        if (isAdmin(userName, userSource)) {
            return false;
        }

        String noAccessUserHash =
                DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS)
                        .getRuleHash();
        Vector<String> writeHash = new Vector<String>();
        writeHash.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                .getRuleHash());
        for (Group g : groups) {
            writeHash.add(DMSecurityRule
                    .getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                            DMSecurityRule.FULLRULE).getRuleHash());
        }
        return securityFactoryInstantiator.getDMEntitySecurityFactory()
                .hasAnyChildNotWritable(dm, userName, userSource, writeHash, noAccessUserHash);
    }

    public boolean hasAnyChildCheckedOut(DMEntity dm, String userName, String userSource)
            throws ConfigException, DataSourceException
    {
        return securityFactoryInstantiator.getDMEntitySecurityFactory().hasAnyChildCheckedOut(dm, userName, userSource);
    }

    public boolean isFullAccess(DMEntity dm, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException
    {
        if (dm == null || userName == null || userSource == null) {
            return false;
        }

        if (dm.getType() == DMEntityType.DOCUMENT) {
            Document doc = (Document) dm;
            if (doc.isCheckedOut() && (!doc.getCheckoutLock().getUser().equals(userName) ||
                    !doc.getCheckoutLock().getUserSource().equals(userSource)))
            {
                return false;
            }
            if (!canWriteDuringWorkflow(doc, userName, userSource, groups)) {
                return false;
            }
        }

        if (isAdmin(userName, userSource)) {
            return true;
        }

        String noAccessUserHash =
                DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.NOACCESS)
                        .getRuleHash();
        /* Generate read hash */
        Vector<String> hashs = new Vector<String>();
        Vector<String> noAccessHash = new Vector<String>();
        noAccessHash.add(noAccessUserHash);
        hashs.add(DMSecurityRule.getInstance(userName, userSource, SecurityEntityType.USER, DMSecurityRule.FULLRULE)
                .getRuleHash());
        for (Group g : groups) {
            hashs.add(DMSecurityRule.getInstance(g.getGid(), g.getAuthenticationSourceName(), SecurityEntityType.GROUP,
                    DMSecurityRule.FULLRULE).getRuleHash());
        }
        return securityFactoryInstantiator.getDMEntitySecurityFactory()
                .ruleExists(dm, userName, userSource, hashs, noAccessHash);
    }

    public boolean isAdmin(String userName, String userSource) throws ConfigException, DataSourceException
    {
        return (securityFactoryInstantiator.getRoleFactory().getRole(Role.ADMIN, userName, userSource) != null);
    }

    private boolean canWriteDuringWorkflow(Document doc, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException
    {
        DocumentWorkflowStatusRequest dwsr =
                dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().getLastPendingRequest(doc);
        if (dwsr == null) {
            return true;
        } else {
            if (isAdmin(userName, userSource)) {
                return true;
            }
            if (doc.getOwner().equalsIgnoreCase(userName) && doc.getOwnerSource().equalsIgnoreCase(userSource)) {
                return true;
            }
            Vector<WorkflowStatusManager> v = dmsFactoryInstantiator.getWorkflowStatusManagerFactory()
                    .getWorkflowStatusManagers(dwsr.getWorkflowStatusUid());
            for (int i = 0; i < v.size(); i++) {
                if (v.elementAt(i).getSecurityEntityName().equals(userName) &&
                        v.elementAt(i).getSecurityEntitySource().equals(userSource) &&
                        v.elementAt(i).getSecurityEntityType() == SecurityEntityType.USER)
                {
                    return true;
                }
                for (int j = 0; j < groups.size(); j++) {
                    if (v.elementAt(i).getSecurityEntityName().equals(groups.elementAt(j).getID()) &&
                            v.elementAt(i).getSecurityEntitySource()
                                    .equals(groups.elementAt(j).getAuthenticationSourceName()) &&
                            v.elementAt(i).getSecurityEntityType() == SecurityEntityType.GROUP)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean canCancelWorkFlow(Document doc, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException
    {
        DocumentWorkflowStatus dws =
                dmsFactoryInstantiator.getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(doc.getUid());
        if (dws == null) {
            return true;
        } else {
            if (isAdmin(userName, userSource)) {
                return true;
            }
            if (doc.getOwner().equalsIgnoreCase(userName) && doc.getOwnerSource().equalsIgnoreCase(userSource)) {
                return true;
            }
            return false;
        }
    }

    public boolean isDocumentOutOfWorkflow(Document doc) throws ConfigException, DataSourceException
    {
        DocumentWorkflowStatus dws =
                dmsFactoryInstantiator.getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(doc.getUid());
        if (dws == null) {
            return (dmsFactoryInstantiator.getDocumentWorkflowStatusRequestFactory().getRequests(doc.getUid()).size() ==
                    0);
        } else if (dmsFactoryInstantiator.getWorkflowStatusFactory().getEndWorkflowStatus(
                dmsFactoryInstantiator.getWorkflowStatusFactory().getWorkflowStatus(dws.getWorkflowStatusUid())
                        .getWorkflow()).getUid() == dws.getWorkflowStatusUid())
        {
            return true;
        } else {
            return false;
        }
    }
}

