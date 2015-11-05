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
package org.kimios.kernel.ws.pojo;

import java.util.Date;

/**
 * @author Fabien Alin (Farf) <fabien.alin@gmail.com>
 */
public class RuleBean
{
    private long id;

    private String javaClass;

    private String name;

    private String path;

    private String ruleOwner;

    private String ruleOwnerSource;

    private Date ruleCreationDate;

    private Date ruleUpdateDate;

    private Boolean recursive = false;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getJavaClass()
    {
        return javaClass;
    }

    public void setJavaClass(String javaClass)
    {
        this.javaClass = javaClass;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRuleOwner()
    {
        return ruleOwner;
    }

    public void setRuleOwner(String ruleOwner)
    {
        this.ruleOwner = ruleOwner;
    }

    public String getRuleOwnerSource()
    {
        return ruleOwnerSource;
    }

    public void setRuleOwnerSource(String ruleOwnerSource)
    {
        this.ruleOwnerSource = ruleOwnerSource;
    }

    public Date getRuleCreationDate()
    {
        return ruleCreationDate;
    }

    public void setRuleCreationDate(Date ruleCreationDate)
    {
        this.ruleCreationDate = ruleCreationDate;
    }

    public Date getRuleUpdateDate()
    {
        return ruleUpdateDate;
    }

    public void setRuleUpdateDate(Date ruleUpdateDate)
    {
        this.ruleUpdateDate = ruleUpdateDate;
    }

    public Boolean isRecursive()
    {
        return recursive;
    }

    public void setRecursive(Boolean recursive)
    {
        this.recursive = recursive;
    }
}
