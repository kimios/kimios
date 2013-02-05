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
package org.kimios.kernel.user.impl.factory.activedirectory;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.user.UserFactory;
import org.kimios.kernel.user.impl.AuthenticationSourceAD;

/**
 * @see org.kimios.kernel.user.impl.factory.genericldaplayer.GenericLDAPUserFactory
 * @deprecated
 */
public class ActiveDirectoryUserFactory implements UserFactory
{
    private AuthenticationSourceAD source;

    public ActiveDirectoryUserFactory(AuthenticationSourceAD source)
    {
        this.source = source;
    }

    public boolean authenticate(String uid, String password) throws DataSourceException, ConfigException
    {
        try {
            DirContext ctxtDir = this.getContext(source.getServerUrl() + "/" + source.getBaseDn(), "simple", "follow",
                    source.getUserWrapLeft() + uid + source.getUserWrapRight(), password);
            ctxtDir.close();
            return true;
        } catch (AuthenticationException e) {
            return false;
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public User getUser(String uid) throws DataSourceException, ConfigException
    {
        try {
            NamingEnumeration<SearchResult> r = this.search("(&(objectClass=user)(sAMAccountName=" + uid + "))");
            if (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                String mail = "";
                if (attrs.get("mail") != null && attrs.get("mail").get() != null) {
                    mail = attrs.get("mail").get().toString();
                }
                return new User(attrs.get("sAMAccountName").get().toString(), attrs.get("cn").get().toString(), null,
                        mail, source.getName());
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<User> getUsers() throws DataSourceException, ConfigException
    {
        try {
            Vector<User> v = new Vector<User>();
            NamingEnumeration<SearchResult> r = this.search("(objectClass=user)");
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                String mail = "";
                if (attrs.get("mail") != null && attrs.get("mail").get() != null) {
                    mail = attrs.get("mail").get().toString();
                }
                v.add(new User(attrs.get("sAMAccountName").get().toString(), attrs.get("cn").get().toString(), null,
                        mail, source.getName()));
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<User> getUsers(Group group) throws DataSourceException, ConfigException
    {
        try {
            Vector<User> v = new Vector<User>();
            NamingEnumeration<SearchResult> r = this.search(
                    "(&(objectClass=user)(memberOf=CN=" + group.getGid() + "," + source.getUsersCn() + "))");
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                String mail = "";
                if (attrs.get("mail") != null && attrs.get("mail").get() != null) {
                    mail = attrs.get("mail").get().toString();
                }
                v.add(new User(attrs.get("sAMAccountName").get().toString(), attrs.get("cn").get().toString(), null,
                        mail, source.getName()));
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public void addUserToGroup(User user, Group group) throws DataSourceException, ConfigException
    {
        // not in AD sources
    }

    public void deleteUser(User user) throws DataSourceException, ConfigException
    {
        // not in AD sources

    }

    public void removeUserFromGroup(User user, Group group) throws DataSourceException, ConfigException
    {
        // not in AD sources

    }

    public void saveUser(User user, String password) throws DataSourceException, ConfigException
    {
        // not in AD sources

    }

    public void updateUser(User user, String password) throws DataSourceException, ConfigException
    {
        // not in AD sources

    }

    private DirContext getContext() throws NamingException, AuthenticationException
    {

        return this.getContext(source.getServerUrl() + "/" + source.getBaseDn(), "simple", "follow",
                source.getUserWrapLeft() + source.getAdminLogin() + source.getUserWrapRight(),
                source.getAdminPassword());
    }

    private DirContext getContext(String adUrl, String adAuthenticateMode, String adReferralMode, String adUser,
            String adPassword) throws NamingException, AuthenticationException
    {

        String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, adUrl);
        env.put(Context.SECURITY_AUTHENTICATION, adAuthenticateMode);
        env.put(Context.SECURITY_PRINCIPAL, adUser);
        env.put(Context.SECURITY_CREDENTIALS, adPassword);
        env.put(Context.REFERRAL, adReferralMode);

        return new InitialDirContext(env);
    }

    public NamingEnumeration<SearchResult> search(String s) throws NamingException
    {
        DirContext context = this.getContext();
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> result;
        result = context.search(source.getUsersCn(), s, sc);
        context.close();
        return result;
    }

    public Object getAttribute(User user, String attributeName)
            throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public void setAttribute(User user, String attributeName,
            Object attributeValue) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public Map<String, String> getAttributes(User user)
            throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public User getUserByAttributeValue(String attributeName,
            String attributeValue) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }
}

