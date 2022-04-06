package org.kimios.kernel.ws.pojo;

import java.util.List;
import java.util.stream.Collectors;

public class DataMessage extends Message {

    private List<DMEntityWrapper> dmEntityList;
    private DMEntityWrapper parent;

    public DataMessage() {
    }

    public DataMessage(String token, String sessionId) {
        super(token, sessionId);
    }

    public DataMessage(String token, String sessionId, List<DMEntityWrapper> dmEntityList, DMEntityWrapper parent) {
        super(token, sessionId);
        this.dmEntityList = dmEntityList;
        this.parent = parent;
    }

    public List<DMEntityWrapper> getDmEntityList() {
        return dmEntityList;
    }

    public DMEntityWrapper getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "{ parent: " + parent.getDmEntity().toString() + " ; "
                + "\n"
                + "dmEntityList (" + dmEntityList.size() + ") : ["
                + "\n"
                + dmEntityList.stream().map(dmEntityWrapper -> dmEntityWrapper.getDmEntity().toString()).collect(Collectors.joining("\n"))
                + (dmEntityList.size() > 0 ? "\n" : "")
                + "]}";
    }
}
