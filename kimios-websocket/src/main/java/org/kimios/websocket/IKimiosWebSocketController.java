package org.kimios.websocket;

import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

public interface IKimiosWebSocketController {

    void sendUpdateNotice(String SessionId, UpdateNoticeMessage updateNoticeMessage);

    void sendKeepAliveToAll();
}
