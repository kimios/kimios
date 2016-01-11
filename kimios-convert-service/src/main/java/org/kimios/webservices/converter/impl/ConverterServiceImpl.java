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

package org.kimios.webservices.converter.impl;

import org.kimios.converter.controller.IConverterController;
import org.kimios.converter.source.InputSource;
import org.kimios.kernel.security.model.Session;
import org.kimios.webservices.converter.ConverterService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.IServiceHelper;

import javax.jws.WebService;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ConverterService")
public class ConverterServiceImpl implements ConverterService {


    private IConverterController convertController;
    private IServiceHelper helper;

    public ConverterServiceImpl(IConverterController controller, IServiceHelper helper) {
        this.convertController = controller;
        this.helper = helper;
    }


    public Response convertDocument(String sessionId, Long documentId, String converterImpl, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            if (inline) {
                return wrapResponseInline(convertController.convertDocument(session, documentId, converterImpl));
            } else
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

    public Response convertDocumentVersion(String sessionId, Long versionId, String converterImpl, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            if (inline) {
                return wrapResponseInline(convertController.convertDocumentVersion(session, versionId, converterImpl));
            } else
                return wrapResponse(convertController.convertDocumentVersion(session, versionId, converterImpl));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocuments(String sessionId, Long[] documentIds, String converterImpl, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<Long> documents = new ArrayList<Long>();
            for (Long documentId : documentIds) {
                documents.add(documentId);
            }
            if (inline) {
                return wrapResponseInline(convertController.convertDocuments(session, documents, converterImpl));
            } else
                return wrapResponse(convertController.convertDocuments(session, documents, converterImpl));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocumentVersions(String sessionId, Long[] versionIds, String converterImpl, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<Long> versions = new ArrayList<Long>();
            for (Long versionId : versionIds) {
                versions.add(versionId);
            }
            if (inline) {
                return wrapResponseInline(convertController.convertDocumentVersions(session, versions, converterImpl));
            } else
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

    private Response wrapResponseInline(InputSource source) throws IOException {

        return Response.ok(source.getInputStream()).header(
                "Content-Disposition",
                "inline; filename=\"" + source.getHumanName() + "\"")
                .header("Content-Type", source.getMimeType())
                .build();
    }


    /*private String encodeFileNameInHeader(HttpServletRequest request, String docName) throws Exception {


        String userAgent = servletRequest.get("user-agent");
        boolean isInternetExplorer = (userAgent.indexOf("MSIE") > -1);

        String item = null;
        if (isInternetExplorer) {
            item = "attachment; filename=\"" + URLEncoder.encode(docName, "utf-8") + "\"";
        } else {
            item = "attachment; filename=\"" + MimeUtility.encodeWord(docName) + "\"";
        }

        return item;
    }*/

}
