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
import org.kimios.kernel.reporting.model.Cell;
import org.kimios.kernel.reporting.model.Report;
import org.kimios.kernel.reporting.model.Row;
import org.kimios.kernel.user.model.User;

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
        try {


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

            String rqEntityLog = "SELECT w.action as ActionType, ";
            rqEntityLog += "w.action_parameter as ActionTypeParameters, ";
            rqEntityLog += "w.dm_entity_id as EntityUid, ";
            rqEntityLog += "w.log_time as Date, ";
            rqEntityLog += "e.dm_entity_name as EntityName, ";
            rqEntityLog += "e.dm_entity_type as EntityType, ";
            rqEntityLog += "e.dm_entity_path as EntityPath, ";
            rqEntityLog += "w.action_data as ActionData ";
            rqEntityLog += "FROM authentication_source a, entity_log w ";
            rqEntityLog += "LEFT JOIN dm_entity e ";
            rqEntityLog += "ON w.dm_entity_id = e.dm_entity_id ";
            rqEntityLog += "WHERE a.source_name=w.user_source ";
            rqEntityLog += "AND w.username=:userName ";
            rqEntityLog += "AND w.user_source=:userSource ";
            rqEntityLog += "AND w.log_time >= :dateFrom ";
            rqEntityLog += "AND w.log_time <= :dateTo";
            if(actionType != null){
               rqEntityLog += " AND w.action = :actionType";
            }
            rqEntityLog += " ORDER BY " + (order == null ? "w.log_time" : order) + " " + (asc ? "ASC" : "DESC");

            SQLQuery sqlEntityLog =
                    FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rqEntityLog);
            sqlEntityLog.addScalar("ActionType", IntegerType.INSTANCE);
            sqlEntityLog.addScalar("ActionTypeParameters", StringType.INSTANCE);
            sqlEntityLog.addScalar("EntityUid", LongType.INSTANCE);
            sqlEntityLog.addScalar("Date", StringType.INSTANCE);
            sqlEntityLog.addScalar("EntityName", StringType.INSTANCE);
            sqlEntityLog.addScalar("EntityType", IntegerType.INSTANCE);
            sqlEntityLog.addScalar("EntityPath", StringType.INSTANCE);
            sqlEntityLog.addScalar("ActionData", StringType.INSTANCE);
            sqlEntityLog.setString("userName", user.getUid());
            sqlEntityLog.setString("userSource", user.getAuthenticationSourceName());
            sqlEntityLog.setDate("dateFrom", dtFrom.getTime());
            sqlEntityLog.setDate("dateTo", dtTo.getTime());

            if(actionType != null){
                sqlEntityLog.setInteger("actionType", Integer.parseInt(actionType));
            }

            Report report = new Report("UserActions");
            report.addColumn("Position");
            report.addColumn("EntityName");
            report.addColumn("EntityType");
            report.addColumn("EntityUid");
            report.addColumn("ActionType");
            report.addColumn("ActionTypeParameters");
            report.addColumn("ActionData");
            report.addColumn("Date");

            List<Object[]> reportEntityLog = sqlEntityLog.list();
            for (Object[] r : reportEntityLog) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("ActionType", (Integer) r[0]));
                cells.add(new Cell("ActionTypeParameters", r[1] == null ? "N/A" : (String) r[1]));
                cells.add(new Cell("ActionData", r[7] == null ? "N/A" : (String) r[7]));
                cells.add(new Cell("Date", (String) r[3]));
                cells.add(new Cell("EntityName", r[4] == null ? "N/A" : (String) r[4]));
                cells.add(new Cell("EntityType", ((Integer)r[5]) != null ? (Integer)r[5] : "N/A" ));
                cells.add(new Cell("EntityUid", (Long)r[2]));
                cells.add(new Cell("Position", r[6] == null ? "N/A" : r[6]));
                report.addRow(new Row(cells));
            }

            return report.toXML();
        } catch (HibernateException he) {
            he.printStackTrace();
            throw he;
        } finally {

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

