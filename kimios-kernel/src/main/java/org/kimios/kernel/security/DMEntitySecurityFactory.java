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
package org.kimios.kernel.security;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.exception.DataSourceException;

import java.util.List;
import java.util.Vector;

public interface DMEntitySecurityFactory {

    public Vector<DMEntitySecurity> getDMEntitySecurities(DMEntity e) throws ConfigException, DataSourceException;

    public List<DMEntityACL> getDMEntityACL(DMEntity e) throws ConfigException, DataSourceException;

    public List<DMEntityACL> saveDMEntitySecurity(DMEntitySecurity des) throws ConfigException, DataSourceException;

    public void updateDMEntitySecurity(DMEntitySecurity des) throws ConfigException, DataSourceException;

    public void deleteDMEntitySecurity(DMEntitySecurity des) throws ConfigException, DataSourceException;

    public boolean ruleExists(DMEntity e, String userName, String userSource, Vector<String> hashs,
                              Vector<String> noAccessHash) throws ConfigException, DataSourceException;

    public void cleanACL(DMEntity e) throws ConfigException, DataSourceException;

    public void cleanACLRecursive(DMEntity d) throws ConfigException, DataSourceException;

    public <T extends DMEntity> List<T> authorizedEntities(List<T> e, String userName, String userSource,
                                                               Vector<String> hashs, Vector<String> noAccessHash) throws ConfigException, DataSourceException;

    public boolean hasAnyChildNotWritable(DMEntity e, String userName, String userSource, Vector<String> writeHash,
                                          String noAccessHash) throws ConfigException, DataSourceException;

    public boolean hasAnyChildCheckedOut(DMEntity e, String userName, String userSource)
            throws ConfigException, DataSourceException;

    public List<DMEntitySecurity> getDefaultDMEntitySecurity(String objectType, String entityPath)
            throws ConfigException, DataSourceException;

    public void saveDefaultDMEntitySecurity(DMEntitySecurity des, String objectType, String entityPath)
            throws ConfigException, DataSourceException;

    public List<DMEntityACL> generateDMEntityAclsFromSecuritiesObject(List<DMEntitySecurity> securities, DMEntity entity);

    public List<DMEntitySecurity> generateDMEntitySecuritiesFromAcls(List<DMEntityACL> acls, DMEntity entity)
            throws ConfigException, DataSourceException;

    public void createSecurityEntityRules(String secEntityName, String secEntitySource, int secEntityType);

}

