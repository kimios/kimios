/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.utils.context;

import org.kimios.utils.spring.ApplicationContextProvider;

import java.util.Map;

/**
 * Created by farf on 23/05/16.
 */
public class SpringServiceHolder implements ContextHolder {

    @Override
    public <T> T getService(Class<T> clazz) {
        Map<String, T> item = ApplicationContextProvider.loadBeans(clazz);
        if(item.size() == 1){
            return item.get(item.keySet().iterator().next());
        } else {
            return null;
        }
    }
}
