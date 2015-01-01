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

package org.kimios.webservices.bonita;

import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.KimiosExtension;
import org.kimios.webservices.pojo.CommentWrapper;
import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TasksResponse;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/bonita")
@WebService(targetNamespace = "http://kimios.org", serviceName = "BonitaService", name = "BonitaService")
public interface BonitaService extends KimiosExtension {

    @GET
    @Path("/processes/getProcesses")
    @Produces("application/json")
    List<ProcessWrapper> getProcesses(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/processes/getPendingTasks")
    @Produces("application/json")
    TasksResponse getPendingTasks(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "start") @WebParam(name = "start") int start,
            @QueryParam(value = "limit") @WebParam(name = "limit") int limit
    ) throws DMServiceException;

    @GET
    @Path("/tasks/getAssignedTasks")
    @Produces("application/json")
    TasksResponse getAssignedTasks(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "start") @WebParam(name = "start") int start,
            @QueryParam(value = "limit") @WebParam(name = "limit") int limit
    ) throws DMServiceException;

    @GET
    @Path("/tasks/getTasksByInstance")
    @Produces("application/json")
    TasksResponse getTasksByInstance(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "processInstanceId") @WebParam(name = "processInstanceId") long processInstanceId,
            @QueryParam(value = "start") @WebParam(name = "start") int start,
            @QueryParam(value = "limit") @WebParam(name = "limit") int limit
    ) throws DMServiceException;

    @GET
    @Path("/tasks/takeTask")
    @Produces("application/json")
    void takeTask(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId
    ) throws DMServiceException;

    @GET
    @Path("/tasks/releaseTask")
    @Produces("application/json")
    void releaseTask(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId
    ) throws DMServiceException;

    @GET
    @Path("/tasks/hideTask")
    @Produces("application/json")
    void hideTask(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId
    ) throws DMServiceException;

    @GET
    @Path("/tasks/addComment")
    @Produces("application/json")
    CommentWrapper addComment(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId,
            @QueryParam(value = "comment") @WebParam(name = "comment") String comment
    ) throws DMServiceException;

    @GET
    @Path("/tasks/getComments")
    @Produces("application/json")
    List<CommentWrapper> getComments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId
    ) throws DMServiceException;

}
