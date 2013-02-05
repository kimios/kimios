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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.core.filters;


import org.kimios.core.WebContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author Fabien Alin
 */
public class CoreFilter implements Filter {
    
    private ServletContext servletContext;
    
    public void destroy() {
        
    }

    public void init(FilterConfig arg0) throws ServletException {
        this.servletContext = arg0.getServletContext();
    }
    
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)arg0;
        HttpServletResponse resp = (HttpServletResponse)arg1;
        try{
            new WebContext(req, resp, this.servletContext);
        }catch(Exception e){
            e.printStackTrace();
           // throw new ServletException(e);
        }
        arg2.doFilter(arg0, arg1);
        WebContext.rm();
    }
}

