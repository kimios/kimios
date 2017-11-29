package org.kimios.kernel.share.controller.impl;

import org.apache.commons.io.IOUtils;
import org.kimios.api.templates.ITemplate;
import org.kimios.api.templates.ITemplateProcessor;
import org.kimios.api.templates.ITemplateProvider;
import org.kimios.api.templates.TemplateType;
import org.kimios.kernel.share.controller.IShareTransferController;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ShareTransferController implements IShareTransferController {

    private ITemplateProcessor templateProcessor;

    private ITemplateProvider templateProvider;

    public ITemplateProcessor getTemplateProcessor() {
        return templateProcessor;
    }

    public void setTemplateProcessor(ITemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    public ITemplateProvider getTemplateProvider() {
        return templateProvider;
    }

    public void setTemplateProvider(ITemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public String buildAskPasswordResponseHtml(String formAction, Map<String, String> hiddenParams) throws Exception {
        String template = loadMailTemplate();
        if (template == null
                || template.isEmpty()) {
            InputStream inputStream = ShareTransferController.class.getClassLoader().getResourceAsStream("templates/download-document-password-form.html");
            template = Pattern.quote(IOUtils.toString(inputStream));
        }
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

    public String loadMailTemplate() throws Exception {
        ITemplate mailTemplate = templateProvider.getDefaultTemplate(TemplateType.SHARE_ASK_PASSWORD);
        return mailTemplate != null ? mailTemplate.getContent() : null;
    }
}
