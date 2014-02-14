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

package org.kimios.webservices.pojo;

import java.util.List;

public class TasksResponse {

    private List<TaskWrapper> tasks;
    private Number totalProperty;

    public TasksResponse() {
    }

    public TasksResponse(List<TaskWrapper> tasks, Number totalProperty) {
        this.tasks = tasks;
        this.totalProperty = totalProperty;
    }

    public List<TaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskWrapper> tasks) {
        this.tasks = tasks;
    }

    public Number getTotalProperty() {
        return totalProperty;
    }

    public void setTotalProperty(Number totalProperty) {
        this.totalProperty = totalProperty;
    }
}
