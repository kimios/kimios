package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.kimios.kernel.ws.pojo.DataMessage;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

public class DataSenderRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:sendData")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        DataMessage dataMessage = exchange.getIn().getBody(DataMessage.class);

                        exchange.getOut().setBody(dataMessage);
                        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
                    }
                })
                .to("bean:webSocketManager?method=sendData");
    }
}
