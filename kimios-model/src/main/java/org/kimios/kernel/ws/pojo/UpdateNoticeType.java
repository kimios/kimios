package org.kimios.kernel.ws.pojo;

public enum UpdateNoticeType {
    SHARES_BY_ME("shares by me"),
    SHARES_WITH_ME("shares with me"),
    DOCUMENT("document"),
    FOLDER("folder"),
    WORKSPACE("workspace"),
    PREVIEW_READY("preview ready"),
    PREVIEW_PROCESSING("preview processing"),
    KEEP_ALIVE_PING("keep alive ping"),
    KEEP_ALIVE_PONG("keep alive pong"),
    USER_GROUP_ADD("user group add"),
    USER_GROUP_REMOVE("user group remove"),
    USER_CREATED("user created"),
    USER_MODIFIED("user modified"),
    USER_REMOVED("user_removed"),
    GROUP_CREATED("group created"),
    GROUP_REMOVED("group removed"),
    GROUP_MODIFIED("group modified"),
    WORKSPACE_CREATED("workspace created"),
    WORKSPACE_UPDATED("workspace updated"),
    WORKSPACE_REMOVED("workspace removed"),
    FOLDER_CREATED("folder created"),
    FOLDER_UPDATED("folder updated"),
    FOLDER_REMOVED("folder removed"),
    // todo : virtual folders
    DOCUMENT_CREATED("document created"),
    DOCUMENT_VERSION_CREATED("document version created"),
    DOCUMENT_VERSION_UPDATED("document version updated"),
    DOCUMENT_REMOVED("document removed");
    private final String value;

    UpdateNoticeType(String state) {
        this.value = state;
    }

    public String getValue() {
        return value;
    }
}
