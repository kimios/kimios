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
import org.kimios.converter.ConverterImpl;
import org.kimios.converter.exception.BadInputSource;
import org.kimios.converter.exception.ConverterException;
import org.kimios.converter.impl.utils.ToHtml;
import org.kimios.converter.source.InputSource;
import org.kimios.converter.source.InputSourceFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Allows to convert .xlsx and .xls to HTML content
 */
public class XlsToHTML extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"xlsx", "xls"};
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

            // Load DOCX into XWPFDocument
            InputStream in = new FileInputStream(sourcePath);
            String targetPathImg = targetPath + "_img";
            File imgFolder = new File(targetPathImg);
            imgFolder.mkdirs();



            // Convert XWPFDocument to XHTML
            log.debug("Converting " + sourcePath + " to HTML content...");
            ToHtml toHtml = ToHtml.create(in, new PrintWriter(new FileWriter(new File(targetPath))));
            toHtml.setCompleteHTML(true);
            toHtml.printPage();

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
        if(sources.size() == 1){
            return convertInputSource(sources.get(0));
        } else
            throw new ConverterException("Converter " + this.getClass().getName() + " cannot process many versions at once");

    }

    @Override
    public String converterTargetMimeType() {
        return "text/html";
    }
}
