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
package org.kimios.kernel.user.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.model.AuthenticationSourceImpl;
import org.kimios.kernel.user.model.GroupFactory;
import org.kimios.kernel.user.model.UserFactory;
import org.kimios.kernel.user.impl.factory.hibernate.HGroupFactory;
import org.kimios.kernel.user.impl.factory.hibernate.HUserFactory;

public class HAuthenticationSource extends AuthenticationSourceImpl
{
    public UserFactory getUserFactory() throws DataSourceException, ConfigException
    {

        HUserFactory userFactory = new HUserFactory();
        userFactory.setAuth(this);
        return userFactory;
    }

    public GroupFactory getGroupFactory() throws DataSourceException, ConfigException
    {
        HGroupFactory groupFactory = new HGroupFactory();
        groupFactory.setAuth(this);
        return groupFactory;
    }
}

