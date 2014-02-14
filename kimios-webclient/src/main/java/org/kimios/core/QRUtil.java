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

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class QRUtil {

    public static String scan(File file) throws IOException, NotFoundException, FormatException, ChecksumException {
        return readQrCode(new FileInputStream(file)).getText();
    }

    public static String scan(String filepath) throws IOException, NotFoundException, FormatException, ChecksumException {
        return readQrCode(new FileInputStream(filepath)).getText();
    }

    public static String scan(InputStream in) throws IOException, NotFoundException, FormatException, ChecksumException {
        return readQrCode(in).getText();
    }

    private static Result readQrCode(InputStream in) throws IOException, NotFoundException, FormatException, ChecksumException {
        try {
            BufferedImageLuminanceSource image = new BufferedImageLuminanceSource(ImageIO.read(in));
            HybridBinarizer binarizer = new HybridBinarizer(image);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            return new QRCodeReader().decode(binaryBitmap);
//        return new MultiFormatReader().decode(binaryBitmap);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}