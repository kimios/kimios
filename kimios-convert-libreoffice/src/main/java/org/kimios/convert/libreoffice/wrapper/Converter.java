package org.kimios.convert.libreoffice.wrapper;

import org.kimios.api.controller.IFileConverterController;
import org.kimios.exceptions.ConverterException;
import org.kimios.kernel.controller.AKimiosController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Converter extends AKimiosController implements IFileConverterController {

    public static String LIBREOFFICE_DEFAULT_PATH = "/usr/bin/libreoffice";

    private String libreOfficePath = "";

    boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");

    @Override
    public File convertFile(File file, String fileResultName) throws ConverterException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("libreoffice", "--headless", "--convert-to", "pdf", file.getAbsolutePath());

        File fileResult = new File(fileResultName);
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
                System.out.println("Success!");
                System.out.println(output);
                System.exit(0);
            } else {
                //abnormal...
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return fileResult;
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public int compareTo(IFileConverterController o) {
        return 0;
    }
}
