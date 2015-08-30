/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.user;

import org.hibernate.annotations.Formula;
import org.kimios.kernel.security.SecurityEntity;
import org.kimios.kernel.security.SecurityEntityType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a DMS user object.
 *
 * @author Louis Sicard
 * @author Fabien Alin (farf)  <fabien.alin@gmail.com>
 */
@Entity
@Table(name = "users")
@IdClass(UserPK.class)
public class User implements SecurityEntity, Serializable
{
    @Id
    @Column(name = "user_id")
    private String uid;

    @Column(name = "user_fullname")
    private String name;

    @Column(name = "user_firstname")
    private String firstName;

    @Column(name = "user_lastname")
    private String lastName;

    @Column(name = "user_phone_number")
    private String phoneNumber;

    @Column(name = "user_avatar")
    private byte[] avatar;

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(name = "mail")
    private String mail;

    @Column(name = "user_password")
    private String password;

    @Id @Column(name = "authentication_source")
    private String authenticationSourceName;

    @ManyToMany(targetEntity = Group.class)
    @JoinTable(name = "user_group",
            joinColumns = { @JoinColumn(name = "user_id"), @JoinColumn(name = "authentication_source") },
            inverseJoinColumns = { @JoinColumn(name = "gid", referencedColumnName = "gid"),
                    @JoinColumn(name = "authentication_source", referencedColumnName = "authentication_source")
            })
    @Formula("authentication_source")
    private Set<Group> groups = new HashSet<Group>();

    @ElementCollection
    @MapKeyColumn(name = "attribute_name")
    @MapKeyClass(String.class)
    @CollectionTable(name = "user_attributes",
            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
                    @JoinColumn(name = "authentication_source", referencedColumnName = "authentication_source") })
    @Column(name = "attribute_value")
    private Map<String, String> attributes = new HashMap<java.lang.String, java.lang.String>();

    @Column(name = "user_enabled", nullable = false)
    private boolean enabled = true;



    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="user_emails", joinColumns=@JoinColumn(name="user_id"))
    @Column(name="user_email")
    private Set<String> emails = new HashSet<String>();

    public Set<String> getEmails() { return this.emails; }

    public void setEmails(Set<String> emails){
        this.emails = emails;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public User()
    {
    }

    public User(String uid, String source){
        this.uid = uid;
        this.authenticationSourceName = source;
    }

    public User(String uid, String firstName, String lastName, String phoneNumber, Date lastLogin, String mail, String authenticationSourceName)
    {
        this(uid, firstName, lastName, phoneNumber, lastLogin, mail, authenticationSourceName, true);
    }

    public User(String uid, String firstName, String lastName, String phoneNumber, Date lastLogin, String mail, String authenticationSourceName, boolean enabled)
    {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.name = firstName +" " + lastName;
        this.lastLogin = lastLogin;
        this.mail = mail;
        this.authenticationSourceName = authenticationSourceName;
        this.enabled = enabled;
    }

    public String getAuthenticationSourceName()
    {
        return authenticationSourceName;
    }

    public void setAuthenticationSourceName(String authenticationSourceName)
    {
        this.authenticationSourceName = authenticationSourceName;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public int getType()
    {
        return SecurityEntityType.USER;
    }

    public String getID()
    {
        return this.uid;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean equals(Object o)
    {
        if (o.getClass().getName().equals(this.getClass().getName())) {
            return ((User) o).getID().equals(this.getID());
        } else {
            return false;
        }
    }

    public Set<Group> getGroups()
    {
        return groups;
    }

    public void setGroups(Set<Group> groups)
    {
        this.groups = groups;
    }

    public org.kimios.kernel.ws.pojo.User toPojo()
    {
        return new org.kimios.kernel.ws.pojo.User(this.uid, this.firstName, lastName, phoneNumber, this.authenticationSourceName, this.lastLogin,
                this.mail, this.enabled);
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}

