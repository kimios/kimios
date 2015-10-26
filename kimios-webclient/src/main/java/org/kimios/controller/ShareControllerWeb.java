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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    

}

