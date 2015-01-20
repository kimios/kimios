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
package org.kimios.kernel.controller.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IDocumentVersionController;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.utils.MetaProcessor;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.exception.*;
import org.kimios.kernel.repositories.RepositoryManager;
import org.kimios.kernel.security.Session;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * @author Fabien Alin
 */

@Transactional
public class DocumentVersionController extends AKimiosController implements IDocumentVersionController {

    private static Logger logger = LoggerFactory.getLogger(DocumentVersionController.class);

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getDocumentVersion(org.kimios.kernel.security.Session, long)
    */
    public DocumentVersion getDocumentVersion(Session session, long documentVersionUid) throws ConfigException,
            DataSourceException, AccessDeniedException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (getSecurityAgent()
                .isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            return dv;
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#createDocumentVersion(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_CREATE })
    public long createDocumentVersion(Session session, long documentUid)
            throws CheckoutViolationException, ConfigException, DataSourceException, AccessDeniedException {
        DocumentFactory docFactory = dmsFactoryInstantiator.getDocumentFactory();
        Document d = docFactory.getDocument(documentUid);
        DocumentVersion dv =
                new DocumentVersion(-1, session.getUserName(), session.getUserSource(), new Date(), new Date(),
                        documentUid, 0, null);
        if (getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getDocumentVersionFactory().saveDocumentVersion(dv);
            return dv.getUid();
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#createDocumentVersionFromLatest(org.kimios.kernel.security.Session, long)
    */
    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_CREATE_FROM_LATEST })
    public long createDocumentVersionFromLatest(Session session, long documentUid)
            throws CheckoutViolationException, ConfigException, DataSourceException, AccessDeniedException,
            RepositoryException {
        Document doc = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        DocumentVersion dv = this.getLastDocumentVersion(session, documentUid);
        DocumentVersion newVersion =
                new DocumentVersion(-1, session.getUserName(), session.getUserSource(), new Date(), new Date(),
                        doc.getUid(), dv.getLength(), dv.getDocumentType());
        newVersion.setHashMD5(dv.getHashMD5());
        newVersion.setHashSHA1(dv.getHashSHA1());
        /*
            The new store path will be set on version save (@see HDocumentVersionFactory)
         */
        if (getSecurityAgent().isWritable(doc, session.getUserName(), session.getUserSource(), session.getGroups())) {
            dmsFactoryInstantiator.getDocumentVersionFactory().saveDocumentVersion(newVersion);
            /*
                Copy old version
             */
            RepositoryManager.copyVersion(dv, newVersion);
            //Copying metas values
            List<MetaValue> vMetas = dmsFactoryInstantiator.getMetaValueFactory().getMetaValues(dv);
            Vector<MetaValue> toSave = new Vector<MetaValue>();
            for (MetaValue m : vMetas) {
                switch (m.getMeta().getMetaType()) {
                    case MetaType.STRING:
                        toSave.add(new MetaStringValue(newVersion, m.getMeta(),
                                (m.getValue() != null ? (String) m.getValue() : "")));
                        break;
                    case MetaType.NUMBER:
                        toSave.add(new MetaNumberValue(newVersion, m.getMeta(),
                                (m.getValue() != null ? (Double) m.getValue() : -1)));
                        break;

                    case MetaType.DATE:
                        toSave.add(new MetaDateValue(newVersion, m.getMeta(),
                                (m.getValue() != null ? (Date) m.getValue() : null)));
                        break;

                    case MetaType.BOOLEAN:
                        toSave.add(new MetaBooleanValue(newVersion, m.getMeta(),
                                (m.getValue() != null ? (Boolean) m.getValue() : null)));
                        break;
                    case MetaType.LIST:
                        toSave.add(new MetaListValue(newVersion, m.getMeta(),
                                (m.getValue() != null ? (List) m.getValue() : null)));
                        break;
                }
            }
            MetaValueFactory mvf = dmsFactoryInstantiator.getMetaValueFactory();
            for (MetaValue b : toSave) {
                mvf.saveMetaValue(b);
            }
            return newVersion.getUid();
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#updateDocumentVersion(org.kimios.kernel.security.Session, long, long)
    */
    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_UPDATE })
    public void updateDocumentVersion(Session session, long documentUid, long documentTypeUid, String xmlStream) throws
            XMLException, CheckoutViolationException, ConfigException, DataSourceException, AccessDeniedException {
        DocumentTypeFactory typeFactory = dmsFactoryInstantiator.getDocumentTypeFactory();
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(d);
        if (getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            //Getting existing values
            List<MetaValue> vMetaValuesExisting = dmsFactoryInstantiator.getMetaValueFactory().getMetaValues(dv);
            //Changing document version to new type
            DocumentType newDt = typeFactory.getDocumentType(documentTypeUid);
            dv.setDocumentType(newDt);
            dmsFactoryInstantiator.getDocumentVersionFactory().updateDocumentVersion(dv);

            if (xmlStream == null || xmlStream.length() == 0) {
                //Metas list of new DocumentType
                // keep existing value for inheritance
                List<Meta> vMetaNewType = null;
                if (dv.getDocumentType() != null) {
                    vMetaNewType = dmsFactoryInstantiator.getMetaFactory().getMetas(newDt);
                } else {
                    vMetaNewType = new ArrayList<Meta>();
                }

                Vector<MetaValue> toDelete = new Vector<MetaValue>();
                for (MetaValue v : vMetaValuesExisting) {
                    if (!vMetaNewType.contains(v.getMeta())) {
                        toDelete.add(v);
                    }
                }
                for (MetaValue v : toDelete) {
                    dmsFactoryInstantiator.getMetaValueFactory().deleteMetaValue(v);
                }
            } else {
                //set all value
                MetaValueFactory fact = dmsFactoryInstantiator.getMetaValueFactory();
                List<MetaValue> vNewMetas = MetaProcessor.getMetaValuesFromXML(xmlStream, dv.getUid());
                List<MetaValue> vMetas = fact.getMetaValues(dv);

                for (MetaValue m : vNewMetas) {
                    if (m.getMeta().isMandatory()) {
                        if (m.getValue() == null) {
                            throw new AccessDeniedException();
                        }

                        switch (m.getMeta().getMetaType()) {
                            case MetaType.BOOLEAN:
                                // do nothing: never undefined (true if checked, else false)
                                break;
                            case MetaType.DATE:
                                if (String.valueOf(m.getValue()).isEmpty() || String.valueOf(m.getValue()).length() < 1)
                                    throw new AccessDeniedException();
                                break;
                            case MetaType.NUMBER:
                                if ((Double) m.getValue() == 0)
                                    throw new AccessDeniedException();
                                break;
                            case MetaType.STRING:
                                if (((String) m.getValue()).isEmpty() || ((String) m.getValue()).length() < 1)
                                    throw new AccessDeniedException();
                                break;
                            case MetaType.LIST:
                                if(((List)m.getValue()).size() == 0)
                                    throw new AccessDeniedException();
                                break;
                        }
                    }
                }

                for (MetaValue m : vMetas) {
                    fact.deleteMetaValue(m);
                }
                for (MetaValue m : vNewMetas) {
                    fact.saveMetaValue(m);
                }
            }

            dv.setModificationDate(new Date());
            dmsFactoryInstantiator.getDocumentVersionFactory().updateDocumentVersion(dv);
            EventContext.addParameter("version", dv);
            EventContext.addParameter("documentTypeSet", newDt);
        } else {
            throw new AccessDeniedException();
        }
    }


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#updateDocumentVersion(org.kimios.kernel.security.Session, long, long)
    */
    @DmsEvent(eventName = { DmsEventName.DOCUMENT_VERSION_UPDATE })
    public void updateDocumentVersion(Session session, long documentUid, long documentTypeUid, List<MetaValue> metaValues) throws
            XMLException, CheckoutViolationException, ConfigException, DataSourceException, AccessDeniedException {
        DocumentTypeFactory typeFactory = dmsFactoryInstantiator.getDocumentTypeFactory();
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(d);
        if (getSecurityAgent().isWritable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            //Getting existing values
            List<MetaValue> vMetaValuesExisting = dmsFactoryInstantiator.getMetaValueFactory().getMetaValues(dv);
            //Changing document version to new type
            DocumentType newDt = typeFactory.getDocumentType(documentTypeUid);
            dv.setDocumentType(newDt);
            dmsFactoryInstantiator.getDocumentVersionFactory().updateDocumentVersion(dv);

            if (metaValues == null || metaValues.size() == 0) {
                //Metas list of new DocumentType
                // keep existing value for inheritance
                List<Meta> vMetaNewType = null;
                if (dv.getDocumentType() != null) {
                    vMetaNewType = dmsFactoryInstantiator.getMetaFactory().getMetas(newDt);
                } else {
                    vMetaNewType = new ArrayList<Meta>();
                }

                Vector<MetaValue> toDelete = new Vector<MetaValue>();
                for (MetaValue v : vMetaValuesExisting) {
                    if (!vMetaNewType.contains(v.getMeta())) {
                        toDelete.add(v);
                    }
                }
                for (MetaValue v : toDelete) {
                    dmsFactoryInstantiator.getMetaValueFactory().deleteMetaValue(v);
                }
            } else {
                //set all value
                MetaValueFactory fact = dmsFactoryInstantiator.getMetaValueFactory();
                for(MetaValue v: metaValues)  {

                    logger.debug("setting metavalue {} to {}", v.getMetaUid(), v.getValue());
                    v.setDocumentVersionUid(dv.getUid());

                }

                List<MetaValue> vMetas = fact.getMetaValues(dv);

                for (MetaValue m : metaValues) {
                    if (m.getMeta().isMandatory()) {
                        if (m.getValue() == null) {
                            throw new AccessDeniedException();
                        }

                        switch (m.getMeta().getMetaType()) {
                            case MetaType.BOOLEAN:
                                // do nothing: never undefined (true if checked, else false)
                                break;
                            case MetaType.DATE:
                                if (String.valueOf(m.getValue()).isEmpty() || String.valueOf(m.getValue()).length() < 1)
                                    throw new AccessDeniedException();
                                break;
                            case MetaType.NUMBER:
                                if ((Double) m.getValue() == 0)
                                    throw new AccessDeniedException();
                                break;
                            case MetaType.STRING:
                                if (((String) m.getValue()).isEmpty() || ((String) m.getValue()).length() < 1)
                                    throw new AccessDeniedException();
                                break;
                            case MetaType.LIST:
                                if(((List)m.getValue()).size() == 0)
                                    throw new AccessDeniedException();
                                break;
                        }
                    }
                }

                for (MetaValue m : vMetas) {
                    fact.deleteMetaValue(m);
                }
                for (MetaValue m : metaValues) {
                    fact.saveMetaValue(m);
                }
            }

            /*
                Check Configuration to know if change update date on simple meta value or right change
             */
            if(ConfigurationManager.getValue("dms.version.date.keep.on.update").equals("true")){
                dmsFactoryInstantiator.getDocumentVersionFactory().updateDocumentVersionBulk(dv);
            } else {
                dv.setModificationDate(new Date());
                dmsFactoryInstantiator.getDocumentVersionFactory().updateDocumentVersion(dv);
            }

            EventContext.addParameter("version", dv);
            EventContext.addParameter("documentTypeSet", newDt);
        } else {
            throw new AccessDeniedException();
        }
    }


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#deleteDocumentVersion(org.kimios.kernel.security.Session, long)
    */
    public void deleteDocumentVersion(long documentVersionUid)
            throws CheckoutViolationException, ConfigException, DataSourceException, AccessDeniedException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        dmsFactoryInstantiator.getDocumentVersionFactory().deleteDocumentVersion(dv);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getMetaValue(org.kimios.kernel.security.Session, long, long)
    */
    public Object getMetaValue(Session session, long documentVersionUid, long metaUid)
            throws ConfigException, DataSourceException, AccessDeniedException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (getSecurityAgent()
                .isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            Meta m = dmsFactoryInstantiator.getMetaFactory().getMeta(metaUid);
            MetaValue mv = dmsFactoryInstantiator.getMetaValueFactory().getMetaValue(dv, m);
            if (mv == null) {
                return mv;
            } else {
                return mv.getValue();
            }
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#toMetaValue(int, org.kimios.kernel.dms.DocumentVersion, long, java.lang.String)
    */
    public MetaValue toMetaValue(int metaType, DocumentVersion version, Meta meta, String metaValue) {
        return MetaProcessor.toMetaValue(metaType, version, meta, metaValue);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#updateMetasValue(org.kimios.kernel.security.Session, long, java.lang.String)
    */
    public void updateMetasValue(Session session, long uid, String xmlStream)
            throws XMLException, AccessDeniedException, ConfigException, DataSourceException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(uid);
        if (getSecurityAgent()
                .isWritable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            MetaValueFactory fact = dmsFactoryInstantiator.getMetaValueFactory();
            List<MetaValue> vNewMetas = MetaProcessor.getMetaValuesFromXML(xmlStream, uid);
            List<MetaValue> vMetas = fact.getMetaValues(dv);
            for (MetaValue m : vMetas) {
                fact.deleteMetaValue(m);
            }
            for (MetaValue m : vNewMetas) {
                fact.saveMetaValue(m);
            }
            dv.setModificationDate(new Date());
            dmsFactoryInstantiator.getDocumentVersionFactory().updateDocumentVersion(dv);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getDocumentVersions(org.kimios.kernel.security.Session, long)
    */
    public Vector<DocumentVersion> getDocumentVersions(Session session, long documentUid)
            throws ConfigException, DataSourceException {
        DocumentFactory docFactory = dmsFactoryInstantiator.getDocumentFactory();
        Document d = docFactory.getDocument(documentUid);
        Vector<DocumentVersion> vVersions = new Vector<DocumentVersion>();
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            vVersions = dmsFactoryInstantiator.getDocumentVersionFactory().getVersions(d);
        }
        return vVersions;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getLastDocumentVersion(org.kimios.kernel.security.Session, long)
    */
    public DocumentVersion getLastDocumentVersion(Session session, long documentUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        Document d = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentUid);
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(d);
            return dv;
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getDocumentComment(org.kimios.kernel.security.Session, long)
    */
    public DocumentComment getDocumentComment(Session session, long uid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        DocumentComment comment = dmsFactoryInstantiator.getDocumentCommentFactory().getDocumentComment(uid);
        Document d = comment.getDocumentVersion().getDocument();
        if (getSecurityAgent().isReadable(d, session.getUserName(), session.getUserSource(), session.getGroups())) {
            return comment;
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getDocumentComments(org.kimios.kernel.security.Session, long)
    */
    public Vector<DocumentComment> getDocumentComments(Session session, long documentVersionUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (getSecurityAgent()
                .isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            return dmsFactoryInstantiator.getDocumentCommentFactory().getDocumentComments(documentVersionUid);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#createDocumentComment(org.kimios.kernel.security.Session, long, java.lang.String)
    */
    public long createDocumentComment(Session session, long documentVersionUid, String comment)
            throws AccessDeniedException, ConfigException, DataSourceException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (getSecurityAgent()
                .isWritable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            DocumentComment dc =
                    new DocumentComment(-1, documentVersionUid, session.getUserName(), session.getUserSource(), comment,
                            new Date());
            long idComment = dmsFactoryInstantiator.getDocumentCommentFactory().saveDocumentComment(dc);
            return idComment;
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#updateDocumentComment(org.kimios.kernel.security.Session, long, long, java.lang.String)
    */
    public void updateDocumentComment(Session session, long documentVersionUid, long commentUid, String newComment)
            throws AccessDeniedException, ConfigException, DataSourceException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        DocumentComment dc = dmsFactoryInstantiator.getDocumentCommentFactory().getDocumentComment(commentUid);
        if (getSecurityAgent()
                .isWritable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups()) &&
                session.getUserName().equals(dc.getAuthorName()) &&
                session.getUserSource().equals(dc.getAuthorSource()) ||
                getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            dc.setDate(new Date());
            dc.setComment(newComment);
            dmsFactoryInstantiator.getDocumentCommentFactory().updateDocumentComment(dc);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#deleteDocumentComment(org.kimios.kernel.security.Session, long)
    */
    public void deleteDocumentComment(Session session, long commentUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        DocumentComment comment = dmsFactoryInstantiator.getDocumentCommentFactory().getDocumentComment(commentUid);
        DocumentVersion dv =
                dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(comment.getDocumentVersionUid());
        if (getSecurityAgent()
                .isWritable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups()) &&
                session.getUserName().equals(comment.getAuthorName()) &&
                session.getUserSource().equals(comment.getAuthorSource()) ||
                getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource())) {
            dmsFactoryInstantiator.getDocumentCommentFactory().deleteDocumentComment(comment);
        } else {
            throw new AccessDeniedException();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getMetas(org.kimios.kernel.security.Session, long)
    */
    public List<Meta> getMetas(Session session, long documentTypeUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        MetaFactory mf = dmsFactoryInstantiator.getMetaFactory();
        DocumentType dt = dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(documentTypeUid);
        if (dt != null) {
            return mf.getMetas(dt);
        } else {
            return new Vector<Meta>();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getMeta(org.kimios.kernel.security.Session, long)
    */
    public Meta getMeta(Session session, long metaUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        Meta m = dmsFactoryInstantiator.getMetaFactory().getMeta(metaUid);
        return m;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getUnheritedMetas(org.kimios.kernel.security.Session, long)
    */
    public List<Meta> getUnheritedMetas(Session session, long documentTypeUid)
            throws AccessDeniedException, ConfigException, DataSourceException {
        MetaFactory mf = dmsFactoryInstantiator.getMetaFactory();
        DocumentType dt = dmsFactoryInstantiator.getDocumentTypeFactory().getDocumentType(documentTypeUid);
        if (dt != null) {
            return mf.getUnheritedMetas(dt);
        } else {
            return new ArrayList<Meta>();
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getDocumentVersion(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    @Deprecated
    public DocumentVersion getDocumentVersion(Session session, String hashMD5, String hashSHA)
            throws AccessDeniedException, ConfigException, DataSourceException {
        return null;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IDocumentVersionController#getDocumentTypeByName(org.kimios.kernel.security.Session, java.lang.String)
    */
    public DocumentType getDocumentTypeByName(Session session, String name)
            throws AccessDeniedException, ConfigException, DataSourceException {
        DocumentTypeFactory dtf = dmsFactoryInstantiator.getDocumentTypeFactory();
        return dtf.getDocumentTypeByName(name);
    }

    public void updateDocumentVersionInformation(Session session, long documentVersionUid)
            throws ConfigException, DataSourceException, AccessDeniedException,
            RepositoryException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionUid);
        if (getSecurityAgent()
                .isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            dv.updateVersionInformation();
        } else {
            throw new AccessDeniedException();
        }
    }

    public List<MetaValue> getMetaValues(Session session, long documentVersionId)
            throws ConfigException, DataSourceException, AccessDeniedException {
        DocumentVersion dv = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionId);
        if (getSecurityAgent()
                .isReadable(dv.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
            List<MetaValue> items = dmsFactoryInstantiator.getMetaValueFactory().getMetaValues(dv);
            for(MetaValue v: items){
                if(v.getMeta().getMetaType() == MetaType.LIST){
                    //read subcollection
                    for(Object u: ((List)v.getValue())){
                        logger.debug("list metavalue item: {}", u);
                    }
                }
            }
            return items;
        } else {
            throw new AccessDeniedException();
        }
    }

    public List<DocumentVersion> getOprhansDocumentVersion() {
        return FactoryInstantiator.getInstance().getDocumentVersionFactory().getVersionsToDelete();
    }
}

