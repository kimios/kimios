package org.kimios.kernel.ws.pojo;

public class ShareSessionWrapper {
    Share share;
    org.kimios.kernel.security.model.Session session;

    public ShareSessionWrapper(Share share, org.kimios.kernel.security.model.Session session) {
        this.share = share;
        this.session = session;
    }

    public Share getShare() {
        return share;
    }

    public org.kimios.kernel.security.model.Session getSession() {
        return session;
    }
}
