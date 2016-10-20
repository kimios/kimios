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

package org.kimios.converter.impl.docx4j.osgi;

import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.common.AbstractWriterRegistry;
import org.docx4j.convert.out.common.Exporter;
import org.docx4j.convert.out.common.WmlXsltExporterDelegate;
import org.docx4j.convert.out.html.AbstractHTMLExporter3;
import org.docx4j.convert.out.html.HTMLConversionContext;
import org.docx4j.convert.out.html.HTMLExporterXslt;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.kimios.api.Converter;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by farf on 09/01/16.
 */
public class HTMLExporterOsgi extends AbstractHTMLExporter3 {


    private static final Logger log = LoggerFactory.getLogger(HTMLExporterOsgi.class);

    /**
     * Usual use case.  Ordinarily you'd use the Docx4J facade,
     * rather than using this constructor directly.
     */
    public HTMLExporterOsgi() {
        super(new HTMLExporterXsltDelegate());
    }

    /**
     * using a customised WriterRegistry
     *
     * @param writerRegistry
     */
    public HTMLExporterOsgi(AbstractWriterRegistry writerRegistry) {
        super(new HTMLExporterXsltDelegate(), writerRegistry);
    }

    protected static final String PROPERTY_HTML_OUTPUT_TYPE =
            "docx4j.Convert.Out.HTML.OutputMethodXML";

    protected static final String XHTML_TEMPLATE_RESOURCE =
            "org/docx4j/convert/out/html/docx2xhtml.xslt";
    protected static final String HTML_TEMPLATE_RESOURCE =
            "org/docx4j/convert/out/html/docx2html.xslt";

    protected static final String XSLT_RESOURCE_ROOT =
            "org/docx4j/convert/out/html/";

    protected static final URIResolver RESOURCES_URI_RESOLVER =
            new OutHtmlURIResolver();


    protected static class OutHtmlURIResolver implements URIResolver {
        @Override
        public Source resolve(String href, String base) throws TransformerException {
            try{

                URL url = FrameworkUtil.getBundle(Converter.class)
                        .getResource(XSLT_RESOURCE_ROOT + href);
                return new StreamSource(url.openStream());


            }   catch (NoClassDefFoundError ex){
                log.warn("not in osgi environment. will load as usual");
                try {
                return new StreamSource(
                        org.docx4j.utils.ResourceUtils.getResource(
                                XSLT_RESOURCE_ROOT + href));
                } catch (IOException e) {
                    throw new TransformerException(e);
                }
            }

                catch (Exception ex){
                try {
                    return new StreamSource(
                            org.docx4j.utils.ResourceUtils.getResource(
                                    XSLT_RESOURCE_ROOT + href));
                } catch (IOException e) {
                    throw new TransformerException(e);
                }
            }



        }
    }

    protected static class HTMLExporterXsltDelegate extends WmlXsltExporterDelegate<HTMLSettings, HTMLConversionContext> {
        public HTMLExporterXsltDelegate() {
            super(null);
        }

        @Override
        protected Templates loadDefaultTemplates() throws Docx4JException {
            Source xsltSource = null;
            Templates ret = null;
            URIResolver originalURIResolver = null;
            try {
                originalURIResolver = XmlUtils.getTransformerFactory().getURIResolver();

                // TODO FIXME - partially thread safe,
                // loading of Templates in the delegates is synchronized on the
                // XmlUtils.getTransformerFactory() but other parts of the application
                // are not.


                TransformerFactory factory = TransformerFactory.newInstance(
                        org.apache.xalan.processor.TransformerFactoryImpl.class.getName(), Docx4J.class.getClassLoader());

                factory.setURIResolver(RESOURCES_URI_RESOLVER);
                XmlUtils.getTransformerFactory().setURIResolver(RESOURCES_URI_RESOLVER);
                if (Docx4jProperties.getProperty(PROPERTY_HTML_OUTPUT_TYPE, true)){
                    log.info("Outputting well-formed XHTML..");
                    defaultTemplatesResource = XHTML_TEMPLATE_RESOURCE;
                } else {
                    log.info("Outputting HTML tag soup..");
                    defaultTemplatesResource = HTML_TEMPLATE_RESOURCE;
                }
                try{

                    URL url = FrameworkUtil.getBundle(Converter.class)
                            .getResource(defaultTemplatesResource);
                    xsltSource = new StreamSource(url.openStream());
                }
                catch (NoClassDefFoundError ex){
                    log.warn("not in osgi environment. will load as usual");
                    xsltSource = new StreamSource(org.docx4j.utils.ResourceUtils.getResource(
                            defaultTemplatesResource));
                }
                catch (Exception ex){
                    xsltSource = new StreamSource(org.docx4j.utils.ResourceUtils.getResource(
                            defaultTemplatesResource));
                }

                ret = XmlUtils.getTransformerTemplate(xsltSource);
            } catch (IOException e) {
                throw new Docx4JException("Exception loading template \"" + defaultTemplatesResource + "\", " + e.getMessage(), e);
            } catch (TransformerConfigurationException e) {
                throw new Docx4JException("Exception loading template \"" + defaultTemplatesResource + "\", " + e.getMessage(), e);
            }
            finally {
                XmlUtils.getTransformerFactory().setURIResolver(originalURIResolver);
            }
            return ret;
        }

        @Override
        protected Document getSourceDocument(HTMLSettings conversionSettings, HTMLConversionContext conversionContext) throws Docx4JException {
            WordprocessingMLPackage wmlPackage = conversionContext.getWmlPackage();
            //TODO: the docx2xhtml-core.xslt only knows about the MainDocumentPart, therefore it's
            //unable to process any sections....
            return XmlUtils.marshaltoW3CDomDocument(wmlPackage.getMainDocumentPart().getJaxbElement());
        }

        @Override
        public void process(HTMLSettings conversionSettings, HTMLConversionContext conversionContext, OutputStream outputStream) throws Docx4JException {
            Document domDoc = getSourceDocument(conversionSettings, conversionContext);
            Templates templates = getTemplates(conversionSettings, conversionContext);
            Result intermediateResult = new StreamResult(outputStream);
            XmlUtils.transform(domDoc, templates, conversionContext.getXsltParameters(), intermediateResult);
        }
    }

    protected static HTMLExporterOsgi instance = null;

    public static Exporter<HTMLSettings> getInstance() {
        if (instance == null) {
            synchronized(HTMLExporterXslt.class) {
                if (instance == null) {
                    instance = new HTMLExporterOsgi();
                }
            }
        }
        return instance;
    }
}
