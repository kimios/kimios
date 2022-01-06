package org.kimios.kernel.ws.pojo.web;

import java.util.List;

public class FolderUidListParam {
    private String sessionId;
    private List<Long> folderUidList;

    public FolderUidListParam() {
    }

    public FolderUidListParam(String sessionId, List<Long> folderUidList) {
        this.sessionId = sessionId;
        this.folderUidList = folderUidList;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getFolderUidList() {
        return folderUidList;
    }

    public void setFolderUidList(List<Long> folderUidList) {
        this.folderUidList = folderUidList;
    }
}
