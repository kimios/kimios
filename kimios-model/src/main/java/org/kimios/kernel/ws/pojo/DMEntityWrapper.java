package org.kimios.kernel.ws.pojo;

public class DMEntityWrapper {
    DMEntity dmEntity;
    Boolean canRead;
    Boolean canWrite;

    Boolean hasFullAccess;

    public DMEntityWrapper() {
    }

    public DMEntityWrapper(DMEntity dmEntity) {
        this.dmEntity = dmEntity;
    }

    public DMEntityWrapper(DMEntity dmEntity, Boolean canRead, Boolean canWrite, Boolean hasFullAccess) {
        this.dmEntity = dmEntity;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.hasFullAccess = hasFullAccess;
    }

    public DMEntity getDmEntity() {
        return dmEntity;
    }

    public void setDmEntity(DMEntity dmEntity) {
        this.dmEntity = dmEntity;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanWrite() {
        return canWrite;
    }

    public void setCanWrite(Boolean canWrite) {
        this.canWrite = canWrite;
    }

    public Boolean getHasFullAccess() {
        return hasFullAccess;
    }

    public void setHasFullAccess(Boolean hasFullAccess) {
        this.hasFullAccess = hasFullAccess;
    }
}
