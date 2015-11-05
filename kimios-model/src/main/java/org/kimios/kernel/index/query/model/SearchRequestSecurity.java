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

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;

/**
 * Created by farf on 5/9/14.
 */
public class SearchRequestSecurity {

    private long searchRequestId;

    private String name;

    private String source;

    private String fullName;

    private int type;

    private boolean read;

    private boolean write;

    private boolean fullAccess;

    private SearchRequest searchRequest;

    public SearchRequestSecurity()
    {
    }

    public SearchRequestSecurity(long searchRequestId, String name, String source, int type, boolean read,
                            boolean write, boolean fullAccess)
    {
        this.searchRequestId = searchRequestId;
        this.name = name;
        this.source = source;
        this.type = type;
        this.read = read;
        this.write = write;
        this.fullAccess = fullAccess;
    }

    public SearchRequestSecurity(long searchRequestId, String name, String fullName, String source, int type,
                            boolean read, boolean write, boolean fullAccess)
    {
        this.searchRequestId = searchRequestId;
        this.name = name;
        this.source = source;
        this.type = type;
        this.read = read;
        this.write = write;
        this.fullAccess = fullAccess;
        this.fullName = fullName;
    }

    public SearchRequestSecurity(long searchRequestId, String name, String source, int type, boolean read,
                            boolean write, boolean fullAccess, SearchRequest searchRequest)
    {
        this.searchRequestId = searchRequestId;
        this.name = name;
        this.source = source;
        this.type = type;
        this.read = read;
        this.write = write;
        this.fullAccess = fullAccess;
        this.searchRequest = searchRequest;
    }

    public SearchRequestSecurity(long searchRequestId,  String name, String source, String fullName, int type,
                            boolean read, boolean write, boolean fullAccess, SearchRequest request)
    {
        this.searchRequest = request;
        this.name = name;
        this.source = source;
        this.type = type;
        this.read = read;
        this.write = write;
        this.fullAccess = fullAccess;
        this.searchRequestId = searchRequestId;
        this.fullName = fullName;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public void setFullAccess(boolean fullAccess)
    {
        this.fullAccess = fullAccess;
    }

    public boolean isFullAccess()
    {
        return this.fullAccess;
    }

    public SearchRequest getSearchRequest()
    {
        return this.searchRequest;
    }

    public void setSearchRequest(SearchRequest searchRequest)
    {
        this.searchRequest = searchRequest;
        if(searchRequest != null){
            this.searchRequestId = searchRequest.getId();
        }

    }


    public boolean isRead()
    {
        return read;
    }

    public void setRead(boolean read)
    {
        this.read = read;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public boolean isWrite()
    {
        return write;
    }

    public void setWrite(boolean write)
    {
        this.write = write;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public long getSearchRequestId() {
        return searchRequestId;
    }

    public void setSearchRequestId(long searchRequestId) {
        this.searchRequestId = searchRequestId;
    }


    @Override
    public String toString() {
        return "SearchRequestSecurity{" +
                "searchRequestId=" + searchRequestId +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                ", read=" + read +
                ", write=" + write +
                ", fullAccess=" + fullAccess +
                ", searchRequest=" + searchRequest +
                '}';
    }
}
