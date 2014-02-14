/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Joomla15DBManagerMysql
{
    private static String DB_Url;

    private static String DB_User;

    private static String DB_Password;

    private static String driverName;

    private static String driver;

    private Connection conn = null;

    /**
     * Data that contains the current instance
     */
    private static Joomla15DBManagerMysql dbm = new Joomla15DBManagerMysql();

    /**
     * The constructor is private to avoid external instanciation
     */
    private Joomla15DBManagerMysql()
    {

    }

    public static void init(String _url, String _login, String _password) throws ConfigException, DataSourceException
    {
        DB_Url = _url;
        DB_User = _login;
        DB_Password = _password;
        driverName = "mysql";
        driver = "com.mysql.jdbc.Driver";
    }

    /**
     * Returns the current DBManager instance
     */
    public static synchronized Joomla15DBManagerMysql getInstance() throws ConfigException, DataSourceException
    {
        if (dbm.isConnected()) {
            return dbm;
        } else {
            dbm.connect();
            return dbm;
        }
    }

    /**
     * Checks whether the DBManager is connected to the database
     */
    public boolean isConnected() throws DataSourceException
    {
        if (conn != null) {
            try {
                return !conn.isClosed();
            } catch (SQLException e) {
                throw new DataSourceException(e, e.getMessage());
            }
        } else {
            return false;
        }
    }

    /**
     * Connects to the database
     */
    public void connect() throws ConfigException, DataSourceException
    {
        try {
            Class.forName(driver);
            this.conn = DriverManager.getConnection(DB_Url, DB_User, DB_Password);
        } catch (SQLException e) {
            throw new DataSourceException(e, e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    /**
     * Disconnect from the database
     */
    public void disconnect() throws DataSourceException
    {
        if (dbm.isConnected()) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DataSourceException(e, e.getMessage());
            }
        }
    }

    /**
     * Executes an SQL query without any return
     */
    public void execute(String query) throws DataSourceException
    {
        try {
            conn.createStatement().execute(query);
        } catch (SQLException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    /**
     * Executes an SQL query and returns a ResultSet from the database
     */
    public ResultSet executeQuery(String query) throws DataSourceException
    {
        try {
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public java.sql.PreparedStatement getPreparedStatement(String query) throws DataSourceException
    {
        try {
            return conn.prepareStatement(query);
        } catch (SQLException e) {
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

