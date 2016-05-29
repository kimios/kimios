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

import org.kimios.api.InputSource;
import org.kimios.converter.ConverterDescriptor;
import org.kimios.converter.controller.IConverterController;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.webservices.converter.ConverterService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.IServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ConverterService")
public class ConverterServiceImpl implements ConverterService {


    private IConverterController convertController;
    private IServiceHelper helper;
    private static Logger logger = LoggerFactory.getLogger(ConverterServiceImpl.class);

    public ConverterServiceImpl(IConverterController controller, IServiceHelper helper) {
        this.convertController = controller;
        this.helper = helper;
    }


    public Response convertDocument(String sessionId, Long documentId, String converterImpl, String outputFormat, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            if (inline) {
                return wrapResponseInline(convertController.convertDocument(session, documentId, converterImpl, outputFormat));
            } else
                return wrapResponse(convertController.convertDocument(session, documentId, converterImpl, outputFormat));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public String convertDocumentUrlOnly(String sessionId, Long documentId, String converterImpl, String outputFormat) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            InputSource inputSource = convertController.convertDocument(session, documentId, converterImpl, outputFormat);
            String retUrl = inputSource.getPublicUrl();
            return helper.getResourceUrl(retUrl);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocumentVersion(String sessionId, Long versionId, String converterImpl, String outputFormat, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            if (inline) {
                return wrapResponseInline(convertController.convertDocumentVersion(session, versionId, converterImpl, outputFormat));
            } else
                return wrapResponse(convertController.convertDocumentVersion(session, versionId, converterImpl, outputFormat));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocuments(String sessionId, Long[] documentIds, String converterImpl, String outputFormat, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<Long> documents = new ArrayList<Long>();
            for (Long documentId : documentIds) {
                documents.add(documentId);
            }
            if (inline) {
                return wrapResponseInline(convertController.convertDocuments(session, documents, converterImpl, outputFormat));
            } else
                return wrapResponse(convertController.convertDocuments(session, documents, converterImpl, outputFormat));

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    public Response convertDocumentVersions(String sessionId, Long[] versionIds, String converterImpl,
                                            String outputFormat, Boolean inline) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<Long> versions = new ArrayList<Long>();
            for (Long versionId : versionIds) {
                versions.add(versionId);
            }
            if (inline) {
                return wrapResponseInline(convertController.convertDocumentVersions(session, versions, converterImpl, outputFormat));
            } else
                return wrapResponse(convertController.convertDocumentVersions(session, versions, converterImpl, outputFormat));

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

    @WebMethod(exclude = true)
    public Response preview(String sessionId, String resourcePath) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            //TODO: should check session
            if(logger.isDebugEnabled()){
                logger.debug("trying to load resource from path: {}", resourcePath);
            }
            String temporaryRepository = ConfigurationManager.getValue(Config.DEFAULT_TEMPORARY_PATH);
            return Response.ok(new FileInputStream(temporaryRepository + resourcePath)).build();

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }


    @WebMethod(exclude = true)
    public Response previewPathSession(String idPreview, String resPath) throws DMServiceException {
        try {
            //Session session = helper.getSession(sessionId);
            //TODO: should check session
            if(logger.isDebugEnabled()){
                logger.debug("trying to load resource from path: {}", idPreview, resPath);
            }
            String temporaryRepository = ConfigurationManager.getValue(Config.DEFAULT_TEMPORARY_PATH);

            //check in cache with preview id, to get data related to document

            InputSource source = convertController.loadPreviewDataFromCache(null, idPreview);

            return Response
                    .ok(new FileInputStream(temporaryRepository + "/" + idPreview + "_dir/" + idPreview + "_img/" + resPath))
                    .header(
                            "Content-Disposition",
                            "attachment; filename=\"" + source.getHumanName() + "\"")
                    .header("Content-Type", source.getMimeType())
                    .build();

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public Map<String, List<ConverterDescriptor>> descriptors(String sessionId) throws DMServiceException {
        try{
            Session session = helper.getSession(sessionId);
            return convertController.loadDescriptors();
        }catch (Exception ex){
            throw helper.convertException(ex);
        }
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
