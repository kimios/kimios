package org.kimios.kernel.converter.source;

import org.kimios.kernel.converter.exception.MethodNotImplemented;

public abstract class InputSourceImpl implements InputSource {
    /**
     * A alternative user-friendly document name
     */
    protected String humanName;

    protected String mimeType = "application/octet-stream";

    public void setHumanName(String name) {
        this.humanName = name;
    }

    public String getHumanName() {
        return humanName;
    }

    /**
     * A public url, used in cache
     */
    protected String publicUrl;

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) throws MethodNotImplemented {
        this.mimeType = mimeType;
    }
}
