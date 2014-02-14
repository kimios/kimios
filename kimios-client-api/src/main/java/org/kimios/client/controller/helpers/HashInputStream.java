/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
