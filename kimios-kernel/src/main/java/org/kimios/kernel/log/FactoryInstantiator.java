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

public class FactoryInstantiator implements ILogFactoryInstantiator {
    private static FactoryInstantiator instance;

    synchronized public static FactoryInstantiator getInstance()
    {
        if (instance == null) {
            instance = new FactoryInstantiator();
        }
        return instance;
    }

    private FactoryInstantiator()
    {
    }

    private LogFactory logFactory;

    private DMEntityLogFactory entityLogFactory;

    @Override
    public LogFactory getLogFactory()
    {
        return logFactory;
    }

    public void setLogFactory(LogFactory logFactory)
    {
        this.logFactory = logFactory;
    }

    @Override
    public DMEntityLogFactory getEntityLogFactory()
    {
        return entityLogFactory;
    }

    public void setEntityLogFactory(DMEntityLogFactory entityLogFactory)
    {
        this.entityLogFactory = entityLogFactory;
    }

    public static void setInstance(FactoryInstantiator instance)
    {
        FactoryInstantiator.instance = instance;
    }
}

