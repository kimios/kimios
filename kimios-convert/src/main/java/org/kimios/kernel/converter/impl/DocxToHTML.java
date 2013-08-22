package org.kimios.kernel.converter.impl;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.kimios.kernel.converter.ConverterImpl;
import org.kimios.kernel.converter.exception.BadInputSource;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;

import java.io.*;

/**
 * Allows to convert .DOCX to HTML content
 */
public class DocxToHTML extends ConverterImpl {

    private static final String INPUT_EXTENSION = "docx";
    private static final String OUTPUT_EXTENSION = "html";

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        if (!INPUT_EXTENSION.equals(source.getType())) {
            throw new BadInputSource(this);
        }

        String sourcePath = null;

        try {
            // Copy given resource to temporary repository
            sourcePath = temporaryRepository + "/" + source.getName() + "_" +
                    FileNameGenerator.generate() + "." + source.getType();
            IOUtils.copyLarge(source.getInputStream(), new FileOutputStream(sourcePath));

            // Convert file located to sourcePath into HTML web content
            String targetPath = temporaryRepository + "/" +
                    FileNameGenerator.generate() + "." + OUTPUT_EXTENSION;

            // Load DOCX into XWPFDocument
            InputStream in = new FileInputStream(sourcePath);
            XWPFDocument document = new XWPFDocument(in);

            // Prepare XHTML options
            XHTMLOptions options = XHTMLOptions.create();

            // Convert XWPFDocument to XHTML
            log.debug("Converting " + sourcePath + " to HTML content...");
            OutputStream out = new FileOutputStream(targetPath);
            XHTMLConverter.getInstance().convert(document, out, options);

            // Return HTML-based InputSource
            InputSource result = InputSourceFactory.getInputSource(targetPath);
            result.setHumanName(source.getName() + "_" + source.getType() + "." + OUTPUT_EXTENSION);
            return result;

        } catch (Exception e) {
            throw new ConverterException(e);

        } finally {

            // Delete obsolete file
            new File(sourcePath).delete();
        }
    }
}
