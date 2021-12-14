package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.kimios.kernel.ws.pojo.Share;
import org.kimios.kernel.ws.pojo.ShareSessionWrapper;

public class GenerateShareDmsEventRouteBuilder  extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:generateShareDmsEvent")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        ShareSessionWrapper shareSessionWrapper = exchange.getIn().getBody(ShareSessionWrapper.class);

                        exchange.getOut().setBody(shareSessionWrapper);
                        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
                    }
                })
                .to("bean:documentController?method=generateShareDmsEvent");
    }
}
