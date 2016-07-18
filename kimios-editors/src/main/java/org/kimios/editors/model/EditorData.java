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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by farf on 08/01/16.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "classType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EtherpadEditorData.class, name = "etherpad")
})
public class EditorData {


    protected long documentId;

    protected long createdVersionId;

    protected String userId;

    protected String userSource;

    private String proxyName;

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    private HashMap<String, String> cookiesDatas;

    public HashMap<String, String> getCookiesDatas() {
        return cookiesDatas;
    }

    public void setCookieDatas(HashMap<String, String> cookieDatas) {
        this.cookiesDatas = cookieDatas;
    }

    //Should be overrided by impl to handle specific security logic
    public Map<String, String> getCookiesData(String userId, String userSource) {
        return new HashMap<String, String>();
    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    public long getCreatedVersionId() {
        return createdVersionId;
    }

    public void setCreatedVersionId(long createdVersionId) {
        this.createdVersionId = createdVersionId;
    }
}
