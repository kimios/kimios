package org.kimios.core;

import java.io.File;

public class QRCodeScanTest {
    public static void main(String args[]) throws Exception {
        try {
            String prefix = "/users/jerome/downloads/";

            String fileOK = prefix + "PDF_QRCode_ebd5ba92-e7b2-404a-9150-873d5f6f8199.pdf";  // pdf text OK
            String fileERROR = prefix + "PDF_QRCode_18655cc5-2aaa-4490-821d-741334b06212.pdf";  // pdf image ERROR

            scan(fileOK);
            scan(fileERROR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scan(String filepath) throws Exception {

        /* PDF To Image */
        String imagePath = PDFUtil.toImage(filepath);

        /* QR Code Scan */
        String data = QRUtil.scan(imagePath);

        /* Clean images */
        new File(imagePath).delete();

        System.out.println("input file to scan: " + filepath);
        System.out.println("converted file to image: " + imagePath);
        System.out.println("data:" + data);
    }
}
