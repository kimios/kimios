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
import org.kimios.client.controller.ExtensionController;
import org.kimios.kernel.ws.pojo.DMEntityAttribute;

import java.util.HashMap;
import java.util.Map;

public class ExtensionControllerWeb extends Controller {
	private static final String c21Id = "C21ID";

	public ExtensionControllerWeb(Map<String, String> parameters) {
		super(parameters);
	}

	public String execute() throws Exception {
		if ("getDMEntityAttribute".equalsIgnoreCase(action))
			return getDMEntityAttribute();
		return "Unknown action: " + action;
	}

	private String getDMEntityAttribute() throws Exception {
		Long uid = Long.valueOf(parameters.get("dmEntityUid"));
		ExtensionController ec = extensionController;
		DMEntityAttribute attr = ec.getDMEntityAttribute(sessionUid, uid, c21Id);
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("value", attr.getValue());
		return "["+new JSONSerializer().serialize(attrs)+"]";
	}
}
