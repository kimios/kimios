package org.kimios.kernel.ws.pojo;

public class MailContact {

    private String emailAddress;
    private String fullName;

    public MailContact(String emailAddress, String fullName) {
        this.emailAddress = emailAddress;
        this.fullName = fullName;
    }

    public String getEmailAddress() {
            return emailAddress;
        }

    public void setEmailAddress(String emailAdress) {
            this.emailAddress = emailAdress;
        }

    public String getFullName() {
            return fullName;
        }

    public void setFullName(String fullName) {
            this.fullName = fullName;
        }
}
