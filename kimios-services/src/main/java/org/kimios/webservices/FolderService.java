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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.MetaValue;
import org.kimios.kernel.ws.pojo.Folder;
import org.kimios.webservices.exceptions.DMServiceException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 4:58 PM To change this template use File | Settings | File
 * Templates.
 */
@Path("/folder")
@WebService(targetNamespace = "http://kimios.org", serviceName = "FolderService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Api(value="/folder", description = "Folder Operations")
public interface FolderService
{
     @GET @ApiOperation(value ="")
    @Path("/getFolder")
    @Produces("application/json")
    public Folder getFolder(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderId) throws DMServiceException;

     @GET @ApiOperation(value ="")
    @Path("/getFolders")
    @Produces("application/json")
    public Folder[] getFolders(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentId) throws DMServiceException;

     @GET @ApiOperation(value ="")
    @Path("/createFolder")
    @Produces("application/json")
    public long createFolder(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentId,
            @QueryParam(value = "isSecurityInherited") @WebParam(name = "isSecurityInherited")
            boolean isSecurityInherited) throws DMServiceException;

     @GET @ApiOperation(value ="")
    @Path("/updateFolder")
    @Produces("application/json")
    public void updateFolder(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderId,
            @QueryParam(value = "name") @WebParam(name = "name") String name,
            @QueryParam(value = "parentId") @WebParam(name = "parentId") long parentId) throws DMServiceException;

     @GET @ApiOperation(value ="")
    @Path("/deleteFolder")
    @Produces("application/json")
    public void deleteFolder(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderId) throws DMServiceException;


     @GET @ApiOperation(value ="")
    @Path("/getFolderMetaValues")
    @Produces("application/json")
    public MetaValue[] getFolderMetaValues(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
                               @QueryParam(value = "folderId") @WebParam(name = "folderId") long folderId)
            throws DMServiceException;


     @GET @ApiOperation(value ="")
    @Path("/getFoldersWithMetaValues")
    @Produces("application/json")
    public HashMap<Folder, List<MetaValue>>
                getFoldersWithMetaValues(@QueryParam(value = "sessionId") @WebParam(name = "sessionId")  String sessionId,
                                         @QueryParam(value = "foldersId") @WebParam(name = "foldersId") List<Long> foldersIds)
            throws DMServiceException;
}
