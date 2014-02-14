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
