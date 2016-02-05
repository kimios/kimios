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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IReportingController;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.exception.ReportingException;
import org.kimios.kernel.reporting.ReportImpl;
import org.kimios.kernel.reporting.XMLReportHelper;
import org.kimios.kernel.reporting.impl.factory.DocumentTransactionsReportFactory;
import org.kimios.kernel.reporting.model.Report;
import org.kimios.kernel.reporting.model.ReportParam;
import org.kimios.kernel.security.model.Role;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.extension.ExtensionRegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Transactional
public class ReportingController extends AKimiosController implements IReportingController
{
    private XMLReportHelper xmlHelper = new XMLReportHelper();

    private static Logger logger = LoggerFactory.getLogger(ReportingController.class);

    private DocumentTransactionsReportFactory documentTransactionsReportFactory;

    public DocumentTransactionsReportFactory getDocumentTransactionsReportFactory()
    {
        return documentTransactionsReportFactory;
    }

    public void setDocumentTransactionsReportFactory(
            DocumentTransactionsReportFactory documentTransactionsReportFactory)
    {
        this.documentTransactionsReportFactory = documentTransactionsReportFactory;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IReportingController#getReport(org.kimios.kernel.security.Session, java.lang.String, java.lang.String)
    */
    public String getReport(Session session, String className, String xmlParameters)
            throws AccessDeniedException, ReportingException, ConfigException,
            DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        return new XMLReportHelper().getReport(session.getUid(), className, xmlParameters);
    }

    public String getReport(Session session, String className, Map<String, ReportParam> reportParameters)
            throws AccessDeniedException, ReportingException, ConfigException,
            DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        return new XMLReportHelper().getReport(session.getUid(), className, reportParameters);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IReportingController#getReportsListXml(org.kimios.kernel.security.Session)
    */
    public String getReportsListXml(Session session) throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        return xmlHelper.getReportsList();
    }

    /* (non-Javadoc)
   * @see org.kimios.kernel.controller.impl.IReportingController#getReportsList(org.kimios.kernel.security.Session)
   */
    public List<Report> getReportsList(Session session) throws AccessDeniedException, ConfigException, DataSourceException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        Collection<Class<? extends ReportImpl>> classes = ExtensionRegistryManager.itemsAsClass(ReportImpl.class);
        logger.info("loaded reporting registry {}", classes);
        List<Report> items = new ArrayList<Report>();
        for(Class c: classes){
            Report r = new Report();
            r.setName(c.getSimpleName());
            r.setClassName(c.getName());
            items.add(r);
        }
        return items;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IReportingController#getReportAttributes(org.kimios.kernel.security.Session, java.lang.String)
    */
    @Deprecated
    public String getReportAttributesXml(Session session, String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            ConfigException, DataSourceException, AccessDeniedException, ReportingException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        return xmlHelper.getReportAttributesXml(className);
    }

    public List<ReportParam> getReportAttributes(Session session, String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            ConfigException, DataSourceException, AccessDeniedException, ReportingException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        return xmlHelper.getReportAttributes(className);
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IReportingController#removeGhostTransaction(org.kimios.kernel.security.Session, long)
    */
    public void removeGhostTransaction(Session session, long transactionUid)
            throws ConfigException, DataSourceException, AccessDeniedException
    {
        if (securityFactoryInstantiator.getRoleFactory()
                .getRole(Role.REPORTING, session.getUserName(), session.getUserSource()) == null)
        {
            throw new AccessDeniedException();
        }
        documentTransactionsReportFactory.removeGhostTransaction(transactionUid);
    }
}

