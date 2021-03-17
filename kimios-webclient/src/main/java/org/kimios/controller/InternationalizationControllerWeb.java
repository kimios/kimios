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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
;

/**
 * @author jludmann
 */
public class InternationalizationControllerWeb extends Controller {


    public HttpServletResponse response;

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public InternationalizationControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception {
        Properties props = new Properties();
        URL url = null;

        try {
            if (action == null)
                throw new NullPointerException();
            url = getClass().getResource(
                    "/org/kimios/i18n/client_" + action + ".properties");
            props.load(url.openStream());
        } catch (NullPointerException e) {
            url = getClass().getResource(
                    "/org/kimios/i18n/client_en.properties");
            props.load(url.openStream());
        }

        List<Map<String, String>> propsList = new ArrayList<Map<String, String>>();

        for (Object key : props.keySet()) {
            Map<String, String> propMap = new HashMap<String, String>();
            propMap.put("label", String.valueOf(key));
            propMap.put("value", String.valueOf(props.get(key)));
            propsList.add(propMap);
        }

        response.addCookie(new Cookie("selected_lang", action));
        return new JSONSerializer().serialize(propsList);
    }

}

