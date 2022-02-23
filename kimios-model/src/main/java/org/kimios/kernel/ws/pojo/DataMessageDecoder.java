package org.kimios.kernel.ws.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public class DataMessageDecoder implements Decoder.Text<DataMessage> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DataMessage decode(String s) throws DecodeException {
        DataMessage dataMessage = null;
        try {
            dataMessage = objectMapper.readValue(s, DataMessage.class);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new DecodeException("ioException", "in DataMessageDecoder");
        }
        return dataMessage;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
