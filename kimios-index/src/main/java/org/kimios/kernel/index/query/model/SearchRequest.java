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

package org.kimios.kernel.index.query.model;


import java.util.List;

/**
 *  @author Fabien Alin
 *
 */
public class SearchRequest
{


    private Long id;

    private String name;

    private String owner;

    private String ownerSource;

    private List<Criteria> criteriaList;

    private String criteriasListJson;

    public String getCriteriasListJson()
    {
        return criteriasListJson;
    }

    public void setCriteriasListJson( String criteriasListJson )
    {
        this.criteriasListJson = criteriasListJson;
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List<Criteria> getCriteriaList()
    {
        return criteriaList;
    }

    public void setCriteriaList( List<Criteria> criteriaList )
    {
        this.criteriaList = criteriaList;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner( String owner )
    {
        this.owner = owner;
    }

    public String getOwnerSource()
    {
        return ownerSource;
    }

    public void setOwnerSource( String ownerSource )
    {
        this.ownerSource = ownerSource;
    }

    @Override
    public String toString()
    {
        return "SearchRequest{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", owner='" + owner + '\'' +
            ", ownerSource='" + ownerSource + '\'' +
            ", criteriaList=" + criteriaList +
            ", criteriasListJson='" + criteriasListJson + '\'' +
            '}';
    }
}
