package org.kimios.kernel.ws.pojo;

public class UpdateNoticeMessage {
    private UpdateNoticeType updateNoticeType;

    public UpdateNoticeMessage(UpdateNoticeType updateNoticeType) {
        this.updateNoticeType = updateNoticeType;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public UpdateNoticeType getUpdateNoticeType() {
        return updateNoticeType;
    }

    public void setUpdateNoticeType(UpdateNoticeType updateNoticeType) {
        this.updateNoticeType = updateNoticeType;
    }
}
