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

package org.kimios.kernel.converter.impl;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.ghost4j.converter.PDFConverter;
import org.ghost4j.document.PSDocument;
import org.kimios.kernel.converter.ConverterImpl;
import org.kimios.kernel.converter.exception.BadInputSource;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Allows to convert .DOCX to HTML content
 */
public class PostscriptToPDF extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"ps", "postscript"};
    private static final String OUTPUT_EXTENSION = "pdf";

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        if (!Arrays.asList(INPUT_EXTENSIONS).contains(source.getType())) {
            throw new BadInputSource(this);
        }

        String sourcePath = null;
        FileOutputStream fos = null;
        try {
            String fileName = FileNameGenerator.generate();
            // Convert file located to sourcePath into HTML web content
            String targetPath = temporaryRepository + "/" +
                    fileName + "_dir";

            File targetFld = new File(targetPath);
            targetFld.mkdirs();

            targetPath +=  "/" + fileName + "." + OUTPUT_EXTENSION;

            //load PostScript document
            PSDocument document = new PSDocument();
            document.load(source.getInputStream());
            //create OutputStream
            fos = new FileOutputStream(targetPath);
            //create converter
            PDFConverter converter = new PDFConverter();
            //set options
            converter.setPDFSettings(PDFConverter.OPTION_PDFSETTINGS_PREPRESS);
            //convert
            converter.convert(document, fos);

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
