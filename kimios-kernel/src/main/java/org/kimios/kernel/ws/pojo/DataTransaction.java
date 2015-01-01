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
package org.kimios.kernel.ws.pojo;

public class DataTransaction
{
    private long uid;

    private long size;

    private boolean isCompressed;

    private String hashMD5;

    private String hashSHA;

    private String downloadToken;

    public DataTransaction()
    {
    }

    public DataTransaction(long uid, long size, boolean isCompressed, String hashMD5, String hashSHA,
                           String downloadToken)
    {
        this.uid = uid;
        this.size = size;
        this.isCompressed = isCompressed;
        this.hashMD5 = hashMD5;
        this.hashSHA = hashSHA;
        this.downloadToken = downloadToken;
    }

    public long getUid()
    {
        return uid;
    }

    public void setUid(long uid)
    {
        this.uid = uid;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public boolean isCompressed()
    {
        return isCompressed;
    }

    public void setCompressed(boolean isCompressed)
    {
        this.isCompressed = isCompressed;
    }

    public String getHashMD5()
    {
        return hashMD5;
    }

    public void setHashMD5(String hashMD5)
    {
        this.hashMD5 = hashMD5;
    }

    public String getHashSHA()
    {
        return hashSHA;
    }

    public void setHashSHA(String hashSHA)
    {
        this.hashSHA = hashSHA;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public void setDownloadToken(String downloadToken) {
        this.downloadToken = downloadToken;
    }
}

