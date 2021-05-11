package org.kimios.websocket.websocket;

import com.google.gson.Gson;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class UpdateNoticeMessageDecoder implements Decoder.Text<UpdateNoticeMessage> {

    private static Gson gson = new Gson();

    @Override
    public UpdateNoticeMessage decode(String s) throws DecodeException {
        UpdateNoticeMessage updateNoticeMessage = gson.fromJson(s, UpdateNoticeMessage.class);
        return updateNoticeMessage;
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
