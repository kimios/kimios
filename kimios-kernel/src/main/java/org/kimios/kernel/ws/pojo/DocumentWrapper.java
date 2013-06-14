package org.kimios.kernel.ws.pojo;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;

public class DocumentWrapper {
    private String storagePath;
    private String filename;
    private Long length;
    private MimetypesFileTypeMap mft;

    public DocumentWrapper(String storagePath, String filename, Long length) throws IOException {
        this.storagePath = storagePath;
        this.filename = filename;
        this.length = length;
        this.mft = new MimetypesFileTypeMap(this.getClass().getClassLoader().getResourceAsStream("META-INF/mime.types"));
    }

    public String getContentType() {
        return mft.getContentType(filename);
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
