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
package org.kimios.kernel.log;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.log.model.DMEntityLog;
import org.kimios.kernel.user.model.User;

import java.util.Date;
import java.util.Vector;

public interface DMEntityLogFactory
{
    public <T extends DMEntityImpl> DMEntityLog<T> getLog(long uid) throws ConfigException, DataSourceException;

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(T e) throws ConfigException, DataSourceException;

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(User user)
            throws ConfigException, DataSourceException;

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(T e, Date from, Date to)
            throws ConfigException, DataSourceException;

    public <T extends DMEntityImpl> Vector<DMEntityLog<T>> getLogs(User user, Date from, Date to)
            throws ConfigException, DataSourceException;

    public <T extends DMEntityImpl> void saveLog(DMEntityLog<T> log) throws ConfigException, DataSourceException;

    public void cleanLogs(User user) throws ConfigException, DataSourceException;

    public void cleanLogs(User user, Date from, Date to) throws ConfigException, DataSourceException;
}

