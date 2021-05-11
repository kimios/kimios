package org.kimios.websocket.websocket;

import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeType;
import org.kimios.websocket.IKimiosWebSocketController;
import org.kimios.websocket.model.Message;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(
        value = "/chat/{username}",
        decoders = { MessageDecoder.class, UpdateNoticeMessageDecoder.class },
        encoders = { MessageEncoder.class, UpdateNoticeMessageEncoder.class }
)
public class ChatEndpoint implements IKimiosWebSocketController {
    private Session session;
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static final Map<String, ChatEndpoint> chatEndpointsMap = new HashMap<String, ChatEndpoint>();
    private static HashMap<String, String> users = new HashMap<>();

    public ChatEndpoint() {
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {

        this.session = session;
        chatEndpoints.add(this);
        chatEndpointsMap.put(username, this);
        users.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        broadcast(message);

        /*UpdateNoticeMessage updateNoticeMessage = new UpdateNoticeMessage(UpdateNoticeType.SHARES_BY_ME);
        sendUpdateNotice(username, updateNoticeMessage);*/
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        message.setFrom(users.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message) throws IOException, EncodeException {
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote()
                        .sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void sendUpdateNotice(String sessionId, UpdateNoticeMessage updateNoticeMessage) {
        try {
            ChatEndpoint chatEndpoint = chatEndpointsMap.get(sessionId);
            synchronized (chatEndpoint) {
                chatEndpoint.session.getBasicRemote()
                        .sendObject(updateNoticeMessage);
            }
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
