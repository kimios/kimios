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

package org.kimios.deployer.migration;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ScriptExecutor {
    //    private static final String DB_URL_TPL = "jdbc:postgresql://%s:%s/%s";
    private static final String DB_URL_TPL = "jdbc:postgresql://%s:%s/%s";
    private static final String DB_HOST = "localhost";      // server hostname
    private static final Integer DB_PORT = 5432;
    private static final String DB_NAME = "quotero_mig2";    // db to migrate
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "postgres";

    private InputStream input;
    private Connection connection;
    private Statement statement;


    public ScriptExecutor(InputStream input) throws SQLException, ClassNotFoundException {
        this.input = input;
//        String url = String.format(DB_URL_TPL, DB_HOST, DB_NAME);
        String url = String.format(DB_URL_TPL, DB_HOST, DB_PORT, DB_NAME);
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(url, DB_USERNAME, DB_PASSWORD);
        statement = connection.createStatement();
    }

    public void execute() throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
//        StringBuffer query = new StringBuffer();

        String line = null;
        while ((line = reader.readLine()) != null) {
//            line = new String(line.getBytes("UTF-8"), Charset.forName("UTF-8")) + " ";

            // skip blank line
            if (line == null || line.length() == 0 || line.isEmpty()) {
                continue;
            }
            // skip commented line
            if (line.trim().startsWith("--")) {
                continue;
            }

            System.out.println("sql >> '" + line + "'");
//            statement.execute(new String(line.getBytes("UTF-8"), Charset.forName("UTF-8")));
            boolean executed = statement.execute(line);
            System.out.println(line + " => " + executed);


        }
    }
}
