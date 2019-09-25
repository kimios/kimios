package org.kimios.jod.controller.impl;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Response;
import org.kimios.exceptions.ConverterException;
import org.kimios.jod.controller.IJodConverterController;
import org.kimios.jodconverter.ApiClient;
import org.kimios.jodconverter.ApiException;
import org.kimios.jodconverter.handler.ConverterControllerApi;
import org.kimios.kernel.controller.AKimiosController;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

@Transactional
public class JodConverterController extends AKimiosController implements IJodConverterController {

    private static String OUT_FORMAT = "pdf";

    public String getJodConverterUrl() {
        return jodConverterUrl;
    }

    public void setJodConverterUrl(String jodConverterUrl) {
        this.jodConverterUrl = jodConverterUrl;
    }

    private String jodConverterUrl;

    private ConverterControllerApi controllerApi;

    public JodConverterController(String jodConverterUrl) {
        this.jodConverterUrl = jodConverterUrl;

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(this.jodConverterUrl);
        controllerApi = new ConverterControllerApi(apiClient);
    }

    @Override
    public File convertFile(File file, String fileResultName) throws ConverterException {
        File result = null;
        try {

            Call call = controllerApi.convertToUsingParamUsingPOSTCall(
                    file,
                    OUT_FORMAT,
                    "",
                    null,
                    null
            );
            Response response = call.execute();

            result = new File(fileResultName);
            File resultParent = (result.getParentFile());
            resultParent.mkdirs();
            FileOutputStream outStream = new FileOutputStream(result);
            InputStream initialStream = response.body().byteStream();
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = initialStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            initialStream.close();
            outStream.close();

        } catch (FileNotFoundException e) {
            throw new ConverterException(e);
        } catch (IOException e) {
            throw new ConverterException(e);
        } catch (ApiException e) {
            throw new ConverterException(e);
        }

        return result;
    }
}
