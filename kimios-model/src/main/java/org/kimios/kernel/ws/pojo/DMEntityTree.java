package org.kimios.kernel.ws.pojo;

import java.util.ArrayList;
import java.util.List;

public class DMEntityTree {
    List<DMEntityTreeNode> treeNodeList;

    public DMEntityTree() {
        this.treeNodeList = new ArrayList<>();
    }

    public DMEntityTree(List<DMEntityTreeNode> treeNodeList) {
        this.treeNodeList = treeNodeList;
    }

    public List<DMEntityTreeNode> getTreeNodeList() {
        return treeNodeList;
    }

    public void setTreeNodeList(List<DMEntityTreeNode> treeNodeList) {
        this.treeNodeList = treeNodeList;
    }

    public void addNode(DMEntityTreeNode node) {
        this.treeNodeList.add(node);
    }
}
