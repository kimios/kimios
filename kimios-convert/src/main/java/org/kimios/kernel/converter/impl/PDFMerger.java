/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
import org.apache.pdfbox.util.PDFMergerUtility;
import org.kimios.kernel.converter.ConverterImpl;
import org.kimios.kernel.converter.exception.BadInputSource;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to merge any PDF files
 */
public class PDFMerger extends ConverterImpl {

    private static final String OUTPUT_PREFIX = "Merged_PDF";
    private static final String OUTPUT_EXTENSION = "pdf";

    @Override
    public InputSource convertInputSources(List<InputSource> sources) throws ConverterException {
        try {

            // Prepare output PDF file
            String targetPath = temporaryRepository + "/" +
                    FileNameGenerator.generate() + "." + OUTPUT_EXTENSION;
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationFileName(targetPath);

            // Temporary file used to delete generated files
            List<String> filesToDelete = new ArrayList<String>();

            for (int i = 0; i < sources.size(); ++i) {

                // Check if entry file corresponds to converter source type
                if (!OUTPUT_EXTENSION.equals(sources.get(i).getType())) {
                    throw new BadInputSource(this);
                }

                // Copy given resource to temporary repository
                String sourcePath = temporaryRepository + "/" + sources.get(i).getName() + "_" +
                        FileNameGenerator.generate() + sources.get(i).getType();
                IOUtils.copyLarge(sources.get(i).getInputStream(), new FileOutputStream(sourcePath));
                filesToDelete.add(sourcePath);

                // Add given data to merged PDF
                log.debug("Merging PDF: " + sourcePath + "...");
                merger.addSource(sourcePath);
            }

            // Merge all given PDF
            merger.mergeDocuments();

            // Delete obsolete files
            for (String fileToDelete : filesToDelete)
                new File(fileToDelete).delete();

            InputSource result = InputSourceFactory.getInputSource(targetPath);
            result.setHumanName(OUTPUT_PREFIX + "_" + FileNameGenerator.getTime() + "." + OUTPUT_EXTENSION);
            return result;

        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {
        throw new ConverterException("Converter " + this.getClass().getName() + " should process more than one version at once");
    }

    @Override
    public String converterTargetMimeType() {
        return "application/pdf";
    }
}
