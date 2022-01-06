package org.kimios.services.utils;

import org.apache.camel.CamelContext;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.Share;
import org.kimios.kernel.ws.pojo.DataTransactionWrapper;
import org.kimios.kernel.ws.pojo.ShareSessionWrapper;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.utils.configuration.ConfigurationManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CamelTool implements CamelToolInterface {


    private CamelContext camelContext;

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    public InputStream generateCsv( List documentList)
            throws IOException {
        org.apache.camel.ProducerTemplate template = camelContext.createProducerTemplate();
        //read file
        String fileName = "Kimios_Export_" + new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date()) + ".csv";
        template.sendBodyAndHeader("direct:csvExport", documentList, "kimiosCsvFileName", fileName);
        return new FileInputStream(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/csv/" + fileName);

    }

    @Override
    public void sendUpdateNotice(UpdateNoticeMessage updateNoticeMessage) {
        org.apache.camel.ProducerTemplate template = camelContext.createProducerTemplate();
        template.sendBodyAndHeader(
                "direct:updateNotice",
                updateNoticeMessage,
                "header_example",
                "header_example_value"
        );
    }

    @Override
    public void sendData(DataMessage dataMessage) {
        org.apache.camel.ProducerTemplate template = camelContext.createProducerTemplate();
        template.sendBody(
                "direct:sendData",
                dataMessage
        );
    }

    @Override
    public void generateShareDmsEvent(Share share, Session session) {
        org.apache.camel.ProducerTemplate template = camelContext.createProducerTemplate();
        template.sendBody(
                "direct:generateShareDmsEvent",
                new ShareSessionWrapper(share, session)
        );
    }

    @Override
    public void launchDocumentVersionConversion(DataTransactionWrapper dataTransactionWrapper) {
        org.apache.camel.ProducerTemplate template = camelContext.createProducerTemplate();
        template.sendBodyAndHeader(
                "direct:documentVersionConversion",
                dataTransactionWrapper,
                "header_example",
                "header_example_value"
        );
    }

}
