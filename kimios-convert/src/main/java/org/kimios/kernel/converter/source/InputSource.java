package org.kimios.kernel.converter.source;

import org.kimios.kernel.converter.exception.MethodNotImplemented;

import java.io.IOException;
import java.io.InputStream;

public interface InputSource {

    InputStream getStream() throws MethodNotImplemented, IOException;

    String getType() throws MethodNotImplemented;

    String getName() throws MethodNotImplemented;

    void setHumanName(String altName);

    String getHumanName();
}
