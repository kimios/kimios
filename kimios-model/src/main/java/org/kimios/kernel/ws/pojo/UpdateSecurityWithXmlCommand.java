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

package org.kimios.kernel.ws.pojo;

import java.util.List;

/**
 * Created by farf on 27/01/16.
 */
public class UpdateSecurityWithXmlCommand {


    private String sessionId;
    private long dmEntityId;
    private boolean isRecursive;
    private boolean appendMode;
    private String xmlStream;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getDmEntityId() {
        return dmEntityId;
    }

    public void setDmEntityId(long dmEntityId) {
        this.dmEntityId = dmEntityId;
    }

    public boolean isRecursive() {
        return isRecursive;
    }

    public void setRecursive(boolean recursive) {
        isRecursive = recursive;
    }

    public boolean isAppendMode() {
        return appendMode;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public String getXmlStream() {
        return xmlStream;
    }

    public void setXmlStream(String xmlStream) {
        this.xmlStream = xmlStream;
    }
}
