package org.kimios.webservices;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;

@Path("/workflow")
@WebService(targetNamespace = "http://kimios.org", serviceName = "WorkflowService")
public interface WorkflowService {

//    @GET
//    @Path("/getPendingTasks")
//    @Produces("application/json")
//    void getPendingTasks(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId);
}
