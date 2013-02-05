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
package org.kimios.kernel.user.impl.factory.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.user.UserFactory;
import org.kimios.kernel.user.impl.AuthenticationSourceLDAP;

/**
 * @see org.kimios.kernel.user.impl.factory.genericldaplayer.GenericLDAPUserFactory
 * @deprecated
 */
public class LDAPUserFactory implements UserFactory
{
    private AuthenticationSourceLDAP source;

    public LDAPUserFactory(AuthenticationSourceLDAP source)
    {
        this.source = source;
    }

    public boolean authenticate(String uid, String passwd)
            throws DataSourceException, ConfigException
    {
        try {
            DirContext ctxtDir = this.getContext(source.getServerUrl() + "/"
                    + source.getBaseDn(), source.getAuthenticationMode(),
                    source.getReferralMode(), source.getUidAttribute() + "="
                    + uid + "," + source.getUsersDn(), passwd);
            ctxtDir.close();
            return true;
        } catch (AuthenticationException e) {
            return false;
        } catch (NamingException e) {
            throw new DataSourceException(e);
        }
    }

    public User getUser(String uid) throws DataSourceException, ConfigException
    {
        try {
            NamingEnumeration<SearchResult> r = this.search(
                    "(&(objectClass=posixAccount)(" + source.getUidAttribute()
                            + "=" + uid + "))", SecurityEntityType.USER);
            if (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                return new User(uid, attrs.get(source.getUserNameAttribute())
                        .get().toString(), null, attrs
                        .get(source.getMailAttribute()).get().toString(),
                        source.getName());
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new ConfigException(
                    "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<User> getUsers(Group group) throws DataSourceException,
            ConfigException
    {
        try {
            Vector<User> v = new Vector<User>();
            NamingEnumeration<SearchResult> r = this.search(
                    "(&(objectClass=posixGroup)(" + source.getGidAttribute()
                            + "=" + group.getGid() + "))",
                    SecurityEntityType.GROUP);
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                Attribute a = attrs.get("memberUid");
                for (int i = 0; i < a.size(); i++) {
                    NamingEnumeration<SearchResult> r2 = this.search(
                            "(&(objectClass=posixAccount)("
                                    + source.getUidAttribute() + "="
                                    + a.get(i).toString() + "))",
                            SecurityEntityType.USER);
                    if (r2.hasMoreElements()) {
                        SearchResult sr2 = r2.nextElement();
                        Attributes attrs2 = sr2.getAttributes();
                        v.add(new User(a.get(i).toString(), attrs2
                                .get(source.getUserNameAttribute()).get()
                                .toString(), null, attrs2
                                .get(source.getMailAttribute()).get()
                                .toString(), source.getName()));
                    }
                }
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException(
                    "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<User> getUsers() throws DataSourceException, ConfigException
    {
        try {
            Vector<User> v = new Vector<User>();
            NamingEnumeration<SearchResult> r = this.search(
                    "(objectClass=posixAccount)", SecurityEntityType.USER);
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                if (attrs.get(source.getUserNameAttribute()) != null
                        && attrs.get(source.getMailAttribute()) != null)
                {
                    v.add(new User(attrs.get(source.getUidAttribute()).get()
                            .toString(), attrs
                            .get(source.getUserNameAttribute()).get()
                            .toString(), null, attrs
                            .get(source.getMailAttribute()).get().toString(),
                            source.getName()));
                }
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException(
                    "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e);
        }
    }

    private DirContext getContext() throws NamingException,
            AuthenticationException
    {
        return this.getContext(
                source.getServerUrl() + "/" + source.getBaseDn(),
                source.getAuthenticationMode(), source.getReferralMode(),
                source.getRootDn(), source.getRootDnPassword());
    }

    private DirContext getContext(String ldapUrl, String ldapAuthenticateMode,
            String ldapReferralMode, String ldapUser, String ldapPassword)
            throws NamingException, AuthenticationException
    {
        String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, ldapUrl);
        ;
        env.put(Context.SECURITY_AUTHENTICATION, ldapAuthenticateMode);
        env.put(Context.SECURITY_PRINCIPAL, ldapUser);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put(Context.REFERRAL, ldapReferralMode);
        return new InitialDirContext(env);
    }

    public NamingEnumeration<SearchResult> search(String s, int type)
            throws NamingException
    {
        try {
            String where;
            if (type == SecurityEntityType.GROUP) {
                where = source.getGroupsDn().substring(0,
                        source.getGroupsDn().indexOf(","));
            } else {
                where = source.getUsersDn().substring(0,
                        source.getUsersDn().indexOf(","));
            }
            DirContext context = this.getContext();
            NamingEnumeration<SearchResult> result = context.search(where, s,
                    new SearchControls());
            context.close();
            return result;
        } catch (NamingException ne) {
            // If connection to LDAP server is lost
            if (ne.getCause() != null
                    && ne.getCause().getClass().getName()
                    .equals("java.net.SocketException"))
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                return search(s, type);
            } else {
                throw ne;
            }
        }
    }

    public void addUserToGroup(User user, Group group)
            throws DataSourceException, ConfigException
    {

    }

    public void deleteUser(User user) throws DataSourceException,
            ConfigException
    {

    }

    public void removeUserFromGroup(User user, Group group)
            throws DataSourceException, ConfigException
    {

    }

    public void saveUser(User user, String password)
            throws DataSourceException, ConfigException
    {

    }

    public void updateUser(User user, String password)
            throws DataSourceException, ConfigException
    {

    }

    public Object getAttribute(User user, String attributeName)
            throws DataSourceException, ConfigException
    {
        try {
            String s = "(&(objectClass=posixAccount)("
                    + source.getUidAttribute() + "=" + user.getUid() + "))";
            NamingEnumeration<SearchResult> r = this.search(s,
                    SecurityEntityType.USER);
            if (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                Attribute attr = attrs.get(attributeName);
                return attr.get();
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new ConfigException(e,
                    "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : "
                    + e.getMessage());
        }
    }

    public void setAttribute(User user, String attributeName,
            Object attributeValue) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public Map<String, String> getAttributes(User user)
            throws DataSourceException, ConfigException
    {
        try {
            String s = "(&(objectClass=posixAccount)("
                    + source.getUidAttribute() + "=" + user.getUid() + "))";
            NamingEnumeration<SearchResult> r = this.search(s,
                    SecurityEntityType.USER);
            if (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                Map<String, String> items = new HashMap<String, String>();
                NamingEnumeration<? extends Attribute> lst = attrs.getAll();
                while (lst.hasMoreElements()) {
                    Attribute attr = lst.nextElement();
                    if (attr.get() != null) {
                        items.put(attr.getID(), attr.get().toString());
                    }
                }
                return items;
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new ConfigException(e,
                    "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : "
                    + e.getMessage());
        }
    }

    public User getUserByAttributeValue(String attributeName,
            String attributeValue) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }
}
