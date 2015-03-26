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

package org.kimios.kernel.converter.impl;

import org.apache.commons.io.IOUtils;
import org.kimios.kernel.converter.ConverterImpl;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Allows to compress one file or any files
 */
public class FileToZip extends ConverterImpl {

    private static final String OUTPUT_PREFIX = "ZIP_Archive";
    private static final String OUTPUT_EXTENSION = "zip";

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {
        String sourcePath = null;
        ZipFileWriter writer = null;

        try {
            // Copy given resource to temporary repository
            sourcePath = temporaryRepository + "/" + source.getName() + "_" +
                    FileNameGenerator.generate() + "." + source.getType();
            IOUtils.copyLarge(source.getInputStream(), new FileOutputStream(sourcePath));

            // Add given data to zip file
            String targetPath = temporaryRepository + "/" +
                    FileNameGenerator.generate() + "." + OUTPUT_EXTENSION;
            writer = new ZipFileWriter(targetPath);

            log.debug("Adding " + sourcePath + "...");
            writer.addFile(sourcePath, source.getName() + (source.getType() != null ? "." + source.getType() : ""));
            InputSource result = InputSourceFactory.getInputSource(targetPath);
            result.setHumanName(source.getName() + "_"
                    + (source.getType() != null ? source.getType() : "") + "." + OUTPUT_EXTENSION);
            return result;

        } catch (Exception e) {
            throw new ConverterException(e);

        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new ConverterException(e);
            }

            // Delete obsolete file
            new File(sourcePath).delete();
        }
    }

    @Override
    public InputSource convertInputSources(List<InputSource> sources) throws ConverterException {
        ZipFileWriter writer = null;

        // Temporary file used to delete generated files
        List<String> filesToDelete = new ArrayList<String>();

        try {
            // Prepare output zipped file
            String targetPath = temporaryRepository + "/" +
                    FileNameGenerator.generate() + "." + OUTPUT_EXTENSION;
            writer = new ZipFileWriter(targetPath);

            for (InputSource source: sources) {
                // Copy given resource to temporary repository
                String sourcePath = temporaryRepository + "/" + source.getName() + "_" +
                        FileNameGenerator.generate() + "." + source.getType();
                IOUtils.copyLarge(source.getInputStream(), new FileOutputStream(sourcePath));
                filesToDelete.add(sourcePath);

                // Add given data to zip file
                log.debug("Adding " + sourcePath + "...");
                writer.addFile(sourcePath, source.getName() + (source.getType() != null ? "." + source.getType() : ""));
            }

            InputSource result = InputSourceFactory.getInputSource(targetPath);
            result.setHumanName(OUTPUT_PREFIX + "_" + FileNameGenerator.getTime() + "." + OUTPUT_EXTENSION);
            result.setMimeType(this.converterTargetMimeType());
            return result;

        } catch (Exception e) {
            throw new ConverterException(e);

        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new ConverterException(e);
            }

            // Delete obsolete files
            for (String fileToDelete : filesToDelete)
                new File(fileToDelete).delete();
        }
    }

    /**
     * Private ZIP file writer
     */
    class ZipFileWriter {
        private ZipOutputStream output;

        public ZipFileWriter(String zipFile) throws FileNotFoundException {
            this(new FileOutputStream(zipFile));
        }

        public ZipFileWriter(FileOutputStream zipFile) throws FileNotFoundException {
            FileOutputStream fos = zipFile;
            CheckedOutputStream checksum = new CheckedOutputStream(fos, new Adler32());
            this.output = new ZipOutputStream(new BufferedOutputStream(checksum));
        }

        public void addFile(String fileName, String zipInternalFileName) throws FileNotFoundException, IOException {
            FileInputStream fis = new FileInputStream(fileName);
            int size = 0;
            byte[] buffer = new byte[1024];
            File file = new File(fileName);
            ZipEntry zipEntry = new ZipEntry(zipInternalFileName);
            this.output.putNextEntry(zipEntry);
            while ((size = fis.read(buffer, 0, buffer.length)) > 0) {
                this.output.write(buffer, 0, size);
            }
            this.output.closeEntry();
            fis.close();
        }

        public void close() throws IOException {
            this.output.close();
        }
    }


    @Override
    public String converterTargetMimeType() {
        return "application/zip";
    }
}


