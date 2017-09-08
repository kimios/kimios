package org.kimios.kernel.notification.model;

import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "notifications")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "notification_id_seq")
public class Notification {
    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(targetEntity = Document.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = true)
    private Document document;

    @Column(name = "notification_status", nullable = false)
    private String status;

    public Notification(User user, Document document) {
        this.user = user;
        this.document = document;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Document getDocument() {
        return document;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
