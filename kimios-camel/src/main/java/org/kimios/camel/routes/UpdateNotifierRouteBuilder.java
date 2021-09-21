package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.kimios.kernel.ws.pojo.UpdateNoticeMessage;

public class UpdateNotifierRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:updateNotice")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        UpdateNoticeMessage updateNoticeMessage = exchange.getIn().getBody(UpdateNoticeMessage.class);

                        exchange.getOut().setBody(updateNoticeMessage);
                        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
                    }
                })
                .to("bean:webSocketManager?method=display");
    }
}
