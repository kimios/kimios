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

import com.aspose.words.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kimios.api.InputSource;
import org.kimios.converter.ConverterImpl;
import org.kimios.converter.exceptions.*;
import org.kimios.converter.impl.FileNameGenerator;
import org.kimios.aspose.converters.utils.LicenceLoader;
import org.kimios.converter.source.*;
import org.kimios.exceptions.ConverterException;
import org.kimios.utils.configuration.ConfigurationManager;


import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows to convert .DOCX to HTML content
 */
public class DocxToHTML extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"docx", "odt", "doc"};
    private static final String OUTPUT_EXTENSION = "html";

    private static final String[] AVAILABLE_OUTPUT_TYPE = new String[]{"html", "pdf"};


    private static final Map<String, String> types = new HashMap<String, String>();
    static {
        types.clear();
        types.put("pdf", "application/pdf");
        types.put("html", "text/html");
    }


    public DocxToHTML(){
        this.selectedOutput = OUTPUT_EXTENSION;
    }

    public DocxToHTML(String selectedOutput){
        this.selectedOutput = selectedOutput;
    }

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {

        if (!Arrays.asList(INPUT_EXTENSIONS).contains(source.getType())) {
            throw new BadInputSource(this);
        }

        String sourcePath = null;

        try{
            String licenceFile = ConfigurationManager.getValue("dms.converters.aspose.lic");
            if(StringUtils.isNotBlank(licenceFile)){
                LicenceLoader.loadWordLicence(licenceFile + ".words");
            }
        }catch (Exception ex){
            ConverterImpl.log.error("error while loading Aspose licence", ex);
        }

        int loadFormat = -1;
        if(source.getType().equalsIgnoreCase("odt")){
            loadFormat = LoadFormat.ODT;
        } else if(source.getType().equalsIgnoreCase("doc")){
            loadFormat = LoadFormat.DOC;
        } else if(source.getType().equalsIgnoreCase("docx")){
            loadFormat = LoadFormat.DOCX;
        }


        try {
            // Copy given resource to temporary repository
            sourcePath = temporaryRepository + "/" + source.getName() + "_" +
                    FileNameGenerator.generate() + "." + source.getType();
            IOUtils.copyLarge(source.getInputStream(), new FileOutputStream(sourcePath));

            final String fileName = FileNameGenerator.generate();

            String baseDir = fileName + "_dir/";
            String fileExtension = StringUtils.isNotBlank(selectedOutput) ? selectedOutput : OUTPUT_EXTENSION;
            final String finalFileName = fileName + "." + fileExtension;
            String targetPath = temporaryRepository + "/" + baseDir;
            String targetPathImg = targetPath + fileName + "_img";
            File imgFolder = new File(targetPathImg);
            imgFolder.mkdirs();


            final String finalTargetPath = targetPath;
            // The encoding of the text file is automatically detected.
            LoadOptions lOptions = new LoadOptions();
            if(loadFormat != -1)
                lOptions.setLoadFormat(loadFormat);
            Document doc = new Document(sourcePath, lOptions);
            // Create and pass the object which implements the handler methods.
            SaveOptions saveOptions = null;
            if(selectedOutput.equalsIgnoreCase("pdf")){
                PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
                pdfSaveOptions.setSaveFormat(SaveFormat.PDF);
                saveOptions = pdfSaveOptions;
            } else {
                HtmlSaveOptions htmlSaveOptions = new HtmlSaveOptions();
                htmlSaveOptions.setImagesFolder(imgFolder.getAbsolutePath());
                htmlSaveOptions.setExportTextInputFormFieldAsText(true);
                htmlSaveOptions.setImageSavingCallback(new HandleImageSaving());
                htmlSaveOptions.setImagesFolderAlias(externalBaseUrl + fileName);
                htmlSaveOptions.setDocumentSplitCriteria(DocumentSplitCriteria.PAGE_BREAK);
                htmlSaveOptions.setDocumentPartSavingCallback(new IDocumentPartSavingCallback() {

                    public int i = 1;
                    @Override
                    public void documentPartSaving(DocumentPartSavingArgs documentPartSavingArgs) throws Exception {
                        documentPartSavingArgs.setDocumentPartFileName(fileName + "_" + i + "." + selectedOutput);
                        documentPartSavingArgs.setDocumentPartStream(new FileOutputStream(finalTargetPath + "/" + fileName + "_" + i + "." + selectedOutput));
                        i++;
                    }
                });
                saveOptions = htmlSaveOptions;
            }

            targetPath += "/" + finalFileName;
            if(selectedOutput.equalsIgnoreCase("pdf")){
                saveOptions.setSaveFormat(SaveFormat.PDF);
            } else{
                saveOptions.setSaveFormat(SaveFormat.HTML);
            }
            doc.save(new FileOutputStream(targetPath), saveOptions);
            InputSource result = InputSourceFactory.getInputSource(targetPath, fileName);
            result.setHumanName(source.getName() + "_" + source.getType() + "." + selectedOutput);
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
        return types.get(selectedOutput);
    }

}
