package org.kimios.kernel.controller;

import org.kimios.kernel.converter.source.InputSource;
import org.kimios.kernel.converter.exception.ConverterException;
import org.kimios.kernel.security.Session;

import java.util.List;

public interface IConvertController {

    /**
     * Check current document access, create input source from last version of document,
     * get the appropriate converter by factory call, execute the convert process
     * and return a JAX RS response.
     */
    InputSource convertDocument(Session session, Long documentId, String converterImpl) throws ConverterException;

    /**
     * Check current document access, create input source from document version,
     * get the appropriate converter by factory call, execute the convert process
     * and return a JAX RS response.
     */
    InputSource convertDocumentVersion(Session session, Long documentVersionId, String converterImpl) throws ConverterException;

    /**
     * Check document accesses, create multi input sources from last versions of documents,
     * get the appropriate converter by factory call, execute the convert process
     * and return a JAX RS response.
     */
    InputSource convertDocuments(Session session, List<Long> documentIds, String converterImpl) throws ConverterException;


    /**
     * Check document accesses, create multi input sources from versions,
     * get the appropriate converter by factory call, execute the convert process
     * and return a JAX RS response.
     */
    InputSource convertDocumentVersions(Session session, List<Long> documentVersionIds, String converterImpl) throws ConverterException;

}
