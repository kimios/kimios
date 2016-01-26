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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.controller;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.kimios.editors.model.EditorData;
import org.kimios.editors.model.EtherpadEditorData;
import org.kimios.kernel.share.model.MailContact;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Farf
 */
public class EditorsControllerWeb extends Controller {


    private HttpServletRequest request;
    private HttpServletResponse response;
    public EditorsControllerWeb(Map<String, String> parameters, HttpServletRequest request, HttpServletResponse response) {
        super(parameters);
        this.request = request;
        this.response = response;
    }

    public String execute() throws Exception{
        String jsonResp = "";
        if(action != null){
            if(action.equalsIgnoreCase("StartEdit")){
                jsonResp = startEdit();
            }
            if(action.equalsIgnoreCase("StopEdit")){
                jsonResp = stopEdit();
            }
            if(action.equalsIgnoreCase("InviteUsers")){
                //inviteUsers();
            }
            if(action.equalsIgnoreCase("VersionDocument")){
                jsonResp = versionDocument();
            }


            return jsonResp;
        }else
            return "NOACTION";

    }

    private String startEdit() throws Exception {

        long documentId = Long.parseLong(parameters.get("documentId"));
        EditorData data = editorController.startEdit(sessionUid, documentId);
        if (data.getCookiesDatas() != null && data.getCookiesDatas().size() > 0) {
            for (String cName : data.getCookiesDatas().keySet()) {
                javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(cName,
                        data.getCookiesDatas().get(cName));
                cookie.setMaxAge(3600);
                cookie.setValue(data.getCookiesDatas().get(cName));
                response.addCookie(cookie);
            }
        }
        String jsonResp = new JSONSerializer()
                .exclude("class")
                .serialize(data);
        return jsonResp;
    }

    private String stopEdit() throws Exception {

        String editorData = parameters.get("editorData");
        EtherpadEditorData etherpadEditorData = new EtherpadEditorData();
        EditorData data = new JSONDeserializer<EtherpadEditorData>()
                .deserializeInto(editorData, etherpadEditorData);
        String jsonResp = new JSONSerializer()
                .exclude("class")
                .serialize(editorController.stopEdit(sessionUid, data));
        return jsonResp;
    }


    
     private String versionDocument() throws Exception {
         String editorData = parameters.get("editorData");
         EtherpadEditorData etherpadEditorData = new EtherpadEditorData();
         EditorData data = new JSONDeserializer<EtherpadEditorData>()
                 .deserializeInto(editorData, etherpadEditorData);
         String jsonResp = new JSONSerializer()
                 .exclude("class")
                 .serialize(editorController.versionDocument(sessionUid, data));
         return jsonResp;
    }
    
    

}

