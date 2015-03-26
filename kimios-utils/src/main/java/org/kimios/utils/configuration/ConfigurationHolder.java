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
package org.kimios.utils.configuration;

import org.kimios.exceptions.ConfigException;

import java.util.List;

/**
 * @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 *
 *         Refactor Hold Configuration manager.
 *
 *         Handle Properties and old Xml Format
 */
public interface ConfigurationHolder
{
    public boolean exists(String keyOrPrefix);

    public Object getValue(String key);

    public String getStringValue(String key);

    public List<String> getValues(String prefix);

    public void refresh() throws ConfigException;
}
