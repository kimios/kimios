/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2017  DevLib'
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

package org.kimios.aspose.converters;

import com.aspose.email.*;
import org.apache.commons.lang.StringUtils;
import org.kimios.api.InputSource;
import org.kimios.converter.ConverterImpl;
import org.kimios.converter.impl.FileNameGenerator;
import org.kimios.aspose.converters.utils.LicenceLoader;
import org.kimios.converter.source.*;
import org.kimios.exceptions.ConverterException;

import org.kimios.utils.configuration.ConfigurationManager;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MailToHTML extends ConverterImpl {




    private static final String[] INPUT_EXTENSIONS = new String[]{"eml", "msg"};
    private static final String OUTPUT_EXTENSION = "html";

    private static final String[] AVAILABLE_OUTPUT_TYPE = new String[]{"html", "pdf"};


    private static final Map<String, String> types = new HashMap<String, String>();


    static {
        types.clear();
        types.put("html", "text/html");
    }

    @Override
    public String converterTargetMimeType() {
        return "text/html";
    }


    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        /*
            Try to parse email
         */
        try{
            String licenceFile = ConfigurationManager.getValue("dms.converters.aspose.lic");
            if(StringUtils.isNotBlank(licenceFile)){
                LicenceLoader.loadMailLicence(licenceFile + ".mail");
            }
        }catch (Exception ex){
            ConverterImpl.log.error("error while loading Aspose licence", ex);
        }
        try {
                // Convert file located to sourcePath into HTML web content
                String fileName = FileNameGenerator.generate();
                String targetPath = temporaryRepository + "/" +
                        fileName + ".html";

                LoadOptions options = new EmlLoadOptions();
                options.setPrefferedTextEncoding(Charset.forName("UTF-8"));
                MailMessage mailMessage = MailMessage.load(source.getInputStream(), options);
                MailMessageSaveType saveType = MailMessageSaveType.getHtmlFormat();
                SaveOptions saveOptions = SaveOptions.createSaveOptions(saveType);
                mailMessage.save(new FileOutputStream(targetPath), saveOptions);
                InputSource result = InputSourceFactory.getInputSource(targetPath, fileName);
                result.setHumanName(source.getName() + "_" + source.getType() + ".html");
                result.setMimeType(this.converterTargetMimeType());
                return result;

        } catch (Exception exception) {
            throw new ConverterException(exception);
        }


    }
}
