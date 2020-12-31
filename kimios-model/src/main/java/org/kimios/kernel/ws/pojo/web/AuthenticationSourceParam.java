package org.kimios.kernel.ws.pojo.web;

import org.kimios.kernel.ws.pojo.AuthenticationSource;

import java.util.Map;

public class AuthenticationSourceParam extends AuthenticationSource {

    private String sessionId;
    private Map<String, String> parameters;

    public AuthenticationSourceParam() {
    }

    public AuthenticationSourceParam(String sessionId, Map<String, String> params) {
        this.sessionId = sessionId;
        this.parameters = params;
    }

    public AuthenticationSourceParam(String name, String className, Boolean enableSso, Boolean enableMailCheck, String sessionId, Map<String, String> params) {
        super(name, className, enableSso, enableMailCheck);
        this.sessionId = sessionId;
        this.parameters = params;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> params) {
        this.parameters = params;
    }
}
