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

package org.kimios.kernel.security.sso;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.kimios.kernel.security.Authenticator;

import java.util.Map;

public class CasAuthenticator implements Authenticator {


    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CasAuthenticator.class);


    public String authenticate(String externalToken) throws Exception {
        return validateTicket(externalToken);
    }

    public boolean authenticate(String user, String password, Map<String, Object> additionnalParameters) throws Exception{
        return casUtils.validateAuthentication(user, password, serviceUrl);
    }

    public boolean disconnect(String tgt) throws Exception {
        casUtils.disconnect(tgt);
        return true;
    }


    private String casServerUrl;

    protected String validateTicket(String ticket) {
        AttributePrincipal principal = null;
        Cas20ServiceTicketValidator sv = new Cas20ServiceTicketValidator(casServerUrl);
        try {
            String legacyServerServiceUrl = serviceUrl;
            Assertion a = sv.validate(ticket, legacyServerServiceUrl);
            principal = a.getPrincipal();
            log.info("Identified user from cas " + a.getPrincipal());


        } catch (TicketValidationException e) {
            log.error(ticket +" not validated", e);
        }
        return principal.getName();
    }



    public String getCasServerUrl() {
        return casServerUrl;
    }

    public void setCasServerUrl(String casServerUrl) {
        this.casServerUrl = casServerUrl;
    }



    private String serviceUrl;

    private CasUtils casUtils;




    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public CasUtils getCasUtils() {
        return casUtils;
    }

    public void setCasUtils(CasUtils casUtils) {
        this.casUtils = casUtils;
    }
}
