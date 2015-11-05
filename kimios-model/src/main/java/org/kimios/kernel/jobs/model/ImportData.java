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

package org.kimios.kernel.jobs.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by farf on 12/16/14.
 */
@Entity
@Table(name = "imports")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "import_id_seq")
public class ImportData {


    @Id
    @GeneratedValue(generator = "seq", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    public String getFirstIndexedId() {
        return firstIndexedId;
    }

    public void setFirstIndexedId(String firstIndexedId) {
        this.firstIndexedId = firstIndexedId;
    }

    @Column(name = "start_date", nullable = false)

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;


    @Column(name = "first_indexed_external_id", nullable = true)
    private String firstIndexedId;

    @Column(name = "last_indexed_external_id", nullable = true)
    private String lastIndexedId;

    @Column(name = "imported_count", nullable = true)
    private Long importedCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = true)
    private Date endDate;

    @Column(name = "stack_trace", nullable = true, columnDefinition = "text")
    private String stackTrace;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getLastIndexedId() {
        return lastIndexedId;
    }

    public void setLastIndexedId(String lastIndexedId) {
        this.lastIndexedId = lastIndexedId;
    }

    public Long getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(Long importedCount) {
        this.importedCount = importedCount;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
