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

public class DatabaseCreator
{
    private InstallerProgress installerProgress;

    public DatabaseCreator()
    {
    }

    public String getDatatabaseType()
    {
        return datatabaseType;
    }

    public void setDatatabaseType(String datatabaseType)
    {
        this.datatabaseType = datatabaseType;
    }

    public String datatabaseType;

    public void createDatabase(String databaseHost, String databaseName, String login, String password,
            String dbType, String jdbcUrl, String dbDriverClass, String dbCreateDbScript, String scriptPath) throws Exception
    {
        datatabaseType = dbType;
        DBManager dbm = null;
        try {
            DBManager.getInstance().disconnect();
        } catch (Exception e) {
            this.installerProgress.setStatus(e.getMessage(), null);
        }

        //String host, String login, String dbName, String dbPassword, String _driver, String _jdbcUrl
        DBManager.init(databaseHost, login, databaseName, password, dbDriverClass, jdbcUrl);
        dbm = DBManager.getInstance();

        if (!dbType.equals("sqlserver")) {
            System.err.println(
                    (new StringBuilder()).append("DB Manger Connection State :").append(dbm.isConnected()).toString());
            String req = String.format(dbCreateDbScript, databaseName);
            dbm.execute(req);
            dbm.disconnect();
            dbm.connectDb();
        }
        SqlScript script = new SqlScript(scriptPath, datatabaseType);
        script.loadScript();
    }
}

