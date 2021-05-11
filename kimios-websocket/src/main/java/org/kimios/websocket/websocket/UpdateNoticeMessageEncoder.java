package org.kimios.websocket.websocket;

import com.google.gson.Gson;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class UpdateNoticeMessageEncoder implements Encoder.Text<UpdateNoticeMessage> {

    private static Gson gson = new Gson();

    @Override
    public String encode(UpdateNoticeMessage message) throws EncodeException {
        String json = gson.toJson(message);
        return json;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
