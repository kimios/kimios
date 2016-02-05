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
import org.kimios.editors.model.EtherpadEditorData;
import org.kimios.kernel.security.model.Session;
import org.kimios.webservices.IServiceHelper;
import org.kimios.webservices.editors.EditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by farf on 08/01/16.
 */
public class EditorServiceImpl implements EditorService {

    private static Logger logger = LoggerFactory.getLogger(EditorServiceImpl.class);

    private ExternalEditor externalEditor;
    private IServiceHelper helper;

    public EditorServiceImpl(ExternalEditor externalEditor, IServiceHelper helper) {
        this.externalEditor = externalEditor;
        this.helper = helper;
    }


    @Context
    private javax.servlet.http.HttpServletResponse response;

    @Context
    private HttpServletRequest request;

    @Override
    public EditorData startDocumentEdit(String sessionId, long documentId) throws Exception {
        try {

            Session session = helper.getSession(sessionId);
            EditorData data = externalEditor.startDocumentEdit(session, documentId);
            //if necessary, set security information (cookies, headers ...), required by the editor!

            if (data.getCookiesData(session.getUserName(), session.getUserSource())
                    != null && data.getCookiesData(session.getUserName(), session.getUserSource()).size() > 0) {

                data.setCookieDatas(new HashMap<String, String>());
                for (String cName : data.getCookiesData(session.getUserName(), session.getUserSource()).keySet()) {
                    javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(cName,
                            data.getCookiesData(session.getUserName(), session.getUserSource()).get(cName));
                    response.addCookie(cookie);
                    data.getCookiesDatas().put(cookie.getName(), cookie.getValue());
                }
            }
            return data;
        } catch (Exception ex) {
            throw helper.convertException(ex);
        }
    }

    @Override
    public EditorData versionDocument(String sessionId, EditorData editData) throws Exception {
        try {

            Session session = helper.getSession(sessionId);
            EditorData data = externalEditor.versionDocument(session, editData);
            if (data.getCookiesData(session.getUserName(), session.getUserSource())
                    != null && data.getCookiesData(session.getUserName(), session.getUserSource()).size() > 0) {
                for (String cName : data.getCookiesData(session.getUserName(), session.getUserSource()).keySet()) {
                    javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(cName,
                            data.getCookiesData(session.getUserName(), session.getUserSource()).get(cName));
                    response.addCookie(cookie);
                }
            }
            return data;
        } catch (Exception ex) {
            throw helper.convertException(ex);
        }
    }

    @Override
    public EditorData endDocumentEdit(String sessionId, EditorData editData) throws Exception {
        try {
            return externalEditor.endDocumentEdit(helper.getSession(sessionId), editData);
        } catch (Exception ex) {
            throw helper.convertException(ex);
        }
    }
}
