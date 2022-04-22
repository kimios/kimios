package org.kimios.kernel.ws.pojo;

import org.kimios.kernel.security.model.DMEntityACL;

import java.util.List;

public class ACLUpdate {
    List<DMEntityACL> toBeDeleted;
    List<DMEntityACL> toBeAdded;

    public ACLUpdate() {
    }

    public ACLUpdate(List<DMEntityACL> toBeDeleted, List<DMEntityACL> toBeAdded) {
        this.toBeDeleted = toBeDeleted;
        this.toBeAdded = toBeAdded;
    }

    public List<DMEntityACL> getToBeDeleted() {
        return toBeDeleted;
    }

    public void setToBeDeleted(List<DMEntityACL> toBeDeleted) {
        this.toBeDeleted = toBeDeleted;
    }

    public List<DMEntityACL> getToBeAdded() {
        return toBeAdded;
    }

    public void setToBeAdded(List<DMEntityACL> toBeAdded) {
        this.toBeAdded = toBeAdded;
    }
}
