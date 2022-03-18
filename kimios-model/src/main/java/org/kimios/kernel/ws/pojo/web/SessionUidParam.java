package org.kimios.kernel.ws.pojo.web;

public class SessionUidParam {
    private String sessionUid;
    private String webSocketToken;

    public SessionUidParam(String sessionUid, String webSocketToken) {
        this.sessionUid = sessionUid;
        this.webSocketToken = webSocketToken;
    }

    public String getSessionUid() {
        return sessionUid;
    }

    public String getWebSocketToken() {
        return webSocketToken;
    }
}
