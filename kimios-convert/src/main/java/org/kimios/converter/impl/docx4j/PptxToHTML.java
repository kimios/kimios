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

package org.kimios.converter.impl.docx4j;

import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.packages.PresentationMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PresentationML.SlidePart;
import org.kimios.api.InputSource;
import org.kimios.converter.exceptions.BadInputSource;
import org.kimios.exceptions.ConverterException;
import org.kimios.converter.impl.FileNameGenerator;
import org.kimios.converter.source.InputSourceFactory;
import org.kimios.converter.ConverterImpl;
import org.pptx4j.convert.out.svginhtml.SvgExporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Allows to convert .DOCX to HTML content using DocX4j
 */
public class PptxToHTML extends ConverterImpl {

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

            // Where to save images
            SvgExporter.setImageDirPath(imgFolder.getAbsolutePath());

            PresentationMLPackage presentationMLPackage =
                    PresentationMLPackage.load(new File(sourcePath));

            // TODO - render slides in document order!
            Iterator partIterator = presentationMLPackage.getParts().getParts().entrySet().iterator();

            StringWriter sw = new StringWriter();
            while (partIterator.hasNext()) {

                Map.Entry pairs = (Map.Entry)partIterator.next();

                Part p = (Part)pairs.getValue();
                if (p instanceof SlidePart) {
                    sw.write(SvgExporter.svg(presentationMLPackage, (SlidePart)p));
                }
            }


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
