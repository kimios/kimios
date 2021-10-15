package org.kimios.kernel.ws.pojo.web;

import java.util.Map;

public class UpdateDocumentVersionMetaDataParam {
    private String sessionId;
    private boolean createNewVersion;
    private long documentUid;
    private long documentTypeUid;
    private Map<Long, String> metaValues ;

    public UpdateDocumentVersionMetaDataParam() {
    }

    public UpdateDocumentVersionMetaDataParam(String sessionId, long documentUid, long documentTypeUid) {
        this.sessionId = sessionId;
        this.documentUid = documentUid;
        this.documentTypeUid = documentTypeUid;
        this.createNewVersion = true;
    }

    public UpdateDocumentVersionMetaDataParam(
            String sessionId,
            boolean createNewVersion,
            long documentUid,
            long documentTypeUid,
            Map<Long, String> metaValues
    ) {
        this.sessionId = sessionId;
        this.createNewVersion = createNewVersion;
        this.documentUid = documentUid;
        this.documentTypeUid = documentTypeUid;
        this.metaValues = metaValues;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isCreateNewVersion() {
        return createNewVersion;
    }

    public void setCreateNewVersion(boolean createNewVersion) {
        this.createNewVersion = createNewVersion;
    }

    public long getDocumentUid() {
        return documentUid;
    }

    public void setDocumentUid(long documentUid) {
        this.documentUid = documentUid;
    }

    public long getDocumentTypeUid() {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(long documentTypeUid) {
        this.documentTypeUid = documentTypeUid;
    }

    public Map<Long, String> getMetaValues() {
        return metaValues;
    }

    public void setMetaValues(Map<Long, String> metaValues) {
        this.metaValues = metaValues;
    }
}
