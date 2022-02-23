package org.kimios.kernel.ws.pojo;

public class Message {
    private String token;
    private String sessionId;

    public Message() {
    }

    public Message(String token, String sessionId) {
        this.token = token;
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void clearSessionId() {
        this.sessionId = null;
    }

    public void clearToken() {
        this.token = null;
    }
}
