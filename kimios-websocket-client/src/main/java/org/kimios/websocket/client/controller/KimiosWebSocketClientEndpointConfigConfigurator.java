package org.kimios.websocket.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KimiosWebSocketClientEndpointConfigConfigurator extends ClientEndpointConfig.Configurator {

    private Logger logger = LoggerFactory.getLogger(KimiosWebSocketClientEndpointConfigConfigurator.class);

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        headers.put("Upgrade", Arrays.asList("websocket"));
        headers.put("Cache-Control", Arrays.asList("no-cache"));
        headers.put("Connection", Arrays.asList("Upgrade"));
    }

    @Override
    public void afterResponse(HandshakeResponse hr){
        //introspect the handshake response
        logger.debug("handshake response");
        hr.getHeaders().entrySet().forEach(stringListEntry ->
                logger.debug(stringListEntry.getKey() + " : " + stringListEntry.getValue()));
    }
}
