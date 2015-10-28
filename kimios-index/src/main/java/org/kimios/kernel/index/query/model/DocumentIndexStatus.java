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
import org.kimios.kernel.dms.DMEntityImpl;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by farf on 15/06/15.
 */
@Entity
@Table(name = "dm_index_status")
@SequenceGenerator(name = "seq", allocationSize = 1, sequenceName = "doc_idx_status_seq")
public class DocumentIndexStatus {

    @Id
    @Column(name = "dm_doc_index_status_id")
    @GeneratedValue(generator = "seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "dm_indexed_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar indexedDate = Calendar.getInstance();

    @Column(name = "dm_body_indexed")
    private boolean bodyIndexed = false;

    @Column(name = "dm_entity_id", nullable = false)
    private Long entityId;

    @Column(name = "parsing_error", columnDefinition = "text")
    private String error;

    @ManyToOne(targetEntity = DMEntityImpl.class)
    @JoinColumn(name = "dm_entity_id", updatable = false, insertable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DMEntityImpl dmEntity;


    @Transient
    private Map<String, Object> readFileDatas = null;

    public Map<String, Object> getReadFileDatas() {
        return readFileDatas;
    }

    public void setReadFileDatas(Map<String, Object> readFileDatas) {
        this.readFileDatas = readFileDatas;
    }

    public DMEntityImpl getDmEntity() {
        return dmEntity;
    }

    public void setDmEntity(DMEntityImpl dmEntity) {
        this.dmEntity = dmEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getIndexedDate() {
        return indexedDate;
    }

    public void setIndexedDate(Calendar indexedDate) {
        this.indexedDate = indexedDate;
    }

    public boolean isBodyIndexed() {
        return bodyIndexed;
    }

    public void setBodyIndexed(boolean bodyIndexed) {
        this.bodyIndexed = bodyIndexed;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
