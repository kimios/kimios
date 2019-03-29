package org.kimios.services.utils;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.kimios.kernel.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CamelTool implements CamelContextAware {


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

}
