package org.kimios.kernel.controller.impl;

import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IConverterController;
import org.kimios.kernel.converter.Converter;
import org.kimios.kernel.converter.ConverterFactory;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConverterController extends AKimiosController implements IConverterController {

    private static Logger log = LoggerFactory.getLogger(ConverterController.class);

    public InputSource convertDocumentVersion(Session session, Long documentVersionId, String converterImpl) throws ConverterException {
        try {
            // Check rights
            DocumentVersion version = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionId);
            if (version == null || !getSecurityAgent().isReadable(version.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
                throw new AccessDeniedException();
            }

            // Build InputSource
            log.debug("Build InputSource for " + version.getDocument().getName() + "...");
            InputSource source = InputSourceFactory.getInputSource(version);

            // Get converter
            log.debug("Getting Converter implementation: " + converterImpl);
            Converter converter = ConverterFactory.getConverter(converterImpl);

            // Convert and return the result source
            return converter.convertInputSource(source);

        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

    public InputSource convertDocumentVersions(Session session, List<Long> documentVersionIds, String converterImpl) throws ConverterException {
        try {
            List<InputSource> sources = new ArrayList<InputSource>();
            for (Long documentVersionId : documentVersionIds) {

                // Check rights
                DocumentVersion version = dmsFactoryInstantiator.getDocumentVersionFactory().getDocumentVersion(documentVersionId);
                if (version == null || !getSecurityAgent().isReadable(version.getDocument(), session.getUserName(), session.getUserSource(), session.getGroups())) {
                    throw new AccessDeniedException();
                }

                // Build InputSource
                log.debug("Build InputSource from " + version.getDocument().getName() + "...");
                sources.add(InputSourceFactory.getInputSource(version));
            }

            // Get converter
            log.debug("Getting Converter implementation: " + converterImpl);
            Converter converter = ConverterFactory.getConverter(converterImpl);

            // Convert and return the result source
            return converter.convertInputSources(sources);

        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

    // aliases

    public InputSource convertDocument(Session session, Long documentId, String converterImpl) throws ConverterException {
        Document document = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
        DocumentVersion version = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(document);
        return convertDocumentVersion(session, version.getUid(), converterImpl);
    }

    public InputSource convertDocuments(Session session, List<Long> documentIds, String converterImpl) throws ConverterException {
        List<Long> versionIds = new ArrayList<Long>();
        for (Long documentId : documentIds) {
            Document document = dmsFactoryInstantiator.getDocumentFactory().getDocument(documentId);
            DocumentVersion version = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(document);
            versionIds.add(version.getUid());
        }
        return convertDocumentVersions(session, versionIds, converterImpl);
    }
}
