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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlScript
{
    public static String BLANK = " ";

    public SqlScript(String scriptFileName, String databaseType) throws SQLException, DataBaseException, Exception
    {
        con = null;
        script = InstallerCore.getInputStream(scriptFileName);
        this.databaseType = databaseType;

        try {
            DBManager dbm = DBManager.getInstance();
            if (dbm.isConnected()) {
                con = dbm.getConnection();
                stat = con.createStatement();
            } else {
                throw new SQLException("Not Connected");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadScript() throws IOException, SQLException
    {
        try {
            InputStreamReader instrR = new InputStreamReader(script, "UTF-8");
            BufferedReader reader = new BufferedReader(instrR);
            StringBuffer query = new StringBuffer();
            boolean queryEnds = false;
            boolean functionStatment = false;
            boolean functionEnded = true;
            this.installerProgress = InstallerProgress.getInstance();
            this.installerProgress.init(script.available());
            this.installerProgress.setStatus("Creating database...", null);

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = new String(line.getBytes("UTF-8"), Charset.forName("UTF-8")) + " ";
                this.installerProgress.addProgression(line.length());
                if (!isComment(line)) {
                    if (!functionStatment) {
                        functionStatment = line.contains("CREATE") && line.contains("FUNCTION");
                        if (functionStatment) {
                            try {
                                System.out.println("Creating procedural language plpgsql ...");
                                stat.execute("CREATE PROCEDURAL LANGUAGE plpgsql;");
                            } catch (SQLException e) {
                                System.out.println("Plpgsql language already exists!");
                            }
                            if (databaseType.equalsIgnoreCase("postgresql")) {
                                line = (new StringBuilder()).append(line).append("$BODY$").toString();
                            }
                        }
                    }
                    functionEnded = functionStatment && line.trim().equals("END;");
                    queryEnds = checkStatementEnds(line) && (!functionStatment || functionEnded);
                    query.append(
                            (new StringBuilder()).append(new String(line.getBytes("UTF-8"), Charset.forName("UTF-8")))
                                    .append(
                                            !functionStatment || functionEnded ? "" : "\n").append(
                                    !functionStatment || !functionEnded ||
                                            !databaseType.equalsIgnoreCase("postgresql") ? "" :
                                            "$BODY$ LANGUAGE 'plpgsql';").toString());
                    if (queryEnds) {
                        System.out.println((new StringBuilder()).append("query->").append(query).toString());
                        stat.execute(new String(query.toString().getBytes("UTF-8"), Charset.forName("UTF-8")));
                        query.setLength(0);
                        if (functionStatment) {
                            functionStatment = false;
                            functionEnded = true;
                        }
                    }
                }
            }

            this.installerProgress.setCompleted();
        } catch (SQLException sql) {
            SQLException t = sql.getNextException();
            SQLException toThrow = null;
            for (; t != null; t = t.getNextException()) {
                t.printStackTrace();
                toThrow = t;
            }

            throw toThrow == null ? t == null ? sql : t : toThrow;
        }
    }

    private boolean isComment(String line)
    {
        if (line != null && line.length() > 0) {
            return line.trim().startsWith("--");
        } else {
            return false;
        }
    }

    public void execute() throws IOException, SQLException
    {
        try {
            for (ResultSet rs = con.prepareStatement("SHOW client_encoding").executeQuery(); rs.next();
                    System.err.println(rs.getString(1)))
            {
                ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stat.executeBatch();
        } catch (SQLException sql) {
            SQLException t = sql.getNextException();
            SQLException toThrow = null;
            for (; t != null; t = t.getNextException()) {
                t.printStackTrace();
                toThrow = t;
            }
            throw toThrow == null ? t == null ? sql : t : toThrow;
        }
        try {
            for (ResultSet rs = con.prepareStatement("SHOW client_encoding").executeQuery(); rs.next();
                    System.err.println(rs.getString(1)))
            {
                ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkStatementEnds(String s)
    {
        return s.indexOf(';') != -1;
    }

    public static final char QUERY_ENDS = 59;

    private InputStream script;

    private Connection con;

    private Statement stat;

    private String databaseType;

    private InstallerProgress installerProgress;
}

