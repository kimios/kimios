package org.kimios.utils.media.controller.impl;

import org.apache.tika.Tika;
import org.kimios.utils.media.controller.IMediaUtilsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MediaUtilsController implements IMediaUtilsController {
    private static Logger log = LoggerFactory.getLogger(MediaUtilsController.class);

    private Tika tika = new Tika();

    public String detectMimeType(String filePath, String completeName) throws IOException {

        File file = new File(filePath);
        String mimeType = "";

        mimeType = tika.detect(Files.readAllBytes(file.toPath()), completeName);

        return mimeType;
    }
}
