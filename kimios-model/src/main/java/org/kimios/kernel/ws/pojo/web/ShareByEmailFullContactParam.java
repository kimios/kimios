package org.kimios.kernel.ws.pojo.web;

import org.kimios.kernel.share.model.MailContact;

import java.util.List;

public class ShareByEmailFullContactParam {
    String sessionId;
    List<Long> documentIds;
    List<MailContact> recipients;
    String subject;
    String content;
    String senderAddress;
    String senderName;
    Boolean defaultSender;
    String password;
    String expirationDate;

    public ShareByEmailFullContactParam() {
    }

    public ShareByEmailFullContactParam(String sessionId, List<Long> documentIds, List<MailContact> recipients,
                                        String subject, String content, String senderAddress, String senderName,
                                        Boolean defaultSender, String password, String expirationDate) {
        this.sessionId = sessionId;
        this.documentIds = documentIds;
        this.recipients = recipients;
        this.subject = subject;
        this.content = content;
        this.senderAddress = senderAddress;
        this.senderName = senderName;
        this.defaultSender = defaultSender;
        this.password = password;
        this.expirationDate = expirationDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }

    public List<MailContact> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<MailContact> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Boolean getDefaultSender() {
        return defaultSender;
    }

    public void setDefaultSender(Boolean defaultSender) {
        this.defaultSender = defaultSender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
