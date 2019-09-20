package org.kimios.converter.jodconverter.impl;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Response;
import org.apache.commons.io.IOUtils;
import org.kimios.api.InputSource;
import org.kimios.converter.ConverterImpl;
import org.kimios.converter.source.impl.FileInputSource;
import org.kimios.exceptions.ConverterException;
import org.kimios.jodconverter.ApiClient;
import org.kimios.jodconverter.ApiException;
import org.kimios.jodconverter.handler.ConverterControllerApi;
import org.kimios.utils.configuration.ConfigurationManager;

import java.io.*;
import java.util.Date;
import java.util.List;

public class JodConverter extends ConverterImpl {

    private static String KEY_CONFIG_JOD_CONVERTER_URL = "jodconverter.url";
    private static String OUT_FORMAT = "pdf";

    private ConverterControllerApi controllerApi;

    public JodConverter() {
        if (ConfigurationManager.getValue(KEY_CONFIG_JOD_CONVERTER_URL) == null
                || ConfigurationManager.getValue(KEY_CONFIG_JOD_CONVERTER_URL).isEmpty()) {
            return;
        }

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(ConfigurationManager.getValue(KEY_CONFIG_JOD_CONVERTER_URL));
        controllerApi = new ConverterControllerApi(apiClient);
    }

    private String makeTemporaryFileName() {
        return (new Date()).getTime() + "" + this.hashCode();
    }

    private void writeInputSourceToFile(InputSource source, File targetFile) throws IOException {
        OutputStream outStream = new FileOutputStream(targetFile);
        InputStream initialStream = source.getInputStream();
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = initialStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        IOUtils.closeQuietly(initialStream);
        IOUtils.closeQuietly(outStream);
    }

    @Override
    public InputSource convertInputSource(InputSource source) throws ConverterException {
        FileInputSource fileInputSource = null;

        try {
            File targetFile = new File(this.makeTemporaryFileName());
            writeInputSourceToFile(source, targetFile);

            Call call = controllerApi.convertToUsingParamUsingPOSTCall(
                    targetFile,
                    OUT_FORMAT,
                    "",
                    null,
                    null
            );
            Response response = call.execute();

            String fileResultName = targetFile.getName().concat("." + OUT_FORMAT);
            OutputStream outStream = new FileOutputStream(fileResultName);
            InputStream initialStream = response.body().byteStream();
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = initialStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            initialStream.close();
            outStream.close();

            fileInputSource = new FileInputSource(new File(fileResultName));
        } catch (ApiException e) {
            throw new ConverterException(e);
        } catch (IOException e) {
            throw new ConverterException(e);
        }

        return fileInputSource;
    }

    @Override
    public InputSource convertInputSources(List<InputSource> sources) throws ConverterException {
        return null;
    }

    @Override
    public String converterTargetMimeType() {
        return null;
    }
}
