package org.kimios.kernel.ws.pojo;

import java.util.List;

public class DataMessage extends Message {

    private List<DMEntity> dmEntityList;
    private long parentUid;

    public DataMessage(String token, String sessionId, List<DMEntity> dmEntityList, long parentUid) {
        super(token, sessionId);
        this.dmEntityList = dmEntityList;
        this.parentUid = parentUid;
    }

    public List<DMEntity> getDmEntityList() {
        return dmEntityList;
    }

    public long getParentUid() {
        return parentUid;
    }
}
