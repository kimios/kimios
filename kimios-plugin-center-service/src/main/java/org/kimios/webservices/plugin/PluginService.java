package org.kimios.webservices.plugin;

import io.swagger.annotations.Api;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.Plugin;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "PluginService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/plugin", description = "Plugin Operations")
public interface PluginService {

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    List<Plugin> getAll(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId) throws DMServiceException;

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    Plugin get(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "id") @WebParam(name = "id") long id
    ) throws DMServiceException;

    @POST
    @Path("/deactivate")
    @Consumes(MediaType.APPLICATION_JSON)
    void deactivate(
            @WebParam(name = "sessionId") String sessionId,
            @WebParam(name = "id") long id
    ) throws DMServiceException;

    @POST
    @Path("activate")
    @Consumes(MediaType.APPLICATION_JSON)
    void activate(
            @WebParam(name = "sessionId") String sessionId,
            @WebParam(name = "id") long id
    ) throws DMServiceException;
}
