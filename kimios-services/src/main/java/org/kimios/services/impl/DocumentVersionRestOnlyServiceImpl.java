package org.kimios.services.impl;

import org.kimios.kernel.dms.model.MetaValue;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.web.UpdateDocumentVersionMetaDataParam;
import org.kimios.webservices.DocumentVersionRestOnlyService;
import org.kimios.webservices.exceptions.DMServiceException;

import java.util.ArrayList;

public class DocumentVersionRestOnlyServiceImpl extends CoreService implements DocumentVersionRestOnlyService {

    @Override
    public void updateDocumentMetaData(UpdateDocumentVersionMetaDataParam updateDocumentVersionMetaData)
            throws DMServiceException {
        try {
            Session session = getHelper().getSession(updateDocumentVersionMetaData.getSessionId());
            org.kimios.kernel.dms.model.DocumentVersion currentDocumentVersion = this.documentVersionController.getLastDocumentVersion(
                    session,
                    updateDocumentVersionMetaData.getDocumentUid()
            );

            if (updateDocumentVersionMetaData.getDocumentTypeUid() == -1) {
                if (currentDocumentVersion.getDocumentType() != null) {
                    // create new version and remove it on that version
                    long documentVersionId = this.documentVersionController.createDocumentVersionFromLatest(session,
                            updateDocumentVersionMetaData.getDocumentUid());
                    this.documentVersionController.updateDocumentVersion(
                            session,
                            updateDocumentVersionMetaData.getDocumentUid(),
                            updateDocumentVersionMetaData.getDocumentTypeUid(),
                            new ArrayList<MetaValue>()
                    );
                }
            } else {
                // if (current document type is the same than sent and new document version creation is asked)
                // OR document type is different
                if (
                        currentDocumentVersion.getDocumentType() == null
                        || (currentDocumentVersion.getDocumentType().getUid() == updateDocumentVersionMetaData.getDocumentTypeUid()
                                && updateDocumentVersionMetaData.isCreateNewVersion())
                                || (currentDocumentVersion.getDocumentType().getUid() != updateDocumentVersionMetaData.getDocumentTypeUid())
                ) {
                    long documentVersionId = this.documentVersionController.createDocumentVersionFromLatest(session,
                            updateDocumentVersionMetaData.getDocumentUid());
                }

                org.kimios.kernel.dms.model.DocumentVersion documentVersion =
                        this.documentVersionController.getLastDocumentVersion(session, updateDocumentVersionMetaData.getDocumentUid());
                this.documentVersionController.updateDocumentVersion(
                        session,
                        updateDocumentVersionMetaData.getDocumentUid(),
                        updateDocumentVersionMetaData.getDocumentTypeUid(),
                        updateDocumentVersionMetaData.getMetaValues()
                );
            }

        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }
}
