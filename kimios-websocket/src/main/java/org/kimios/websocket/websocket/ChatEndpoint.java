package org.kimios.websocket.websocket;

import com.google.gson.Gson;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessageDecoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessageEncoder;
import org.kimios.websocket.IKimiosWebSocketController;
import org.kimios.websocket.model.Message;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
    private static final HashMap<String, Session> webSocketSessions = new HashMap<>();

    private ISecurityController securityController;
    private Gson gson = new Gson();

    public ChatEndpoint() {
        try {
            Context context = new InitialContext();
            this.securityController = (ISecurityController) context
                    .lookup("osgi:service/org.kimios.kernel.controller.ISecurityController");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        System.out.println("We are in init() of ChatEndpoint");
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {

        String kimiosSessionUid = this.securityController.checkWebSocketToken(username);
        if (kimiosSessionUid == null) {
            return;
        }

        this.session = session;
        chatEndpoints.add(this);
        chatEndpointsMap.put(username, this);
        users.put(session.getId(), username);

        webSocketSessions.put(kimiosSessionUid, session);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        this.sendMessage(message);
    }

    @OnMessage
    public void onMessage(Session session, UpdateNoticeMessage message) throws IOException, EncodeException {
        // message.setFrom(users.get(session.getId()));
        System.out.println("IKimiosWebSocketController: received message : " + message.toString());

        this.sendUpdateNotice(message.getSessionId(), message);
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

    private void sendMessage(Message message) throws IOException, EncodeException {
        try {
            session.getBasicRemote()
                    .sendObject(gson.toJson(message, Message.class));
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendUpdateNotice(String sessionId, UpdateNoticeMessage updateNoticeMessage) {
        try {
            // ChatEndpoint chatEndpoint = chatEndpointsMap.get(sessionId);
            Session sessionDestination = webSocketSessions.get(sessionId);
            if (sessionDestination == null) {
                return;
            }
            updateNoticeMessage.clearSessionId();
            synchronized (sessionDestination) {
                sessionDestination.getBasicRemote()
                        .sendObject(gson.toJson(updateNoticeMessage, UpdateNoticeMessage.class));
            }
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }
}
