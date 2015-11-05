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

/**
 * Created by farf on 5/9/14.
 */

@Entity
@IdClass(SearchRequestACLPk.class)
@Table(name = "search_request_acl")
public class SearchRequestACL {

    @Id
    @Column(name = "search_request_id")
    private long searchRequestId;

    @Id @Column(name = "rule_hash")
    private String ruleHash;

    @ManyToOne(targetEntity = SearchRequest.class)
    @JoinColumn(name = "search_request_id", nullable = false, insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SearchRequest searchRequest;

    public SearchRequestACL()
    {
    }

    public SearchRequestACL(SearchRequest item)
    {
        this.searchRequestId = item.getId();
    }

    public String getRuleHash()
    {
        return ruleHash;
    }

    public void setRuleHash(String ruleHash)
    {
        this.ruleHash = ruleHash;
    }

    public long getSearchRequestId() {
        return searchRequestId;
    }

    public void setSearchRequestId(long searchRequestId) {
        this.searchRequestId = searchRequestId;
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    public void setSearchRequest(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }
}
