/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.BindyAbstractDataFormat;
import org.apache.camel.dataformat.bindy.BindyAbstractFactory;
import org.apache.camel.dataformat.bindy.BindyCsvFactory;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.utils.configuration.ConfigurationManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by farf on 27/10/16.
 */

public class DocumentListExportRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        String basePath = ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) + "/csv";
        BindyCsvDataFormat csvDataFormat = new BindyCsvDataFormat(EntityDelegate.class);

        from("direct:csvExport")
                .setHeader("CamelFileName", header("kimiosCsvFileName"))
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        List<Document> docs = exchange.getIn().getBody(List.class);
                        List<EntityDelegate> items = new ArrayList<EntityDelegate>();
                        for(Document document: docs){
                            EntityDelegate outDoc = new EntityDelegate(document);
                            items.add(outDoc);
                        }

                        exchange.getOut().setBody(items);
                        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
                    }
                })
                .marshal(csvDataFormat)
                .setHeader("CamelFileName", header("kimiosCsvFileName"))
                .to("file:" + basePath + "?charset=UTF-8");
    }
}
