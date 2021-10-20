package org.kimios.services.utils;

import org.kimios.kernel.controller.IFileTransferController;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

public class FileStreamingOutput implements StreamingOutput {

    private static Logger logger = LoggerFactory.getLogger(FileStreamingOutput.class);
    private IFileTransferController transferController;
    private Session session;
    private long transactionId;

    public FileStreamingOutput(IFileTransferController transferController, Session session, long transactionId) {
        this.transferController = transferController;
        this.session = session;
        this.transactionId = transactionId;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
            transferController.readFileStream(session, transactionId, output);
            output.flush();
            output.close();
        } catch (Exception ex) {
            logger.error("error on streaming", ex);
        }
    }
}
