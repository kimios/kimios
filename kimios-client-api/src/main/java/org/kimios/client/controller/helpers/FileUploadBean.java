/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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

import java.io.InputStream;

public class FileUploadBean {

    private InputStream stream;
    private String md5;
    private String sha1;

    public FileUploadBean() {
    }

    public FileUploadBean(InputStream stream) {
        this.stream = stream;
    }

    public FileUploadBean(InputStream stream, String md5, String sha1) {
        this.stream = stream;
        this.md5 = md5;
        this.sha1 = sha1;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    @Override
    public String toString() {
        return "FileUploadBean{" +
                "stream=" + stream +
                ", md5='" + md5 + '\'' +
                ", sha1='" + sha1 + '\'' +
                '}';
    }
}
