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
package org.kimios.kernel.user.impl.factory.genericldaplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.GroupFactory;
import org.kimios.kernel.user.impl.GenericLDAPImpl;

public class GenericLDAPGroupFactory extends GenericLDAPFactory implements GroupFactory
{
    public GenericLDAPGroupFactory(GenericLDAPImpl source)
    {
        this.source = source;
    }

    public Group getGroup(String gid) throws ConfigException, DataSourceException
    {
        try {
            List<SearchResult> r = this.search(
                    "(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" + source.getGroupsIdKey() + "=" +
                            gid + "))",
                    SecurityEntityType.GROUP);
            if (!r.isEmpty()) {
                Attributes attrs = r.get(0).getAttributes();
                return new Group(gid, attrs.get(source.getGroupsDescriptionKeyn()).get().toString(), source.getName());
            } else {
                return null;
            }
        } catch (AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<Group> getGroups() throws ConfigException, DataSourceException
    {
        try {
            Vector<Group> v = new Vector<Group>();
            List<SearchResult> r = this.search("(&(objectClass=" + source.getGroupsObjectClassValue() + "))",
                    SecurityEntityType.GROUP);
            for (SearchResult sr : r) {
                Attributes attrs = sr.getAttributes();
                v.add(new Group(attrs.get(source.getGroupsIdKey()).get().toString(),
                        attrs.get(source.getGroupsDescriptionKeyn()).get().toString(), source.getName()));
            }
            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    public Vector<Group> getGroups(String userUid) throws ConfigException, DataSourceException
    {
        try {
            Vector<Group> v = new Vector<Group>();
            List<SearchResult> r = null;
            List<String> nodes = null;
            if (source.isActiveDirectory()) {
                if (source.getUsersDn() != null) {
                    if (source.getUsersDn().indexOf(':') != -1) {
                        String[] ns = source.getUsersDn().split(":");
                        nodes = new ArrayList<String>();
                        for (int i = 0; i < ns.length; i++) {
                            nodes.add(ns[i]);
                        }
                    }
                }
                if (nodes == null) {
                    if (this.source.getUsersDn() == null || this.source.getUsersDn().length() == 0) {
                        v = getGroupsAD(userUid);
                    } else {
                        r = this.search("(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" +
                                source.getGroupsMemberKey() + "=CN=" + userUid + ","
                                + source.getUsersDn() + "))", SecurityEntityType.GROUP);

                        for (SearchResult sr : r) {
                            Attributes attrs = sr.getAttributes();
                            v.add(new Group(attrs.get(source.getGroupsIdKey()).get().toString(),
                                    attrs.get(source.getGroupsDescriptionKeyn()).get().toString(), source.getName()));
                        }
                    }
                } else {
                    r = new ArrayList<SearchResult>();
                    for (String node : nodes) {
                        List<SearchResult> lst = this.search(
                                "(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" +
                                        source.getGroupsMemberKey()
                                        + "=CN=" + userUid + "," + node + "))", SecurityEntityType.GROUP);
                        r.addAll(lst);
                    }
                }
            } else {
                // classic ldap
                r = this.search(
                        "(&(objectClass=" + source.getGroupsObjectClassValue() + ")(" + source.getGroupsMemberKey() +
                                "=" + userUid + "))",
                        SecurityEntityType.GROUP);

                for (SearchResult sr : r) {
                    Attributes attrs = sr.getAttributes();
                    v.add(new Group(attrs.get(source.getGroupsIdKey()).get().toString(),
                            attrs.get(source.getGroupsDescriptionKeyn()).get().toString(), source.getName()));
                }
            }

            return v;
        } catch (AuthenticationException e) {
            throw new ConfigException(e, "LDAP connection failed, please check your settings");
        } catch (NamingException e) {
            throw new DataSourceException(e, "LDAP Exception : " + e.getMessage());
        }
    }

    private Vector<Group> getGroupsAD(String userId) throws NamingException, AuthenticationException
    {
        Vector<Group> v = new Vector<Group>();

        List<SearchResult> r = this.search("(&(objectClass=" + this.source.getUsersObjectClassValue() + ")("
                + this.source.getUsersIdKey() + "=" + userId + "))", SecurityEntityType.USER);

        SearchResult rUser = r.get(0);

        List<SearchResult> rGroup = this.search("(&(objectClass=" + this.source.getGroupsObjectClassValue() + ")("
                + this.source.getGroupsMemberKey() + "=" +
                rUser.getAttributes().get("distinguishedName").get().toString() + "))", SecurityEntityType.GROUP);

        for (SearchResult sr : rGroup) {
            Attributes gAttrs = sr.getAttributes();
            v.add(new Group(gAttrs.get(this.source.getGroupsIdKey()).get().toString(),
                    gAttrs.get(this.source.getGroupsDescriptionKeyn()).get().toString(), source.getName()));
        }

//        System.out.println(" >> getting result for " + userId + " " + r.size());
//        for (SearchResult sr : r) {
//            Attributes attrs = sr.getAttributes();
//            if (attrs != null && attrs.get(this.source.getUserGroupMemberOfKey()) != null) {
//                NamingEnumeration<?> values = attrs.get(this.source.getUserGroupMemberOfKey()).getAll();
//                if (values != null) {
//                    while (values.hasMoreElements()) {
//                        System.out.println(" >> geting group " + "(&(objectClass=" + this.source.getGroupsObjectClassValue() + ")(distinguishedName=" + values.nextElement().toString()
//                                + "))");
//                        List<SearchResult> gr = this.search("(&(objectClass=" + this.source.getGroupsObjectClassValue() + ")(distinguishedName=" + values.nextElement().toString()
//                                + "))", SecurityEntityType.GROUP);
//                        System.out.println(">>>>>>>> gr: " + gr.size());
//                        if (gr != null && gr.size() > 0) {
//                            SearchResult group = gr.get(0);
//                            Attributes gAttrs = group.getAttributes();
//                            v.add(new Group(gAttrs.get(this.source.getGroupsIdKey()).get().toString(), gAttrs.get(this.source.getGroupsDescriptionKeyn()).get().toString(), source.getName()));
//                        }
//                    }
//                }
//            }
//        }
        return v;
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
