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
                IOUtils.copyLarge(sources.get(i).getStream(), new FileOutputStream(sourcePath));
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
}
