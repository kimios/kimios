package org.kimios.websocket;

import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.Message;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

public interface IKimiosWebSocketController {

    void sendUpdateNotice(String SessionId, UpdateNoticeMessage updateNoticeMessage);

    void sendData(String sessionId, DataMessage dataMessage);

    void sendKeepAliveToAll();

    public void sendMessage(String sessionId, Message message);
}
