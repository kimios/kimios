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

package org.kimios.kernel.security.model;

import java.io.Serializable;

public class DMDefaultSecurityRulePK implements Serializable
{
    public DMDefaultSecurityRulePK()
    {
    }

    private String ruleHash;

    private String securityEntityUid;

    private String securityEntitySource;

    private int securityEntityType;

    private String objectType;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    private String entityPath;

    public String getEntityPath() {
        return entityPath;
    }

    public void setEntityPath(String entityPath) {
        this.entityPath = entityPath;
    }

    public String getRuleHash()
    {
        return ruleHash;
    }

    public void setRuleHash(String ruleHash)
    {
        this.ruleHash = ruleHash;
    }

    public String getSecurityEntityUid()
    {
        return securityEntityUid;
    }

    public void setSecurityEntityUid(String securityEntityUid)
    {
        this.securityEntityUid = securityEntityUid;
    }

    public String getSecurityEntitySource()
    {
        return securityEntitySource;
    }

    public void setSecurityEntitySource(String securityEntitySource)
    {
        this.securityEntitySource = securityEntitySource;
    }

    public int getSecurityEntityType()
    {
        return securityEntityType;
    }

    public void setSecurityEntityType(int securityEntityType)
    {
        this.securityEntityType = securityEntityType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DMDefaultSecurityRulePK that = (DMDefaultSecurityRulePK) o;

        if (securityEntityType != that.securityEntityType) return false;
        if (ruleHash != null ? !ruleHash.equals(that.ruleHash) : that.ruleHash != null) return false;
        if (securityEntityUid != null ? !securityEntityUid.equals(that.securityEntityUid) : that.securityEntityUid != null)
            return false;
        if (securityEntitySource != null ? !securityEntitySource.equals(that.securityEntitySource) : that.securityEntitySource != null)
            return false;
        if (objectType != null ? !objectType.equals(that.objectType) : that.objectType != null) return false;
        return !(entityPath != null ? !entityPath.equals(that.entityPath) : that.entityPath != null);

    }

    @Override
    public int hashCode() {
        int result = ruleHash != null ? ruleHash.hashCode() : 0;
        result = 31 * result + (securityEntityUid != null ? securityEntityUid.hashCode() : 0);
        result = 31 * result + (securityEntitySource != null ? securityEntitySource.hashCode() : 0);
        result = 31 * result + securityEntityType;
        result = 31 * result + (objectType != null ? objectType.hashCode() : 0);
        result = 31 * result + (entityPath != null ? entityPath.hashCode() : 0);
        return result;
    }
}

