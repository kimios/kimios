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


import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
    public static final String ENCODING = "encoding"; //key for encoding.
    private String encoding;
    public void init(FilterConfig filterConfig) throws ServletException {
        this.encoding = filterConfig.getInitParameter(ENCODING);
        
        
    }
    public void doFilter(ServletRequest req, ServletResponse resp,
    FilterChain filterChain)   throws IOException, ServletException {
        req.setCharacterEncoding(encoding);
        resp.setContentType("text/html;charset="+encoding);
        
        filterChain.doFilter(req, resp);
    }
    public void destroy() {}
}

