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
package org.kimios.webservices;

import org.kimios.exceptions.*;
import org.kimios.kernel.security.ISessionManager;
import org.kimios.kernel.security.model.Session;
import org.kimios.webservices.exceptions.DMServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

public class ServiceHelper implements IServiceHelper {
    final Logger log = LoggerFactory.getLogger("kimios");

    private ISessionManager sessionManager;

    private String publicBaseUrl = "http://localhost:8080/kimios";

    @Override
    public ISessionManager getSessionManager()
    {
        return sessionManager;
    }

    public void setSessionManager(ISessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    @Override
    public Session getSession(String sessionUid) throws Exception
    {
        try {
            Session session = sessionManager.getSession(sessionUid);
            log.debug("no exception, but session is empty with {}", sessionUid);
            if (session == null) {
                throw new Exception("Error 01 : Invalid session");
            } else {
                return session;
            }
        } catch (Exception e) {
            throw this.convertException(e);
        }
    }

    @Override
    public DMServiceException convertException(Exception e)
    {
        if (log.isDebugEnabled()) {
            log.debug("", e);
            log.debug("==================== CAUSED BY =================");
            Throwable cause = e.getCause();
            while (cause != null){
                log.debug(cause.getMessage(), cause);
                cause = cause.getCause();
            }
        } else {
            if (!(e.getMessage() != null && e.getMessage().equalsIgnoreCase("Error 01 : Invalid session")) &&
                    !(e instanceof AccessDeniedException))
            {
                log.error("==================== CAUSED BY =================");
                Throwable cause = e.getCause();
                while (cause != null){
                    log.error(cause.getMessage(), cause);
                    cause = cause.getCause();
                }
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

        if (e instanceof RequiredPasswordException) {
            return new DMServiceException(15, "Error 15 : Required Password Exception : " + e.toString(), e);
        }

        if (e instanceof DateExpiredException) {
            return new DMServiceException(16, "Error 16 : Date Expired Exception : " + e.toString(), e);
        }

        if (e instanceof DeleteDocumentWithActiveShareException) {
            return new DMServiceException(17, "Error 17 : " + e.toString(), e);
        }

        if (e instanceof Exception) {
            return new DMServiceException(0, e.getMessage(), e);
        }

        return new DMServiceException(0, "Error 00 : Exception occured", e.getCause());
    }

    @Override
    public String getResourceUrl(String targetUrl){
        //Append public Kimios Service URL Datas
        String item = publicBaseUrl + targetUrl;
        return item;
    }
}

