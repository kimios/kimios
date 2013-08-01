package org.kimios.client.controller.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;

/**
 * @author Fabien ALIN
 */
public class HashInputStream
        extends InputStream {

    private List<MessageDigest> digests;

    private InputStream inputStream;

    public HashInputStream(List<MessageDigest> digests, InputStream inputStream) {

        this.digests = digests;
        this.inputStream = inputStream;

    }

    @Override
    public int read()
            throws IOException {
        throw new IOException("Hash Stream doesn't permit direct read");
    }

    @Override
    public int read(byte[] b)
            throws IOException {
        int ret = inputStream.read(b);
        if (ret > 0) {
            for (MessageDigest m : digests) {

                m.update(b, 0, ret);
            }

        }
        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len)
            throws IOException {

        int ret = inputStream.read(b, off, len);
        if (ret > 0) {
            for (MessageDigest m : digests) {

                m.update(b, 0, ret);
            }
        }
        return ret;
    }

    public List<MessageDigest> getDigests() {
        return digests;
    }

    public void setDigests(List<MessageDigest> digests) {
        this.digests = digests;
    }
}
