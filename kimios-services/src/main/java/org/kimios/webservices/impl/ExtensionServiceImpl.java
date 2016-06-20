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
package org.kimios.webservices.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.kimios.kernel.dms.model.DMEntityAttribute;
import org.kimios.kernel.dms.MetaProcessor;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.MetaValue;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.ExtensionService;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ExtensionService", name = "ExtensionService")
public class ExtensionServiceImpl extends CoreService implements ExtensionService {
    public String getEntityAttributeValue(String sessionId, long dmEntityId, String attributeName)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return extensionController.getAttributeValue(session, dmEntityId, attributeName);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.DMEntityAttribute getEntityAttribute(String sessionId, long dmEntityId,
                                                                          String attributeName) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return extensionController.getAttribute(session, dmEntityId, attributeName).toPojo();
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.DMEntityAttribute[] getEntityAttributes(String sessionId, long dmEntityId,
                                                                             String attributeName) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<DMEntityAttribute> items = extensionController.getAttributes(session, dmEntityId);
            org.kimios.kernel.ws.pojo.DMEntityAttribute[] arr =
                    new org.kimios.kernel.ws.pojo.DMEntityAttribute[items.size()];
            int idx = 0;
            for (DMEntityAttribute it : items) {
                arr[idx] = it.toPojo();
            }
            return arr;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public void setEntityAttribute(String sessionId, long dmEntityId, String attributeName, String attributeValue,
                                   boolean isIndexed) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            extensionController.setAttribute(session, dmEntityId, attributeName, attributeValue, isIndexed);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public String generatePasswordForUser(String sessionId, String userId, String userSource, boolean sendMail)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return extensionController.generatePasswordForUser(session, userId, userSource, sendMail);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public void trashEntity(String sessionId, long dmEntityId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            extensionController.trashEntity(session, dmEntityId);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public List<DMEntity> viewTrash(String sessionId, Integer start, Integer count) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<org.kimios.kernel.dms.model.DMEntity> items = extensionController.viewTrash(session, start, count);
            List<? extends DMEntity> toReturn = documentController.convertEntitiesToPojos(session, (List)items);
            return (List)toReturn;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public DMEntity restoreFromTrash(String sessionId, Long dmEntityId) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            org.kimios.kernel.dms.model.DMEntity d = extensionController.restoreEntity(session, dmEntityId);

            List<org.kimios.kernel.dms.model.DMEntity> items =
                    new ArrayList<org.kimios.kernel.dms.model.DMEntity>();
            items.add(d);
            return documentController.convertEntitiesToPojos(session, items).get(0);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    @Override
    public long saveVirtualFolder(String sessionId, Long id, String folderName, Long parentId,
                                  boolean isSecurityInherited, Long documentTypeId, String metaItemsJsonString)
            throws DMServiceException {

        try {
            Session session = getHelper().getSession(sessionId);
            List<MetaValue> metaValues = null;
            try {
                metaValues =
                        new ObjectMapper().readValue(metaItemsJsonString, new TypeReference<List<MetaValue>>() {
                        });
            } catch (Exception ex) {

            }
            if(metaValues == null){
                //try xml format
                metaValues = MetaProcessor.getMetaValuesFromXML(metaItemsJsonString);
            }
            return folderController.createVirtualFolder(session, id, folderName, parentId, metaValues);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }


    @Override
    public boolean canHandleAutomaticPathDeposit(String sessionId)
            throws DMServiceException{
        try {
            Session session = getHelper().getSession(sessionId);
            return extensionController.canHandleAutomaticDocumentDeposit(session);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }


    @Override
    public List<String> listExtensions(String sessionId, String extensionType) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return extensionController.listExtensions(session, extensionType);
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }
}

