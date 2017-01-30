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
package org.kimios.kernel.dms.utils;

import org.kimios.exceptions.NamingException;

public class PathUtils
{
    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.impl.IPathController#validDmEntityName(java.lang.String)
    */
    public static void validDmEntityName(String entityName) throws NamingException
    {

        if (entityName.length() == 0
                || entityName.contains("/")
                || entityName.contains("\\")
                || entityName.contains(":")
                || entityName.contains("*")
                || entityName.contains("?")
                || entityName.contains("\"")
                || entityName.contains("<")
                || entityName.contains(">")
                || entityName.contains("|"))
        {
            throw new NamingException(
                    "DM entity name cannot : be empty, or contain the following charaters \"\\ / : * ? \" < > |\"");
        }
    }

    public static String cleanDmEntityName(String entityName)
    {
        entityName = entityName.replace('/', '_');
        entityName = entityName.replace('\\', '_');
        entityName = entityName.replace(':', '_');
        entityName = entityName.replace('*', '_');
        entityName = entityName.replace('?', '_');
        entityName = entityName.replace('"', '_');
        entityName = entityName.replace('<', '_');
        entityName = entityName.replace('>', '_');
        entityName = entityName.replace('|', '_');
        entityName = entityName.replace('\'', '_');
        entityName = entityName.replace(',', '_');

        return entityName;
    }

    public static String getFileNameWithoutExtension(String fileName)
    {
        if (fileName == null || fileName.indexOf(".") < 0) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileExtension(String fileName)
    {
        if (fileName == null || fileName.indexOf(".") < 0 || fileName.lastIndexOf(".") == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

