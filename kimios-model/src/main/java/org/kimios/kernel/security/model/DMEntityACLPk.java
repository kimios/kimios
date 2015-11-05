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

public class DMEntityACLPk implements Serializable
{
    private long dmEntityUid;

    private String ruleHash;

    public long getDmEntityUid()
    {
        return dmEntityUid;
    }

    public void setDmEntityUid(long dmEntityUid)
    {
        this.dmEntityUid = dmEntityUid;
    }

    public String getRuleHash()
    {
        return ruleHash;
    }

    public void setRuleHash(String ruleHash)
    {
        this.ruleHash = ruleHash;
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

        DMEntityACLPk that = (DMEntityACLPk) o;

        if (dmEntityUid != that.dmEntityUid) {
            return false;
        }
        if (ruleHash != null ? !ruleHash.equals(that.ruleHash) : that.ruleHash != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (dmEntityUid ^ (dmEntityUid >>> 32));
        result = 31 * result + (ruleHash != null ? ruleHash.hashCode() : 0);
        return result;
    }
}
