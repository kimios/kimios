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

import com.aspose.cells.*;
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

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows to convert .xlsx and .xls to HTML content
 */
public class XlsToHTML extends ConverterImpl {

    private static final String[] INPUT_EXTENSIONS = new String[]{"xlsx", "xls", "ods"};
    private static final String OUTPUT_EXTENSION = "html";

    private static final Map<String, String> types = new HashMap<String, String>();
    static {
        types.clear();
        types.put("pdf", "application/pdf");
        types.put("html", "text/html");
    }

    public XlsToHTML(){
        this.selectedOutput = OUTPUT_EXTENSION;
    }

    public XlsToHTML(String outputMimeType){
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
                LicenceLoader.loadCellsLicence(licenceFile + ".cells");
            }
        }catch (Exception ex){
            log.error("error while loading Aspose licence", ex);
        }
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

            //Load a spreadsheet to be converted
            Workbook book = new Workbook(sourcePath);
            SaveOptions saveOptions = null;
            if(this.selectedOutput != null && this.selectedOutput.equalsIgnoreCase("pdf")){
                saveOptions = new PdfSaveOptions(SaveFormat.PDF);
                ((PdfSaveOptions)saveOptions).setImageType(ImageFormat.getPng());
                ((PdfSaveOptions)saveOptions).setCalculateFormula(true);
                ((PdfSaveOptions)saveOptions).setOnePagePerSheet(true);
            } else {
                saveOptions = new HtmlSaveOptions(SaveFormat.HTML);
                ((HtmlSaveOptions)saveOptions).setFullPathLink(true);
                ((HtmlSaveOptions)saveOptions).getImageOptions().setImageFormat(ImageFormat.getPng());
                ((HtmlSaveOptions)saveOptions).getImageOptions().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((HtmlSaveOptions)saveOptions).getImageOptions().setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                ((HtmlSaveOptions)saveOptions).setAttachedFilesUrlPrefix(externalBaseUrl + fileName);
                ((HtmlSaveOptions)saveOptions).setAttachedFilesDirectory(targetPathImg);
            }

            targetPath += "/" + finalFileName;
            book.save(targetPath, saveOptions);

            // Return HTML-based InputSource
            InputSource result = InputSourceFactory.getInputSource(targetPath, fileName);
            result.setHumanName(source.getName() + "_" + source.getType() + "." + selectedOutput);

            result.setPublicUrl(targetPath);
            result.setMimeType(this.converterTargetMimeType());
            return result;

        } catch (Exception e) {
            log.error("error while converting xls like document", e);
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
