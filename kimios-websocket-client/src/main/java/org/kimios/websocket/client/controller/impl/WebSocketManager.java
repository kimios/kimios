package org.kimios.websocket.client.controller.impl;

import org.eclipse.jetty.websocket.jsr356.JettyClientContainerProvider;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.DataMessageEncoder;
import org.kimios.kernel.ws.pojo.Message;
import org.kimios.kernel.ws.pojo.MessageDecoder;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessageEncoder;
import org.kimios.websocket.client.controller.IWebSocketManager;
import org.kimios.websocket.client.controller.KimiosWebSocketClientEndpointConfigConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        decoders = { MessageDecoder.class },
        encoders = { UpdateNoticeMessageEncoder.class, DataMessageEncoder.class }
)
public class WebSocketManager implements IWebSocketManager {

    private Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

    private String webSocketUrl;

    private Session session;

    private WebSocketContainer containerProvider;

    public WebSocketManager(){}

    public void init() {
        logger.info("websocket manager started");
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
        logger.info("Connected to endpoint: " + session.getBasicRemote());
        this.session = session;
    }

    //response
    @OnMessage
    public void onMessage(Message message) {
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
            logger.debug("is webSocket open (session.isOpen())? " + (session.isOpen() ? "true" : "false"));
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendData(DataMessage dataMessage) {
        if (this.session == null || !this.session.isOpen()) {
            String urlConnection = this.webSocketUrl + "/" + dataMessage.getToken();
            this.connect(urlConnection);
        }
        try {
            session.getBasicRemote().sendObject(dataMessage);
        } catch (IOException e) {
            logger.debug("is webSocket open (session.isOpen())? " + (session.isOpen() ? "true" : "false"));
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
        logger.debug("display() with camel : " + updateNoticeMessage.toString());
    }
}
