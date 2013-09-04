package org.kimios.webservices;

import org.kimios.webservices.pojo.ProcessWrapper;
import org.kimios.webservices.pojo.TaskWrapper;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/bonita")
@WebService(targetNamespace = "http://kimios.org", serviceName = "BonitaService", name = "BonitaService")
public interface BonitaService {

    @GET
    @Path("/getProcesses")
    @Produces("application/json")
    List<ProcessWrapper> getProcesses(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getPendingTasks")
    @Produces("application/json")
    List<TaskWrapper> getPendingTasks(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "min") @WebParam(name = "min") int min,
            @QueryParam(value = "max") @WebParam(name = "max") int max
    ) throws DMServiceException;

    @GET
    @Path("/getAssignedTasks")
    @Produces("application/json")
    List<TaskWrapper> getAssignedTasks(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "min") @WebParam(name = "min") int min,
            @QueryParam(value = "max") @WebParam(name = "max") int max
    ) throws DMServiceException;

    @GET
    @Path("/task/take")
    @Produces("application/json")
    void takeTask(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId
    ) throws DMServiceException;

    @GET
    @Path("/task/release")
    @Produces("application/json")
    void releaseTask(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId
    ) throws DMServiceException;

    @GET
    @Path("/task/hide")
    @Produces("application/json")
    void hideTask(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "taskId") @WebParam(name = "taskId") Long taskId
    ) throws DMServiceException;

}
