/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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

package org.kimios.kernel.index.filters.impl;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.index.FileFilterException;
import org.kimios.kernel.index.filters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by farf on 10/06/15.
 */
public class ThreadedGlobalFilter implements Filter {


    private static Logger logger = LoggerFactory.getLogger(ThreadedGlobalFilter.class);


    private Map<String, Object> metaDatas;


    private long timeout;

    private TimeUnit timeUnit;

    private ExecutorService fileReadExecutor;

    public ThreadedGlobalFilter(long timeout, TimeUnit timeUnit, ExecutorService fileReadExecutor) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.fileReadExecutor = fileReadExecutor;
    }

    @Override
    public String[] handledExtensions() {
        return new String[0];
    }

    @Override
    public String[] handledMimeTypes() {
        return new String[0];
    }

    @Override
    public Object getFileBody(Document document, final InputStream inputStream) throws Throwable {
        {

            Callable<Map<String, Object>> rn = new Callable<Map<String, Object>>() {
                @Override
                public Map<String, Object> call() {

                    Map<String, Object> parsingData = new HashMap<String, Object>();


                    try {

                        Parser parser = new AutoDetectParser(); // Should auto-detect!
                        /*
                            create content handler with unlimited content length
                         */
                        ContentHandler contentHandler = new BodyContentHandler(-1);
                        Metadata metadata = new Metadata();
                        ParseContext context = new ParseContext();
                        parser.parse(inputStream, contentHandler, metadata, context);


                        Map<String, Object> parsedMetaDatas = new HashMap<String, Object>();
                        for (String m : metadata.names()) {

                            parsedMetaDatas.put(m, metadata.isMultiValued(m) ? metadata.getValues(m) : metadata.get(m));
                        }

                        String val = contentHandler.toString();
                        if (logger.isDebugEnabled()) {
                            for (String m : parsedMetaDatas.keySet()) {
                                logger.debug("Metadata {} --> {}", m, parsedMetaDatas.get(m));
                            }
                            logger.debug(val);
                        }

                        parsingData.put("metas", parsedMetaDatas);
                        parsingData.put("body", val);
                    } catch (Exception e) {
                        throw new FileFilterException(e);
                    } finally {
                        try {
                            inputStream.close();
                        } catch (Exception ex) {

                        }
                    }
                    return parsingData;
                }

            };
            try {
                Future<Map<String, Object>> futureParsingData = fileReadExecutor.submit(rn);
                Map<String, Object> item = futureParsingData.get(timeout, timeUnit);
                //set values on filter
                String parsedBody = (String) item.get("body");
                Map<String, Object> parsedMetas = (Map<String, Object>) item.get("metas");
                this.metaDatas = parsedMetas;
                return parsedBody;

            }
            catch (ExecutionException ex){
                logger.error("error while parsing document content", ex);
                throw ex.getCause();
            }
            catch (TimeoutException ex){
                logger.error("timeout parsing document content. Parsing excessed {} {}", timeout, timeUnit.name());
                throw ex;
            }
            catch (Exception ex) {
                logger.error("error while parsing document content", ex);
                throw ex;
            }
        }
    }

    @Override
    public Map<String, Object> getMetaDatas() throws FileFilterException {
        return metaDatas;
    }
}
