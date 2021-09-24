package org.kimios.kernel.ws.pojo;

public class UpdateNoticeMessage {
    private UpdateNoticeType updateNoticeType;
    private String token;
    private String message;
    private String sessionId;

    public UpdateNoticeMessage(UpdateNoticeType updateNoticeType, String token, String sessionId) {
        this.updateNoticeType = updateNoticeType;
        this.token = token;
        this.sessionId = sessionId;
    }

    public UpdateNoticeMessage(UpdateNoticeType updateNoticeType, String token, String sessionId, String message) {
        this.updateNoticeType = updateNoticeType;
        this.token = token;
        this.sessionId = sessionId;
        this.message = message;
    }

    @Override
    public String toString() {
        return "{ updateNoticeType: " + updateNoticeType.getValue() + " ; "
                + "token: " + token + " ; "
                + "sessionId: " + sessionId + " ; "
                + "message: " + message + " ; "
                + " }";
    }

    public UpdateNoticeType getUpdateNoticeType() {
        return updateNoticeType;
    }

    public void setUpdateNoticeType(UpdateNoticeType updateNoticeType) {
        this.updateNoticeType = updateNoticeType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
