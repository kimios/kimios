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