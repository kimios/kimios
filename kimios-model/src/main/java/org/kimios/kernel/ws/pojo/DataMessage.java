package org.kimios.kernel.ws.pojo;

import java.util.List;
import java.util.stream.Collectors;

public class DataMessage extends Message {

    private List<DMEntity> dmEntityList;
    private DMEntity parent;

    public DataMessage() {
    }

    public DataMessage(String token, String sessionId) {
        super(token, sessionId);
    }

    public DataMessage(String token, String sessionId, List<DMEntity> dmEntityList, DMEntity parent) {
        super(token, sessionId);
        this.dmEntityList = dmEntityList;
        this.parent = parent;
    }

    public List<DMEntity> getDmEntityList() {
        return dmEntityList;
    }

    public DMEntity getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "{ parent: " + parent.toString() + " ; "
                + "\n"
                + "dmEntityList (" + dmEntityList.size() + ") : ["
                + "\n"
                + dmEntityList.stream().map(dmEntity -> dmEntity.toString()).collect(Collectors.joining("\n"))
                + (dmEntityList.size() > 0 ? "\n" : "")
                + "]}";
    }
}
