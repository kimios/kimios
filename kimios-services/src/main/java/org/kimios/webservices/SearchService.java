/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.webservices;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.Document;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:07 PM
 */
@Path("/search")
@WebService(targetNamespace = "http://kimios.org", serviceName = "SearchService")
public interface SearchService
{
    @GET
    @Path("/quickSearch")
    @Produces("application/json")
    public Document[] quickSearch(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionUid,
            @QueryParam(value = "query") @WebParam(name = "query") String query,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "dmEntityType") @WebParam(name = "dmEntityType") int dmEntityType)
            throws DMServiceException;

    @GET
    @Path("/advancedSearch")
    @Produces("application/json")
    public Document[] advancedSearch(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream,
            @QueryParam(value = "dmEntityId") @WebParam(name = "dmEntityId") long dmEntityId,
            @QueryParam(value = "dmEntityType") @WebParam(name = "dmEntityType") int dmEntityType)
            throws DMServiceException;

    @GET
    @Path("/getDMentityFromPath")
    @Produces("application/json")
    public DMEntity getDMentityFromPath(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "path") @WebParam(name = "path") String path) throws DMServiceException;

    @GET
    @Path("/getPathFromDMEntity")
    @Produces("application/json")
    public String getPathFromDMEntity(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "entityId") @WebParam(name = "entityId") long entityId,
            @QueryParam(value = "entityType") @WebParam(name = "entityType") int entityType) throws DMServiceException;
}
