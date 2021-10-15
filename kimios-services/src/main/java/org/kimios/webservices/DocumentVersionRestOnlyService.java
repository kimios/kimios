package org.kimios.webservices;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.web.UpdateDocumentVersionMetaDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/document-version-rest-only")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/document-version-rest-only")
public interface DocumentVersionRestOnlyService {
    @POST
    @ApiOperation(value ="")
    @Path("/updateDocumentMetaData")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateDocumentMetaData(
            @ApiParam() UpdateDocumentVersionMetaDataParam updateDocumentVersionMetaDataParam
    ) throws Exception;
}
