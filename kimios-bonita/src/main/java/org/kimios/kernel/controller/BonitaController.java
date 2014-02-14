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

package org.kimios.kernel.controller;

import org.kimios.kernel.exception.DmsKernelException;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.pojo.CommentWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TasksResponse;

import java.util.List;

public interface BonitaController {

    List<ProcessWrapper> getProcesses(Session session) throws DmsKernelException;

    TasksResponse getPendingTasks(Session session, int start, int limit) throws DmsKernelException;

    TasksResponse getAssignedTasks(Session session, int start, int limit) throws DmsKernelException;

    TasksResponse getTasksByInstance(Session session, long processInstanceId, int start, int limit) throws DmsKernelException;

    void takeTask(Session session, Long taskId) throws DmsKernelException;

    void releaseTask(Session session, Long taskId) throws DmsKernelException;

    void hideTask(Session session, Long taskId) throws DmsKernelException;

    CommentWrapper addComment(Session session, Long taskId, String comment)
            throws DmsKernelException;

    List<CommentWrapper> getComments(Session session, Long taskId)
            throws DmsKernelException;
}
