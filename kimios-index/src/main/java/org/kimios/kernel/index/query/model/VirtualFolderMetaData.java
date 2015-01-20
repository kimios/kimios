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

import org.kimios.kernel.dms.Meta;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by farf on 1/8/15.
 */
@Entity
@Table(name = "virtual_folder_meta")
@IdClass(VirtualFolderMetaData.VirtualFolderMetaDataPK.class)
public class VirtualFolderMetaData {

    public class VirtualFolderMetaDataPK implements Serializable {
        protected Long virtualFolderId;
        protected Long metaId;

        public VirtualFolderMetaDataPK() {}

        public VirtualFolderMetaDataPK(Long virtualFolderId, Long metaId) {
            this.virtualFolderId = virtualFolderId;
            this.metaId = metaId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VirtualFolderMetaDataPK that = (VirtualFolderMetaDataPK) o;

            if (metaId != null ? !metaId.equals(that.metaId) : that.metaId != null) return false;
            if (virtualFolderId != null ? !virtualFolderId.equals(that.virtualFolderId) : that.virtualFolderId != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = virtualFolderId != null ? virtualFolderId.hashCode() : 0;
            result = 31 * result + (metaId != null ? metaId.hashCode() : 0);
            return result;
        }
    }

    @Id
    @Column(name = "virtual_folder_id")
    private Long virtualFolderId;

    @Id
    @Column(name = "meta_id")
    private Long metaId;

    @ManyToOne
    @JoinColumn(name = "virtual_folder_id", insertable = false, updatable = false)
    private VirtualFolder virtualFolder;

    @ManyToOne
    @JoinColumn(name = "meta_id", insertable = false, updatable = false)
    private Meta meta;

    @Column(name = "virtual_folder_meta_date_value", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateValue;

    @Column(name = "virtual_folder_meta_string_value", nullable = true)
    private String stringValue;


    public VirtualFolder getVirtualFolder() {
        return virtualFolder;
    }

    public void setVirtualFolder(VirtualFolder virtualFolder) {
        this.virtualFolder = virtualFolder;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }


    public Long getVirtualFolderId() {
        return virtualFolderId;
    }

    public void setVirtualFolderId(Long virtualFolderId) {
        this.virtualFolderId = virtualFolderId;
    }

    public Long getMetaId() {
        return metaId;
    }

    public void setMetaId(Long metaId) {
        this.metaId = metaId;
    }
}
