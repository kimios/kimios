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

package org.kimios.webservices.impl;

import org.kimios.kernel.controller.impl.ConverterController;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.ConverterService;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.ServiceHelper;

import javax.jws.WebService;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ConverterService")
public class ConverterServiceImpl implements ConverterService {


    private ConverterController convertController;
    private ServiceHelper helper;

    public ConverterServiceImpl(ConverterController controller, ServiceHelper helper) {
        this.convertController = controller;
        this.helper = helper;
    }


    public Response convertDocument(String sessionId, Long documentId, String converterImpl) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            return wrapResponse(convertController.convertDocument(session, documentId, converterImpl));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public String convertDocumentUrlOnly(String sessionId, Long documentId, String converterImpl) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            InputSource inputSource = convertController.convertDocument(session, documentId, converterImpl);
            String retUrl = inputSource.getPublicUrl();
            return helper.getResourceUrl(retUrl);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocumentVersion(String sessionId, Long versionId, String converterImpl) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            return wrapResponse(convertController.convertDocumentVersion(session, versionId, converterImpl));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocuments(String sessionId, Long[] documentIds, String converterImpl) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<Long> documents = new ArrayList<Long>();
            for (Long documentId : documentIds) {
                documents.add(documentId);
            }
            return wrapResponse(convertController.convertDocuments(session, documents, converterImpl));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocumentVersions(String sessionId, Long[] versionIds, String converterImpl) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<Long> versions = new ArrayList<Long>();
            for (Long versionId : versionIds) {
                versions.add(versionId);
            }
            return wrapResponse(convertController.convertDocumentVersions(session, versions, converterImpl));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    // private

    private Response wrapResponse(InputSource source) throws IOException {

        return Response.ok(source.getInputStream()).header(
                "Content-Disposition",
                "attachment; filename=\"" + source.getHumanName() + "\"")
                .header("Content-Type", source.getMimeType())
                .build();
    }
}
