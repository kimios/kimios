/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.converter.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.kimios.exceptions.ConverterException;
import org.kimios.api.InputSource;
import org.kimios.converter.source.InputSourceFactory;
import org.kimios.converter.ConverterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class MailToHtml extends ConverterImpl {


    private static Logger logger = LoggerFactory.getLogger(MailToHtml.class);

    @Override
    public String converterTargetMimeType() {
        return "text/html";
    }


    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        /*
            Try to parse email
         */
        try {
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()), source.getInputStream());
            if (message.getContent() instanceof Multipart) {
                // Convert file located to sourcePath into HTML web content
                String fileName = FileNameGenerator.generate();
                String targetPath = temporaryRepository + "/" +
                        fileName + ".html";
                StringBuilder buffer = new StringBuilder();

                Multipart multipart = (Multipart) message.getContent();

                for (int x = 0; x < multipart.getCount(); x++) {
                    BodyPart bodyPart = multipart.getBodyPart(x);
                    String disposition = bodyPart.getDisposition();
                    if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT))) {
                        DataHandler handler = bodyPart.getDataHandler();
                        logger.debug(("mail have some attachment : " + handler.getName()));
                    } else {
                        if(bodyPart.getContentType().equals("text/html")){

                            IOUtils.copy(MimeUtility.decode(bodyPart.getInputStream(), "quoted-printable"), new StringBuilderWriter(buffer));
                            //(bodyPart.getContent());
                            break;
                        }
                    }

                }

                HtmlCleaner cleaner = new HtmlCleaner();
                CleanerProperties props = cleaner.getProperties();
                props.setCharset("UTF-8");

                TagNode node = cleaner.clean(buffer.toString());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                new PrettyXmlSerializer(props).writeToStream(node, out);

                IOUtils.write(out.toString(), new FileOutputStream(targetPath), "UTF-8");
                InputSource result = InputSourceFactory.getInputSource(targetPath, fileName);
                result.setHumanName(source.getName() + "_" + source.getType() + ".html");
                result.setMimeType(this.converterTargetMimeType());
                return result;

            } else {

                String content = "<pre>" + message.getContent().toString() + "</pre>";
                // Convert file located to sourcePath into HTML web content
                String fileName = FileNameGenerator.generate();
                String targetPath = temporaryRepository + "/" +
                        fileName + ".html";


                IOUtils.write(content, new FileOutputStream(targetPath), "UTF-8");

                InputSource result = InputSourceFactory.getInputSource(targetPath, fileName);

                result.setHumanName(source.getName() + "_" + source.getType() + ".html");
                result.setMimeType(this.converterTargetMimeType());
                return result;

            }
        } catch (Exception exception) {
            throw new ConverterException(exception);
        }


    }
}
