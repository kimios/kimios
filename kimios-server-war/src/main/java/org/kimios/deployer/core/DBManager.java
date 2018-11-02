/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
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
package org.kimios.deployer.core;

import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBManager
{
    private static org.slf4j.Logger log = LoggerFactory.getLogger(DBManager.class);

    private DBManager()
    {
    }

    public static void init(String host, String login, String dbName, String dbPassword, String _driver,
            String _jdbcUrl)
    {
        DB_Host = host;
        DB_User = login;
        DB_Password = dbPassword;
        DB_Name = dbName;
        driver = _driver;
        jdbcUrl = _jdbcUrl;
    }

    public static synchronized DBManager getInstance() throws Exception
    {
        if (dbm.isConnected()) {
            return dbm;
        } else {
            dbm.connect();
            return dbm;
        }
    }

    public boolean isConnected() throws Exception
    {
        if (conn != null) {
            try {
                return !conn.isClosed();
            } catch (SQLException e) {
                throw new Exception("NO COMMAND: " + e.getMessage());
            }
        } else {
            return false;
        }
    }

    public void connect() throws Exception
    {
        try {
            Class.forName(driver);
        } catch (Exception e) {
            log.error("Error while loading ", e);
        }
        String url = String.format(jdbcUrl, DB_Host);
        conn = DriverManager.getConnection(url, DB_User, DB_Password);
    }

    public void connectDb() throws Exception
    {
        try {
            Class.forName(driver);
        } catch (Exception e) {
            log.error("Error while loading ", e);
        }
        conn = DriverManager.getConnection(jdbcUrl, DB_User, DB_Password);
    }

    public void disconnect() throws Exception
    {
        if (dbm.isConnected()) {
            conn.close();
        }
    }

    public void execute(String query) throws DataBaseException
    {
        try {
            conn.createStatement().execute(query);
        } catch (SQLException e) {
            throw new DataBaseException(query, e.getMessage());
        }
    }

    public ResultSet executeQuery(String query) throws DataBaseException
    {
        try {
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new DataBaseException(query, e.getMessage());
        }
    }

    public PreparedStatement getPreparedStatement(String query) throws DataBaseException
    {
        try {
            return conn.prepareStatement(query);
        } catch (SQLException e) {
            throw new DataBaseException(query, e.getMessage());
        }
    }

    public Connection getConnection() throws DataBaseException
    {
        try {
            return conn;
        } catch (Exception e) {
            log.error("Error while loading ", e);
            throw new DataBaseException(e.getMessage(), "");
        }
    }

    private static String DB_User;

    private static String DB_Password;

    private static String DB_Name;

    private static String DB_Host;

    private static String jdbcUrl;

    private static String driver;

    private static Connection conn = null;

    private static DBManager dbm = new DBManager();
}

