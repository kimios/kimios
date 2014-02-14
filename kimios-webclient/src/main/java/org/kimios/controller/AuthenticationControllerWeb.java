/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.controller;

import flexjson.JSONSerializer;
import org.kimios.kernel.ws.pojo.AuthenticationSource;

import java.util.Map;

/**
 *
 * @author Fabien Alin
 */
public class AuthenticationControllerWeb extends Controller {

    public AuthenticationControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception {
        String jsonResp = "";
        if(action.equalsIgnoreCase("login")){
            String login = parameters.get("username");
            String password = parameters.get("password");
            String source = parameters.get("domain");
            sessionUid = securityController.startSession(login, password, source);
            return sessionUid;
        }

        if(action.equalsIgnoreCase("authenticationSources")){
            AuthenticationSource[] items = securityController.getAuthenticationSources();
            jsonResp = new JSONSerializer().exclude("class").serialize(items);
            jsonResp = "{list:" + jsonResp +"}";
        }
        return jsonResp;
    }

   
}

