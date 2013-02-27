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


import javax.persistence.*;
import java.util.List;

/**
 * @author Fabien Alin
 */

@Entity
@Table(name = "searches",
        uniqueConstraints = @UniqueConstraint(columnNames = {"search_name", "owner", "owner_source"})
)
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "search_id_sed")
public class SearchRequest
{

    @Id
    @GeneratedValue(generator = "seq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;


    @Column(name = "search_name", nullable = false)
    private String name;


    @Column(name = "owner", nullable = false)
    private String owner;


    @Column(name = "owner_source", nullable = false)
    private String ownerSource;

    @Transient
    private List<Criteria> criteriaList;

    @Column(name = "search_criterias", length = 20000, nullable = false)
    private String criteriasListJson;


    @Column(name = "search_sort_field", nullable = true)
    private String sortField;

    @Column(name = "search_sort_dir", nullable = true)
    private String sortDir;

    @Column( name = "search_virtual_tree", nullable = true )
    private Boolean virtualTree;

    public Boolean isVirtualTree()
    {
        return virtualTree;
    }

    public void setVirtualTree( Boolean virtualTree )
    {
        this.virtualTree = virtualTree;
    }

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

    public String getSortField()
    {
        return sortField;
    }

    public void setSortField( String sortField )
    {
        this.sortField = sortField;
    }

    public String getSortDir()
    {
        return sortDir;
    }

    public void setSortDir( String sortDir )
    {
        this.sortDir = sortDir;
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
            ", sortField='" + sortField + '\'' +
            ", sortDir='" + sortDir + '\'' +
            ", virtualTree=" + virtualTree +
            '}';
    }
}
