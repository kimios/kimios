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

package org.kimios.kernel.converter.impl.vendors.impl;

import com.aspose.email.*;
import org.apache.commons.io.FileUtils;
import org.kimios.kernel.converter.ConverterImpl;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.impl.FileNameGenerator;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

public class MailToHTML extends ConverterImpl {


    private static Logger logger = LoggerFactory.getLogger(MailToHTML.class);


    private static final String[] INPUT_EXTENSIONS = new String[]{"eml", "msg"};
    private static final String OUTPUT_EXTENSION = "html";

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
                // Convert file located to sourcePath into HTML web content
                String targetPath = temporaryRepository + "/" +
                        FileNameGenerator.generate() + ".html";

                LoadOptions options = new EmlLoadOptions();
                MailMessage mailMessage = MailMessage.load(source.getInputStream(), options);
                SaveOptions saveOptions = SaveOptions.createSaveOptions(MailMessageSaveType.getHtmlFormat());
                mailMessage.save(new FileOutputStream(targetPath), saveOptions);


                String content = FileUtils.readFileToString(new File(targetPath));

                content = content.replaceAll("<br><center>This is an evaluation copy of Aspose\\.Email for Java</center>" +
                        "<br><a href=\\\\\"http://www\\.aspose\\.com/corporate/purchase/end\\-user\\-license\\-agreement\\.aspx\\\\\"><center>View EULA Online</center></a><hr>","");

                FileUtils.writeStringToFile(new File(targetPath), content);

                InputSource result = InputSourceFactory.getInputSource(targetPath);
                result.setHumanName(source.getName() + "_" + source.getType() + ".html");
                result.setMimeType(this.converterTargetMimeType());
                return result;

        } catch (Exception exception) {
            throw new ConverterException(exception);
        }


    }
}
