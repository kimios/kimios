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
package org.kimios.kernel.user.impl.factory.joomla;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.user.UserFactory;
import org.kimios.kernel.user.impl.AuthenticationSourceJoomla15;

public class Joomla15UserFactory implements UserFactory
{
    private AuthenticationSourceJoomla15 source;

    private Joomla15DBManagerMysql dbm;

    public Joomla15UserFactory(Joomla15DBManagerMysql _dbm, AuthenticationSourceJoomla15 _source)
    {
        this.dbm = _dbm;
        this.source = _source;
    }

    public void addUserToGroup(User user, Group group) throws DataSourceException, ConfigException
    {
    }

    public boolean authenticate(String uid, String password) throws DataSourceException, ConfigException
    {
        try {
            String query = "SELECT password as pwd FROM " + source.getTablePrefix()
                    + "users where username= ? and block=0";
            String pwd = null;
            PreparedStatement ps = dbm.getPreparedStatement(query);
            ps.setString(1, uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pwd = rs.getString("pwd");
            }
            if (pwd != null) {
                return Joomla15PasswordHash.check(password, pwd);
            } else {
                return false;
            }
        } catch (SQLException he) {
            throw new DataSourceException(he, he.getMessage());
        }
    }

    public void deleteUser(User user) throws DataSourceException, ConfigException
    {

    }

    public User getUser(String uid) throws DataSourceException, ConfigException
    {
        try {
            String query = "SELECT name as nom,email as mail,username as uname FROM " + source.getTablePrefix()
                    + "users where username= ?";

            PreparedStatement ps = dbm.getPreparedStatement(query);
            ps.setString(1, uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // tx.commit();
                User user = new User();
                user.setUid(uid);
                user.setName(rs.getString("nom"));
                user.setMail(rs.getString("mail"));
                user.setAuthenticationSourceName(source.getName());
                return user;
            } else {
                return null;
            }
        } catch (SQLException he) {
            throw new DataSourceException(he, he.getMessage());
        }
    }

    public Vector<User> getUsers(Group group) throws DataSourceException, ConfigException
    {
        try {
            String query = "SELECT username as uname,name as nom,email as mail FROM " + source.getTablePrefix()
                    + "users where gid=? and block=0";
            PreparedStatement ps = dbm.getPreparedStatement(query);
            ps.setInt(1, Integer.parseInt(group.getGid()));
            ResultSet rs = ps.executeQuery();

            Vector<User> vUsers = new Vector<User>();
            while (rs.next()) {
                User user = new User();
                user.setUid(rs.getString("uname"));
                user.setName(rs.getString("nom"));
                user.setMail(rs.getString("mail"));
                user.setAuthenticationSourceName(source.getName());
                vUsers.add(user);
            }
            return vUsers;
        } catch (SQLException sq) {
            throw new DataSourceException(sq, sq.getMessage());
        }
    }

    public Vector<User> getUsers() throws DataSourceException, ConfigException
    {
        try {
            String query = "SELECT name as nom,email as mail,username as uname FROM " + source.getTablePrefix()
                    + "users where block=0";
            PreparedStatement ps = dbm.getPreparedStatement(query);
            ResultSet rs = ps.executeQuery();
            Vector<User> vUsers = new Vector<User>();
            while (rs.next()) {
                User user = new User();
                user.setUid(rs.getString("uname"));
                user.setName(rs.getString("nom"));
                user.setMail(rs.getString("mail"));
                user.setAuthenticationSourceName(source.getName());
                vUsers.add(user);
            }
            return vUsers;
        } catch (SQLException he) {
            throw new DataSourceException(he, he.getMessage());
        }
    }

    public void removeUserFromGroup(User user, Group group) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public void saveUser(User user, String password) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
    }

    public void updateUser(User user, String password) throws DataSourceException, ConfigException
    {
        throw new ConfigException("Not Implemented Yet");
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

