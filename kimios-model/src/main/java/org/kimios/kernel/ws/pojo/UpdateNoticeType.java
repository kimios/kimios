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
    DOCUMENT_UPDATE("document update"),
    DOCUMENT_REMOVED("document removed"),
    DOCUMENT_CHECKOUT("document checkout"),
    DOCUMENT_CHECKIN("document checkin"),
    DOCUMENT_ADD_RELATED("document add_related"),
    DOCUMENT_REMOVE_RELATED("document remove_related"),
    DOCUMENT_VERSION_CREATE("document version_create"),
    DOCUMENT_VERSION_CREATE_FROM_LATEST("document version_create_from_latest"),
    DOCUMENT_VERSION_UPDATE("document version_update"),
    DOCUMENT_VERSION_READ("document version_read"),
    META_VALUE_UPDATE("meta value_update"),
    DOCUMENT_VERSION_COMMENT_CREATE("document version_comment_create"),
    DOCUMENT_VERSION_COMMENT_UPDATE("document version_comment_update"),
    DOCUMENT_VERSION_COMMENT_DELETE("document version_comment_delete"),
    DOCUMENT_TRASH("document trash"),
    DOCUMENT_UNTRASH("document untrash"),
    DOCUMENT_SHARED("document shared"),
    NEW_TAG("new tag");
    private final String value;

    UpdateNoticeType(String state) {
        this.value = state;
    }

    public String getValue() {
        return value;
    }
}
