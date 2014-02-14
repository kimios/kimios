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

package org.kimios.webservices.impl.factory;

import org.bonitasoft.engine.identity.User;
import org.kimios.webservices.pojo.UserWrapper;

public class UserWrapperFactory {
    public static UserWrapper createUserWrapper(User user) {
        UserWrapper wrapper = new UserWrapper();
        wrapper.setId(user.getId());
        wrapper.setUserName(user.getUserName());
        wrapper.setCreatedBy(user.getCreatedBy());
        wrapper.setCreationDate(user.getCreationDate());
        wrapper.setFirstName(user.getFirstName());
        wrapper.setLastName(user.getLastName());
        wrapper.setIconName(user.getIconName());
        wrapper.setIconPath(user.getIconPath());
        wrapper.setJobTitle(user.getJobTitle());
        wrapper.setLastConnection(user.getLastConnection());
        wrapper.setLastUpdate(user.getLastUpdate());
        wrapper.setManagerUserId(user.getManagerUserId());
        wrapper.setTitle(user.getTitle());
        return wrapper;
    }
}
