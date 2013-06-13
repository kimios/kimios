package org.kimios.kernel.ws.pojo;

public class DocumentWrapper {
    private String storagePath;
    private String filename;
    private Long length;

    public DocumentWrapper() {
    }

    public DocumentWrapper(String storagePath, String filename, Long length) {
        this.storagePath = storagePath;
        this.filename = filename;
        this.length = length;
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
