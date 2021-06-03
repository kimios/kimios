package org.kimios.webservices.zipper;

import io.swagger.annotations.Api;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ZipperService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/zip")
public interface ZipperService {
    @GET
    @Path("/make")
    @Produces(value = { MediaType.APPLICATION_OCTET_STREAM })
    Response makeZip(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "id") @WebParam(name = "id") List<Long> ids
    ) throws DMServiceException;
}

