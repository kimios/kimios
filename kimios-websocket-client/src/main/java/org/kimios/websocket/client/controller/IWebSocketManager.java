package org.kimios.websocket.client.controller;

import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

public interface IWebSocketManager {

    void sendUpdateNotice(UpdateNoticeMessage updateNoticeMessage);

    void sendData(DataMessage dataMessage);

    public void display(UpdateNoticeMessage updateNoticeMessage);
}
