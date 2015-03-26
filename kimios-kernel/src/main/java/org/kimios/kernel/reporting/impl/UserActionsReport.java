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
package org.kimios.kernel.reporting.impl;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.reporting.FactoryInstantiator;
import org.kimios.kernel.reporting.ReportImpl;
import org.kimios.kernel.reporting.report.Cell;
import org.kimios.kernel.reporting.report.Report;
import org.kimios.kernel.reporting.report.Row;
import org.kimios.kernel.user.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class UserActionsReport extends ReportImpl
{
    private Date dateFrom;

    private Date dateTo;

    private User user;

    private String actionType;

    private String order;

    private Boolean asc;

    public String getData() throws ConfigException, DataSourceException
    {
        if (order != null && order.length() == 0) {
            order = null;
        }
        if (actionType != null && order != null && order.length() == 0) {
            actionType = null;
        }
        String tmpTable = null;
        try {
            Report temporaryReport = new Report("UserActions");
            temporaryReport.addColumn("Position");
            temporaryReport.addColumn("ActionType");
            temporaryReport.addColumn("ActionTypeParameters");
            temporaryReport.addColumn("Date");

            Calendar dtFrom = Calendar.getInstance();
            dtFrom.setTime(dateFrom);
            Calendar dtTo = Calendar.getInstance();
            dtTo.setTime(dateTo);
            dtFrom.set(Calendar.SECOND, 0);
            dtFrom.set(Calendar.MINUTE, 0);
            dtFrom.set(Calendar.HOUR, 0);
            dtTo.set(Calendar.SECOND, 59);
            dtTo.set(Calendar.MINUTE, 59);
            dtTo.set(Calendar.HOUR, 23);

            /* Workspace log */

            String rqWorkspaceLog = "SELECT w.action as ActionType, ";
            rqWorkspaceLog += "w.action_parameter as ActionTypeParameters, ";
            rqWorkspaceLog += "w.dm_entity_id as EntityUid, ";
            rqWorkspaceLog += "w.log_time as Date, ";
            rqWorkspaceLog += "e.dm_entity_name as EntityName ";
            rqWorkspaceLog += "FROM authentication_source a, entity_log w ";
            rqWorkspaceLog += "LEFT JOIN dm_entity e ";
            rqWorkspaceLog += "ON w.dm_entity_id = e.dm_entity_id ";
            rqWorkspaceLog += "WHERE a.source_name=w.user_source ";
            rqWorkspaceLog += "AND w.username=:userName ";
            rqWorkspaceLog += "AND w.user_source=:userSource ";
            rqWorkspaceLog += "AND w.log_time >= :dateFrom ";
            rqWorkspaceLog += "AND w.log_time <= :dateTo";

            SQLQuery sqlWorkspaceLog =
                    FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rqWorkspaceLog);
            sqlWorkspaceLog.addScalar("ActionType", IntegerType.INSTANCE);
            sqlWorkspaceLog.addScalar("ActionTypeParameters", StringType.INSTANCE);
            sqlWorkspaceLog.addScalar("EntityUid", LongType.INSTANCE);
            sqlWorkspaceLog.addScalar("Date", StringType.INSTANCE);
            sqlWorkspaceLog.addScalar("EntityName", StringType.INSTANCE);
            sqlWorkspaceLog.setString("userName", user.getUid());
            sqlWorkspaceLog.setString("userSource", user.getAuthenticationSourceName());
            sqlWorkspaceLog.setDate("dateFrom", dtFrom.getTime());
            sqlWorkspaceLog.setDate("dateTo", dtTo.getTime());

            List<Object[]> reportWorkspaceLog = sqlWorkspaceLog.list();
            for (Object[] r : reportWorkspaceLog) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("ActionType", (Integer) r[0]));
                cells.add(new Cell("ActionTypeParameters", r[1] == null ? "" : (String) r[1]));
                cells.add(new Cell("Date", (String) r[3]));
                cells.add(new Cell("EntityName", r[4] == null ? "" : (String) r[4]));
                cells.add(new Cell("Position", new String("/")));
                temporaryReport.addRow(new Row(cells));
            }

            /* Folder log */

            String rqFolderLog = "SELECT f.action as ActionType, ";
            rqFolderLog += "f.action_parameter as ActionTypeParameters, ";
            rqFolderLog += "f.dm_entity_id as EntityUid, ";
            rqFolderLog += "f.log_time as Date, ";
            rqFolderLog += "e.dm_entity_name as EntityName, ";
            rqFolderLog += "entt.dm_entity_path as Position ";
            rqFolderLog += "FROM authentication_source a, entity_log f ";
            rqFolderLog += "LEFT JOIN dm_entity e ";
            rqFolderLog += "ON f.dm_entity_id = e.dm_entity_id ";
            rqFolderLog += "LEFT JOIN dm_entity entt ";
            rqFolderLog += "ON f.dm_entity_id = entt.dm_entity_id ";
            rqFolderLog += "WHERE a.source_name=f.user_source ";
            rqFolderLog += "AND f.username=:userName ";
            rqFolderLog += "AND f.user_source=:userSource ";
            rqFolderLog += "AND f.log_time >= :dateFrom ";
            rqFolderLog += "AND f.log_time <= :dateTo ";

            SQLQuery sqlFolderLog =
                    FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rqFolderLog);
            sqlFolderLog.addScalar("ActionType", IntegerType.INSTANCE);
            sqlFolderLog.addScalar("ActionTypeParameters", StringType.INSTANCE);
            sqlFolderLog.addScalar("EntityUid", LongType.INSTANCE);
            sqlFolderLog.addScalar("Date", StringType.INSTANCE);
            sqlFolderLog.addScalar("EntityName", StringType.INSTANCE);
            sqlFolderLog.addScalar("Position", StringType.INSTANCE);
            sqlFolderLog.setString("userName", user.getUid());
            sqlFolderLog.setString("userSource", user.getAuthenticationSourceName());
            sqlFolderLog.setDate("dateFrom", dtFrom.getTime());
            sqlFolderLog.setDate("dateTo", dtTo.getTime());

            List<Object[]> reportFolderLog = sqlFolderLog.list();
            for (Object[] r : reportFolderLog) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("ActionType", (Integer) r[0]));
                cells.add(new Cell("ActionTypeParameters", r[1] == null ? "" : (String) r[1]));
                cells.add(new Cell("Date", (String) r[3]));
                cells.add(new Cell("EntityName", r[4] == null ? "" : (String) r[4]));
                cells.add(new Cell("Position", r[5] == null ? "" : (String) r[5]));
                temporaryReport.addRow(new Row(cells));
            }

            /* Document log */

            String rqDocumentLog = "SELECT d.action as ActionType, ";
            rqDocumentLog += "d.action_parameter as ActionTypeParameters, ";
            rqDocumentLog += "d.dm_entity_id as EntityUid, ";
            rqDocumentLog += "d.log_time as Date, ";
            rqDocumentLog += "entt.dm_entity_name as EntityName, ";
            rqDocumentLog += "entt.dm_entity_path as Position ";
            rqDocumentLog += "FROM authentication_source a, entity_log d ";
            rqDocumentLog += "LEFT JOIN dm_entity entt ";
            rqDocumentLog += "ON d.dm_entity_id = entt.dm_entity_id ";
            rqDocumentLog += "WHERE a.source_name=d.user_source ";
            rqDocumentLog += "AND d.username=:userName ";
            rqDocumentLog += "AND d.user_source=:userSource ";
            rqDocumentLog += "AND d.log_time >= :dateFrom ";
            rqDocumentLog += "AND d.log_time <= :dateTo";

            SQLQuery sqlDocumentLog =
                    FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rqDocumentLog);
            sqlDocumentLog.addScalar("ActionType", IntegerType.INSTANCE);
            sqlDocumentLog.addScalar("ActionTypeParameters", StringType.INSTANCE);
            sqlDocumentLog.addScalar("EntityUid", LongType.INSTANCE);
            sqlDocumentLog.addScalar("Date", StringType.INSTANCE);
            sqlDocumentLog.addScalar("EntityName", StringType.INSTANCE);
            sqlDocumentLog.addScalar("Position", StringType.INSTANCE);
            sqlDocumentLog.setString("userName", user.getUid());
            sqlDocumentLog.setString("userSource", user.getAuthenticationSourceName());
            sqlDocumentLog.setDate("dateFrom", dtFrom.getTime());
            sqlDocumentLog.setDate("dateTo", dtTo.getTime());

            List<Object[]> reportDocumentLog = sqlDocumentLog.list();
            for (Object[] r : reportDocumentLog) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("ActionType", (Integer) r[0]));
                cells.add(new Cell("ActionTypeParameters", r[1] == null ? "" : (String) r[1]));
                cells.add(new Cell("Date", (String) r[3]));
                cells.add(new Cell("EntityName", r[4] == null ? "" : (String) r[4]));
                cells.add(new Cell("Position", r[5] == null ? "" : (String) r[5]));
                temporaryReport.addRow(new Row(cells));
            }

            /* Create temporary table */

            tmpTable = "tmp_" + sessionUid.substring(0, 8) + "_" + new Date().getTime();
            String rqCreateTable = "CREATE TABLE " + tmpTable + " ( ";
            rqCreateTable += "ReportActionType character varying(2), ";
            rqCreateTable += "ReportActionTypeParameters character varying(255), ";
            rqCreateTable += "ReportEntityName character varying(255), ";
            rqCreateTable += "ReportDate character varying(255), ";
            rqCreateTable += "ReportPosition character varying(255) )";

            SQLQuery sqlCreateTable =
                    FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rqCreateTable);
            sqlCreateTable.executeUpdate();

            for (Row row : temporaryReport.getBody().getRows()) {
                String action = String.valueOf(row.getValue("ActionType"));
                String parameters = String.valueOf(row.getValue("ActionTypeParameters"));
                String entityName = String.valueOf(row.getValue("EntityName"));
                String date = String.valueOf(row.getValue("Date"));
                String position = String.valueOf(row.getValue("Position"));
                String rqInsertTable = "INSERT INTO " + tmpTable + " ( ";
                rqInsertTable +=
                        "ReportActionType, ReportActionTypeParameters, ReportEntityName, ReportDate, ReportPosition ";
                rqInsertTable += " ) VALUES (:actionType,:parameters,:entityName,:date,:position)";
                SQLQuery sqlInsertTable = getSession().createSQLQuery(rqInsertTable);
                sqlInsertTable.setString("actionType", action);
                sqlInsertTable.setString("parameters", parameters);
                sqlInsertTable.setString("entityName", entityName);
                sqlInsertTable.setString("date", date);
                sqlInsertTable.setString("position", position);
                sqlInsertTable.executeUpdate();
            }

            /* Report */

            String rq =
                    "SELECT ReportActionType, ReportActionTypeParameters, ReportEntityName, ReportDate, ReportPosition ";
            rq += "FROM " + tmpTable + " ";
            rq += (actionType != null ? " WHERE ReportActionType=:actionType " : " ");
            rq += " ORDER BY " + (order == null ? "ReportDate" : order) + " " + (asc ? "ASC" : "DESC");

            SQLQuery sql = FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rq);
            sql.addScalar("ReportActionType", StringType.INSTANCE);
            sql.addScalar("ReportActionTypeParameters", StringType.INSTANCE);
            sql.addScalar("ReportEntityName", StringType.INSTANCE);
            sql.addScalar("ReportDate", StringType.INSTANCE);
            sql.addScalar("ReportPosition", StringType.INSTANCE);
            if (actionType != null) {
                sql.setString("actionType", actionType);
            }

            Report report = new Report("UserActions");
            report.addColumn("Position");
            report.addColumn("EntityName");
            report.addColumn("ActionType");
            report.addColumn("ActionTypeParameters");
            report.addColumn("Date");

            List<Object[]> reports = sql.list();
            for (Object[] r : reports) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("ActionType", (String) r[0]));
                cells.add(new Cell("ActionTypeParameters", (String) r[1]));
                cells.add(new Cell("EntityName", (String) r[2]));
                cells.add(new Cell("Date", (String) r[3]));

                String pos = ((String) r[4]);
                int index = pos.lastIndexOf('/');
                if (index != -1 && !"/".equals(pos)) {
                    pos = pos.substring(0, index);
                }
                cells.add(new Cell("Position", pos));
                report.addRow(new Row(cells));
            }
            return report.toXML();
        } catch (HibernateException he) {
            he.printStackTrace();
            throw he;
        } finally {
            /* Drop temporary table */
            FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery("DROP TABLE " + tmpTable)
                    .executeUpdate();
        }
    }

    public Long getDateFrom()
    {
        return dateFrom.getTime();
    }

    public Long getDateTo()
    {
        return dateTo.getTime();
    }

    public User getUser()
    {
        return user;
    }

    public String getActionType()
    {
        return actionType;
    }

    public String getOrder()
    {
        return order;
    }

    public Boolean getAsc()
    {
        return asc;
    }
}

