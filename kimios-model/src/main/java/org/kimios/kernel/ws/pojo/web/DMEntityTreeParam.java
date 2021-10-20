package org.kimios.kernel.ws.pojo.web;

import org.kimios.kernel.ws.pojo.DMEntityTree;

public class DMEntityTreeParam {
    private String sessionId;
    private DMEntityTree dmEntityTree;

    public DMEntityTreeParam() {
    }

    public DMEntityTreeParam(String sessionId, DMEntityTree dmEntityTree) {
        this.sessionId = sessionId;
        this.dmEntityTree = dmEntityTree;
    }

    public DMEntityTreeParam(String sessionId) {
        this.sessionId = sessionId;
        this.dmEntityTree = new DMEntityTree();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public DMEntityTree getDmEntityTree() {
        return dmEntityTree;
    }

    public void setDmEntityTree(DMEntityTree dmEntityTree) {
        this.dmEntityTree = dmEntityTree;
    }
}
