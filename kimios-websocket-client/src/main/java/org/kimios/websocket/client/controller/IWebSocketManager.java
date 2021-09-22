package org.kimios.websocket.client.controller;

import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

public interface IWebSocketManager {

    void sendUpdateNotice(UpdateNoticeMessage updateNoticeMessage);

    public void display(UpdateNoticeMessage updateNoticeMessage);
}
