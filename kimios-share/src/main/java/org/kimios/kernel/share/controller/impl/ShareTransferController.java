package org.kimios.kernel.share.controller.impl;

import org.apache.commons.io.IOUtils;
import org.kimios.api.templates.ITemplateProcessor;
import org.kimios.kernel.share.controller.IShareTransferController;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ShareTransferController implements IShareTransferController {

    private ITemplateProcessor templateProcessor;

    public ITemplateProcessor getTemplateProcessor() {
        return templateProcessor;
    }

    public void setTemplateProcessor(ITemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    @Override
    public String buildAskPasswordResponseHtml(String formAction, Map<String, String> hiddenParams) throws Exception {
        InputStream inputStream = ShareTransferController.class.getClassLoader().getResourceAsStream("templates/download-document-password-form.html");
        String template = Pattern.quote(IOUtils.toString(inputStream));
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("formAction", formAction);
        items.put("hiddenParams", hiddenParams);
        String html;
        try {
            html = templateProcessor.processStringTemplateToString(template, items);
        } catch (Exception e) {
            throw e;
        }

        return html;
    }
}
