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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.controller;

import flexjson.JSONSerializer;

import org.kimios.core.wrappers.DocumentType;
import org.kimios.core.wrappers.Meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Farf
 */
public class MetaControllerWeb extends Controller {

    public MetaControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception{
        String jsonResp = "";
        if(action != null){
            if(action.equalsIgnoreCase("types")){
                jsonResp = documentTypesList();
            }
            if(action.equalsIgnoreCase("metas")){
                jsonResp = metasList();
            }

            return jsonResp;
        }else
            return "NOACTION";

    }
    
    private String documentTypesList() throws Exception {
        org.kimios.kernel.ws.pojo.DocumentType[] list = studioController.getDocumentTypes(sessionUid);
        List<DocumentType> items = new ArrayList<DocumentType>();
        for(org.kimios.kernel.ws.pojo.DocumentType type: list){
            items.add(new DocumentType(type.getUid(), type.getName()));
        }
        String jsonResp = new JSONSerializer()
                .exclude("class")
                .serialize(items);
        return jsonResp;
    }
    
     private String metasList() throws Exception {
        long typeUid = Long.parseLong(parameters.get("documentTypeUid"));
        org.kimios.kernel.ws.pojo.Meta[] list = documentVersionController.getMetas(sessionUid, typeUid);
        List<Meta> items = new ArrayList<Meta>();
        for(org.kimios.kernel.ws.pojo.Meta type: list){
            items.add(new Meta(type.getUid(), type.getName(), null, type.getMetaType(), type.getMetaFeedUid(), type.isMandatory()));
        }
        String jsonResp = new JSONSerializer()
                .exclude("class")
                .serialize(items);
        return jsonResp;
    }
    
    

}

