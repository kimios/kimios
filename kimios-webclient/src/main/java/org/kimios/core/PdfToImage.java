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

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import org.kimios.core.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfToImage
{
    @SuppressWarnings("finally")
    public static List<Map<String, String>> convert(long uid, long versionUid,
            String pdfPath, String hashMd5, String hashSha) throws Exception
    {
        File file = new File(pdfPath);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        List<Map<String, String>> imagePaths = new ArrayList<Map<String, String>>();
        try {
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            PDFFile pdffile = new PDFFile(buf);
            int num = pdffile.getNumPages();

            for (int i = 1; i <= num; i++) {
                String path =ConfigurationManager.getValue("client",Config.DM_TMP_FILES_PATH)
                        + "/pdf_" + uid + "_" + versionUid + "_" + hashMd5 + "_" + hashSha + "_" + i + ".png";
                if (!new File(path).exists()) {
                    PDFPage page = pdffile.getPage(i);
                    int width = (int) page.getBBox().getWidth();
                    int height = (int) page.getBBox().getHeight();
                    Rectangle rect = new Rectangle(0, 0, width, height);
                    int rotation = page.getRotation();
                    Rectangle rect1 = rect;
                    if (rotation == 90 || rotation == 270) {
                        rect1 = new Rectangle(0, 0, rect.height, rect.width);
                    }
                    BufferedImage img = (BufferedImage) page.getImage(
                            (int) (rect.width * 1.5), (int) (rect.height * 1.5), rect1, null,
                            true, true);
                    ImageIO.write(img, "png", new File(path));
                }

                Map<String, String> map = new HashMap<String, String>();
                map.put("path", path);
                map.put("num", String.valueOf(i));
                imagePaths.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imagePaths;
        }
    }
}
