package org.kimios.kernel.converter.source.impl;

import org.kimios.kernel.converter.source.InputSourceImpl;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.converter.exception.MethodNotImplemented;
import org.kimios.kernel.repositories.RepositoryManager;

import java.io.IOException;
import java.io.InputStream;

public class DocumentVersionInputSource extends InputSourceImpl {
    private DocumentVersion version;

    public DocumentVersionInputSource(org.kimios.kernel.dms.DocumentVersion version) {
        this.version = version;
    }

    public InputStream getInputStream() throws MethodNotImplemented, IOException {
        return RepositoryManager.accessVersionStream(version);
    }

    public String getType() throws MethodNotImplemented {
        return version.getDocument().getExtension().toLowerCase();
    }

    public String getName() throws MethodNotImplemented {
        return version.getDocument().getName();
    }

    public String getStoragePath() {
        return version.getStoragePath();
    }

    public DocumentVersion getVersion() {
        return version;
    }
}
