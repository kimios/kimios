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

package org.kimios.converter.impl;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import static org.asciidoctor.Asciidoctor.Factory.create;

import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.internal.JRubyAsciidoctor;
import org.jruby.RubyInstanceConfig;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.jruby.javasupport.JavaEmbedUtils;
import org.kimios.converter.exceptions.BadInputSource;
import org.kimios.exceptions.ConverterException;
import org.kimios.api.InputSource;
import org.kimios.converter.source.InputSourceFactory;
import org.kimios.converter.ConverterImpl;
import org.osgi.framework.FrameworkUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by farf on 06/01/16.
 */
public class AsciiDocToPDF extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"asciidoc"};
    private static final String OUTPUT_EXTENSION = "pdf";

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        if (!Arrays.asList(INPUT_EXTENSIONS).contains(source.getType())) {
            throw new BadInputSource(this);
        }

        FileOutputStream fos = null;
        try {


            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            String fileName = FileNameGenerator.generate();
            // Convert file located to sourcePath into HTML web content
            String targetPath = temporaryRepository + "/" +
                    fileName + "_dir";
            File targetFld = new File(targetPath);
            targetFld.mkdirs();

            targetPath +=  "/" + fileName + "." + OUTPUT_EXTENSION;




            Asciidoctor asciidoctor = null;
            /*try{
                OSGiScriptingContainer container = new OSGiScriptingContainer(FrameworkUtil.getBundle(JRubyAsciidoctor.class));
                asciidoctor = Asciidoctor.Factory.create(container.getOSGiBundleClassLoader());
            } catch (Exception ex){*/
                /*RubyInstanceConfig config = new RubyInstanceConfig();
                config.setLoader(this.getClass().getClassLoader());
                JavaEmbedUtils.initialize(Arrays.asList("META-INF/jruby.home/lib/ruby/2.0",
                        "gems/asciidoctor-1.5.4/lib",
                        "gems/asciidoctor-pdf-1.5.0.alpha.11/lib",
                        "gems/addressable-2.4.0/lib",
                        "gems/afm-0.2.2/lib",
                        "gems/Ascii85-1.0.2/lib",
                        "gems/css_parser-1.3.7/lib",
                        "gems/hashery-2.1.1/lib",
                        "gems/pdf-core-0.4.0/lib",
                        "gems/pdf-reader-1.3.3/lib",
                        "gems/polyglot-0.3.5/lib",
                        "gems/prawn-1.3.0/lib",
                        "gems/prawn-icon-1.0.0/lib",
                        "gems/prawn-svg-0.21.0/lib",
                        "gems/prawn-table-0.2.2/lib",
                        "gems/prawn-templates-0.0.3.0/lib",
                        "gems/rouge-1.10.1/lib",
                        "gems/ruby-rc4-0.1.5/lib",
                        "gems/safe_yaml-1.0.4/lib",
                        "gems/treetop-1.5.3/lib",
                        "gems/ttfunk-1.4.0/lib"), config);  */
                //asciidoctor = Asciidoctor.Factory.create(JRubyAsciidoctor.class.getClassLoader());
                asciidoctor = Asciidoctor.Factory.create(new OSGiScriptingContainer(FrameworkUtil.getBundle(JRubyAsciidoctor.class)).getOSGiBundleClassLoader(), "gems");
                /*     OSGiScriptingContainer container = new OSGiScriptingContainer(FrameworkUtil.getBundle(JRubyAsciidoctor.class));
                    asciidoctor = Asciidoctor.Factory.create(container.getOSGiBundleClassLoader());*/
            //}
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("backend", "pdf");

            asciidoctor.convert(
                    new InputStreamReader(source.getInputStream(), Charset.forName("UTF-8")),
                    new OutputStreamWriter(new FileOutputStream(targetPath), Charset.forName("UTF-8")),
                    OptionsBuilder.options().backend("pdf"));

            // Return HTML-based InputSource
            InputSource result = InputSourceFactory.getInputSource(targetPath, fileName);
            result.setHumanName(source.getName() + "_" + source.getType() + "." + OUTPUT_EXTENSION);
            /*
                Set url, to use in cache.
             */
            result.setPublicUrl(targetPath);
            result.setMimeType(this.converterTargetMimeType());




            return result;

        } catch (Exception e) {
            throw new ConverterException(e);

        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    @Override
    public InputSource convertInputSources(List<InputSource> sources) throws ConverterException {
        if (sources.size() == 1) {
            return convertInputSource(sources.get(0));
        } else
            throw new ConverterException("Converter " + this.getClass().getName() + " cannot process many versions at once");

    }

    @Override
    public String converterTargetMimeType() {
        return "application/pdf";
    }
}
