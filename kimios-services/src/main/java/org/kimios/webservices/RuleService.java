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

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.kimios.kernel.ws.pojo.Rule;
import org.kimios.kernel.ws.pojo.RuleBean;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by IntelliJ IDEA. User: farf Date: 4/1/12 Time: 5:06 PM
 */
@Path("/rule")
@WebService(targetNamespace = "http://kimios.org", serviceName = "RuleService", name = "RuleService")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface RuleService
{
    @GET
    @Path("/getAvailablesRules")
    @Produces("application/json")
    public String[] getAvailablesRules(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId)
            throws DMServiceException;

    @GET
    @Path("/getRuleParam")
    @Produces("application/json")
    public String getRuleParam(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "javaClassName") @WebParam(name = "javaClassName") String javaClassName)
            throws DMServiceException;

    @GET
    @Path("/createRule")
    @Produces("application/json")
    public void createRule(@QueryParam(value = "sessionId") @WebParam(name = "sessionId") String sessionId,
            @QueryParam(value = "conditionJavaClass") @WebParam(name = "conditionJavaClass") String conditionJavaClass,
            @QueryParam(value = "path") @WebParam(name = "path") String path,
            @QueryParam(value = "ruleName") @WebParam(name = "ruleName") String ruleName,
            @QueryParam(value = "xmlStream") @WebParam(name = "xmlStream") String xmlStream) throws DMServiceException;

    @GET
    @Path("/getBeans")
    @Produces("application/json")
    public RuleBean[] getBeans() throws DMServiceException;

    @GET
    @Path("/sendList")
    @Produces("application/json")
    public void sendList(RuleBean[] beans) throws DMServiceException;

    @GET
    @Path("/getRuleItems")
    @Produces("application/json")
    public Rule[] getRuleItems(Rule[] rules) throws DMServiceException;
}
