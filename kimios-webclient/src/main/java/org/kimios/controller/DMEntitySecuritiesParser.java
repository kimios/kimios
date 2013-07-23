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
package org.kimios.controller;

import flexjson.JSONDeserializer;
import org.kimios.client.controller.DocumentVersionController;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.kernel.ws.pojo.Meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author Fabien Alin
 */
public class DMEntitySecuritiesParser {
    
    public static Vector<DMEntitySecurity> parseFromJson(String json, long entityUid, int entityType){
        JSONDeserializer<DMEntitySecurity> des = new JSONDeserializer<DMEntitySecurity>();
        Object v = des.deserialize(json);
        ArrayList<HashMap> v2 = (ArrayList<HashMap>)v;
        Vector<DMEntitySecurity> items = new Vector<DMEntitySecurity>();
        for(HashMap it: v2){
            DMEntitySecurity tmp = new DMEntitySecurity();
            tmp.setDmEntityType(entityType);
            tmp.setDmEntityUid(entityUid);
            tmp.setName((String)it.get("name"));
            tmp.setSource((String)it.get("source"));
            tmp.setRead(Boolean.parseBoolean(it.get("read").toString()));
            tmp.setWrite(Boolean.parseBoolean(it.get("write").toString()));
            tmp.setFullAccess(Boolean.parseBoolean(it.get("fullAccess").toString()));
            tmp.setType(Integer.parseInt(it.get("type").toString()));
            items.add(tmp);
        }
        return items;
    }
    
    public static Map<Meta, String> parseMetasValuesFromJson(String sessionUid,String json, DocumentVersionController versionController) throws Exception{
        JSONDeserializer<org.kimios.core.wrappers.Meta> des = new JSONDeserializer<org.kimios.core.wrappers.Meta>();
        Object v = des.deserialize(json);
        ArrayList<HashMap> v2 = (ArrayList<HashMap>)v;
        Map<Meta, String> mMetasValues = new HashMap<Meta, String>();
        for(HashMap it: v2){
            long uid = Long.parseLong(it.get("uid").toString());
            Meta meta = versionController.getMeta(sessionUid, uid);
            String mValue = "";
//            if(meta.getMetaType() == 3){
//                mValue = (it.get("value").toString().length() > 0 ? it.get("value").toString().substring(0, 10) : "");
//            }else
                mValue = it.get("value").toString();
            mMetasValues.put(meta, mValue);
        }
        return mMetasValues;
    }

}

