package org.kimios.kernel.ws.pojo;

public class DataTransactionWrapper {
    Session session;
    DataTransaction dataTransaction;

    public DataTransactionWrapper(Session session, DataTransaction dataTransaction) {
        this.session = session;
        this.dataTransaction = dataTransaction;
    }

    public Session getSession() {
        return session;
    }

    public DataTransaction getDataTransaction() {
        return dataTransaction;
    }
}
