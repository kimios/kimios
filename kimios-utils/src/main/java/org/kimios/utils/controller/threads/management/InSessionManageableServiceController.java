package org.kimios.utils.controller.threads.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class InSessionManageableServiceController extends ManageableServiceController {

    private static Logger logger = LoggerFactory.getLogger(InSessionManageableServiceController.class);

    private String login;
    private String domain;

    public InSessionManageableServiceController(String serviceName, long initialDelay, long period, TimeUnit periodTimeUnit) {
        super(serviceName, initialDelay, period, periodTimeUnit);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
