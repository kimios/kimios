package org.kimios.services.utils;

import org.apache.camel.CamelContextAware;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.DataTransactionWrapper;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface CamelToolInterface extends CamelContextAware {
    public InputStream generateCsv(List documentList) throws IOException;

    public void sendUpdateNotice(UpdateNoticeMessage updateNoticeMessage);

    void sendData(DataMessage dataMessage);

    void generateShareDmsEvent(org.kimios.kernel.ws.pojo.Share share, Session session);

    void launchDocumentVersionConversion(DataTransactionWrapper dataTransactionWrapper);
}
