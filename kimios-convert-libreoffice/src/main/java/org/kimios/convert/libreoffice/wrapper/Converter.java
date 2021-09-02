package org.kimios.convert.libreoffice.wrapper;

import org.kimios.api.controller.IFileConverterController;
import org.kimios.exceptions.ConverterException;
import org.kimios.kernel.controller.AKimiosController;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Transactional
public class Converter extends AKimiosController implements IFileConverterController {

    public static String LIBREOFFICE_DEFAULT_PATH = "/usr/bin/libreoffice";

    private String libreOfficePath = "";
    private int score = 200;

    public Converter() {
    }

    public Converter(String libreOfficePath) {
        this.libreOfficePath = libreOfficePath;
    }

    @Override
    public File convertFile(File file, String fileResultName) throws ConverterException {
        if (this.libreOfficePath.isEmpty()) {
            this.libreOfficePath = LIBREOFFICE_DEFAULT_PATH;
        }

        File fileResult = new File(fileResultName);
        File fileDirectory = fileResult.getParentFile();
        File fileOutput = new File(
                fileDirectory,
                file.getName().replaceFirst("^(.*)\\..+$", "$1.pdf")
        );
        if (fileDirectory == null) {
            //TODO log error
            throw new ConverterException("file has no parent directory");
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(
                "libreoffice",
                "--headless",
                "--convert-to",
                "pdf",
                "--outdir",
                fileResult.getParent(),
                file.getAbsolutePath()
        );
        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                // log info

                /*System.out.println("Success!");
                System.out.println(output);
                System.exit(0);*/
            } else {
                // log error
            }

            if (! fileOutput.isFile() || ! fileOutput.canRead()) {
                // log error
                throw new ConverterException("fileOutput is not found or not readable");
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileResult);
            Files.copy(fileOutput.toPath(), fileOutputStream);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return fileResult;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public int compareTo(IFileConverterController o) {
        return Integer.compare(this.score, o.getScore());
    }

    public String getLibreOfficePath() {
        return libreOfficePath;
    }

    public void setLibreOfficePath(String libreOfficePath) {
        this.libreOfficePath = libreOfficePath;
    }

    public void init() throws Exception {
        File file = new File(libreOfficePath);
        if (! file.isFile() || ! file.canExecute()) {
            throw new Exception("libreoffice binary is not found or is not executable at given path: "
                    + libreOfficePath
                    + ". Install LibreOffice and set parameter dms.converter.libreoffice.path");
        }
    }
}
