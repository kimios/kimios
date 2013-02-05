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
package org.kimios.kernel.controller;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.rules.impl.RuleImpl;
import org.kimios.kernel.security.Session;
import org.xml.sax.SAXException;

public interface IRuleManagementController
{
    //  public void saveRule(Session session, String ruleJavaClass,  String path, String ruleName, String xmlStream) throws DataSourceException, ConfigException, AccessDeniedException;
    public void deleteRule(long idRule) throws DataSourceException, ConfigException, AccessDeniedException;

    public Collection<Class<? extends RuleImpl>> getRulesClass(Session session) throws DataSourceException;

    public String getRuleClassParameters(Session session, String javaClassName) throws DataSourceException, Exception;

    public void getRule(Session session, long uid) throws DataSourceException;

    public void getRules(Session session) throws DataSourceException;

    public void getRulesByPath(Session session, String path) throws DataSourceException;

    public void createRule(Session session, String ruleJavaClass, String path, String ruleName, String xmlStream)
            throws DataSourceException, ConfigException, AccessDeniedException, SAXException, IOException,
            ParserConfigurationException;
}

