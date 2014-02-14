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
package org.kimios.kernel.security.pwdgen.md5;

import java.io.*;

public class MD5OutputStream extends FilterOutputStream
{
    /**
     * MD5 context
     */
    private MD5 md5;

    /**
     * Creates MD5OutputStream
     *
     * @param out The output stream
     */
    public MD5OutputStream(OutputStream out)
    {
        super(out);

        md5 = new MD5();
    }

    /**
     * Writes a byte.
     *
     * @see java.io.FilterOutputStream
     */
    public void write(int b) throws IOException
    {
        out.write(b);
        md5.Update((byte) b);
    }

    /**
     * Writes a sub array of bytes.
     *
     * @see java.io.FilterOutputStream
     */
    public void write(byte b[], int off, int len) throws IOException
    {
        out.write(b, off, len);
        md5.Update(b, off, len);
    }

    /**
     * Returns array of bytes representing hash of the stream as finalized for the current state.
     *
     * @see MD5#Final
     */
    public byte[] hash()
    {
        return md5.Final();
    }

    public MD5 getMD5()
    {
        return md5;
    }

    /**
     * This method is here for testing purposes only - do not rely on it being here.
     */
    public static void main(String[] arg)
    {
        try {
            MD5OutputStream out = new MD5OutputStream(new NullOutputStream());
            InputStream in = new BufferedInputStream(new FileInputStream(arg[0]));
            byte[] buf = new byte[65536];
            int num_read;
            long total_read = 0;
            while ((num_read = in.read(buf)) != -1) {
                total_read += num_read;
                out.write(buf, 0, num_read);
            }
            System.out.println(MD5.asHex(out.hash()) + "  " + arg[0]);
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


