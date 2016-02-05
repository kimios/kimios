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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.controller;

import flexjson.JSONSerializer;
import org.kimios.core.wrappers.DocumentType;
import org.kimios.core.wrappers.Meta;
import org.kimios.kernel.share.model.MailContact;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.Share;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Farf
 */
public class ShareControllerWeb extends Controller {

    public ShareControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception{
        String jsonResp = "";
        if(action != null){
            if(action.equalsIgnoreCase("contacts")){
                jsonResp = contactsList();
            }
            if(action.equalsIgnoreCase("share")){
                shareDocuments();
            }
            if(action.equalsIgnoreCase("sharedWithMe")){
                jsonResp = entitiesSharedWithMe();
            }

            if(action.equalsIgnoreCase("ShareWith")){
                shareDocumentWith();
            }
            return jsonResp;
        }else
            return "NOACTION";

    }
    
    private String contactsList() throws Exception {
        List<MailContact> lists = shareController.listContacts(sessionUid, parameters.get("query"));
        String jsonResp = new JSONSerializer()
                .exclude("class")
                .serialize(lists);
        return jsonResp;
    }
    
     private void shareDocuments() throws Exception {
        String[] documentIds = parameters.get("documentIds").split(",");
         String[] recipients = parameters.get("recipients").split(",");

        List<Long> items = new ArrayList<Long>();
         for(String u: documentIds)
            items.add(Long.parseLong(u));

        Map<String, String> contacts = new HashMap<String, String>();
        for(String c: recipients){
             contacts.put(c, c);
        }

         shareController.sendDocuments(sessionUid,
                 items,
                 contacts,
                 parameters.get("subject"),
                 parameters.get("content"),
                 "",
                 "",
                 true
                 );
        return;
    }


    private String entitiesSharedWithMe() throws Exception {

        List<Share> shares = shareController.listDocumentSharedWithMe(getSessionUid());
        List<org.kimios.core.wrappers.DMEntity> entities = new ArrayList<org.kimios.core.wrappers.DMEntity>();
        for(Share s: shares){
            entities.add(new ShareEntity(s.getEntity(), s));
        }

        String jsonResp = new JSONSerializer()
                .exclude("class")
                .serialize(entities);
        return jsonResp;
    }


    private void shareDocumentWith() throws Exception {

        String userId = parameters.get("userId");
        String userSource = parameters.get("userSource");
        Long dmEntityId = Long.parseLong(parameters.get("dmEntityId"));

        boolean read = Boolean.parseBoolean(parameters.get("read"));
        boolean write = Boolean.parseBoolean(parameters.get("write"));
        boolean fullAccess = Boolean.parseBoolean(parameters.get("fullAccess"));
        boolean notify = Boolean.parseBoolean(parameters.get("notify"));

        Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(parameters.get("expirationDate"));

        shareController.shareDocument(getSessionUid(),
                userId,
                userSource,
                dmEntityId,
                read, write, fullAccess, parameters.get("expirationDate"), notify);


        return;
    }



    public static class ShareEntity extends org.kimios.core.wrappers.DMEntity {


        public ShareEntity(DMEntity entity, Share share) {
            super(entity);
            this.expirationDate = share.getExpirationDate();
            this.creatorId = share.getCreatorId();
            this.creatorSource = share.getCreatorSource();
        }

        public ShareEntity(long virtualEntityCount, String virtualPath, String virtualEntityName, long uid, Share share) {
            super(virtualEntityCount, virtualPath, virtualEntityName, uid);
            this.expirationDate = share.getExpirationDate();
            this.creatorId = share.getCreatorId();
            this.creatorSource = share.getCreatorSource();
        }

        private Date expirationDate;

        private String creatorId;

        private String creatorSource;

        public Date getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }

        public String getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(String creatorId) {
            this.creatorId = creatorId;
        }

        public String getCreatorSource() {
            return creatorSource;
        }

        public void setCreatorSource(String creatorSource) {
            this.creatorSource = creatorSource;
        }
    }
    
    

}

