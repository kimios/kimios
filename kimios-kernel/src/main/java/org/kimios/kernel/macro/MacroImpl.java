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
package org.kimios.kernel.macro;

import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.exception.MacroException;
import org.kimios.kernel.security.Session;

public abstract class MacroImpl
{
    protected EventContext context;

    protected Session session;

    public MacroImpl()
    {
        super();
    }

    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

    public void setContext(EventContext context)
    {
        this.context = context;
    }

    public abstract void execute() throws MacroException;
}

