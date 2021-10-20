package org.kimios.webservices.zipper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.web.DMEntityTreeParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/zip-rest-only")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/zip-rest-only")
public interface ZipperRestService {
    @POST
    @ApiOperation(value ="")
    @Path("/make-from-ids")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    long make(
            @ApiParam() DMEntityTreeParam dmEntityTree
    ) throws Exception;
}
