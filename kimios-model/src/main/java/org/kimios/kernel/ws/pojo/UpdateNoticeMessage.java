package org.kimios.kernel.ws.pojo;

public class UpdateNoticeMessage {
    private UpdateNoticeType updateNoticeType;
    private String token;

    public UpdateNoticeMessage(UpdateNoticeType updateNoticeType, String token) {
        this.updateNoticeType = updateNoticeType;
        this.token = token;
    }

    @Override
    public String toString() {
        return "{ updateNoticeType: " + UpdateNoticeType.SHARES_BY_ME.getValue() + " ; "
                + "token: " + token + " }";
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
}
