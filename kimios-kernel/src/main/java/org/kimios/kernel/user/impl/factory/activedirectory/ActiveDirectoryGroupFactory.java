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
import org.kimios.kernel.user.GroupFactory;
import org.kimios.kernel.user.impl.AuthenticationSourceAD;
import org.kimios.kernel.user.impl.factory.genericldaplayer.GenericLDAPGroupFactory;

/**
 * @see GenericLDAPGroupFactory
 * @deprecated
 */
public class ActiveDirectoryGroupFactory implements GroupFactory
{
    private AuthenticationSourceAD source;

    public ActiveDirectoryGroupFactory(AuthenticationSourceAD source)
    {
        this.source = source;
    }

    public Group getGroup(String gid) throws DataSourceException, ConfigException
    {
        try {
            NamingEnumeration<SearchResult> r = this.search("(&(objectClass=group)(cn=" + gid + "))");
            if (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                return new Group(gid, attrs.get("cn").get().toString(), source.getName());
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<Group> getGroups() throws DataSourceException, ConfigException
    {
        try {
            Vector<Group> v = new Vector<Group>();
            NamingEnumeration<SearchResult> r = this.search("(objectClass=group)");
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                v.add(new Group(attrs.get("cn").get().toString(), attrs.get("cn").get().toString(), source.getName()));
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<Group> getGroups(String userUid) throws DataSourceException, ConfigException
    {
        try {
            Vector<Group> v = new Vector<Group>();
            NamingEnumeration<SearchResult> r = this.search("(&(objectClass=user)(sAMAccountName=" + userUid + "))");
            while (r.hasMoreElements()) {
                SearchResult sr = r.nextElement();
                Attributes attrs = sr.getAttributes();
                if (attrs != null && attrs.get("memberOf") != null) {
                    NamingEnumeration<?> values = attrs.get("memberOf").getAll();
                    if (values != null) {
                        while (values.hasMoreElements()) {
                            NamingEnumeration<SearchResult> gr = this.search(
                                    "(&(objectClass=group)(distinguishedName=" + values.nextElement().toString()
                                            + "))");
                            if (gr.hasMoreElements()) {
                                SearchResult group = gr.nextElement();
                                Attributes gAttrs = group.getAttributes();
                                v.add(new Group(gAttrs.get("cn").get().toString(), gAttrs.get("cn").get().toString(),
                                        source.getName()));
                            }
                        }
                    }
                }
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException("LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e);
        }
    }

    public void deleteGroup(Group group) throws DataSourceException, ConfigException
    {
        // not in AD sources

    }

    public void saveGroup(Group group) throws DataSourceException, ConfigException
    {
        // not in AD sources

    }

    public void updateGroup(Group group) throws DataSourceException, ConfigException
    {
        // not in AD sources

    }

    private DirContext getContext() throws NamingException, AuthenticationException
    {
        return this.getContext(source.getServerUrl() + "/" + source.getBaseDn(), "simple", "follow",
                source.getUserWrapLeft() + source.getAdminLogin()
                        + source.getUserWrapRight(), source.getAdminPassword());
    }

    private DirContext getContext(String ldapUrl, String ldapAuthenticateMode, String ldapReferralMode, String ldapUser,
            String ldapPassword)
            throws NamingException, AuthenticationException
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
}

