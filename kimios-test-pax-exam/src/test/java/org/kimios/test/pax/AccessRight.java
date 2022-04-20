package org.kimios.test.pax;

public class AccessRight {
    boolean read;
    boolean write;
    boolean fullAccess;

    public AccessRight() {
    }

    public AccessRight(boolean read, boolean write, boolean fullAccess) {
        this.read = read;
        this.write = write;
        this.fullAccess = fullAccess;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isFullAccess() {
        return fullAccess;
    }

    public void setFullAccess(boolean fullAccess) {
        this.fullAccess = fullAccess;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AccessRight))
            return false;
        AccessRight other = (AccessRight) o;

        return other.read == this.read
                && other.write == this.write
                && other.fullAccess == this.fullAccess;
    }

    @Override
    public String toString() {
        return "{"
                + this.read
                + ", "
                + this.write
                + ", "
                + this.fullAccess
                + "}";
    }
}
