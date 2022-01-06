package org.kimios.websocket.websocket;

import com.google.gson.Gson;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.DataMessageEncoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessageEncoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeType;
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
        decoders = { org.kimios.kernel.ws.pojo.MessageDecoder.class },
        encoders = { MessageEncoder.class, UpdateNoticeMessageEncoder.class, DataMessageEncoder.class}
)
public class ChatEndpoint implements IKimiosWebSocketController {
    private Session session;
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static final Map<String, ChatEndpoint> chatEndpointsMap = new HashMap<String, ChatEndpoint>();
    private static HashMap<String, String> users = new HashMap<>();
    private static final HashMap<String, Session> webSocketSessions = new HashMap<>();

    private ISecurityController securityController;
    private Gson gson = new Gson();
    private int lookupAttempts = 10;

    public ChatEndpoint() {
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws NamingException, InterruptedException {
        this.lookupAttempts--;
        try {
            Context context = new InitialContext();
            this.securityController = (ISecurityController) context
                    .lookup("osgi:service/org.kimios.kernel.controller.ISecurityController");
            System.out.println("WebSocket securityController lookup successful, remaining attempts " + lookupAttempts);
        } catch (NamingException e) {
            if (this.lookupAttempts > 0) {
                Thread.sleep(10000);
                this.init();
            } else {
                throw e;
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {

        try {
            String kimiosSessionUid = this.securityController.checkWebSocketToken(username);
            if (kimiosSessionUid == null) {
                return;
            }

            this.session = session;
            chatEndpoints.add(this);
            chatEndpointsMap.put(username, this);
            users.put(session.getId(), this.securityController.getSessionUserNameAndSource(kimiosSessionUid));

            webSocketSessions.put(kimiosSessionUid, session);

            Message message = new Message();
            message.setFrom(username);
            message.setContent("Connected!");
            this.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Session session, org.kimios.kernel.ws.pojo.Message message) throws IOException, EncodeException {
        // message.setFrom(users.get(session.getId()));
        System.out.println(
                "IKimiosWebSocketController: received message : "
                        + (message == null ? "null" : message.getClass().toString() + " " + message.toString())
        );

        if (message instanceof UpdateNoticeMessage) {
            switch (((UpdateNoticeMessage) message).getUpdateNoticeType()) {
                case KEEP_ALIVE_PING:
                    this.sendKeepAliveToAll();
                    break;
                case KEEP_ALIVE_PONG:
                    this.handlePong(session, (UpdateNoticeMessage) message);
                    break;
                default:
                    this.sendUpdateNotice(message.getSessionId(), (UpdateNoticeMessage) message);
            }
        } else {
            if (message instanceof DataMessage) {
                this.sendData(message.getSessionId(), (DataMessage) message);
            }
        }
    }

    private void handlePong(Session session, UpdateNoticeMessage message) {
        final String[] kimiosSessionId = {null};
        webSocketSessions.forEach((kimiosSessId, sess) -> {
            if (sess.getId().equals(session.getId())) {
                kimiosSessionId[0] = kimiosSessId;
            }
        });
        if (kimiosSessionId[0] == null) {
            System.out.println("WebSocket received pong from unknown session " + session.getId());
        }
        System.out.println(
                "Websocket received pong from session "
                        + session.getId()
                        + " ("
                        + users.get(session.getId())
                        + ")"
        );
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
                System.out.println(
                        "UpdateNoticeMessage ("
                                + updateNoticeMessage.getUpdateNoticeType().getValue()
                                + ") sent to "
                                + users.get(sessionDestination.getId())
                                + " (" + sessionId + ")"
                );
            }
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendData(String sessionId, DataMessage dataMessage) {
        try {
            // ChatEndpoint chatEndpoint = chatEndpointsMap.get(sessionId);
            Session sessionDestination = webSocketSessions.get(sessionId);
            if (sessionDestination == null) {
                return;
            }
            dataMessage.clearSessionId();
            synchronized (sessionDestination) {
                sessionDestination.getBasicRemote()
                        .sendObject(gson.toJson(dataMessage, DataMessage.class));
                System.out.println(
                        "DataMessage (with list size of "
                                + dataMessage.getDmEntityList().size()
                                + ") sent to "
                                + users.get(sessionDestination.getId())
                                + " (" + sessionId + ")"
                );
            }
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendKeepAliveToAll() {
        webSocketSessions.forEach((key, sessionDestination) -> {
            try {
                if (sessionDestination == null) {
                    return;
                }
                UpdateNoticeMessage updateNoticeMessage = new UpdateNoticeMessage(
                        UpdateNoticeType.KEEP_ALIVE_PING,
                        null,
                        null
                );
                synchronized (sessionDestination) {
                    sessionDestination.getBasicRemote()
                            .sendObject(gson.toJson(updateNoticeMessage, UpdateNoticeMessage.class));
                    System.out.println(
                            "UpdateNoticeMessage sent to "
                                    + users.get(sessionDestination.getId())
                                    + " (" + key + ")"
                    );
                }
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
                webSocketSessions.remove(sessionDestination);
                System.out.println("WebSocket session removed for kimios session: "
                        + key
                        + " ("
                        + users.get(sessionDestination.getId())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }
}
