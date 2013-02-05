/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.ws.pojo;

public class LDAPParams
{
    private String authenticationSourceName;

    private String ldapServerUrl;

    private String ldapBaseDn;

    private String ldapAuthenticationMode;

    private String ldapReferralMode;

    private String ldapUsersDn;

    private String ldapGroupsDn;

    private String ldapRootDn;

    private String ldapRootDnPassword;

    private String ldapUsernameAttribute;

    private String ldapUidAttribute;

    private String ldapGidAttribute;

    private String ldapGroupNameAttribute;

    private String ldapMailAttribute;

    public LDAPParams()
    {
    }

    public LDAPParams(String authenticationSourceName, String ldapServerUrl, String ldapBaseDn,
            String ldapAuthenticationMode, String ldapReferralMode, String ldapUsersDn, String ldapGroupsDn,
            String ldapRootDn, String ldapRootDnPassword, String ldapUsernameAttribute, String ldapUidAttribute,
            String ldapGroupNameAttribute, String ldapGidAttribute, String ldapMailAttribute)
    {
        this.authenticationSourceName = authenticationSourceName;
        this.ldapServerUrl = ldapServerUrl;
        this.ldapBaseDn = ldapBaseDn;
        this.ldapAuthenticationMode = ldapAuthenticationMode;
        this.ldapReferralMode = ldapReferralMode;
        this.ldapUsersDn = ldapUsersDn;
        this.ldapGroupsDn = ldapGroupsDn;
        this.ldapRootDn = ldapRootDn;
        this.ldapRootDnPassword = ldapRootDnPassword;
        this.ldapUsernameAttribute = ldapUsernameAttribute;
        this.ldapUidAttribute = ldapUidAttribute;
        this.ldapMailAttribute = ldapMailAttribute;
        this.ldapGidAttribute = ldapGidAttribute;
        this.ldapGroupNameAttribute = ldapGroupNameAttribute;
    }

    public String getAuthenticationSourceName()
    {
        return authenticationSourceName;
    }

    public void setAuthenticationSourceName(String authenticationSourceName)
    {
        this.authenticationSourceName = authenticationSourceName;
    }

    public String getLdapAuthenticationMode()
    {
        return ldapAuthenticationMode;
    }

    public void setLdapAuthenticationMode(String ldapAuthenticationMode)
    {
        this.ldapAuthenticationMode = ldapAuthenticationMode;
    }

    public String getLdapBaseDn()
    {
        return ldapBaseDn;
    }

    public void setLdapBaseDn(String ldapBaseDn)
    {
        this.ldapBaseDn = ldapBaseDn;
    }

    public String getLdapGroupsDn()
    {
        return ldapGroupsDn;
    }

    public void setLdapGroupsDn(String ldapGroupsDn)
    {
        this.ldapGroupsDn = ldapGroupsDn;
    }

    public String getLdapMailAttribute()
    {
        return ldapMailAttribute;
    }

    public void setLdapMailAttribute(String ldapMailAttribute)
    {
        this.ldapMailAttribute = ldapMailAttribute;
    }

    public String getLdapReferralMode()
    {
        return ldapReferralMode;
    }

    public void setLdapReferralMode(String ldapReferralMode)
    {
        this.ldapReferralMode = ldapReferralMode;
    }

    public String getLdapRootDn()
    {
        return ldapRootDn;
    }

    public void setLdapRootDn(String ldapRootDn)
    {
        this.ldapRootDn = ldapRootDn;
    }

    public String getLdapRootDnPassword()
    {
        return ldapRootDnPassword;
    }

    public void setLdapRootDnPassword(String ldapRootDnPassword)
    {
        this.ldapRootDnPassword = ldapRootDnPassword;
    }

    public String getLdapServerUrl()
    {
        return ldapServerUrl;
    }

    public void setLdapServerUrl(String ldapServerUrl)
    {
        this.ldapServerUrl = ldapServerUrl;
    }

    public String getLdapUsernameAttribute()
    {
        return ldapUsernameAttribute;
    }

    public void setLdapUsernameAttribute(String ldapUsernameAttribute)
    {
        this.ldapUsernameAttribute = ldapUsernameAttribute;
    }

    public String getLdapUidAttribute()
    {
        return ldapUidAttribute;
    }

    public void setLdapUidAttribute(String ldapUidAttribute)
    {
        this.ldapUidAttribute = ldapUidAttribute;
    }

    public String getLdapUsersDn()
    {
        return ldapUsersDn;
    }

    public void setLdapUsersDn(String ldapUsersDn)
    {
        this.ldapUsersDn = ldapUsersDn;
    }

    public String getLdapGidAttribute()
    {
        return ldapGidAttribute;
    }

    public void setLdapGidAttribute(String ldapGidAttribute)
    {
        this.ldapGidAttribute = ldapGidAttribute;
    }

    public String getLdapGroupNameAttribute()
    {
        return ldapGroupNameAttribute;
    }

    public void setLdapGroupNameAttribute(String ldapGroupNameAttribute)
    {
        this.ldapGroupNameAttribute = ldapGroupNameAttribute;
    }
}

