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
            IOUtils.copyLarge(source.getStream(), new FileOutputStream(sourcePath));

            // Add given data to zip file
            String targetPath = temporaryRepository + "/" +
                    FileNameGenerator.generate() + "." + OUTPUT_EXTENSION;
            writer = new ZipFileWriter(targetPath);

            log.debug("Adding " + sourcePath + "...");
            writer.addFile(sourcePath);
            InputSource result = InputSourceFactory.getInputSource(targetPath);
            result.setHumanName(source.getName() + "_" + source.getType() + "." + OUTPUT_EXTENSION);
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

            for (int i = 0; i < sources.size(); ++i) {
                // Copy given resource to temporary repository
                String sourcePath = temporaryRepository + "/" + sources.get(i).getName() + "_" +
                        FileNameGenerator.generate() + "." + sources.get(i).getType();
                IOUtils.copyLarge(sources.get(i).getStream(), new FileOutputStream(sourcePath));
                filesToDelete.add(sourcePath);

                // Add given data to zip file
                log.debug("Adding " + sourcePath + "...");
                writer.addFile(sourcePath);
            }

            InputSource result = InputSourceFactory.getInputSource(targetPath);
            result.setHumanName(OUTPUT_PREFIX + "_" + FileNameGenerator.getTime() + "." + OUTPUT_EXTENSION);
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

        public void addFile(String fileName) throws FileNotFoundException, IOException {
            FileInputStream fis = new FileInputStream(fileName);
            int size = 0;
            byte[] buffer = new byte[1024];
            File file = new File(fileName);
            ZipEntry zipEntry = new ZipEntry(file.getName());
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
}


