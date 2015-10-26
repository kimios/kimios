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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.core;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabien Alin
 */
public class ParametersExtractor {

    private static Logger log = LoggerFactory.getLogger(ParametersExtractor.class);

    public static Map<String, String> getParams(HttpServletRequest req){
        Map<String, String> parameters = new HashMap<String, String>();
        Enumeration<String> params = req.getParameterNames();
        while(params.hasMoreElements()){
            String p = params.nextElement();
            parameters.put(p, StringUtils.join(req.getParameterValues(p), ','));
            log.debug(p + " " + req.getParameter(p));
        }
        return parameters;
    }

}

