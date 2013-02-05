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

import java.util.Hashtable;
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
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.GroupFactory;
import org.kimios.kernel.user.impl.AuthenticationSourceLDAP;

/**
 * @see org.kimios.kernel.user.impl.factory.genericldaplayer.GenericLDAPGroupFactory
 * @deprecated
 */
public class LDAPGroupFactory implements GroupFactory
{
    private AuthenticationSourceLDAP source;

    public LDAPGroupFactory(AuthenticationSourceLDAP source)
    {
        this.source = source;
    }

    public Group getGroup(String gid) throws ConfigException, DataSourceException
    {
        try {
            NamingEnumeration<SearchResult> r = this.search("(&(objectClass=posixGroup)(" + source.getGidAttribute()
                    + "=" + gid + "))", SecurityEntityType.GROUP);
            if (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                return new Group(gid, attrs.get(source.getGroupNameAttribute()).get().toString(), source.getName());
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<Group> getGroups() throws ConfigException, DataSourceException
    {
        try {
            Vector<Group> v = new Vector<Group>();
            NamingEnumeration<SearchResult> r = this.search("(&(objectClass=posixGroup))", SecurityEntityType.GROUP);
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                v.add(new Group(attrs.get(source.getGidAttribute()).get().toString(), attrs.get(
                        source.getGroupNameAttribute()).get().toString(), source.getName()));
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<Group> getGroups(String userUid) throws ConfigException, DataSourceException
    {
        try {
            Vector<Group> v = new Vector<Group>();
            NamingEnumeration<SearchResult> r = this.search("(&(objectClass=posixGroup)(memberUid=" + userUid + "))",
                    SecurityEntityType.GROUP);
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                v.add(new Group(attrs.get(source.getGidAttribute()).get().toString(), attrs.get(
                        source.getGroupNameAttribute()).get().toString(), source.getName()));
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    private DirContext getContext() throws NamingException, AuthenticationException
    {
        return this.getContext(source.getServerUrl() + "/" + source.getBaseDn(), source.getAuthenticationMode(), source
                .getReferralMode(), source.getRootDn(), source.getRootDnPassword());
    }

    private DirContext getContext(String ldapUrl, String ldapAuthenticateMode, String ldapReferralMode,
            String ldapUser, String ldapPassword) throws NamingException, AuthenticationException
    {
        String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, ldapAuthenticateMode);
        env.put(Context.SECURITY_PRINCIPAL, ldapUser);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put(Context.REFERRAL, ldapReferralMode);
        return new InitialDirContext(env);
    }

    public NamingEnumeration<SearchResult> search(String s, int type) throws NamingException
    {
        try {
            String where;
            if (type == SecurityEntityType.GROUP) {
                where = source.getGroupsDn().substring(0, source.getGroupsDn().indexOf(","));
            } else {
                where = source.getUsersDn().substring(0, source.getUsersDn().indexOf(","));
            }
            DirContext context = this.getContext();
            NamingEnumeration<SearchResult> result = context.search(where, s, new SearchControls());
            context.close();
            return result;
        } catch (NamingException ne) {
            // If connection to LDAP server is lost
            if (ne.getCause().getClass().getName().equals("java.net.SocketException")) {
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

    public void deleteGroup(Group group) throws DataSourceException, ConfigException
    {

    }

    public void saveGroup(Group group) throws DataSourceException, ConfigException
    {

    }

    public void updateGroup(Group group) throws DataSourceException, ConfigException
    {

    }
}
