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
package org.kimios.client.controller;

import org.kimios.client.exception.ExceptionHelper;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.DMEntityAttribute;
import org.kimios.webservices.ExtensionService;

import java.util.List;

public class ExtensionController {

    private ExtensionService client;

    public ExtensionService getClient() {
        return client;
    }

    public void setClient(ExtensionService client) {
        this.client = client;
    }

    public DMEntityAttribute getDMEntityAttribute(String sessionId, long dmEntityId, String dmEntityAttributeName)
            throws Exception {
        try {
            return client.getEntityAttribute(sessionId, dmEntityId, dmEntityAttributeName);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    // TODO: Create others Attribute functions Call

    public void setDMEntityAttribute(String sessionId, long dmEntityId, String dmEntityAttributeName, String dmEntityAttributeValue, boolean isIndexed)
            throws Exception {
        try {
            client.setEntityAttribute(sessionId, dmEntityId, dmEntityAttributeName, dmEntityAttributeValue, isIndexed);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }



    public void addEntityToTrash(String sessionId, long documentId, boolean force)
        throws Exception {
        try {
            client.trashEntityForce(sessionId, documentId, force);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public void restoreEntityFromTrash(String sessionId, long documentId)
            throws Exception {
        try {
            client.restoreFromTrash(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public List<DMEntity> viewTrash(String sessionId, int start, int count)
            throws Exception {
        try {
            return client.viewTrash(sessionId, start, count);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }
}
