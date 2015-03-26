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

package org.kimios.client.controller;

import org.kimios.client.exception.AccessDeniedException;
import org.kimios.client.exception.ConfigException;
import org.kimios.client.exception.DMSException;
import org.kimios.client.exception.ExceptionHelper;
import org.kimios.webservices.bonita.BonitaService;
import org.kimios.webservices.pojo.CommentWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TasksResponse;

import java.util.List;

public class BonitaController {

    private BonitaService client;

    public BonitaService getClient() {
        return client;
    }

    public void setClient(BonitaService client) {
        this.client = client;
    }

    public List<ProcessWrapper> getProcesses(String sessionId)
            throws Exception, DMSException, ConfigException, AccessDeniedException {
        try {
            return client.getProcesses(sessionId);

        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public TasksResponse getPendingTasks(String sessionId, int min, int max) throws Exception {
        try {
            return client.getPendingTasks(sessionId, min, max);

        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public TasksResponse getAssignedTasks(String sessionId, int min, int max) throws Exception {
        try {
            return client.getAssignedTasks(sessionId, min, max);

        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public TasksResponse getTasksByInstance(String sessionId, long processInstanceId, int min, int max)
            throws Exception {
        try {
            return client.getTasksByInstance(sessionId, processInstanceId, min, max);

        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public void takeTask(String sessionId, Long taskId) throws Exception {
        try {
            client.takeTask(sessionId, taskId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public void releaseTask(String sessionId, Long taskId) throws Exception {
        try {
            client.releaseTask(sessionId, taskId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public void hideTask(String sessionId, Long taskId) throws Exception {
        try {
            client.hideTask(sessionId, taskId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public CommentWrapper addComment(String sessionId, Long taskId, String comment) throws Exception {
        try {
            return client.addComment(sessionId, taskId, comment);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

    public List<CommentWrapper> getComments(String sessionId, Long taskId) throws Exception {
        try {
            return client.getComments(sessionId, taskId);
        } catch (Exception e) {
            throw new ExceptionHelper().convertException(e);
        }
    }

}
