package org.kimios.webservices;

import javax.ws.rs.core.Response;

/**
 *      @author Fabien ALIN <fabien.alin@gmail.com>
 *
 *      Produces JSON Content on Exception in the REST API
 *
 */
public class JaxRSExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<DMServiceException> {

    public Response toResponse(DMServiceException exception) {
        ExceptionMessageWrapper wrap = null;
        if (exception instanceof DMServiceException) {
            wrap = new ExceptionMessageWrapper(exception.getMessage(), exception.getCode(), exception.getClass().getName());
        } else {
            wrap = new ExceptionMessageWrapper(exception.getMessage(), exception.getClass().getName());
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                wrap
        ).build();
    }
}
