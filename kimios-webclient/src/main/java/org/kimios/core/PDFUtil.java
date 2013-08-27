package org.kimios.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class PDFUtil {

    public static String toImage(File file) throws Exception {
        String outputPrefix = "/users/jerome/desktop/qrcode/PDF_" + UUID.randomUUID().toString();
        PDDocument document = PDDocument.load(file);
        return toImage(outputPrefix, document);
    }

    public static String toImage(String sourcePath) throws Exception {
        String outputPrefix = "/users/jerome/desktop/qrcode/PDF_" + UUID.randomUUID().toString();
        PDDocument document = PDDocument.load(new File(sourcePath));
        return toImage(outputPrefix, document);

    }

    public static String toImage(InputStream in) throws Exception {
        String outputPrefix = "/users/jerome/desktop/qrcode/PDF_" + UUID.randomUUID().toString();
        PDDocument document = PDDocument.load(in);
        return toImage(outputPrefix, document);
    }

    private static String toImage(String outputPrefix, PDDocument document) throws IOException {
        PDFImageWriter imageWriter = new PDFImageWriter();

        // imageType: 1 ou 12 (bin)
        // resolution: 48 96 192 384 (384)
        boolean success = imageWriter.writeImage(document, "jpg", null, 1, 1, outputPrefix, 12, 384);
        document.close();

        if (success)
            return outputPrefix + "1.jpg";
        else
            throw new IOException();
    }

}
