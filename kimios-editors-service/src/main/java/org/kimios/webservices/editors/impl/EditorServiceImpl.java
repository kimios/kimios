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

package org.kimios.webservices.editors.impl;

import org.kimios.editors.model.EditorData;
import org.kimios.editors.ExternalEditor;
import org.kimios.webservices.IServiceHelper;
import org.kimios.webservices.editors.EditorService;

/**
 * Created by farf on 08/01/16.
 */
public class EditorServiceImpl implements EditorService {

    private ExternalEditor externalEditor;
    private IServiceHelper helper;

    public EditorServiceImpl(ExternalEditor externalEditor, IServiceHelper helper) {
        this.externalEditor = externalEditor;
        this.helper = helper;
    }

    @Override
    public EditorData startDocumentEdit(String sessionId, long documentId) throws Exception {
        try{
           return externalEditor.startDocumentEdit(helper.getSession(sessionId), documentId);
        } catch (Exception ex){
            throw helper.convertException(ex);
        }
    }

    @Override
    public EditorData versionDocument(String sessionId, EditorData editData) throws Exception {
        try{

            return externalEditor.versionDocument(helper.getSession(sessionId), editData);
        } catch (Exception ex){
            throw helper.convertException(ex);
        }
    }

    @Override
    public EditorData endDocumentEdit(String sessionId, EditorData editData) throws Exception {
        try{
            return externalEditor.endDocumentEdit(helper.getSession(sessionId), editData);
        } catch (Exception ex){
            throw helper.convertException(ex);
        }
    }
}
