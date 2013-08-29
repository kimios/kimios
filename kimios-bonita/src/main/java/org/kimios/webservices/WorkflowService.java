package org.kimios.webservices;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;

@Path("/workflow")
@WebService(targetNamespace = "http://kimios.org", serviceName = "WorkflowService")
public interface WorkflowService {

//    @GET
//    @Path("/getTasks")
//    @Produces("application/json")
//    void getTasks(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId);
}
