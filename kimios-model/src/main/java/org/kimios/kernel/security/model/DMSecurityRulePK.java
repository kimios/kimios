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

public class DMSecurityRulePK implements Serializable
{
    public DMSecurityRulePK()
    {
    }

    private String ruleHash;

    private String securityEntityUid;

    private String securityEntitySource;

    private int securityEntityType;

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
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DMSecurityRulePK that = (DMSecurityRulePK) o;

        if (securityEntityType != that.securityEntityType) {
            return false;
        }
        if (ruleHash != null ? !ruleHash.equals(that.ruleHash) : that.ruleHash != null) {
            return false;
        }
        if (securityEntitySource != null ? !securityEntitySource.equals(that.securityEntitySource) :
                that.securityEntitySource != null)
        {
            return false;
        }
        if (securityEntityUid != null ? !securityEntityUid.equals(that.securityEntityUid) :
                that.securityEntityUid != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = ruleHash != null ? ruleHash.hashCode() : 0;
        result = 31 * result + (securityEntityUid != null ? securityEntityUid.hashCode() : 0);
        result = 31 * result + (securityEntitySource != null ? securityEntitySource.hashCode() : 0);
        result = 31 * result + securityEntityType;
        return result;
    }
}

