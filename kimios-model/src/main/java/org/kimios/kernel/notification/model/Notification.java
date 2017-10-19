package org.kimios.kernel.notification.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "notification_id_seq")
public class Notification {
    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_source", nullable = false)
    private String userSource;

    @Column(name = "document_uid")
    private long documentUid;

    @Column(name = "notification_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    public Notification() {
    }

    public Notification(String userId, String userSource, long docUid) {
        this.userId = userId;
        this.userSource = userSource;
        this.documentUid = docUid;
        this.status = NotificationStatus.TO_BE_SENT;
    }

    public long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserSource() {
        return userSource;
    }

    public long getDocumentUid() {
        return documentUid;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    public void setDocumentUid(long documentUid) {
        this.documentUid = documentUid;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
