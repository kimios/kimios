package org.kimios.kernel.ws.pojo;

public class UpdateNoticeMessage extends Message {
    private UpdateNoticeType updateNoticeType;
    private String message;

    public UpdateNoticeMessage(UpdateNoticeType updateNoticeType, String token, String sessionId) {
        super(token, sessionId);
        this.updateNoticeType = updateNoticeType;
    }

    public UpdateNoticeMessage(UpdateNoticeType updateNoticeType, String token, String sessionId, String message) {
        super(token, sessionId);
        this.updateNoticeType = updateNoticeType;
        this.message = message;
    }

    @Override
    public String toString() {
        return "{ updateNoticeType: " + (updateNoticeType == null ? "null" : updateNoticeType.getValue()) + " ; "
                + "token: " + getToken() + " ; "
                + "sessionId: " + getSessionId() + " ; "
                + "message: " + message + " ; "
                + " }";
    }

    public UpdateNoticeType getUpdateNoticeType() {
        return updateNoticeType;
    }

    public void setUpdateNoticeType(UpdateNoticeType updateNoticeType) {
        this.updateNoticeType = updateNoticeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
