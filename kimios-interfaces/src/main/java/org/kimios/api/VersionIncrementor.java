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

package org.kimios.api;

import java.util.Map;

/**
 * Created by farf on 24/07/16.
 */
public interface VersionIncrementor {


    public String defaultVersion(Map<String, Object> parameters);

    public String nextVersion(String currentId, Map<String, Object> parameters);

    public String previousVersion(String currentId, Map<String, Object> parameters);

}
