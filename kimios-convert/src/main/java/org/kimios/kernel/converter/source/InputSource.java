package org.kimios.kernel.converter.source;

import org.kimios.kernel.converter.exception.MethodNotImplemented;

import java.io.IOException;
import java.io.InputStream;

public interface InputSource {

    InputStream getInputStream() throws MethodNotImplemented, IOException;

    String getType() throws MethodNotImplemented;

    String getMimeType();

    void setMimeType(String  mimeType) throws MethodNotImplemented;

    String getName() throws MethodNotImplemented;

    void setHumanName(String altName);

    String getHumanName();

    void setPublicUrl(String publicUrl);

    public String getPublicUrl();
}
