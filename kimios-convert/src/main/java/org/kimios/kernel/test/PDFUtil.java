package org.kimios.kernel.test;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;
import org.kimios.kernel.converter.impl.FileNameGenerator;

import java.io.File;
import java.io.IOException;

public class PDFUtil {

    public static String toImage(String sourcePath) throws Exception {
        String outputPrefix = "/users/jerome/desktop/qrcode/PDF_" + FileNameGenerator.generate();

        PDDocument document = PDDocument.load(new File(sourcePath));
        PDFImageWriter imageWriter = new PDFImageWriter();

        /* imageType: 1 ou 12 ; resolution: 384 */
        boolean success = imageWriter.writeImage(document, "jpg", null, 1, 1, outputPrefix, 12, 384);  // resolution: 48 96 192 384
        document.close();

        if (success)
            return outputPrefix + "1.jpg";
        else
            throw new IOException();

    }

}
