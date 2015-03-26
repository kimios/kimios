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
package org.kimios.kernel.user.impl.factory.joomla;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.GroupFactory;
import org.kimios.kernel.user.impl.AuthenticationSourceJoomla15;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

//NB : Joomla 1.5 : database field for id group = id 
//     Joomla 1.0.1.3 database field for id group = group_id

public class Joomla15GroupFactory implements GroupFactory
{
    private AuthenticationSourceJoomla15 source;

    private Joomla15DBManagerMysql dbm;

    public Joomla15GroupFactory(Joomla15DBManagerMysql _dbm, AuthenticationSourceJoomla15 _source)
    {
        this.dbm = _dbm;
        this.source = _source;
    }

    public void deleteGroup(Group group) throws DataSourceException, ConfigException
    {

    }

    public Group getGroup(String gid) throws DataSourceException, ConfigException
    {
        try {
            String query = "SELECT id as gid,name as nom FROM " + source.getTablePrefix()
                    + "core_acl_aro_groups where id=?";
            PreparedStatement ps = dbm.getPreparedStatement(query);
            ps.setInt(1, Integer.parseInt(gid));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Group group = new Group();
                group.setGid(gid);
                group.setName(rs.getString("nom"));
                group.setAuthenticationSourceName(source.getName());
                return group;
            } else {
                return null;
            }
        } catch (SQLException sq) {
            throw new DataSourceException(sq);
        }
    }

    public Vector<Group> getGroups() throws DataSourceException, ConfigException
    {
        try {
            // NB : Joomla 1.5 : database field for id group = id
            // Joomla 1.0.1.3 database field for id group = group_id
            String query = "SELECT id as gid,name as nom FROM " + source.getTablePrefix()
                    + "core_acl_aro_groups";

            Vector<Group> vGroups = new Vector<Group>();
            PreparedStatement ps = dbm.getPreparedStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Group group = new Group();
                group.setGid(rs.getString("gid"));
                group.setName(rs.getString("nom"));
                group.setAuthenticationSourceName(source.getName());
                vGroups.add(group);
            }
            return vGroups;
        } catch (SQLException he) {
            throw new DataSourceException(he);
        }
    }

    public Vector<Group> getGroups(String userUid) throws DataSourceException, ConfigException
    {
        try {
            String tablePrefix = source.getTablePrefix();
            String query = "SELECT " + tablePrefix + "core_acl_aro_groups.id as gid," + tablePrefix
                    + "core_acl_aro_groups.name as nom FROM " + tablePrefix + "core_acl_aro_groups, " + tablePrefix
                    + "users where " + tablePrefix + "core_acl_aro_groups.id = " + tablePrefix + "users.gid " + "AND "
                    + tablePrefix + "users.username=?";
            // Session session = s.openSession();
            // tx = session.beginTransaction();
            // List<Object []> lGroups = session.createSQLQuery(query)
            // .addScalar("gid", Hibernate.INTEGER)
            // .addScalar("nom",Hibernate.STRING)
            // .setString("uname", userUid)
            // .list();
            //
            // for(Object[] u: lGroups){
            // Group grp = new Group();
            // grp.setGid(((Integer)u[0]).toString());
            // grp.setName((String)u[1]);
            // grp.setAuthenticationSourceName(this.authenticationSourceName);
            //
            // vGroups.add(grp);
            // }
            //
            Vector<Group> vGroups = new Vector<Group>();
            PreparedStatement ps = dbm.getPreparedStatement(query);
            ps.setString(1, userUid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Group group = new Group();
                group.setGid(rs.getString("gid"));
                group.setName(rs.getString("nom"));
                group.setAuthenticationSourceName(source.getName());
                vGroups.add(group);
            }
            return vGroups;
        } catch (SQLException he) {
            throw new DataSourceException(he);
        }
    }

    public void saveGroup(Group group) throws DataSourceException, ConfigException
    {

    }

    public void updateGroup(Group group) throws DataSourceException, ConfigException
    {

    }
}

