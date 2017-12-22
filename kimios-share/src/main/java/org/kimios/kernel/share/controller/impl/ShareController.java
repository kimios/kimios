/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.kernel.share.controller.impl;

import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.kernel.dms.model.Document;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.filetransfer.model.DataTransferStatus;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.share.controller.IMailShareController;
import org.kimios.kernel.share.controller.IShareController;
import org.kimios.kernel.share.factory.ShareFactory;
import org.kimios.kernel.share.model.Share;
import org.kimios.kernel.share.model.ShareStatus;
import org.kimios.kernel.share.model.ShareType;
import org.kimios.kernel.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by farf on 15/02/16.
 */
@Transactional
public class ShareController extends AKimiosController implements IShareController {

    private ShareFactory shareFactory;

    private ISecurityController securityController;

    private IMailShareController mailShareController;



    public ShareController(ShareFactory shareFactory,
                           ISecurityController securityController,
                           IMailShareController mailShareController){
        this.shareFactory = shareFactory;
        this.securityController = securityController;
        this.mailShareController = mailShareController;
    }


    @Override
    public List<Share> listEntitiesSharedByMe(Session session) throws Exception {
        return  shareFactory.listEntitiesSharedBy(session.getUserName(), session.getUserSource(),
                ShareStatus.ACTIVE, ShareStatus.DISABLED, ShareStatus.EXPIRED);
    }

    @Override
    public List<Share> listEntitiesSharedWithMe(Session session) throws Exception {
        return  shareFactory.listEntitiesSharedWith(session.getUserName(), session.getUserSource(),
                ShareStatus.ACTIVE);
    }

    @Override
    public void removeShare(Session session, long shareId) throws Exception {
        Share share = shareFactory.findById(shareId);
        if(share != null && ((share.getCreatorId().equals(session.getUserName())
                && share.getCreatorSource().equals(session.getUserSource())) ||
                getSecurityAgent().isAdmin(session.getUserName(), session.getUserSource()))
                ){
            //remove share
            shareFactory.removeShare(share);
        } else
            throw new AccessDeniedException();
    }

    @Override
    public Share shareEntity(Session session,
                             long dmEntityId,
                             String sharedToUserId,
                             String sharedToUserSource,
                             boolean read, boolean write, boolean fullAcces,
                             Date expirationDate, boolean notify) throws Exception {

        DMEntity entity = dmsFactoryInstantiator.getDmEntityFactory().getEntity(dmEntityId);
        if(getSecurityAgent().isFullAccess(entity, session.getUserName(), session.getUserSource(), session.getGroups())){

            if(entity instanceof Document){
                //share item
                Share s = new Share();
                s.setCreatorId(session.getUserName());
                s.setCreatorSource(session.getUserSource());
                s.setCreationDate(new Date());
                s.setUpdateDate(s.getCreationDate());


                s.setRead(read);
                s.setWrite(write);
                s.setFullAccess(fullAcces);
                s.setNotify(notify);
                s.setTargetUserId(sharedToUserId);
                s.setTargetUserSource(sharedToUserSource);

                s.setShareStatus(ShareStatus.ACTIVE);
                s.setType(ShareType.SYSTEM);
                s.setExpirationDate(expirationDate);

                s.setEntity((DMEntityImpl) entity);

                s = shareFactory.saveShare(s);

                // set rights on entity, and force reindex !!!
                securityController.simpleSecurityAdd(session, entity.getUid(), sharedToUserId, sharedToUserSource,
                        read, write, fullAcces);

                //if notify : should add message on queue, to send on transaction commit !
                if(notify){

                    //load share target user
                    User recipient = authFactoryInstantiator
                            .getAuthenticationSourceFactory()
                            .getAuthenticationSource(sharedToUserSource)
                            .getUserFactory()
                            .getUser(sharedToUserId);
                    List<Long> documentIds = new ArrayList<Long>();
                    documentIds.add(s.getEntity().getUid());
                    Map<String, String> recipients = new HashMap<String, String>();
                    recipients.put(recipient.getMail(), recipient.getFirstName() + " "
                        + recipient.getLastName());
                    //TODO: translate Mail Subject
                    //TODO: handle external subject submission

                    List<Share> shares = new ArrayList<>();
                    shares.add(s);
                    mailShareController.sendDocumentByEmail(session,
                            shares, recipients, "Kimios Internal Share",
                            "<html><body>__DOCUMENTSLINKS__</body></html>", null, null,
                            true, null);
                }

                return s;

            } else
                throw new AccessDeniedException();
        }
        else
            throw new AccessDeniedException();

    }

    public Integer disableExpiredShares(Session session) throws Exception {
        List<Share> shares = shareFactory.listExpiredShares();
        Integer i = 0;
        for(Share s: shares) {
            s.setShareStatus(ShareStatus.EXPIRED);
            for (DataTransfer dataTransfer: s.getDataTransferSet()) {
                dataTransfer.setStatus(DataTransferStatus.EXPIRED);
            }
            shareFactory.saveShare(s);
            i++;
        }

        return i;
    }

}
