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

package org.kimios.converter.impl.vendors.aspose;

import com.aspose.slides.*;
import com.aspose.slides.LoadFormat;
import com.aspose.slides.LoadOptions;
import com.aspose.slides.SaveFormat;
import com.aspose.slides.SaveOptions;
import com.aspose.words.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kimios.api.InputSource;
import org.kimios.converter.*;
import org.kimios.converter.exceptions.*;
import org.kimios.converter.impl.*;
import org.kimios.converter.impl.vendors.aspose.utils.LicenceLoader;
import org.kimios.converter.source.*;
import org.kimios.exceptions.ConverterException;
import org.kimios.utils.configuration.ConfigurationManager;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows to convert .DOCX to HTML content
 */
public class PptToHTML extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"ppt", "pptx", "odp"};
    private static final String OUTPUT_EXTENSION = "html";

    private static final Map<String, String> types = new HashMap<String, String>();
    static {
        types.clear();
        types.put("pdf", "application/pdf");
        types.put("html", "text/html");
    }

    public PptToHTML(){
        this.selectedOutput = OUTPUT_EXTENSION;
    }

    public PptToHTML(String outputMimeType){
        this.selectedOutput = outputMimeType;
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
                LicenceLoader.loadSlidesLicence(licenceFile + ".slides");
            }
        }catch (Exception ex){
            log.error("error while loading Aspose licence", ex);
        }
        int loadFormat = -1;
        if(source.getType().equalsIgnoreCase("odp")){
            loadFormat = LoadFormat.Odp;
        } else if(source.getType().equalsIgnoreCase("ppt")){
            loadFormat = LoadFormat.Ppt;
        } else if(source.getType().equalsIgnoreCase("pptx")){
            loadFormat = LoadFormat.Pptx;
        } else
            loadFormat = LoadFormat.Auto;

        try {
            // Copy given resource to temporary repository
            sourcePath = temporaryRepository + "/" + source.getName() + "_" +
                    FileNameGenerator.generate() + "." + source.getType();
            IOUtils.copyLarge(source.getInputStream(), new FileOutputStream(sourcePath));

            String fileName = FileNameGenerator.generate();

            String baseDir = fileName + "_dir/";
            String fileExtension = StringUtils.isNotBlank(selectedOutput) ? selectedOutput : OUTPUT_EXTENSION;
            String finalFileName = fileName + "." + fileExtension;
            String targetPath = temporaryRepository + "/" + baseDir;
            String targetPathImg = targetPath + fileName + "_img";
            File imgFolder = new File(targetPathImg);
            imgFolder.mkdirs();


            // The encoding of the text file is automatically detected.
            int saveFormat = -1;
            LoadOptions loadOptions = new LoadOptions();
            loadOptions.setLoadFormat(loadFormat);
            Presentation pres = new Presentation(sourcePath);

            SaveOptions saveOptions = null;
            if(selectedOutput != null && selectedOutput.equalsIgnoreCase("pdf")){
                PdfOptions pdfOptions = new PdfOptions();
                saveFormat = SaveFormat.Pdf;
                saveOptions = pdfOptions;
            } else {
                HtmlOptions htmlOpt = new HtmlOptions();
                htmlOpt.setHtmlFormatter(HtmlFormatter.createDocumentFormatter("", false));
                saveFormat = SaveFormat.Html;
                saveOptions = htmlOpt;
            }

            //Saving the Presentation1 to HTML

            targetPath += "/" + finalFileName;

            pres.save(targetPath, saveFormat, saveOptions);
            // Return HTML-based InputSource
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
