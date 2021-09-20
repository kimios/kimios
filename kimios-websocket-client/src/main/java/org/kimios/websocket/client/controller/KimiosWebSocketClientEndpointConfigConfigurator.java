package org.kimios.websocket.client.controller;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KimiosWebSocketClientEndpointConfigConfigurator extends ClientEndpointConfig.Configurator {

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        headers.put("Upgrade", Arrays.asList("websocket"));
        headers.put("Cache-Control", Arrays.asList("no-cache"));
        headers.put("Connection", Arrays.asList("Upgrade"));
    }

    @Override
    public void afterResponse(HandshakeResponse hr){
        //introspect the handshake response
        System.out.println(hr.getHeaders());
    }
}
