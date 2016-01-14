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
import org.kimios.exceptions.MetaFeedSearchException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IStudioController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.metafeeds.MetaFeedManager;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.exception.*;
import org.kimios.kernel.security.model.Role;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.xml.XSDException;
import org.kimios.kernel.xml.XSDUtil;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Transactional
public class StudioController extends AKimiosController implements IStudioController
{
    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getDocumentType(long)
    */
    public DocumentType getDocumentType(long uid) throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(uid);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getDocumentTypes()
    */
    public Vector<DocumentType> getDocumentTypes() throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentTypes();
    }

    /**
     * Create document type (and metas), from an xml descriptor
     */
    public synchronized void createDocumentType(Session session, String xmlStream)
            throws AccessDeniedException, ConfigException, DataSourceException,
            XMLException, XSDException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        try {
            new XSDUtil().validateXmlStream(xmlStream, "document-type.xsd");
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            Element root = doc.getDocumentElement();
            long docTypeUid = -1;
            String docTypeName = root.getAttribute("name");
            long parentUid = Long.parseLong(root.getAttribute("document-type-uid"));
            List<Meta> metas = this.getMetasFromXml(xmlStream, docTypeUid);

            DocumentType t = new DocumentType(-1, docTypeName,
                    dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(parentUid));
            dmsFactoryInstantiator.getDocumentTypeFactory().saveDocumentType(t);
            for (int i = 0; i < metas.size(); i++) {
                metas.get(i).setDocumentTypeUid(t.getUid());
                dmsFactoryInstantiator.getMetaFactory().saveMeta(metas.get(i));
            }
        } catch (ParserConfigurationException e) {
            throw new XMLException();
        } catch (SAXException e) {
            throw new XMLException();
        } catch (IOException e) {
            throw new XMLException();
        }
    }

    /**
     * Update existing document type
     */
    public synchronized void updateDocumentType(Session session, String xmlStream)
            throws MetaValueTypeException, AccessDeniedException, ConfigException,
            DataSourceException, XMLException, XSDException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        try {
            new XSDUtil().validateXmlStream(xmlStream, "document-type.xsd");
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            Element root = doc.getDocumentElement();
            long docTypeUid = Long.parseLong(root.getAttribute("uid"));
            String docTypeName = root.getAttribute("name");
            long parentUid = Long.parseLong(root.getAttribute("document-type-uid"));
            DocumentType t = dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(docTypeUid);
            DocumentType parent = dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(parentUid);
            if (t != null) {
                t.setName(docTypeName);
                if (parent != null) {
                    if (!this.isChild(parent, t)) {
                        t.setDocumentType(parent);
                    }
                } else {
                    t.setDocumentType(null);
                }
                dmsFactoryInstantiator.getDocumentTypeFactory().updateDocumentType(t);
                List<Meta> metas = this.getMetasFromXml(xmlStream, docTypeUid);
                List<Meta> v = dmsFactoryInstantiator.getMetaFactory().getUnheritedMetas(t);
                for (Meta m : v) {
                    boolean delete = true;
                    for (Meta m2 : metas) {
                        if (m2.getUid() == m.getUid()) {
                            delete = false;
                        }
                    }
                    if (delete) {
                        dmsFactoryInstantiator.getMetaFactory().deleteMeta(m);
                    }
                }
                for (Meta m : metas) {
                    Meta m2 = dmsFactoryInstantiator.getMetaFactory().getMeta(m.getUid());
                    if (m2 == null) {
                        dmsFactoryInstantiator.getMetaFactory().saveMeta(m);
                    } else {
                        if (dmsFactoryInstantiator.getMetaValueFactory().hasValue(m) &&
                                m.getMetaType() != m2.getMetaType())
                        {
                            throw new MetaValueTypeException("Existing Values for this Meta definition!");
                        }
                        dmsFactoryInstantiator.getMetaFactory().updateMeta(m);
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            throw new XMLException();
        } catch (SAXException e) {
            throw new XMLException();
        } catch (IOException e) {
            throw new XMLException();
        }
    }

    /**
     * Convenience method to detect type inheritance
     */
    private boolean isChild(DocumentType t1, DocumentType t2) throws ConfigException, DataSourceException
    {
        if (t1.getUid() == t2.getUid()) {
            return true;
        }
        DocumentType parent = t1.getDocumentType();
        if (parent == null) {
            return false;
        }
        if (parent.getUid() == t2.getUid()) {
            return true;
        } else {
            return isChild(parent, t2);
        }
    }

    /**
     * Convenience method to generate meta beans from an xml descriptor
     */
    private List<Meta> getMetasFromXml(String xmlStream, long docTypeUid)
            throws XMLException, ConfigException, DataSourceException, XSDException
    {
        try {
            ArrayList<Meta> v = new ArrayList<Meta>();
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            Element root = doc.getDocumentElement();
            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().equalsIgnoreCase("meta")) {
                    MetaFeedImpl feed = dmsFactoryInstantiator.getMetaFeedFactory().getMetaFeed(
                            Long.parseLong(list.item(i).getAttributes().getNamedItem("meta_feed").getTextContent()));
                    v.add(
                            new Meta(Long.parseLong(list.item(i).getAttributes().getNamedItem("uid").getTextContent()),
                            list.item(i).getAttributes()
                                    .getNamedItem("name").getTextContent(), docTypeUid, feed,
                            Integer.parseInt(list.item(i).getAttributes().getNamedItem("meta_type")
                                    .getTextContent()), Boolean.parseBoolean(list.item(i).getAttributes()
                                    .getNamedItem("mandatory").getTextContent()),
                                    Integer.parseInt(list.item(i).getAttributes().getNamedItem("position")
                                            .getTextContent())
                                    )
                    );


                }
            }
            return v;
        } catch (ParserConfigurationException e) {
            throw new XMLException();
        } catch (SAXException e) {
            throw new XMLException();
        } catch (IOException e) {
            throw new XMLException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#deleteDocumentType(org.kimios.kernel.security.Session, long)
    */
    public void deleteDocumentType(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException, XMLException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        DocumentTypeFactory fact = dmsFactoryInstantiator.getDocumentTypeFactory();
        DocumentType dt = fact.getDocumentType(uid);
        dmsFactoryInstantiator.getDocumentVersionFactory().removeDocumentType(dt);
        fact.deleteDocumentType(dt);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getAvailableMetaFeedTypes(org.kimios.kernel.security.Session)
    */
    public List<String> getAvailableMetaFeedTypes(Session session)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        List<String> availables = new ArrayList<String>(metaFeedManager.listAsString());
        return availables;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getMetaFeed(long)
    */
    public MetaFeedImpl getMetaFeed(long uid) throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getMetaFeedFactory().getMetaFeed(uid);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getMetaFeeds(org.kimios.kernel.security.Session)
    */
    public List<MetaFeedImpl> getMetaFeeds(Session session)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getMetaFeedFactory().getMetaFeeds();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#searchMetaFeedValues(org.kimios.kernel.security.Session, long, java.lang.String)
    */
    public String[] searchMetaFeedValues(Session session, long metaFeedUid, String criteria)
            throws AccessDeniedException, ConfigException,
            DataSourceException, MetaFeedSearchException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.METAFEEDDENIED, session.getUserName(), session.getUserSource()) != null)
        {
            throw new AccessDeniedException();
        }
        MetaFeedImpl feed = dmsFactoryInstantiator.getMetaFeedFactory().getMetaFeed(metaFeedUid);
        return feed.search(criteria);
    }

    private MetaFeedManager metaFeedManager;

    public MetaFeedManager getMetaFeedManager() {
        return metaFeedManager;
    }

    public void setMetaFeedManager(MetaFeedManager metaFeedManager) {
        this.metaFeedManager = metaFeedManager;
    }

    /* (non-Javadoc)
        * @see org.kimios.kernel.controller.impl.IStudioController#createMetaFeed(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
        */
    public long createMetaFeed(Session session, String name, String className)
            throws RepositoryException, AccessDeniedException, ConfigException,
            DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        try {
            Class mClass = metaFeedManager.readClass(className);
            MetaFeedImpl m = (MetaFeedImpl) mClass.newInstance();
            m.setName(name);
            m.setJavaClass(className);
            return dmsFactoryInstantiator.getMetaFeedFactory().saveMetaFeed(m);
        }
        /*catch (ClassNotFoundException e) {
            throw new RepositoryException(e.getMessage());
        }*/
        catch (IllegalAccessException e) {
            throw new RepositoryException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#updateMetaFeed(org.kimios.kernel.security.Session, long, java.lang.String)
    */
    public void updateMetaFeed(Session session, long uid, String name)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        MetaFeedImpl m = dmsFactoryInstantiator.getMetaFeedFactory().getMetaFeed(uid);
        m.setName(name);
        dmsFactoryInstantiator.getMetaFeedFactory().updateMetaFeed(m);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#deleteMetaFeed(org.kimios.kernel.security.Session, long)
    */
    public void deleteMetaFeed(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        MetaFeedImpl m = dmsFactoryInstantiator.getMetaFeedFactory().getMetaFeed(uid);
        dmsFactoryInstantiator.getMetaFeedFactory().deleteMetaFeed(m);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getMetaFeedValues(org.kimios.kernel.security.Session, long)
    */
    public List<String> getMetaFeedValues(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory().getRole(Role.METAFEEDDENIED, session.getUserName(),
                session.getUserSource()) != null)
        {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getMetaFeedFactory().getMetaFeed(uid).getValues();
    }

    /**
     * Update value list for the default enumeration meta feed
     */
    public void updateEnumerationValues(Session session, String xmlStream)
            throws XMLException, AccessDeniedException, ConfigException, DataSourceException,
            XSDException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        Vector<String> v = new Vector<String>();
        long uid = -1;
        new XSDUtil().validateXmlStream(xmlStream, "enumeration.xsd");
        try {
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            Element root = doc.getDocumentElement();
            uid = Long.parseLong(root.getAttribute("uid"));
            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().equalsIgnoreCase("entry")) {
                    v.add(list.item(i).getAttributes().getNamedItem("value").getTextContent());
                }
            }
        } catch (Exception e) {
            throw new XMLException();
        }
        dmsFactoryInstantiator.getEnumerationValueFactory().updateValues(uid, v);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getWorkflows()
    */
    public Vector<Workflow> getWorkflows() throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getWorkflowFactory().getWorkflows();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getWorkflow(long)
    */
    public Workflow getWorkflow(long uid) throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getWorkflowFactory().getWorkflow(uid);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getWorkflowStatuses(long)
    */
    public Vector<WorkflowStatus> getWorkflowStatuses(long workflowUid) throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getWorkflowStatusFactory()
                .getWorkflowStatuses(dmsFactoryInstantiator.getWorkflowFactory().getWorkflow(workflowUid));
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getWorkflowStatus(long)
    */
    public WorkflowStatus getWorkflowStatus(long workflowStatusUid) throws ConfigException, DataSourceException
    {
        return dmsFactoryInstantiator.getWorkflowStatusFactory().getWorkflowStatus(workflowStatusUid);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#createWorkflowStatusManager(org.kimios.kernel.security.Session, long, java.lang.String, java.lang.String, int)
    */
    public void createWorkflowStatusManager(Session session, long workflowStatusUid, String securityEntityName,
            String securityEnitySource,
            int securityEntityType) throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        dmsFactoryInstantiator.getWorkflowStatusManagerFactory().saveWorkflowStatusManager(
                new WorkflowStatusManager(securityEntityName, securityEnitySource, securityEntityType,
                        workflowStatusUid));
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#deleteWorkflowStatusManager(org.kimios.kernel.security.Session, long, java.lang.String, java.lang.String, int)
    */
    public void deleteWorkflowStatusManager(Session session, long workflowStatusUid, String securityEntityName,
            String securityEnitySource,
            int securityEntityType) throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        dmsFactoryInstantiator.getWorkflowStatusManagerFactory().deleteWorkflowStatusManager(
                new WorkflowStatusManager(securityEntityName, securityEnitySource, securityEntityType,
                        workflowStatusUid));
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#getWorkflowStatusManagers(org.kimios.kernel.security.Session, long)
    */
    public Vector<WorkflowStatusManager> getWorkflowStatusManagers(Session session, long workflowStatusUid)
            throws AccessDeniedException, ConfigException,
            DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        return dmsFactoryInstantiator.getWorkflowStatusManagerFactory().getWorkflowStatusManagers(workflowStatusUid);
    }

    /**
     * Create a new worfklow from an xml descriptor
     */
    public synchronized long createWorkflow(Session session, String name, String description, String xmlStream)
            throws XMLException, AccessDeniedException,
            ConfigException, DataSourceException, XSDException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        Workflow wf = new Workflow(-1, name, description);
        long wUid = dmsFactoryInstantiator.getWorkflowFactory().saveWorkflow(wf);
        Vector<StatusManagers> sms = this.getWorkflowStatusesFromXml(xmlStream, wUid);
        long wfsUid = -1;
        for (int i = sms.size() - 1; i >= 0; i--) {
            sms.elementAt(i).getStatus().setWorkflowUid(wUid);
            sms.elementAt(i).getStatus().setSuccessorUid(wfsUid);
            wfsUid = dmsFactoryInstantiator.getWorkflowStatusFactory().saveWorkflowStatus(sms.elementAt(i).getStatus());
            for (WorkflowStatusManager m : sms.elementAt(i).getManagers()) {
                m.setWorkflowStatusUid(wfsUid);
                dmsFactoryInstantiator.getWorkflowStatusManagerFactory().saveWorkflowStatusManager(m);
            }
        }
        return wUid;
    }

    /**
     * Update workflow from an xml descriptor
     */
    public synchronized void updateWorkflow(Session session, long workflowUid, String name, String description,
            String xmlStream) throws XMLException,
            AccessDeniedException, ConfigException, DataSourceException, XSDException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        Workflow wf = dmsFactoryInstantiator.getWorkflowFactory().getWorkflow(workflowUid);
        wf.setDescription(description);
        wf.setName(name);
        dmsFactoryInstantiator.getWorkflowFactory().updateWorkflow(wf);
        Vector<StatusManagers> sms = this.getWorkflowStatusesFromXml(xmlStream, workflowUid);
        WorkflowStatusFactory wfsf = dmsFactoryInstantiator.getWorkflowStatusFactory();
        Vector<WorkflowStatus> v = wfsf.getWorkflowStatuses(wf);
        for (int i = 0; i < v.size(); i++) {
            if (!isIn(v.elementAt(i), sms)) {
                wfsf.deleteWorkflowStatus(v.elementAt(i));
            }
        }
        Vector<WorkflowStatus> vOrdered = new Vector<WorkflowStatus>();
        for (StatusManagers sMan : sms) {
            vOrdered.add(new WorkflowStatus(sMan.getStatus().getUid(), sMan.getStatus().getName(),
                    sMan.getStatus().getSuccessorUid(), sMan.getStatus()
                    .getWorkflowUid()));
        }
        long wfsUid = -1;
        for (int i = sms.size() - 1; i >= 0; i--) {
            wfsf.updateWorkflowStatus(vOrdered.elementAt(i), vOrdered, i);
            sms.elementAt(i).setStatus(vOrdered.elementAt(i));
            wfsUid = sms.elementAt(i).getStatus().getUid();
            Vector<WorkflowStatusManager> managers =
                    this.getWorkflowStatusManagers(session, sms.elementAt(i).getStatus().getUid());
            for (WorkflowStatusManager m : managers) {
                dmsFactoryInstantiator.getWorkflowStatusManagerFactory().deleteWorkflowStatusManager(m);
            }
            for (WorkflowStatusManager m : sms.elementAt(i).getManagers()) {
                m.setWorkflowStatusUid(wfsUid);
                dmsFactoryInstantiator.getWorkflowStatusManagerFactory().saveWorkflowStatusManager(m);
            }
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#deleteWorkflow(org.kimios.kernel.security.Session, long)
    */
    public void deleteWorkflow(Session session, long workflowUid)
            throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        Workflow wf = dmsFactoryInstantiator.getWorkflowFactory().getWorkflow(workflowUid);
        dmsFactoryInstantiator.getWorkflowFactory().deleteWorkflow(wf);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#createWorkflowStatus(org.kimios.kernel.security.Session, long, java.lang.String, long)
    */
    public long createWorkflowStatus(Session session, long workflowUid, String name, long successorUid)
            throws ConfigException, DataSourceException,
            AccessDeniedException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        WorkflowStatus ws = new WorkflowStatus(-1, name, successorUid, workflowUid);
        return dmsFactoryInstantiator.getWorkflowStatusFactory().saveWorkflowStatus(ws);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#updateWorkflowStatus(org.kimios.kernel.security.Session, long, long, java.lang.String, long)
    */
    public void updateWorkflowStatus(Session session, long workflowStatusUid, long workflowUid, String name,
            long successorUid) throws ConfigException,
            DataSourceException, AccessDeniedException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        WorkflowStatus ws = dmsFactoryInstantiator.getWorkflowStatusFactory().getWorkflowStatus(workflowStatusUid);
        dmsFactoryInstantiator.getWorkflowStatusFactory().changeWorkflowStatus(ws);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IStudioController#deleteWorkflowStatus(org.kimios.kernel.security.Session, long)
    */
    public void deleteWorkflowStatus(Session session, long workflowStatusUid)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.STUDIO, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        WorkflowStatus ws = dmsFactoryInstantiator.getWorkflowStatusFactory().getWorkflowStatus(workflowStatusUid);
        dmsFactoryInstantiator.getWorkflowStatusFactory().deleteWorkflowStatus(ws);
    }

    /**
     * Convenience method to get workflow status managers beans list from an xml descriptor
     */
    private Vector<StatusManagers> getWorkflowStatusesFromXml(String xmlStream, long workflowUid)
            throws XMLException, XSDException
    {
        Vector<StatusManagers> sms = new Vector<StatusManagers>();
        try {
            new XSDUtil().validateXmlStream(xmlStream, "workflow.xsd");
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xmlStream.getBytes()));
            Element root = doc.getDocumentElement();
            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeName().equalsIgnoreCase("status")) {
                    Vector<WorkflowStatusManager> managers = new Vector<WorkflowStatusManager>();
                    String wsName = "";
                    for (int j = 0; j < list.item(i).getChildNodes().getLength(); j++) {
                        if (list.item(i).getChildNodes().item(j).getNodeName().equalsIgnoreCase("name")) {
                            wsName = list.item(i).getChildNodes().item(j).getTextContent();
                        }
                        if (list.item(i).getChildNodes().item(j).getNodeName().equalsIgnoreCase("manager")) {
                            managers.add(new WorkflowStatusManager(
                                    list.item(i).getChildNodes().item(j).getAttributes().getNamedItem(
                                            "uid").getTextContent(),
                                    list.item(i).getChildNodes().item(j).getAttributes().getNamedItem("source")
                                            .getTextContent(),
                                    Integer.parseInt(
                                            list.item(i).getChildNodes().item(j).getAttributes().getNamedItem("type")
                                                    .getTextContent()), Long
                                    .parseLong(list.item(i).getAttributes().getNamedItem("uid").getTextContent())));
                        }
                    }
                    StatusManagers sm =
                            new StatusManagers(new WorkflowStatus(Long.parseLong(list.item(i).getAttributes()
                                    .getNamedItem("uid").getTextContent()), wsName,
                                    Long.parseLong(list.item(i).getAttributes().getNamedItem("successor-uid")
                                            .getTextContent()), workflowUid), managers);
                    sms.add(sm);
                }
            }
            return sms;
        } catch (ParserConfigurationException e) {
            throw new XMLException();
        } catch (IOException e) {
            throw new XMLException();
        } catch (SAXException e) {
            throw new XMLException();
        }
    }

    private boolean isIn(WorkflowStatus wfs, Vector<StatusManagers> v)
    {
        for (int i = 0; i < v.size(); i++) {
            if (v.elementAt(i).getStatus().getUid() == wfs.getUid()) {
                return true;
            }
        }
        return false;
    }
}

