/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.editors.impl.etherpad;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpCookie;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * Created by farf on 18/01/16.
 */
public class T3Servlet extends ProxyServlet  {

    private static Logger logger = LoggerFactory.getLogger(T3Servlet.class);


    private String targetUrl;

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    protected String rewriteTarget(HttpServletRequest clientRequest)
    {
        StringBuffer target = new StringBuffer(targetUrl);
        target.append(clientRequest.getPathInfo());
        String query = clientRequest.getQueryString();
        if (query != null)
            target.append("?").append(query);
        return target.toString();
    }


    @Override
    protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest)
    {
        proxyRequest.getHeaders().remove("Host");
        proxyRequest.getHeaders().remove("Cookie");
        for(Cookie c: clientRequest.getCookies()){
            if(c.getName().endsWith("sessionID") || c.getName().endsWith("authorID")){
                HttpCookie coo = new HttpCookie(c.getName(), c.getValue());
                coo.setValue(c.getValue());
                proxyRequest = proxyRequest.cookie(coo);
            } else {
                if(c.getName().equals("io") || c.getName().equals("express_sid") || c.getName().equals("sid")){
                    HttpCookie coo = new HttpCookie(c.getName(), c.getValue());
                    proxyRequest  = proxyRequest.cookie(coo);
                }
            }
        }
        proxyRequest.send(newProxyResponseListener(clientRequest, proxyResponse));
    }

}
