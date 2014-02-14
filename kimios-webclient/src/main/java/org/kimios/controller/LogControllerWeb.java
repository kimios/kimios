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
import org.kimios.kernel.ws.pojo.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogControllerWeb extends Controller {

    public LogControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    @Override
    public String execute() throws Exception {

        if (action.equals("getDocumentLogs")) {
            return getDocumentLogs(parameters);
        }

        return "";
    }

    private String getDocumentLogs(Map<String, String> parameters) throws Exception {
        long documentUid = Long.parseLong(String.valueOf(parameters.get("documentUid")));
        List<Map<String, Object>> logs = new ArrayList<Map<String, Object>>();
        for (Log l : logController.getDocumentLogs(sessionUid, documentUid)) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("date", l.getDate().getTime());
            m.put("user", l.getUser());
            m.put("userSource", l.getUserSource());
            m.put("operation", l.getOperation());
            logs.add(m);
        }
        return "{logs:" + new JSONSerializer().serialize(logs) + "}";
    }
}

