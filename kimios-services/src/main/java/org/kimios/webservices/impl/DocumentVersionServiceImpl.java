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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.jws.WebService;

import org.kimios.kernel.dms.MetaValue;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.DocumentComment;
import org.kimios.kernel.ws.pojo.DocumentVersion;
import org.kimios.kernel.ws.pojo.Meta;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.DocumentVersionService;

@WebService(targetNamespace = "http://kimios.org", serviceName = "DocumentVersionService", name = "DocumentVersionService")
public class DocumentVersionServiceImpl extends CoreService implements DocumentVersionService
{
    /**
     * @param sessionId
     * @param documentVersionId
     * @return
     * @throws org.kimios.webservices.DMServiceException
     *
     */
    public DocumentVersion getDocumentVersion(String sessionId,
            long documentVersionId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            DocumentVersion dv = documentVersionController.getDocumentVersion(
                    session, documentVersionId).toPojo();

            return dv;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public DocumentVersion getLastDocumentVersion(String sessionId,
            long documentId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            DocumentVersion dv = documentVersionController
                    .getLastDocumentVersion(session, documentId).toPojo();

            return dv;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public DocumentVersion[] getDocumentVersions(String sessionId,
            long documentId) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionId);

            Vector<org.kimios.kernel.dms.DocumentVersion> docVersions = documentVersionController
                    .getDocumentVersions(session, documentId);
            int i = 0;
            DocumentVersion[] pojos = new DocumentVersion[docVersions.size()];
            for (org.kimios.kernel.dms.DocumentVersion dv : docVersions) {
                pojos[i] = dv.toPojo();
                i++;
            }

            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentId
     * @return
     * @throws DMServiceException
     */
    public long createDocumentVersion(String sessionUid, long documentId)
            throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            long uid = documentVersionController.createDocumentVersion(session,
                    documentId);

            return uid;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentUid
     * @return
     * @throws DMServiceException
     */
    public long createDocumentVersionFromLatest(String sessionUid,
            long documentUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            long uid = documentVersionController
                    .createDocumentVersionFromLatest(session, documentUid);

            return uid;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionId
     * @param documentId
     * @param documentTypeId
     * @throws DMServiceException
     */
    public void updateDocumentVersion(String sessionId, long documentId,
            long documentTypeId, String xmlStream) throws DMServiceException
    {
        try {
            Session session = getHelper().getSession(sessionId);
            documentVersionController.updateDocumentVersion(session,
                    documentId, documentTypeId, xmlStream);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param metaUid
     * @return
     * @throws DMServiceException
     */
    public String getMetaString(String sessionUid, long documentVersionUid,
            long metaUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Object o = documentVersionController.getMetaValue(session,
                    documentVersionUid, metaUid);

            if (o != null) {
                return (String) o;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param metaUid
     * @return
     * @throws DMServiceException
     */
    public double getMetaNumber(String sessionUid, long documentVersionUid,
            long metaUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Object o = documentVersionController.getMetaValue(session,
                    documentVersionUid, metaUid);

            if (o != null) {
                return (Double) o;
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param metaUid
     * @return
     * @throws DMServiceException
     */
    public Date getMetaDate(String sessionUid, long documentVersionUid,
            long metaUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);
            Object o = documentVersionController.getMetaValue(session,
                    documentVersionUid, metaUid);
            if (o != null) {
                return (Date) o;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param metaUid
     * @return
     * @throws DMServiceException
     */
    public boolean getMetaBoolean(String sessionUid, long documentVersionUid,
            long metaUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Object o = documentVersionController.getMetaValue(session,
                    documentVersionUid, metaUid);

            if (o != null) {
                return (Boolean) o;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentTypeUid
     * @return
     * @throws DMServiceException
     */
    public Meta[] getMetas(String sessionUid, long documentTypeUid)
            throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Vector<org.kimios.kernel.dms.Meta> vMetas = documentVersionController
                    .getMetas(session, documentTypeUid);
            Meta[] pojos = new Meta[vMetas.size()];
            int i = 0;
            for (org.kimios.kernel.dms.Meta m : vMetas) {
                pojos[i] = m.toPojo();
                i++;
            }

            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentTypeUid
     * @return
     * @throws DMServiceException
     */
    public Meta[] getUnheritedMetas(String sessionUid, long documentTypeUid)
            throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Vector<org.kimios.kernel.dms.Meta> vMetas = documentVersionController
                    .getUnheritedMetas(session, documentTypeUid);
            Meta[] pojos = new Meta[vMetas.size()];
            int i = 0;
            for (org.kimios.kernel.dms.Meta m : vMetas) {
                pojos[i] = m.toPojo();
                i++;
            }

            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param metaUid
     * @return
     * @throws DMServiceException
     */
    public Meta getMeta(String sessionUid, long metaUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Meta meta = documentVersionController.getMeta(session, metaUid)
                    .toPojo();

            return meta;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param xmlStream
     * @throws DMServiceException
     */
    public void updateMetas(String sessionUid, long documentVersionUid,
            String xmlStream) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            documentVersionController.updateMetasValue(session,
                    documentVersionUid, xmlStream);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param comment
     * @throws DMServiceException
     */
    public void addDocumentComment(String sessionUid, long documentVersionUid,
            String comment) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            documentVersionController.createDocumentComment(session,
                    documentVersionUid, comment);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param commentUid
     * @throws DMServiceException
     */
    public void removeDocumentComment(String sessionUid, long commentUid)
            throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            documentVersionController
                    .deleteDocumentComment(session, commentUid);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @param commentUid
     * @param newComment
     * @throws DMServiceException
     */
    public void updateDocumentComment(String sessionUid,
            long documentVersionUid, long commentUid, String newComment)
            throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            documentVersionController.updateDocumentComment(session,
                    documentVersionUid, commentUid, newComment);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param commentUid
     * @return
     * @throws DMServiceException
     */
    public DocumentComment getDocumentComment(String sessionUid, long commentUid)
            throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            DocumentComment comment = documentVersionController
                    .getDocumentComment(session, commentUid).toPojo();

            return comment;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param documentVersionUid
     * @return
     * @throws DMServiceException
     */
    public DocumentComment[] getDocumentComments(String sessionUid,
            long documentVersionUid) throws DMServiceException
    {

        try {

            Session session = getHelper().getSession(sessionUid);

            Vector<org.kimios.kernel.dms.DocumentComment> comments = documentVersionController
                    .getDocumentComments(session, documentVersionUid);
            int i = 0;
            DocumentComment[] pojos = new DocumentComment[comments.size()];
            for (org.kimios.kernel.dms.DocumentComment dc : comments) {
                pojos[i] = dc.toPojo();
                i++;
            }

            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public List<org.kimios.kernel.ws.pojo.MetaValue> getMetaValues(String sessionId,
            long documentVersionId) throws DMServiceException
    {
        try {

            Session session = getHelper().getSession(sessionId);

            List<MetaValue> values = documentVersionController
                    .getMetaValues(session, documentVersionId);

            List<org.kimios.kernel.ws.pojo.MetaValue> pojos = new ArrayList<org.kimios.kernel.ws.pojo.MetaValue>();
            for (MetaValue mv : values) {
                org.kimios.kernel.ws.pojo.MetaValue pojo = new org.kimios.kernel.ws.pojo.MetaValue();
                pojo.setDocumentVersionId(mv.getDocumentVersionUid());
                pojo.setMeta(mv.getMeta().toPojo());
                pojo.setMetaId(mv.getMetaUid());
                pojo.setValue(mv.getValue());

                pojos.add(pojo);
            }

            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }
}

