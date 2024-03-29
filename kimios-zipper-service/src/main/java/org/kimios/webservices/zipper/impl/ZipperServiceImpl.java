package org.kimios.webservices.zipper.impl;

import org.kimios.kernel.security.model.Session;
import org.kimios.webservices.IServiceHelper;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.zipper.ZipperService;
import org.kimios.zipper.controller.IZipperController;

import javax.jws.WebService;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ZipperService")
public class ZipperServiceImpl implements ZipperService {

    IZipperController zipperController;
    private IServiceHelper helper;

    public IZipperController getZipperController() {
        return zipperController;
    }

    public void setZipperController(IZipperController zipperController) {
        this.zipperController = zipperController;
    }

    public IServiceHelper getHelper() {
        return helper;
    }

    public void setHelper(IServiceHelper helper) {
        this.helper = helper;
    }

    @Override
    public Response makeZip(String sessionId, List<Long> ids) throws DMServiceException {
        File zip = null;
        try {
            Session session = helper.getSession(sessionId);
            zip = zipperController.makeZipWithEntities(session, ids);
            String fileName = "files_from_kimios.zip";
            return Response.ok(new FileInputStream(zip)).header(
                    "Content-Disposition",
                    "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM)
                    .build();
        } catch (Exception e) {
            throw helper.convertException(e);
        } finally {
            zipperController.markFileDownloaded(zip);
        }
    }
}
