package org.kimios.kernel.ws.pojo;

import java.util.ArrayList;
import java.util.List;

public class DMEntityTreeNode {
    Long dmEntityUid;
    List<DMEntityTreeNode> children;

    public DMEntityTreeNode() {
        this.dmEntityUid = null;
        this.children = new ArrayList<>();
    }

    public DMEntityTreeNode(Long dmEntityUid) {
        this.dmEntityUid = dmEntityUid;
        children = new ArrayList<>();
    }

    public Long getDmEntityUid() {
        return dmEntityUid;
    }

    public void setDmEntityUid(Long dmEntityUid) {
        this.dmEntityUid = dmEntityUid;
    }

    public List<DMEntityTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<DMEntityTreeNode> children) {
        this.children = children;
    }

    public void addChildren(DMEntityTreeNode node) {
        this.children.add(node);
    }
}
