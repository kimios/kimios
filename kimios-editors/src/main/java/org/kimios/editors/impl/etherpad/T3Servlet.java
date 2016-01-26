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



    /*@Override
    protected void copyRequestHeaders(HttpServletRequest clientRequest, Request proxyRequest)
    {
        // First clear possibly existing headers, as we are going to copy those from the client request.
        proxyRequest.getHeaders().clear();

        Set<String> headersToRemove = findConnectionHeaders(clientRequest);

        for (Enumeration<String> headerNames = clientRequest.getHeaderNames(); headerNames.hasMoreElements();)
        {
            String headerName = headerNames.nextElement();
            String lowerHeaderName = headerName.toLowerCase(Locale.ENGLISH);

            // Remove hop-by-hop headers.
            if (HOP_HEADERS.contains(lowerHeaderName))
                continue;
            if (headersToRemove != null && headersToRemove.contains(lowerHeaderName))
                continue;

            for (Enumeration<String> headerValues = clientRequest.getHeaders(headerName); headerValues.hasMoreElements();)
            {
                String headerValue = headerValues.nextElement();

                if(!headerName.equals(HttpHeader.COOKIE)){
                    if (headerValue != null)
                        proxyRequest.header(headerName, headerValue);
                }

            }
        }

    }*/


    private StringBuilder convertCookies(List<HttpCookie> cookies, StringBuilder builder)
    {
        for (int i = 0; i < cookies.size(); ++i)
        {
            if (builder == null)
                builder = new StringBuilder();
            if (builder.length() > 0)
                builder.append("; ");
            HttpCookie cookie = cookies.get(i);
            builder.append(cookie.toString());
        }
        return builder;
    }


    protected String rewriteTarget(HttpServletRequest clientRequest)
    {

        StringBuffer target = new StringBuffer("http://etherpad.kimios.app");

        logger.info("targetting to " + clientRequest.getPathInfo());


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
                coo.setDomain("etherpad.kimios.app");
                coo.setPath("/");
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



    /*@Override
    protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest)
    {

        //Customize

        // Pass through the upgrade and connection header fields for websocket handshake request.
        String upgradeValue = clientRequest.getHeader("Upgrade");
        if (upgradeValue != null && upgradeValue.compareToIgnoreCase("websocket") == 0)
        {
            proxyRequest = proxyRequest.header("Upgrade", upgradeValue);
            proxyRequest.header("Connection", clientRequest.getHeader("Connection"));
        }


        //add cookie


        proxyRequest.getHeaders().remove("Cookie");

        for(Cookie c: clientRequest.getCookies()){
            if(c.getName().endsWith("sessionID") || c.getName().endsWith("authorID")){
                HttpCookie coo = new HttpCookie(c.getName().substring("!Proxy!etherpadProxy".length()), c.getValue());
                coo.setValue(c.getValue());
                coo.setDomain("etherpad.kimios.app");
                coo.setPath("/");
                proxyRequest = proxyRequest.cookie(coo);
            } else {
                HttpCookie coo = new HttpCookie(c.getName(), c.getValue());
                proxyRequest  = proxyRequest.cookie(coo);
            }
        }


        logger.info(proxyRequest.getCookies() + "");

        /*String cookies = "";
        for(HttpCookie c: proxyRequest.getCookies()){
            logger.info("final cookies: {} ================+> {}", c.getName(), c.getValue());


            cookies += " " + c.getName() + "=" + c.getValue() + "; ";
        }
        if(cookies.length() > 0)
            cookies = cookies.substring(0, cookies.length() - 2);


        proxyRequest.header("Cookie", cookies);*/


        /*proxyRequest.getHeaders().remove(HttpHeader.HOST);

        proxyRequest.header(HttpHeader.HOST.asString(), "etherpad.kimios.app");



        //proxyRequest.header(HttpHeader.COOKIE.asString(), convertCookies(proxyRequest.getCookies(), null).toString());



        logger.debug("{} proxying to upstream:{}{}{}{}",
                getRequestId(clientRequest),
                System.lineSeparator(),
                proxyRequest,
                System.lineSeparator(),
                proxyRequest.getHeaders().toString().trim());



        proxyRequest.send(newProxyResponseListener(clientRequest, proxyResponse));
    }*/





    /*@Override
    protected void onResponseHeaders(HttpServletRequest request, HttpServletResponse response, Response proxyResponse)
    {
        super.onResponseHeaders(request, response, proxyResponse);

        // Restore the upgrade and connection header fields for websocket handshake request.
        HttpFields fields = proxyResponse.getHeaders();
        for (HttpField field : fields)
        {
            if (field.getName().compareToIgnoreCase("Upgrade") == 0)
            {
                String upgradeValue = field.getValue();
                if (upgradeValue != null && upgradeValue.compareToIgnoreCase("websocket") == 0)
                {
                    response.setHeader(field.getName(), upgradeValue);
                    for (HttpField searchField : fields)
                    {
                        if (searchField.getName().compareToIgnoreCase("Connection") == 0) {
                            response.setHeader(searchField.getName(), searchField.getValue());
                        }
                    }
                }
            }
        }
    } */



}
