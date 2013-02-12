package org.kimios.client.controller;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.kimios.webservices.DMServiceException;
import org.kimios.webservices.ExceptionMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * @author Fabien ALIN <fabien.alin@gmail.com>
 */
public class JaxRSResponseExceptionMapper
    implements ResponseExceptionMapper<DMServiceException>
{


    public DMServiceException fromResponse( Response r )
    {
        DMServiceException ex = r.readEntity( DMServiceException.class );
        return ex;
    }
}
