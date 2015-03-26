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
package org.kimios.kernel.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.ReportingException;
import org.kimios.kernel.security.Session;

public interface IReportingController
{
    /**
     * Return specific report from given XML parameters.
     */
    public String getReport(Session session, String className,
            String xmlParameters) throws AccessDeniedException,
            ReportingException, ConfigException, DataSourceException;

    /**
     * Return XML stream containing all implemented reports.
     */
    public String getReportsList(Session session) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Return XML stream containing all attributes from a specified report by a class name.
     */
    public String getReportAttributes(Session session, String className)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ConfigException, DataSourceException,
            AccessDeniedException, ReportingException;

    /**
     * Clear uncompleted transactions.
     */
    public void removeGhostTransaction(Session session, long transactionUid)
            throws ConfigException, DataSourceException, AccessDeniedException;
}
