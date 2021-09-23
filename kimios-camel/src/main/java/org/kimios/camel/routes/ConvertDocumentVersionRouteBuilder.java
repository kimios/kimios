package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.kimios.converter.controller.IConverterController;
import org.kimios.kernel.ws.pojo.DataTransactionWrapper;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeType;

public class ConvertDocumentVersionRouteBuilder extends RouteBuilder {

    private IConverterController converterController;

    public IConverterController getConverterController() {
        return converterController;
    }

    public void setConverterController(IConverterController converterController) {
        this.converterController = converterController;
    }

    @Override
    public void configure() throws Exception {
        from("direct:documentVersionConversion")
                .process(new ConversionProcessor(this.converterController))
                .to("bean:webSocketManager?method=sendUpdateNotice");
    }

    private class ConversionProcessor implements Processor {
        private IConverterController converterController;

        public ConversionProcessor(IConverterController converterController) {
            super();
            this.converterController = converterController;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            DataTransactionWrapper dataTransactionWrapper =
                    exchange.getIn().getBody(DataTransactionWrapper.class);

            long dataTransactionUid = this.converterController.convertDataTransactionToPdf(dataTransactionWrapper);

            UpdateNoticeMessage updateNoticeMessage = new UpdateNoticeMessage(
                    UpdateNoticeType.PREVIEW_READY,
                    String.valueOf(dataTransactionUid)
            );
            exchange.getOut().setBody(updateNoticeMessage);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
