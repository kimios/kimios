package org.kimios.websocket.websocket;

import com.google.gson.Gson;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.DataMessageEncoder;
import org.kimios.kernel.ws.pojo.Message;
import org.kimios.kernel.ws.pojo.MessageEncoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessageEncoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeType;
import org.kimios.kernel.ws.pojo.web.SessionUidParam;
import org.kimios.websocket.IKimiosWebSocketController;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private static final ConcurrentMap<String, Session> webSocketSessions = new ConcurrentHashMap<>();

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
            System.out.println(hashCode() + " WebSocket securityController lookup successful, remaining attempts " + lookupAttempts);
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
        Message message = null;
        try {
            SessionUidParam sessionUidParam = this.securityController.checkWebSocketToken(username);
            if (sessionUidParam.getSessionUid() == null) {
                return;
            }
            this.session = session;
            webSocketSessions.put(sessionUidParam.getSessionUid(), session);
            message = new Message(sessionUidParam.getWebSocketToken(), null);
            this.sendMessage(sessionUidParam.getSessionUid(), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Session session, org.kimios.kernel.ws.pojo.Message message) throws IOException, EncodeException {
        System.out.println("onMessage() " + this.hashCode());

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
        if (webSocketSessions.get(message.getToken()) == null) {
            System.out.println("WebSocket received pong from unknown session " + session.getId());
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        chatEndpoints.remove(this);
        Map.Entry<String, Session> entryToRemove = webSocketSessions.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getId().equals(session.getId()))
                .findFirst()
                .orElse(null);
        if (entryToRemove != null) {
            webSocketSessions.remove(entryToRemove.getKey());
            System.out.println("WebSocket session removed for kimios session: "
                    + entryToRemove.getKey()
                    + " ("
                    + users.get(entryToRemove.getValue().getId())
                    + ")"
            );
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    @Override
    public void sendUpdateNotice(String sessionId, UpdateNoticeMessage updateNoticeMessage) {
        System.out.println("sendUpdateNotice() " + updateNoticeMessage.getUpdateNoticeType().name() + " : " + this.hashCode());
        try {
            // ChatEndpoint chatEndpoint = chatEndpointsMap.get(sessionId);
            Session sessionDestination = webSocketSessions.get(sessionId);
            if (sessionDestination == null) {
                // System.out.println("message not sent, no websocket session found");
                return;
            }
            updateNoticeMessage.clearSessionId();
            synchronized (sessionDestination) {
                sessionDestination.getBasicRemote()
                        .sendObject(gson.toJson(updateNoticeMessage, UpdateNoticeMessage.class));
                /*System.out.println(
                        "UpdateNoticeMessage ("
                                + updateNoticeMessage.getUpdateNoticeType().getValue()
                                + ") sent to "
                                + users.get(sessionDestination.getId())
                                + " (" + sessionId + ")"
                );*/
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
                sessionDestination.getBasicRemote().sendObject(dataMessage);
                /*System.out.println(
                        "DataMessage (with list size of "
                                + dataMessage.getDmEntityList().size()
                                + ") sent to "
                                + users.get(sessionDestination.getId())
                                + " (" + sessionId + ")"
                );*/
            }
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String sessionId, Message message) {
        try {
            // ChatEndpoint chatEndpoint = chatEndpointsMap.get(sessionId);
            Session sessionDestination = webSocketSessions.get(sessionId);
            if (sessionDestination == null) {
                return;
            }
            message.clearSessionId();
            synchronized (sessionDestination) {
                sessionDestination.getBasicRemote().sendObject(message);
                /*System.out.println(
                        "DataMessage (with list size of "
                                + dataMessage.getDmEntityList().size()
                                + ") sent to "
                                + users.get(sessionDestination.getId())
                                + " (" + sessionId + ")"
                );*/
            }
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendKeepAliveToAll() {
        System.out.println("sendKeepAliveToAll() : " + this.hashCode());
        webSocketSessions.forEach((key, sessionDestination) -> {
            try {
                if (sessionDestination == null) {
                    return;
                }
                UpdateNoticeMessage updateNoticeMessage = new UpdateNoticeMessage(
                        UpdateNoticeType.KEEP_ALIVE_PING,
                        key,
                        null
                );
                synchronized (sessionDestination) {
                    /*System.out.println(
                            "sending UpdateNoticeMessage to "
                                    + users.get(sessionDestination.getId())
                                    + " (" + key + ")"
                    );
                    System.out.println("webSocketSessions.size() : " + webSocketSessions.size());
*/                    sessionDestination.getBasicRemote()
                            .sendObject(gson.toJson(updateNoticeMessage, UpdateNoticeMessage.class));
                    /*System.out.println(
                            "UpdateNoticeMessage sent to "
                                    + users.get(sessionDestination.getId())
                                    + " (" + key + ")"
                    );*/
                }
            } catch (IOException | EncodeException e) {
                // System.out.println(e.getClass());
                // System.out.println(this);
                try {
                    sessionDestination.close();
                } catch (IOException ioException) {
                    // System.out.println("catch " + ioException.getClass() + " when closing websocket session");
                }
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
