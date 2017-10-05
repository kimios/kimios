/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2017  DevLib'
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
package org.kimios.kernel.user.ldap.ldaptive;

import org.apache.commons.lang.StringUtils;
import org.kimios.kernel.security.model.SecurityEntityType;
import org.kimios.kernel.user.ldap.LdaptiveImpl;
import org.kimios.utils.configuration.ConfigurationManager;
import org.ldaptive.*;
import org.ldaptive.pool.BlockingConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class LdaptiveFactory {

    private static Logger logger = LoggerFactory.getLogger(LdaptiveFactory.class);

    protected LdaptiveImpl source;

    protected BlockingConnectionPool pool;

    public LdaptiveFactory() {
        //pool = new BlockingConnectionPool(new DefaultConnectionFactory(source.getProviderUrl()));
        //xpool.initialize();
    }

    protected Connection buildConnection() throws LdapException {
        //PooledConnectionFactory connFactory = new PooledConnectionFactory(pool);
        //return connFactory.getConnection();
        ConnectionConfig connConfig = new ConnectionConfig();
        connConfig.setLdapUrl(source.getProviderUrl());
        connConfig.setConnectionInitializer(
                new BindConnectionInitializer(source.getRootDn(), new Credential(source.getRootDnPassword())));
        return DefaultConnectionFactory.getConnection(connConfig);

    }

    protected Connection buildConnection(String uid, String password) throws LdapException {
        //PooledConnectionFactory connFactory = new PooledConnectionFactory(pool);
        //return connFactory.getConnection();
        ConnectionConfig connConfig = new ConnectionConfig();
        try {
            connConfig.setConnectTimeout(Duration.ofMillis(Integer.parseInt(ConfigurationManager.getValue("dms.ldap.cnx.timeout"))));
        }catch (Exception ex){
            logger.warn("unable to set ldap connect timeout", ex);
        }
        try {
            connConfig.setResponseTimeout(Duration.ofMillis(Integer.parseInt(ConfigurationManager.getValue("dms.ldap.read.timeout"))));
        } catch (Exception ex){
            logger.warn("unable to set ldap response timeout", ex);
        }
        connConfig.setLdapUrl(source.getProviderUrl());
        uid = getUserString(uid);
        logger.debug("binding to ldap with {}", uid);
        connConfig.setConnectionInitializer(
                new BindConnectionInitializer(uid, new Credential(password)));
        return DefaultConnectionFactory.getConnection(connConfig);

    }

    protected String[] buildUserAttributes() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (StringUtils.isNotBlank(source.getUsersIdKey())) {
            arrayList.add(source.getUsersIdKey());
        }
        if (StringUtils.isNotBlank(source.getUsersDescriptionKey())) {
            arrayList.add(source.getUsersDescriptionKey());
        }
        if (StringUtils.isNotBlank(source.getUsersMailKey())) {
            arrayList.add(source.getUsersMailKey());
        }
        if (StringUtils.isNotBlank(source.getUserFirstNameKey())) {
            arrayList.add(source.getUserFirstNameKey());
        }
        if (StringUtils.isNotBlank(source.getUserLastNameKey())) {
            arrayList.add(source.getUserLastNameKey());
        }
        if (StringUtils.isNotBlank(source.getUserPhoneKey())) {
            arrayList.add(source.getUserPhoneKey());
        }
        if (StringUtils.isNotBlank(source.getUserGroupMemberOfKey())) {
            arrayList.add(source.getUserGroupMemberOfKey());
        }
        if (StringUtils.isNotBlank(source.getGroupsMemberKey())) {
            arrayList.add(source.getGroupsMemberKey());
        }
        return arrayList.toArray(new String[]{});
    }

    protected String[] buildGroupAttributes() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (StringUtils.isNotBlank(source.getGroupsDescriptionKeyn())) {
            arrayList.add(source.getGroupsDescriptionKeyn());
        }
        if (StringUtils.isNotBlank(source.getGroupsIdKey())) {
            arrayList.add(source.getGroupsIdKey());
        }
        if (StringUtils.isNotBlank(source.getUserGroupMemberOfKey())) {
            arrayList.add(source.getUserGroupMemberOfKey());
        }
        if (StringUtils.isNotBlank(source.getGroupsMemberKey())) {
            arrayList.add(source.getGroupsMemberKey());
        }
        return arrayList.toArray(new String[]{});
    }

    public Collection<LdapEntry> search(String s, int type) throws LdapException {
        Connection conn = buildConnection();
        try {
            conn.open();
            SearchOperation search = new SearchOperation(conn);
            String searchDn = type == SecurityEntityType.GROUP ? source.getGroupsDn() : source.getUsersDn();
            if(StringUtils.isBlank(searchDn)){
                searchDn = source.getBaseDn();
            }
            SearchRequest searchRequest = new SearchRequest(
                    searchDn, s);
            searchRequest.setReturnAttributes(type == SecurityEntityType.GROUP ? buildGroupAttributes() : buildUserAttributes());
            searchRequest.setSearchScope(source.isSubtreeScope() ? SearchScope.SUBTREE : SearchScope.ONELEVEL);
            searchRequest.setSortBehavior(SortBehavior.SORTED);
            org.ldaptive.SearchResult result = search.execute(searchRequest).getResult();
            logger.debug("ldaptive connector searching on {} with filter {}. Result Count {}",
                    searchDn,
                    searchRequest.getSearchFilter(),
                    result.size());
            return result.getEntries();
        } finally {
            conn.close();
        }
    }

    public Collection<LdapEntry> search(String s, int type, List<String> addonsAttributes) throws LdapException {
        Connection conn = buildConnection();
        try {
            conn.open();
            SearchOperation search = new SearchOperation(conn);
            String searchDn = type == SecurityEntityType.GROUP ? source.getGroupsDn() : source.getUsersDn();
            if(StringUtils.isBlank(searchDn)){
                searchDn = source.getBaseDn();
            }
            SearchRequest searchRequest = new SearchRequest(
                    searchDn, s);

            List<String> attributes = new ArrayList<>();
            attributes.addAll(type == SecurityEntityType.GROUP ? (Arrays.asList(buildGroupAttributes())) :Arrays.asList(buildUserAttributes()));
            if(addonsAttributes != null)
                attributes.addAll(addonsAttributes);
            searchRequest.setReturnAttributes(attributes.toArray(new String[]{}));
            searchRequest.setSearchScope(source.isSubtreeScope() ? SearchScope.SUBTREE : SearchScope.ONELEVEL);
            searchRequest.setSortBehavior(SortBehavior.SORTED);
            org.ldaptive.SearchResult result = search.execute(searchRequest).getResult();
            logger.debug("ldaptive connector searching on {} with filter {}. Result Count {}",
                    searchDn,
                    searchRequest.getSearchFilter(),
                    result.size());
            return result.getEntries();
        } finally {
            conn.close();
        }
    }

    protected String extractCn(String fullEntry) {
        int index = fullEntry.indexOf(this.source.getBaseDn());
        if (index > 0) {
            return fullEntry.substring(0, index - 1);
        } else
            return "";
    }

    protected String getUserString(String uid)
    {
        if (source.isActiveDirectory()) {
            return source.getUsersPrefix() + uid + source.getUsersSuffix();
        } else {
            return source.getUsersIdKey() + "=" + uid + "," + source.getUsersDn();
        }
    }
}

