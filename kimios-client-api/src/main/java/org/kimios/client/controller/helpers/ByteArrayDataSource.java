/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.client.controller.helpers;

import java.io.*;

public class ByteArrayDataSource implements javax.activation.DataSource {
  private byte[] bytes;
  public void setBytes(byte[] bytes) { this.bytes = bytes; }
  public byte[] getBytes() { return bytes; }
  private String contentType;
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  public String getContentType() { return contentType; }
  public InputStream getInputStream() {
    return new ByteArrayInputStream(bytes);
  }
  
  public OutputStream getOutputStream() {
    final ByteArrayOutputStream baos =
      new ByteArrayOutputStream();
    return new FilterOutputStream(baos);
  }
  
  public String getName() {
    return "";
  }
}

