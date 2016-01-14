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

package org.kimios.client.controller;

import org.kimios.client.exception.ExceptionHelper;
import org.kimios.editors.model.EditorData;
import org.kimios.webservices.editors.EditorService;

public class EditorController {

    private EditorService client;

    public EditorService getClient() {
        return client;
    }

    public void setClient(EditorService client) {
        this.client = client;
    }

    public org.kimios.editors.model.EditorData startEdit(String sessionId, long documentId) throws
            Exception {
        try {
            return client.startDocumentEdit(sessionId, documentId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public org.kimios.editors.model.EditorData stopEdit(String sessionId, EditorData editorData) throws
            Exception {
        try {
            return client.endDocumentEdit(sessionId, editorData);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public org.kimios.editors.model.EditorData versionDocument(String sessionId, EditorData editorData) throws
            Exception {
        try {
            return client.versionDocument(sessionId, editorData);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

}
