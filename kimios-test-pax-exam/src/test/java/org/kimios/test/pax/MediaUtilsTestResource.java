package org.kimios.test.pax;

public class MediaUtilsTestResource {
    private String resourcePath;
    private String resourceName;
    private String fileTmpPath;
    private String mediaType;

    public MediaUtilsTestResource(String resourcePath, String fileTmpPath, String mediaType, String resourceName) {
        this.resourcePath = resourcePath;
        this.fileTmpPath = fileTmpPath;
        this.mediaType = mediaType;
        this.resourceName = resourceName;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getFileTmpPath() {
        return fileTmpPath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getResourceName() {
        return resourceName;
    }

    @Override
    public int hashCode() {
        return this.resourceName.hashCode()
                + this.resourcePath.hashCode()
                + this.mediaType.hashCode()
                + this.fileTmpPath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaUtilsTestResource that = (MediaUtilsTestResource) o;
        return this.resourcePath.equals(that.resourcePath)
                && this.mediaType.equals(that.mediaType)
                && this.resourceName.equals(that.resourceName)
                && this.fileTmpPath.equals(that.fileTmpPath);
    }
}
