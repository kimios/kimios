package org.kimios.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 *      @author Fabien ALIN <fabien.alin@gmail.com>
 *
 *      Produces JSON Content on Exception in the REST API
 *
 */
public class JaxRSExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<DMServiceException> {


    private static Logger logger = LoggerFactory.getLogger(JaxRSExceptionMapper.class);

    public Response toResponse(DMServiceException exception) {
        logger.error("kimios server error", exception);
        ExceptionMessageWrapper wrap = null;
        if (exception instanceof DMServiceException) {
            wrap = new ExceptionMessageWrapper(exception.getMessage(), exception.getCode(), exception.getClass().getName());
            wrap.setStackTrace(exception.getStackTrace());
        } else {
            wrap = new ExceptionMessageWrapper(exception.getMessage(), exception.getClass().getName());
            wrap.setStackTrace(exception.getStackTrace());
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                wrap
        ).build();
    }
}
