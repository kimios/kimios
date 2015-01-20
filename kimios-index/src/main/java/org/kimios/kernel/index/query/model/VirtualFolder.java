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

import org.kimios.kernel.dms.Document;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by farf on 1/8/15.
 */

@Entity
@Table(name = "virtual_folders")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "virtual_folder_id_seq")
public class VirtualFolder {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "owner")
    private String owner;

    @Column(name = "owner_source")
    private String ownerSource;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate = new Date();

    @Column(name = "folder_name", nullable = false)
    private String name;

    @OneToMany
    @JoinTable(name = "virtual_folder_document",
            joinColumns = {@JoinColumn(name = "virtual_folder_id")},
            inverseJoinColumns = {@JoinColumn(name = "document_id")})
    private List<Document> documents = new ArrayList<Document>();

    @OneToMany(mappedBy = "virtualFolder")
    private List<VirtualFolderMetaData> virtualFolderMetaDatas = new ArrayList<VirtualFolderMetaData>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerSource() {
        return ownerSource;
    }

    public void setOwnerSource(String ownerSource) {
        this.ownerSource = ownerSource;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<VirtualFolderMetaData> getVirtualFolderMetaDatas() {
        return virtualFolderMetaDatas;
    }

    public void setVirtualFolderMetaDatas(List<VirtualFolderMetaData> virtualFolderMetaDatas) {
        this.virtualFolderMetaDatas = virtualFolderMetaDatas;
    }
}
