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

import java.util.Date;

public interface ILog
{
    public String getUserSource();

    public void setUserSource(String userSource);

    public Date getDate();

    public void setDate(Date date);

    public int getAction();

    public void setAction(int action);

    public long getUid();

    public void setUid(long uid);

    public String getUser();

    public void setUser(String user);

    public Long getActionParameters();

    public void setActionParameters(Long o);
}
