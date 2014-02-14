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
package org.kimios.kernel.ws.pojo;

public class Rule
{
    private String javaClassName;

    private RuleImplP[] beans;

    private Object toto;

    public Rule()
    {
    }

    public String getJavaClassName()
    {
        return javaClassName;
    }

    public void setJavaClassName(String javaClassName)
    {
        this.javaClassName = javaClassName;
    }

    public RuleImplP[] getBeans()
    {
        return beans;
    }

    public void setBeans(RuleImplP[] beans)
    {
        this.beans = beans;
    }

    public Object getToto()
    {
        return toto;
    }

    public void setToto(Object toto)
    {
        this.toto = toto;
    }
}

