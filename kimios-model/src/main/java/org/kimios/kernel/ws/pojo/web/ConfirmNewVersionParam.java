package org.kimios.kernel.ws.pojo.web;

public class ConfirmNewVersionParam {
    private String sessionId;
    private Long dataTransferId;

    public ConfirmNewVersionParam() {
    }

    public ConfirmNewVersionParam(String sessionId, Long dataTransferId) {
        this.sessionId = sessionId;
        this.dataTransferId = dataTransferId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getDataTransferId() {
        return dataTransferId;
    }

    public void setDataTransferId(Long dataTransferId) {
        this.dataTransferId = dataTransferId;
    }
}
