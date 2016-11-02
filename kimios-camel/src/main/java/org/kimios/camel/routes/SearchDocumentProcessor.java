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
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.index.controller.ISearchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by farf on 27/10/16.
 */
public class SearchDocumentProcessor implements Processor {

    private static Logger logger = LoggerFactory.getLogger(SearchDocumentProcessor.class);

    private ISearchController searchController;

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("processing body {}", exchange.getIn());
        List<DMEntity> entities = exchange.getIn().getBody(List.class);
        logger.info("entities count {}", entities);
    }
}
