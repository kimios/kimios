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

package org.kimios.webservices.share.impl;

import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IMailShareController;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.controller.IShareTransferController;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.ws.pojo.Share;
import org.kimios.webservices.FileTransferService;
import org.kimios.webservices.IServiceHelper;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.share.ShareService;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by farf on 19/07/15.
 */

@WebService(targetNamespace = "http://kimios.org", serviceName = "ShareService")
public class ShareServiceImpl implements ShareService {


    private IMailShareController mailShareController;
    private IShareController shareController;
    private IShareTransferController shareTransferController;
    private IServiceHelper helper;
    private IDocumentController documentController;
    private FileTransferService fileTransferService;

    private static String DOWNLOAD_DOCUMENT_BY_TOKEN_AND_PASSWORD_FORM_ACTION = "downloadDocumentByTokenAndPassword";

    public ShareServiceImpl(IMailShareController mailShareController,
                            IShareController shareController,
                            IDocumentController documentController,
                            IServiceHelper serviceHelper,
                            IShareTransferController shareTransferController
    ){
        this.helper = serviceHelper;
        this.mailShareController = mailShareController;
        this.shareController = shareController;
        this.documentController = documentController;
        this.shareTransferController = shareTransferController;
    }

    public FileTransferService getFileTransferService() {
        return fileTransferService;
    }

    public void setFileTransferService(FileTransferService fileTransferService) {
        this.fileTransferService = fileTransferService;
    }

    @Override
    @Deprecated
    public void shareByEmail(String sessionId,
                             List<Long> documentIds,
                             Map<String, String> recipients,
                             String subject, String content,
                             String senderAddress, String senderName,
                             Boolean defaultSender, String password,
                             String expirationDate) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<org.kimios.kernel.share.model.Share> shares = new ArrayList<>();
            for (Long docId : documentIds) {
                Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(expirationDate);
                org.kimios.kernel.share.model.Share share = mailShareController.createShare(session, docId, date);
                shares.add(share);
            }
            mailShareController.sendDocumentByEmail(session, shares, recipients, subject,
                    content, senderAddress, senderName, defaultSender, password);

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    @WebMethod(exclude = true)
    public void shareByEmailFullContact(String sessionId, List<Long> documentIds, List<MailContact> recipients, String subject,
                                        String content, String senderAddress, String senderName, Boolean defaultSender,
                                        String password, String expirationDate)
            throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);

            Map<String, String> recipientsData = new HashMap<String, String>();
            for(MailContact mc: recipients){
                recipientsData.put(mc.getEmailAddress(), mc.getFullName());
            }

            List<org.kimios.kernel.share.model.Share> shares = new ArrayList<>();
            for (Long docId : documentIds) {
                //TODO check date's format sent by other clients than included web client
                Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(expirationDate);
                org.kimios.kernel.share.model.Share share = mailShareController.createShare(session, docId, date);
                shares.add(share);
            }
            mailShareController.sendDocumentByEmail(session, shares, recipientsData, subject,
                    content, senderAddress, senderName, defaultSender, password);

        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public List<MailContact> searchContact(String sessionId, String query) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            return mailShareController.searchContact(session, query);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }


    @Override
    public void shareDocument(String sessionId,
                              long dmEntityId,
                              String userId,
                              String userSource,
                              boolean read,
                              boolean write,
                              boolean fullAccess,
                              String expirationDate,
                              boolean notify) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);


            Date date = null;
            if(expirationDate != null){
                date = new SimpleDateFormat("dd-MM-yyyy HH:mm")
                        .parse(expirationDate);
            }

            shareController.shareEntity(session,
                    dmEntityId,
                    userId,
                    userSource,
                    read,
                    write,
                    fullAccess,
                    date,
                    notify
                    );
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public List<Share> listEntitiesSharedWithMe(String sessionId) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<org.kimios.kernel.share.model.Share> shares =  shareController.listEntitiesSharedWithMe(session);
            List<Share> items = new ArrayList<Share>();
            for(org.kimios.kernel.share.model.Share s: shares){
                Share sPojo = s.toPojo();
                if(s.getEntity().getType() == 3){
                    sPojo.setEntity(documentController.getDocumentPojo((Document)s.getEntity()));
                }
                items.add(sPojo);
            }
            return items;
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public List<Share> listEntitiesSharedByMe(String sessionId) throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            List<org.kimios.kernel.share.model.Share> shares =  shareController.listEntitiesSharedByMe(session);
            List<Share> items = new ArrayList<Share>();
            for(org.kimios.kernel.share.model.Share s: shares){
                Share sPojo = s.toPojo();
                if(s.getEntity().getType() == 3){
                    sPojo.setEntity(documentController.getDocumentPojo((Document)s.getEntity()));
                }
                items.add(sPojo);
            }
            return items;
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public void removeShare(String sessionId,long shareId)
            throws DMServiceException {
        try {
            Session session = helper.getSession(sessionId);
            shareController.removeShare(session, shareId);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @Override
    public String loadDefaultTemplate(String sessionId)
            throws DMServiceException{
        try {
            Session session = helper.getSession(sessionId);
            return mailShareController.loadDefaultMailTemplate(session);
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }

    @WebMethod(exclude = true)
    public Response downloadDocumentByToken(UriInfo uriInfo, final String token, final String password) throws DMServiceException {
        try {
            return fileTransferService.downloadDocumentByToken(uriInfo, token, password);
        } catch (DMServiceException e) {
            if (e.getCode() == 15) {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return buildRequiredPasswordResponse(uriInfo, DOWNLOAD_DOCUMENT_BY_TOKEN_AND_PASSWORD_FORM_ACTION, params);
            }
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @WebMethod(exclude = true)
    public Response downloadDocumentByTokenAndPassword(UriInfo uriInfo, String token, String password) throws DMServiceException {

        try {
            return fileTransferService.downloadDocumentByToken(uriInfo, token, password);
        } catch (DMServiceException e) {
            if (e.getCode() == 15) {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return buildRequiredPasswordResponse(uriInfo, DOWNLOAD_DOCUMENT_BY_TOKEN_AND_PASSWORD_FORM_ACTION, params);
            }
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    private Response buildRequiredPasswordResponse(UriInfo uri, String methodAction, Map<String, String> hiddenParams)
            throws DMServiceException {
        Response response;

        try {
            String uriAbsPath = uri.getAbsolutePath().toString();
            String formAction = uriAbsPath.replaceFirst("/[^/]+$", "/" + methodAction);
            String form = shareTransferController.buildAskPasswordResponseHtml(formAction, hiddenParams);
            if (form == null) {
                throw new DMServiceException();
            }
            Response.ResponseBuilder responseBuilder = Response.ok(form);
            responseBuilder.header("Content-Description", "Password required");
            responseBuilder.header("Content-Type", "text/html");

            response = responseBuilder.build();
        } catch (Exception e) {
            throw helper.convertException(e);
        }

        return response;
    }
}
