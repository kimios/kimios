package org.kimios.kernel.ws.pojo.task;

import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DMEntity;

import java.util.List;

public class TaskGetFoldersAndSendData {

    private Session session;
    private DMEntity parent;
    private List<Long> bookmarkedUidList;

    public TaskGetFoldersAndSendData(Session session, DMEntity parent, List<Long> bookmarkedUidList) {
        this.session = session;
        this.parent = parent;
        this.bookmarkedUidList = bookmarkedUidList;
    }

    public Session getSession() {
        return session;
    }

    public DMEntity getParent() {
        return parent;
    }

    public List<Long> getBookmarkedUidList() {
        return bookmarkedUidList;
    }
}
