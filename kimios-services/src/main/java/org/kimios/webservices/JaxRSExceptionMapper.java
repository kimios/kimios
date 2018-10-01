/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.webservices;

import org.kimios.webservices.exceptions.DMServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 *      @author Fabien ALIN <a href="mailto:fabien.alin@gmail.com">fabien.alin@gmail.com</a>
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
