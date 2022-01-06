package org.kimios.kernel.ws.pojo;

import com.google.gson.Gson;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class DataMessageDecoder implements Decoder.Text<DataMessage> {

    private static Gson gson = new Gson();

    @Override
    public DataMessage decode(String s) throws DecodeException {
        DataMessage dataMessage = gson.fromJson(s, DataMessage.class);
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
