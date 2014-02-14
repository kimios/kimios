/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

package org.kimios.core.wrappers;

import org.kimios.client.controller.DocumentVersionController;

/**
 * @author Fabien Alin
 */
public class Meta {
    private String name;
    private int type;
    private Object value;
    private long uid;
    private Long metaFeedUid;
    private boolean mandatory = false;

    private String sessionUid;

    public String getSessionUid() {
        return sessionUid;
    }

    public void setSessionUid(String sessionUid) {
        this.sessionUid = sessionUid;
    }

    public long getUid() {
        return this.uid;
    }

    public void setMetaFeedUid(Long metaFeedUid) {
        this.metaFeedUid = metaFeedUid;
    }

    public Long getMetaFeedUid() {
        return this.metaFeedUid;
    }

    public void setUid(long id) {
        this.uid = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Meta(long uid, String name, String value, int type) {
        this(uid, name, value, type, new Long(-1));
    }

    public Meta(long uid, String name, Object value, int type, Long metaFeedUid) {
        this.uid = uid;
        this.name = name;
        this.value = value;
        this.type = type;
        this.metaFeedUid = metaFeedUid;
    }

    public Meta(long uid, String name, Object value, int type, Long metaFeedUid, boolean mandatory) {
        this.uid = uid;
        this.name = name;
        this.value = value;
        this.type = type;
        this.metaFeedUid = metaFeedUid;
        this.mandatory = mandatory;
    }

    public Meta(org.kimios.kernel.ws.pojo.Meta m, long docVersion, String sessionUid, DocumentVersionController versionController) throws Exception {
        this.sessionUid = sessionUid;
        /*switch(m.getMetaType()){
            case 1:
                this.value = versionController.getMetaStringValue(sessionUid, docVersion, m.getUid());
                break;
            case 2:
                this.value = versionController.getMetaNumberValue(sessionUid, docVersion, m.getUid());
                break;
            case 3:
                Date d = versionController.getMetaDateValue(sessionUid, docVersion, m.getUid());
                this.value = (d != null ? new SimpleDateFormat("MM-dd-yyyy").format(d) : "");
                break;
            case 4:
                this.value = versionController.getMetaBooleanValue(sessionUid, docVersion, m.getUid());
                break;
        } */

        this.name = m.getName();
        this.uid = m.getUid();
        this.type = m.getMetaType();
        this.metaFeedUid = m.getMetaFeedUid();
        this.mandatory = m.isMandatory();
    }


}

