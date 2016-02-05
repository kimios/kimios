/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.DMEntitySecurity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farf on 27/01/16.
 */
public class TaskInfo {


    private String taskId;

    private String messages;

    private String owner;

    private Date startDate;

    private boolean status;

    private TaskDurationType taskResultType;

    private Map<String, Object> results = new HashMap<String, Object>();

    public TaskDurationType getTaskResultType() {
        return taskResultType;
    }

    public void setTaskResultType(TaskDurationType taskResultType) {
        this.taskResultType = taskResultType;
    }

    private org.kimios.kernel.ws.pojo.DMEntity targetEntity;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DMEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(DMEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }
}
