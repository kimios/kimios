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
package org.kimios.kernel.index.filters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kimios.kernel.index.IndexFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ODTFilter implements IndexFilter
{
    public String getBody(InputStream in) throws IOException
    {

        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().equalsIgnoreCase("content.xml")) {
                break;
            }
        }
        if (entry != null) {
            File tmpXml = File.createTempFile("odtFile", "tmp");
            FileOutputStream fos = new FileOutputStream(tmpXml);
            byte[] buf = new byte[2048];
            int len;
            while ((len = zis.read(buf, 0, buf.length)) != -1) {
                fos.write(buf, 0, len);
            }

            fos.close();
            StringBuffer body = new StringBuffer();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(tmpXml);
                org.w3c.dom.Element rootEl = doc.getDocumentElement();
                NodeList nList = rootEl.getChildNodes();
                body = getText(nList);
                tmpXml.delete();
                return body.toString();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }
        } else {
            return "";
        }
    }

    private StringBuffer getText(NodeList n) throws Exception
    {
        StringBuffer bf = new StringBuffer();
        for (int u = 0; u < n.getLength(); u++) {
            Node it = n.item(u);
            if (it.getNodeType() == Node.TEXT_NODE) {
                bf.append(it.getTextContent() + " ");
            }
            if (it.hasChildNodes()) {
                bf.append(getText(it.getChildNodes()));
            }
        }
        return bf;
    }
}

