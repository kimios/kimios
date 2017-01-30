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
package org.kimios.kernel.user.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.user.model.AuthenticationSourceImpl;
import org.kimios.kernel.user.model.GroupFactory;
import org.kimios.kernel.user.model.UserFactory;
import org.kimios.kernel.user.impl.factory.genericldaplayer.GenericLDAPGroupFactory;
import org.kimios.kernel.user.impl.factory.genericldaplayer.GenericLDAPUserFactory;

/**
 * The generic LDAP authentication source layer implementation.
 *
 * @author jludmann
 * @see GenericLDAPUserFactory
 * @see GenericLDAPGroupFactory
 */
public class GenericLDAPImpl extends AuthenticationSourceImpl
{
    private String domainHostName;

    private String domainHostPort;

    private String domainBaseDN;

    private String domainRootDN;

    private String domainRootDNPassword;

    private String domainReferralMode;

    private String domainAuthenticationMode;

    private String domainIsSubtreeScope;

    private String domainIsActiveDirectory;

    private String domainUserPrefix;

    private String domainUserSuffix;

    private String userDN;

    private String userObjectClassValue;

    private String userIDKey;

    private String userDescriptionKey;

    private String userMailKey;

    private String userGroupMemberOfKey;

    private String userFirstNameKey;

    private String userLastNameKey;

    private String userPhoneKey;

    private String groupDN;

    private String groupObjectClassValue;

    private String groupIDKey;

    private String groupDescriptionKey;

    private String groupMemberKey;

    private String schemaFullCnUserGroupMatching;

    @Override
    public GroupFactory getGroupFactory() throws DataSourceException, ConfigException
    {
        return new GenericLDAPGroupFactory(this);
    }

    @Override
    public UserFactory getUserFactory() throws DataSourceException, ConfigException
    {
        return new GenericLDAPUserFactory(this);
    }

    public String getProviderUrl()
    {
        return "ldap://" + domainHostName + ":" + domainHostPort;
    }

    public String getBaseDn()
    {
        return domainBaseDN;
    }

    public String getAuthenticationMode()
    {
        return domainAuthenticationMode;
    }

    public String getReferralMode()
    {
        return domainReferralMode;
    }

    public String getUsersDn()
    {
        return userDN;
    }

    public String getGroupsDn()
    {
        return groupDN;
    }

    public String getRootDn()
    {
        return domainRootDN;
    }

    public String getRootDnPassword()
    {
        return domainRootDNPassword;
    }

    public String getUsersDescriptionKey()
    {
        return userDescriptionKey;
    }

    public String getUsersIdKey()
    {
        return userIDKey;
    }

    public String getUsersPrefix()
    {
        return domainUserPrefix;
    }

    public String getUsersSuffix()
    {
        return domainUserSuffix;
    }

    public String getGroupsDescriptionKeyn()
    {
        return groupDescriptionKey;
    }

    public String getGroupsIdKey()
    {
        return groupIDKey;
    }

    public String getUsersMailKey()
    {
        return userMailKey;
    }

    public String getGroupsMemberKey()
    {
        return groupMemberKey;
    }

    public String getUsersObjectClassValue()
    {
        return userObjectClassValue;
    }

    public String getGroupsObjectClassValue()
    {
        return groupObjectClassValue;
    }

    public boolean isSubtreeScope()
    {
        return Boolean.parseBoolean(domainIsSubtreeScope);
    }

    public boolean isActiveDirectory()
    {
        return Boolean.parseBoolean(domainIsActiveDirectory);
    }

    public String getUserGroupMemberOfKey()
    {
        return userGroupMemberOfKey;
    }

    public String getSchemaFullCnUserGroupMatching()
    {
        return schemaFullCnUserGroupMatching;
    }

    public void setSchemaFullCnUserGroupMatching(String schemaFullCnUserGroupMatching)
    {
        this.schemaFullCnUserGroupMatching = schemaFullCnUserGroupMatching;
    }

    public String getUserFirstNameKey() {
        return userFirstNameKey;
    }

    public void setUserFirstNameKey(String userFirstNameKey) {
        this.userFirstNameKey = userFirstNameKey;
    }

    public String getUserLastNameKey() {
        return userLastNameKey;
    }

    public void setUserLastNameKey(String userLastNameKey) {
        this.userLastNameKey = userLastNameKey;
    }

    public String getUserPhoneKey() {
        return userPhoneKey;
    }

    public void setUserPhoneKey(String userPhoneKey) {
        this.userPhoneKey = userPhoneKey;
    }
}

