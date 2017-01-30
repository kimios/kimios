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
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.exceptions.ReportingException;
import org.kimios.kernel.reporting.model.Report;
import org.kimios.kernel.reporting.model.ReportParam;
import org.kimios.kernel.security.model.Session;

import java.util.List;
import java.util.Map;

public interface IReportingController
{
    /**
     * Return specific report from given XML parameters.
     */
    public String getReport(Session session, String className,
            String xmlParameters) throws AccessDeniedException,
            ReportingException, ConfigException, DataSourceException;


    /**
     * Return specific report from given list of parameters.
     */
    public String getReport(Session session, String className,
                            Map<String, ReportParam> reportParameters) throws AccessDeniedException,
            ReportingException, ConfigException, DataSourceException;

    /**
     * Return XML stream containing all implemented reports.
     */
    @Deprecated
    public String getReportsListXml(Session session) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Return XML stream containing all implemented reports.
     */
    public List<Report> getReportsList(Session session) throws AccessDeniedException,
            ConfigException, DataSourceException;

    /**
     * Return XML stream containing all attributes from a specified report by a class name.
     */
    @Deprecated
    public String getReportAttributesXml(Session session, String className)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ConfigException, DataSourceException,
            AccessDeniedException, ReportingException;


    /**
     * Return List containing all attributes from a specified report by a class name.
     */
    public List<ReportParam> getReportAttributes(Session session, String className)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ConfigException, DataSourceException,
            AccessDeniedException, ReportingException;

    /**
     * Clear uncompleted transactions.
     */
    public void removeGhostTransaction(Session session, long transactionUid)
            throws ConfigException, DataSourceException, AccessDeniedException;
}
