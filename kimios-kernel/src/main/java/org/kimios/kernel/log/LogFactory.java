/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.log;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.User;

import java.util.Date;
import java.util.Vector;

public interface LogFactory
{
    public Log getLog(long uid) throws ConfigException, DataSourceException;

    public Vector<Log> getLogs(DMEntity e) throws ConfigException, DataSourceException;

    public Vector<Log> getLogs(User user) throws ConfigException, DataSourceException;

    public Vector<Log> getLogs(DMEntity e, Date from, Date to) throws ConfigException, DataSourceException;

    public Vector<Log> getLogs(User user, Date from, Date to) throws ConfigException, DataSourceException;

    public void saveLog(Log log) throws ConfigException, DataSourceException;

    public void cleanLogs(User user) throws ConfigException, DataSourceException;

    public void cleanLogs(User user, Date from, Date to) throws ConfigException, DataSourceException;
}

