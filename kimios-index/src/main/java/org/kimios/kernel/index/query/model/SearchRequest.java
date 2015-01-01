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

package org.kimios.kernel.index.query.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Fabien Alin
 */

@Entity
@Table(name = "searches"
        //uniqueConstraints = @UniqueConstraint(columnNames = {"search_name", "owner", "owner_source"}
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

    @Column(name = "search_creation_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "search_update_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name = "search_sort_field", nullable = true)
    private String sortField;

    @Column(name = "search_sort_dir", nullable = true)
    private String sortDir;

    @Column( name = "search_virtual_tree", nullable = true )
    private Boolean virtualTree;

    @Column(name = "search_public", nullable = false)
    private Boolean publicAccess = true;

    @Column(name = "search_published", nullable = true)
    private Boolean published = false;

    @Column(name = "search_temporary", nullable = true)
    private Boolean temporary = false;


    @Column(name = "search_temporary_session_id", nullable = true)
    private String searchSessionId;

    @Transient
    private boolean isTransient = false;

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    /*@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "search_tag",
            joinColumns = @JoinColumn(name = "id"))
    public List<String> searchTags = new ArrayList<String>();

    public List<String> getSearchTags() {
        return searchTags;
    }

    public void setSearchTags(List<String> searchTags) {
        this.searchTags = searchTags;
    }*/

    @Transient
    private List<SearchRequestSecurity> securities = new ArrayList<SearchRequestSecurity>();

    public List<SearchRequestSecurity> getSecurities() {
        return securities;
    }

    public void setSecurities(List<SearchRequestSecurity> securities) {
        this.securities = securities;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

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

    public String getSearchSessionId() {
        return searchSessionId;
    }

    public void setSearchSessionId(String searchSessionId) {
        this.searchSessionId = searchSessionId;
    }

    @Override
    public String toString() {
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
                ", publicAccess=" + publicAccess +
                ", published=" + published +
                ", temporary=" + temporary +
                '}';
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
