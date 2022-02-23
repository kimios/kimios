package org.kimios.kernel.ws.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class DataMessageEncoder implements Encoder.Text<DataMessage> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String encode(DataMessage message) throws EncodeException {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new EncodeException(e, "in DataMessageEncoder");
        }
        return json;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
