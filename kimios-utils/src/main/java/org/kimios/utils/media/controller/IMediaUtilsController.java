package org.kimios.utils.media.controller;

import java.io.IOException;

public interface IMediaUtilsController {
    public String detectMimeType(String filePath, String completeName) throws IOException;
}
