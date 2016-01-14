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

import java.util.List;
import java.util.Map;

/**
 * Created by farf on 07/01/16.
 */
public class EtherpadEditorData extends EditorData {

    private String etherPadUrl;

    private String padId;

    private String userMapping;

    private List<String> usersLinkedTopad;

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

    public String getUserMapping() {
        return userMapping;
    }

    public void setUserMapping(String userMapping) {
        this.userMapping = userMapping;
    }

    public List<String> getUsersLinkedTopad() {
        return usersLinkedTopad;
    }

    public void setUsersLinkedTopad(List<String> usersLinkedTopad) {
        this.usersLinkedTopad = usersLinkedTopad;
    }
}
