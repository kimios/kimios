/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2017  DevLib'
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

package org.kimios.utils.extension;

import java.util.Collection;

/**
 * Created by farf on 29/01/17.
 */
public interface IExtensionRegistryManager {
    void addClass(Class clazz);

    Collection<String> itemsAsString(Class classz);

    <T> Collection<Class<? extends T>> itemsAsClass(Class<T> classz);

    <T> void registerRegistry(ExtensionRegistry<T> extensionRegistry);
}
