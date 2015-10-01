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

import com.aspose.words.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.kimios.kernel.converter.ConverterImpl;
import org.kimios.kernel.converter.exception.BadInputSource;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.impl.FileNameGenerator;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Allows to convert .DOCX to HTML content
 */
public class DocxToHTML extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"docx", "odt", "doc"};
    private static final String OUTPUT_EXTENSION = "html";

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        if (!Arrays.asList(INPUT_EXTENSIONS).contains(source.getType())) {
            throw new BadInputSource(this);
        }

        String sourcePath = null;

        try {
            // Copy given resource to temporary repository
            sourcePath = temporaryRepository + "/" + source.getName() + "_" +
                    FileNameGenerator.generate() + "." + source.getType();
            IOUtils.copyLarge(source.getInputStream(), new FileOutputStream(sourcePath));

            String fileName = FileNameGenerator.generate();
            // Convert file located to sourcePath into HTML web content
            String targetPath = temporaryRepository + "/" +
                    fileName + "_dir/" + fileName + "." + OUTPUT_EXTENSION;

            // Load DOCX into XWPFDocument
            InputStream in = new FileInputStream(sourcePath);
            String targetPathImg = targetPath + "_img";
            File imgFolder = new File(targetPathImg);
            imgFolder.mkdirs();


            // The encoding of the text file is automatically detected.
            Document doc = new Document(sourcePath);

            // Create and pass the object which implements the handler methods.
            HtmlSaveOptions options = new HtmlSaveOptions(SaveFormat.HTML);
            options.setImagesFolder(imgFolder.getAbsolutePath());
            options.setExportTextInputFormFieldAsText(true);
            options.setImageSavingCallback(new HandleImageSaving());
            options.setEncoding(Charset.forName("UTF-8"));
            doc.save(new FileOutputStream(targetPath), options);



            String content = FileUtils.readFileToString(new File(targetPath));

            content = content.replaceAll("Evaluation Only\\. Created with Aspose\\.Words\\. Copyright 2003\\-2015 Aspose Pty Ltd\\.","");

            FileUtils.writeStringToFile(new File(targetPath), content);


            // Return HTML-based InputSource
            InputSource result = InputSourceFactory.getInputSource(targetPath);
            result.setHumanName(source.getName() + "_" + source.getType() + "." + OUTPUT_EXTENSION);

            /*
                Set url, to use in cache.
             */
            result.setPublicUrl(targetPath);
            result.setMimeType(this.converterTargetMimeType());
            return result;

        } catch (Exception e) {
            throw new ConverterException(e);

        } finally {

            // Delete obsolete file
            new File(sourcePath).delete();
        }
    }


    public class HandleImageSaving implements IImageSavingCallback
    {
        public void imageSaving(ImageSavingArgs e) throws Exception
        {
            // Change any images in the document being exported with the extension of "jpeg" to "jpg".
            if (e.getImageFileName().endsWith(".jpeg"))
                e.setImageFileName(e.getImageFileName().replace(".jpeg", ".jpg"));
        }
    }

    @Override
    public InputSource convertInputSources(List<InputSource> sources) throws ConverterException {
        if(sources.size() == 1){
            return convertInputSource(sources.get(0));
        } else
            throw new ConverterException("Converter " + this.getClass().getName() + " cannot process many versions at once");

    }

    @Override
    public String converterTargetMimeType() {
        return "text/html";
    }

}
