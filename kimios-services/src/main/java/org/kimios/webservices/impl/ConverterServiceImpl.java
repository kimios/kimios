package org.kimios.webservices.impl;

import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.security.Session;
import org.kimios.webservices.ConverterService;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.DMServiceException;

import javax.jws.WebService;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebService(targetNamespace = "http://kimios.org", serviceName = "ConverterService")
public class ConverterServiceImpl extends CoreService implements ConverterService {

    public Response convertDocument(String sessionId, Long documentId, String converterImpl) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return wrapResponse(convertController.convertDocument(session, documentId, converterImpl));

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public Response convertDocumentVersion(String sessionId, Long versionId, String converterImpl) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            return wrapResponse(convertController.convertDocumentVersion(session, versionId, converterImpl));

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public Response convertDocuments(String sessionId, Long[] documentIds, String converterImpl) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<Long> documents = new ArrayList<Long>();
            for (Long documentId : documentIds) {
                documents.add(documentId);
            }
            return wrapResponse(convertController.convertDocuments(session, documents, converterImpl));

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public Response convertDocumentVersions(String sessionId, Long[] versionIds, String converterImpl) throws DMServiceException {
        try {
            Session session = getHelper().getSession(sessionId);
            List<Long> versions = new ArrayList<Long>();
            for (Long versionId : versionIds) {
                versions.add(versionId);
            }
            return wrapResponse(convertController.convertDocumentVersions(session, versions, converterImpl));

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    // private

    private Response wrapResponse(InputSource source) throws IOException {
        return Response.ok(source.getInputStream()).header(
                "Content-Disposition",
                "attachment; filename=\"" + source.getHumanName() + "\"")
                .build();
    }
}
