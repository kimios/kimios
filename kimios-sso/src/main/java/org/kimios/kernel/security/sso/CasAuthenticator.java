package org.kimios.kernel.security.sso;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.kimios.kernel.security.Authenticator;

import java.util.Map;
/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 10/10/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
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
