package org.kimios.webservices.zipper.impl;

import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.web.DMEntityTreeParam;
import org.kimios.webservices.IServiceHelper;
import org.kimios.webservices.zipper.ZipperRestService;
import org.kimios.zipper.controller.IZipperController;

import java.io.File;

public class ZipperRestServiceImpl implements ZipperRestService {

    IZipperController zipperController;
    IFileTransferController fileTransferController;
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

    public IFileTransferController getFileTransferController() {
        return fileTransferController;
    }

    public void setFileTransferController(IFileTransferController fileTransferController) {
        this.fileTransferController = fileTransferController;
    }

    @Override
    public long make(DMEntityTreeParam dmEntityTreeParam) throws Exception {
        File zip = null;
        try {
            Session session = helper.getSession(dmEntityTreeParam.getSessionId());
            zip = zipperController.makeZipFromEntityTree(session, dmEntityTreeParam.getDmEntityTree());
            long transactionId = this.fileTransferController.startDownloadTransaction(session, zip).getUid();
            return transactionId;
        } catch (Exception e) {
            throw helper.convertException(e);
        }
    }
}
