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
package org.kimios.kernel.security.factory;

import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.AuthenticatedService;
import org.kimios.kernel.security.AuthenticatedServiceFactory;

/**
 * @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 */
public class HAuthenticatedServiceFactory extends HFactory implements AuthenticatedServiceFactory
{
    public AuthenticatedService loadServiceByIdAndKey(String serviceId, String serviceKey)
    {

        String query = "from AuthenticatedService where serviceId = :serviceId and serviceKey = :serviceKey";

        return (AuthenticatedService) getSession()
                .createQuery(query)
                .setString("serviceId", serviceId)
                .setString("serviceKey", serviceKey)
                .uniqueResult();
    }
}
