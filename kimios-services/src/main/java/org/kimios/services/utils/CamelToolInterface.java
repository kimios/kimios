package org.kimios.services.utils;

import org.apache.camel.CamelContextAware;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface CamelToolInterface extends CamelContextAware {
    public InputStream generateCsv(List documentList) throws IOException;

    public void sendUpdateNotice(UpdateNoticeMessage updateNoticeMessage);
}
