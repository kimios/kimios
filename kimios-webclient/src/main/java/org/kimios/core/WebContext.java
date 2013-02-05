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

package org.kimios.core;

import org.kimios.core.exceptions.CoreMessageException;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 *
 * @author Fabien Alin
 */
public class WebContext {
    
    private static long nbinstance = 0;
    
    public static ThreadLocalWeb contexts = new ThreadLocalWeb();
  
  private HttpSession session;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private ServletContext servletContext;
    private String sessionUid;
  
  public WebContext(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext) throws CoreMessageException{
    this.session = req.getSession();
    this.response = resp;
    this.request = req;
    this.servletContext = servletContext;
        this.sessionUid = null;
        this.sessionUid = (String)req.getSession().getAttribute("session_uid");
        nbinstance++;
  }

    @Override
    protected void finalize() throws Throwable {
        contexts.remove();
        nbinstance--;
        super.finalize();
    }
        
    public static void rm(){
        try{
            contexts.get().session = null;
            contexts.get().request = null;
            contexts.get().response = null;
            contexts.get().sessionUid = null;
            contexts.get().servletContext = null;
            contexts.remove();
        }catch(Exception e){
            
        }
    }
        
    public static void logout(){
        HttpServletResponse resp = contexts.get().response;
          try{
                    contexts.get().request.getSession().removeAttribute("session_uid");
                    contexts.get().request.getSession(false).invalidate();
                    resp.sendRedirect(contexts.get().request.getContextPath() + "/jsp/login.jsp");
            }catch(Exception e){}
    }
  public Locale getLocale() {
    return null;
  }
  public static HttpSession getSession() {
    return contexts.get().session;
  }
  public static HttpServletRequest getRequest() {
    return contexts.get().request;
  }
  public static HttpServletResponse getResponse() {
                HttpServletResponse resp = contexts.get().response;
                resp.setCharacterEncoding("UTF-8");
                return resp;
  }
  public static WebContext getContext() {
    return contexts.get();
  }
  public static ServletContext getServletContext() {
    return contexts.get().servletContext;
  }
    public static String getDmsSessionUid() throws Exception {
        if(contexts.get().sessionUid != null)
    return contexts.get().sessionUid;
        else
            throw new Exception("DMS Authentication Error");
  }
        
    public static Cookie getCookie(String name) throws Exception{
        Cookie[] cookies = contexts.get().request.getCookies();
        Cookie item = null;
        for(Cookie c: cookies){
            if(c.getName().equalsIgnoreCase(name)){
                item = c;
                break;
            }
        }
        return item;
    }
}


