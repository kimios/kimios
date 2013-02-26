package org.kimios.kernel.converter.source;

import org.kimios.kernel.converter.source.impl.DocumentVersionInputSource;
import org.kimios.kernel.converter.source.impl.FileInputSource;
import org.kimios.kernel.dms.DocumentVersion;

import java.io.File;

public class InputSourceFactory {

    public static InputSource getInputSource(DocumentVersion version) {
        return new DocumentVersionInputSource(version);
    }

    public static InputSource getInputSource(File file) {
        return new FileInputSource(file);
    }

    public static InputSource getInputSource(String fileName) {
        return getInputSource(new File(fileName));
    }

}
