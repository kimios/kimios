package org.kimios.kernel.controller.impl;

import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IConverterController;
import org.kimios.kernel.converter.Converter;
import org.kimios.kernel.converter.ConverterCacheHandler;
import org.kimios.kernel.converter.ConverterFactory;
import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.source.InputSourceFactory;
import org.kimios.kernel.converter.source.impl.FileInputSource;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.exception.AccessDeniedException;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Transactional
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
            if (ConverterCacheHandler.cacheExist(version.getUid())) {
                return ConverterCacheHandler.load(version.getUid());
            }
            InputSource source = InputSourceFactory.getInputSource(version);
            // Get converter
            log.debug("Getting Converter implementation: " + converterImpl);
            Converter converter = ConverterFactory.getConverter(converterImpl);

            // Convert and return the result source
            InputSource inputSource = converter.convertInputSource(source);
            ConverterCacheHandler.cachePreviewData(documentVersionId, inputSource);
            return inputSource;
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
            // Cache enabled for singles versions processing only.
            if (documentVersionIds.size() == 1 && ConverterCacheHandler.cacheExist(documentVersionIds.get(0))) {
                return ConverterCacheHandler.load(documentVersionIds.get(0));
            }

            // Get converter
            log.debug("Getting Converter implementation: " + converterImpl);
            Converter converter = ConverterFactory.getConverter(converterImpl);

            // Convert and return the result source
            InputSource inputSource = converter.convertInputSources(sources);
            if (documentVersionIds.size() == 1 && inputSource.getPublicUrl() != null) {
                log.debug("Putting converted form in preview cache ...");
                ConverterCacheHandler.cachePreviewData(documentVersionIds.get(0), inputSource);
            }
            return inputSource;
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
            if (log.isDebugEnabled()) {
                log.debug("Entity loaded: > " + document);
            }
            DocumentVersion version = dmsFactoryInstantiator.getDocumentVersionFactory().getLastDocumentVersion(document);
            versionIds.add(version.getUid());
        }
        return convertDocumentVersions(session, versionIds, converterImpl);
    }
}
