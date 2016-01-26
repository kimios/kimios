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

package org.kimios.editors.model;

import org.kimios.editors.model.EditorData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by farf on 07/01/16.
 */
public class EtherpadEditorData extends EditorData {

    private String etherPadUrl;

    private String padId;

    private String groupId;

    @Override
    public Map<String, String> getCookiesData(String userId, String userSource) {
        EtherpadUserData userData = this.getUsersDatas().get(userId + "@" + userSource);
        HashMap<String, String> udata = new HashMap<String, String>();
        udata.put("authorID", userData.getAuthorID());
        udata.put("sessionID", userData.getSessionID());
        return udata;
    }

    public long getDocumentId() {
        return documentId;
    }

    public String getEtherPadUrl() {
        return etherPadUrl;
    }

    public void setEtherPadUrl(String etherPadUrl) {
        this.etherPadUrl = etherPadUrl;
    }

    public String getPadId() {
        return padId;
    }

    public void setPadId(String padId) {
        this.padId = padId;
    }

    public Map<String, EtherpadUserData> usersDatas = new HashMap<String, EtherpadUserData>();

    public Map<String, EtherpadUserData> getUsersDatas() {
        return usersDatas;
    }

    public void setUsersDatas(Map<String, EtherpadUserData> usersDatas) {
        this.usersDatas = usersDatas;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


}
