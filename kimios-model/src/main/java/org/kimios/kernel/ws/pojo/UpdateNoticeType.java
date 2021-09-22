package org.kimios.kernel.ws.pojo;

public enum UpdateNoticeType {
    SHARES_BY_ME("shares by me"),
    SHARES_WITH_ME("shares with me"),
    DOCUMENT("document"),
    FOLDER("folder"),
    WORKSPACE("workspace"),
    PREVIEW_READY("preview ready"),
    PREVIEW_PROCESSING("preview processing");

    private final String value;

    UpdateNoticeType(String state) {
        this.value = state;
    }

    public String getValue() {
        return value;
    }
}
