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

package org.kimios.kernel.index.query.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by farf on 5/9/14.
 */

public class SearchRequestACLPk implements Serializable {

    private long searchRequestId;

    private String ruleHash;

    public long getSearchRequestId()
    {
        return searchRequestId;
    }

    public void setSearchRequestId(long searchRequestId)
    {
        this.searchRequestId = searchRequestId;
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

        SearchRequestACLPk that = (SearchRequestACLPk) o;

        if (searchRequestId != that.searchRequestId) {
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
        int result = (int) (searchRequestId ^ (searchRequestId >>> 32));
        result = 31 * result + (ruleHash != null ? ruleHash.hashCode() : 0);
        return result;
    }
}
