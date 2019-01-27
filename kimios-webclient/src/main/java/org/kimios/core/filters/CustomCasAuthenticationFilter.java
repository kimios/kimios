/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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

package org.kimios.core.filters;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.kimios.client.controller.SecurityController;
import org.kimios.controller.Controller;
import org.kimios.utils.configuration.ConfigurationManager;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by farf on 08/08/15.
 * Custom CAS Filter to disable CAS Login based on properties
 */
public class CustomCasAuthenticationFilter extends AbstractCasFilter {


    /**
     * The URL to the CAS Server login.
     */
    private String casServerLoginUrl;

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;

    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway = false;

    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            setCasServerLoginUrl(getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null));
            log.trace("Loaded CasServerLoginUrl parameter: " + this.casServerLoginUrl);
            setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
            log.trace("Loaded renew parameter: " + this.renew);
            setGateway(parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false")));
            log.trace("Loaded gateway parameter: " + this.gateway);

            final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass", null);

            if (gatewayStorageClass != null) {
                try {
                    this.gatewayStorage = (GatewayResolver) Class.forName(gatewayStorageClass).newInstance();
                } catch (final Exception e) {
                    log.error(e, e);
                    throw new ServletException(e);
                }
            }
        }
    }


    private boolean casAuthEnabled = false;

    public void init() {
        if (ConfigurationManager.getValue("client","sso.cas.enabled") != null
                && ConfigurationManager.getValue("client","sso.cas.enabled").equals("true")) {

            casAuthEnabled = true;
            String kimiosServiceUrl = ConfigurationManager.getValue("client","sso.cas.service.name");
            String casUrl = ConfigurationManager.getValue("client","sso.cas.url");
            setService(kimiosServiceUrl);
            setCasServerLoginUrl(casUrl);
        }

    }

    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

        //check if CAS is enabled

        SecurityController securityController = Controller.getSecurityController();

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        if(log.isDebugEnabled())
            log.debug(((HttpServletRequest)servletRequest).getQueryString());
        if (casAuthEnabled && (!request.getServletPath().startsWith("/__admininternal"))) {
            final HttpSession session = request.getSession(false);
            final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

            if (assertion != null) {
                filterChain.doFilter(request, response);
                return;
            }

            final String serviceUrl = constructServiceUrl(request, response);
            final String ticket = CommonUtils.safeGetParameter(request, getArtifactParameterName());
            try {
                String sessionUid = (String) request.getSession().getAttribute("sessionUid");
                if (sessionUid != null && securityController.isSessionAlive(sessionUid)) {
                    if(request.getRequestURL().toString().contains("index.jsp")){
                        response.sendRedirect(request.getContextPath() + "/logged.jsp");
                        return;
                    } else {
                        filterChain.doFilter(request, response);
                        return;
                    }

                } else if (ticket != null) {
                    try {
                        sessionUid = securityController.startSessionWithToken(ticket);
                        request.getSession().setAttribute("sessionUid", sessionUid);
                        response.addCookie(new Cookie("sessionUid", sessionUid));
                        response.sendRedirect(request.getContextPath() + "/logged.jsp");
                        return;
                    }catch (Exception ex){
                        log.error("session start with ticket " + ticket + " failed");
                    }
                }
                final boolean wasGatewayed = this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

                if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
                    filterChain.doFilter(request, response);
                    return;
                }

                final String modifiedServiceUrl;

                log.debug("no ticket and no assertion found");
                if (this.gateway) {
                    log.debug("setting gateway attribute in session");
                    modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
                } else {
                    modifiedServiceUrl = serviceUrl;
                }

                if (log.isDebugEnabled()) {
                    log.debug("Constructed service url: " + modifiedServiceUrl);
                }

                final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl, getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

                if (log.isDebugEnabled()) {
                    log.debug("redirecting to \"" + urlToRedirectTo + "\"");
                }

                response.sendRedirect(urlToRedirectTo);
            } catch (Exception ex) {
                throw new ServletException(ex);
            }

        } else {
            //standard auth
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

    }

    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
        this.gatewayStorage = gatewayStorage;
    }


}
