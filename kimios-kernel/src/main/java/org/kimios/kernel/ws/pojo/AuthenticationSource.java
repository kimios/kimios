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
package org.kimios.kernel.ws.pojo;

public class AuthenticationSource
{
    private String name;

    private String className;

    private Boolean enableSso;

    private Boolean enableMailCheck;

    public AuthenticationSource()
    {

    }

    public AuthenticationSource(String name, String className,Boolean enableSso,  Boolean enableMailCheck)
    {
        this.name = name;
        this.className = className;
        this.enableSso = enableSso;
        this.enableMailCheck = enableMailCheck;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getEnableSso() {
        return enableSso;
    }

    public void setEnableSso(Boolean enableSso) {
        this.enableSso = enableSso;
    }

    public Boolean getEnableMailCheck() {
        return enableMailCheck;
    }

    public void setEnableMailCheck(Boolean enableMailCheck) {
        this.enableMailCheck = enableMailCheck;
    }
}

