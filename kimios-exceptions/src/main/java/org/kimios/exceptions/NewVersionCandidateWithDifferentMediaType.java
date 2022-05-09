package org.kimios.exceptions;

public class NewVersionCandidateWithDifferentMediaType extends DmsKernelException {

    long dataTransferId;

    public NewVersionCandidateWithDifferentMediaType(String message) {
        super(message);
        dataTransferId = -1;
    }

    public NewVersionCandidateWithDifferentMediaType(String message, long dataTransferId) {
        super(message);
        this.dataTransferId = dataTransferId;
    }

    public long getDataTransferId() {
        return dataTransferId;
    }
}
