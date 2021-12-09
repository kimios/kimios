package org.kimios.websocket.client.controller.impl;

import org.eclipse.jetty.websocket.jsr356.JettyClientContainerProvider;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessageDecoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessageEncoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeType;
import org.kimios.websocket.client.controller.IWebSocketManager;
import org.kimios.websocket.client.controller.KimiosWebSocketClientEndpointConfigConfigurator;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;


@ClientEndpoint(
        configurator = KimiosWebSocketClientEndpointConfigConfigurator.class,
        decoders = { UpdateNoticeMessageDecoder.class },
        encoders = { UpdateNoticeMessageEncoder.class }
)
public class WebSocketManager implements IWebSocketManager {

    private String webSocketUrl;

    private Session session;

    private WebSocketContainer containerProvider;

    public WebSocketManager(){}

    public void init() {
        System.out.println("websocket manager started");
    }

    public void connect(String connectionUrl) {
        WebSocketContainer container = this.getWebSocketContainer();
        try {
            this.session = container.connectToServer(WebSocketManager.class, URI.create(connectionUrl));
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        this.session = session;
    }

    //response
    @OnMessage
    public void onMessage(UpdateNoticeMessage message) {
        System.out.println("Received response in client from server: " + message.toString());

        // handle ping, response pong
        if (message.getUpdateNoticeType() == UpdateNoticeType.KEEP_ALIVE_PING) {
            this.sendUpdateNotice(new UpdateNoticeMessage(UpdateNoticeType.KEEP_ALIVE_PONG, null, null));
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void sendUpdateNotice(UpdateNoticeMessage updateNoticeMessage) {
        if (this.session == null || !this.session.isOpen()) {
            String urlConnection = this.webSocketUrl + "/" + updateNoticeMessage.getToken();
            this.connect(urlConnection);
        }
        try {
            session.getBasicRemote().sendObject(updateNoticeMessage);
        } catch (IOException e) {
            System.out.println("is webSocket open (session.isOpen())? " + (session.isOpen() ? "true" : "false"));
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getWebSocketUrl() {
        return webSocketUrl;
    }

    public void setWebSocketUrl(String webSocketUrl) {
        this.webSocketUrl = webSocketUrl;
    }

    protected WebSocketContainer getWebSocketContainer() {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(JettyClientContainerProvider.class.getClassLoader());
            return JettyClientContainerProvider.getWebSocketContainer();
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public void display(UpdateNoticeMessage updateNoticeMessage) {
        System.out.println("display() with camel : " + updateNoticeMessage.toString());
    }
}
