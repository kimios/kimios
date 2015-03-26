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
package org.kimios.webservices.impl;

import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.Folder;
import org.kimios.kernel.dms.Workspace;
import org.kimios.kernel.log.DMEntityLog;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.Log;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.LogService;

import javax.jws.WebService;
import java.util.List;
import java.util.Vector;

@WebService(targetNamespace = "http://kimios.org", serviceName = "LogService", name = "LogService")
public class LogServiceImpl extends CoreService implements LogService
{
    public Log[] getDocumentLogs(String sessionUid, long documentUid) throws DMServiceException
    {
        try {
            Session s = getHelper().getSession(sessionUid);
            List<DMEntityLog<Document>> logs = documentController.getDocumentLog(s, documentUid);
            Log[] pojos = new Log[logs.size()];
            int i = 0;
            for (DMEntityLog<Document> l : logs) {
                pojos[i] = l.toPojo();
                i++;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public Log[] getFolderLogs(String sessionUid, long folderUid) throws DMServiceException
    {
        try {
            Session s = getHelper().getSession(sessionUid);
            Vector<DMEntityLog<Folder>> logs = folderController.getLogs(s, folderUid);
            Log[] pojos = new Log[logs.size()];
            int i = 0;
            for (DMEntityLog<Folder> l : logs) {
                pojos[i] = l.toPojo();
                i++;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public Log[] getWorkspaceLogs(String sessionUid, long workspaceUid) throws DMServiceException
    {
        try {
            Session s = getHelper().getSession(sessionUid);
            Vector<DMEntityLog<Workspace>> logs = workspaceController.getLogs(s, workspaceUid);
            Log[] pojos = new Log[logs.size()];
            int i = 0;
            for (DMEntityLog<Workspace> l : logs) {
                pojos[i] = l.toPojo();
                i++;
            }
            return pojos;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }
}

