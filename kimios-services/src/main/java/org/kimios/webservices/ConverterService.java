package org.kimios.webservices;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/converter")
@WebService(targetNamespace = "http://kimios.org", serviceName = "ConverterService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface ConverterService {

    /**
     * Get the last document version and convert to given converter class name
     */
    @GET
    @Path("/convertDocument")
    @Produces(value = {
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.APPLICATION_JSON
    })
    Response convertDocument(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") Long documentId,
            @QueryParam(value = "converterImpl") @WebParam(name = "converterImpl") String converterImpl)
            throws DMServiceException;

    /**
     * Convert to converter class name for the given document version
     */
    @GET
    @Path("/convertDocumentVersion")
    @Produces(value = {
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.APPLICATION_JSON
    })
    Response convertDocumentVersion(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "versionId") @WebParam(name = "versionId") Long versionId,
            @QueryParam(value = "converterImpl") @WebParam(name = "converterImpl") String converterImpl)
            throws DMServiceException;

    /**
     * Get the last versions and convert to given converter class name
     */
    @GET
    @Path("/convertDocuments")
    @Produces(value = {
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.APPLICATION_JSON
    })
    Response convertDocuments(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "documentId") @WebParam(name = "documentId") Long[] documentIds,
            @QueryParam(value = "converterImpl") @WebParam(name = "converterImpl") String converterImpl)
            throws DMServiceException;

    /**
     * Convert to converter class name for the given versions
     */
    @GET
    @Path("/convertDocumentVersions")
    @Produces(value = {
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.APPLICATION_JSON
    })
    Response convertDocumentVersions(
            @QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "versionId") @WebParam(name = "versionId") Long[] versionIds,
            @QueryParam(value = "converterImpl") @WebParam(name = "converterImpl") String converterImpl)
            throws DMServiceException;
}
