/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.webservices;

import java.io.FileNotFoundException;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.exception.MetaFeedSearchException;
import org.kimios.kernel.exception.MetaValueTypeException;
import org.kimios.kernel.exception.NamingException;
import org.kimios.kernel.exception.PathException;
import org.kimios.kernel.exception.RepositoryException;
import org.kimios.kernel.exception.TransferIntegrityException;
import org.kimios.kernel.exception.WorkflowException;
import org.kimios.kernel.exception.XMLException;
import org.kimios.kernel.security.ISessionManager;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.security.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceHelper
{
    final Logger log = LoggerFactory.getLogger("kimios");

    private ISessionManager sessionManager;

    public ISessionManager getSessionManager()
    {
        return sessionManager;
    }

    public void setSessionManager(ISessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    public Session getSession(String sessionUid) throws Exception
    {
        try {
            Session session = sessionManager.getSession(sessionUid);
            if (session == null) {
                throw new Exception("Error 01 : Invalid session");
            } else {
                return session;
            }
        } catch (Exception e) {
            throw this.convertException(e);
        }
    }

    public DMServiceException convertException(Exception e)
    {
        if (log.isDebugEnabled()) {
            log.debug("", e);
        } else {
            if (!(e.getMessage() != null && e.getMessage().equalsIgnoreCase("Error 01 : Invalid session")) &&
                    !(e instanceof AccessDeniedException))
            {
                log.error("", e);
            }
        }

        if (e instanceof ConfigException) {
            return new DMServiceException(2, "Error 02 : Config exception", e);
        }

        if (e instanceof DataSourceException && !((DataSourceException) e).isIntegrityException()) {
            return new DMServiceException(3, "Error 03 : Data source exception", e);
        }

        if (e instanceof DataSourceException && ((DataSourceException) e).isIntegrityException()) {
            return new DMServiceException(15, "Error 15: Integrity exception", e);
        }

        if (e instanceof AccessDeniedException) {
            return new DMServiceException(4, "Error 04 : Access denied exception", e);
        }

        if (e instanceof RepositoryException) {
            return new DMServiceException(5, "Error 05 : Repository exception", e);
        }

        if (e instanceof XMLException) {
            return new DMServiceException(6, "Error 06 : Xml exception", e);
        }

        if (e instanceof FileNotFoundException) {
            return new DMServiceException(7, "Error 07 : Transfer exception", e);
        }

        if (e instanceof IndexException) {
            return new DMServiceException(8, "Error 08 : Index exception", e);
        }

        if (e instanceof TransferIntegrityException) {
            return new DMServiceException(9, "Error 09 : Transfer Integrity exception", e);
        }

        if (e instanceof MetaValueTypeException) {
            return new DMServiceException(10, "Error 10 : MetaValue Type Exception", e);
        }

        if (e instanceof WorkflowException) {
            return new DMServiceException(11, "Error 11 : Workflow Exception", e);
        }

        if (e instanceof PathException) {
            return new DMServiceException(12, "Error 12 : Path Exception : " + e.toString(), e);
        }

        if (e instanceof NamingException) {
            return new DMServiceException(13, "Error 13 : Naming Exception : " + e.toString(), e);
        }

        if (e instanceof MetaFeedSearchException) {
            return new DMServiceException(14, "Error 14 : MetaFeed Search Exception : " + e.toString(), e);
        }

        if (e instanceof Exception) {
            return new DMServiceException(0, e.getMessage(), e);
        }

        return new DMServiceException(0, "Error 00 : Exception occured", e.getCause());
    }
}

