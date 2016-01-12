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
import org.docx4j.Docx4J;
import org.kimios.converter.impl.docx4j.osgi.Docx4jOsgi;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.common.XsltCommonFunctions;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.kimios.converter.exceptions.BadInputSource;
import org.kimios.exceptions.ConverterException;
import org.kimios.api.InputSource;
import org.kimios.converter.source.InputSourceFactory;
import org.kimios.converter.ConverterImpl;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Allows to convert .DOCX to HTML content using DocX4j
 */
public class Docx4jDocxToHTML extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"docx", "odt"};
    private static final String OUTPUT_EXTENSION = "html";

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        if (!Arrays.asList(INPUT_EXTENSIONS).contains(source.getType())) {
            throw new BadInputSource(this);
        }

        String sourcePath = null;

        try {




            // Copy given resource to temporary repository
            sourcePath = temporaryRepository + "/" + source.getName() + "_" +
                    FileNameGenerator.generate() + "." + source.getType();
            IOUtils.copyLarge(source.getInputStream(), new FileOutputStream(sourcePath));
            String fileName = FileNameGenerator.generate();
            // Convert file located to sourcePath into HTML web content
            String targetPath = temporaryRepository + "/" +
                    fileName + "_dir/" + fileName + "." + OUTPUT_EXTENSION;

            String targetPathImg = targetPath + "_img";
            File imgFolder = new File(targetPathImg);
            imgFolder.mkdirs();

            WordprocessingMLPackage wordMLPackage;
            wordMLPackage = Docx4J.load(new java.io.File(sourcePath));
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setImageDirPath(sourcePath + "_files");
            htmlSettings.setImageTargetUri(sourcePath.substring(sourcePath.lastIndexOf("/") + 1)
                    + "_files");
            htmlSettings.setWmlPackage(wordMLPackage);
            String userCSS = "html, body, div, span, h1, h2, h3, h4, h5, h6, p, a, img,  ol, ul, li, table, caption, tbody, tfoot, thead, tr, th, td " +
                    "{ margin: 0; padding: 0; border: 0;}" +
                    "body {line-height: 1;} ";
            htmlSettings.setUserCSS(userCSS);
            OutputStream os;
            os = new FileOutputStream(targetPath);

            // If you want XHTML output
            Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);


            Thread.currentThread().setContextClassLoader(XsltCommonFunctions.class.getClassLoader());
            //Prefer the exporter, that uses a xsl transformation
            Docx4jOsgi.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

            // Return HTML-based InputSource
            InputSource result = InputSourceFactory.getInputSource(targetPath);
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

            // Delete obsolete file
            new File(sourcePath).delete();
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
        return "text/html";
    }
}
