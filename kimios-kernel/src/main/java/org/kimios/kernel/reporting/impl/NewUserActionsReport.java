/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.log.ActionType;
import org.kimios.kernel.reporting.factory.FactoryInstantiator;
import org.kimios.api.reporting.ReportImpl;
import org.kimios.kernel.reporting.model.Cell;
import org.kimios.kernel.reporting.model.Report;
import org.kimios.kernel.reporting.model.Row;
import org.kimios.kernel.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
;

public class NewUserActionsReport extends ReportImpl
{
    private Date dateFrom;

    private Date dateTo;

    private List<User> users;

    private List<String> actionType;

    private String order;

    private Boolean asc;





    public String getData() throws ConfigException, DataSourceException
    {
        Logger logger =  LoggerFactory.getLogger(NewUserActionsReport.class);

        if (order != null && order.length() == 0) {
            order = null;
        }
        if (actionType != null && order != null && order.length() == 0) {
            actionType = null;
        }

        //build map action type

        Map<Integer, String> actionTypes = new HashMap<Integer, String>();
        for(Field f: ActionType.class.getFields()){
            if(Modifier.isStatic(f.getModifiers())){
                try {
                    actionTypes.put(f.getInt(null), f.getName());
                }catch (Exception ex){

                }
            }
        }

        List<Integer> actionTypeInt = new ArrayList<Integer>();
        try{
            if(actionType != null && actionType.size() > 0){
                for(String ac: actionType){
                    Field actionTypeField = ActionType.class.getField(ac);
                    actionTypeInt.add(actionTypeField.getInt(null));
                }

            }
        } catch (Exception ex){
            throw new ConfigException();
        }

        if(users == null){
            users = new ArrayList<User>();
        }

        try {

            Calendar dtTo = Calendar.getInstance();;
            Calendar dtFrom = Calendar.getInstance();;

            if(dateFrom == null){
                Calendar calFrom = Calendar.getInstance();
                calFrom.add(Calendar.DATE, -7);
                dtFrom.setTime(calFrom.getTime());
            }  else {
                dtFrom.setTime(dateFrom);
            }
            if(dateTo == null){
                Calendar calTo = Calendar.getInstance();
                calTo.add(Calendar.DATE, 1);
                dtTo.setTime(calTo.getTime());

            } else {
                dtTo.setTime(dateTo);
                dtTo.add(Calendar.DATE, 1);
            }

            dtFrom.set(Calendar.SECOND, 0);
            dtFrom.set(Calendar.MINUTE, 0);
            dtFrom.set(Calendar.HOUR, 0);


            dtTo.set(Calendar.SECOND, 59);
            dtTo.set(Calendar.MINUTE, 59);
            dtTo.set(Calendar.HOUR, 23);


            logger.info("generating report from {} to {}", dtFrom.getTime(), dtTo.getTime());




            /* Workspace log */

            String rqEntityLog = "SELECT w.action as ActionType, ";
            rqEntityLog += "w.action_parameter as ActionTypeParameters, ";
            rqEntityLog += "w.dm_entity_id as EntityUid, ";
            rqEntityLog += "w.log_time as Date, ";
            rqEntityLog += "e.dm_entity_name as EntityName, ";
            rqEntityLog += "e.dm_entity_type as EntityType, ";
            rqEntityLog += "e.dm_entity_path as EntityPath, ";
            rqEntityLog += "w.action_data as ActionData, ";
            rqEntityLog += "w.username as UserId, ";
            rqEntityLog += "w.user_source as UserSource ";
            rqEntityLog += "FROM entity_log w ";
            rqEntityLog += "LEFT JOIN dm_entity e ";
            rqEntityLog += "ON w.dm_entity_id = e.dm_entity_id ";
            rqEntityLog += "WHERE w.log_time >= :dateFrom ";
            rqEntityLog += "AND w.log_time <= :dateTo ";
            if(users != null && users.size() > 0){
                rqEntityLog += " AND (";
                int uidx = 0;
                for(User u: users){

                    rqEntityLog += "(w.username=:userName" + uidx + "  AND w.user_source=:userSource" + uidx  + ") " + (users.indexOf(u) < (users.size() - 1 ) ? " OR " : "");
                    uidx++;
                }
                rqEntityLog +=") ";
            }
            if(actionTypeInt != null && actionTypeInt.size() > 0){
               rqEntityLog += " AND w.action in (:actionTypeList)";
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
            sqlEntityLog.addScalar("UserId", StringType.INSTANCE);
            sqlEntityLog.addScalar("UserSource", StringType.INSTANCE);
            if(users != null && users.size() > 0) {
                int uidx = 0;
                for (User u : users) {
                    sqlEntityLog.setString("userName" + uidx, u.getUid());
                    sqlEntityLog.setString("userSource" + uidx, u.getAuthenticationSourceName());
                    uidx++;
                }
            }

            sqlEntityLog.setDate("dateFrom", dtFrom.getTime());
            sqlEntityLog.setDate("dateTo", dtTo.getTime());

            if(actionTypeInt != null && actionTypeInt.size() > 0){
                sqlEntityLog.setParameterList("actionTypeList", actionTypeInt);
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
            report.addColumn("UserId");
            report.addColumn("UserSource");

            logger.info("report query {}", sqlEntityLog.getQueryString());

            List<Object[]> reportEntityLog = sqlEntityLog.list();
            for (Object[] r : reportEntityLog) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("ActionType", actionTypes.get((Integer) r[0])));
                cells.add(new Cell("ActionTypeParameters", r[1] == null ? "N/A" : (String) r[1]));
                cells.add(new Cell("ActionData", r[7] == null ? "N/A" : (String) r[7]));
                cells.add(new Cell("Date", (String) r[3]));
                cells.add(new Cell("EntityName", r[4] == null ? "N/A" : (String) r[4]));
                cells.add(new Cell("EntityType", ((Integer)r[5]) != null ? (Integer)r[5] : "N/A" ));
                cells.add(new Cell("EntityUid", (Long)r[2]));
                cells.add(new Cell("Position", r[6] == null ? "N/A" : r[6]));
                cells.add(new Cell("UserId", r[8] == null ? "N/A" : r[8]));
                cells.add(new Cell("UserSource", r[9] == null ? "N/A" : r[9]));
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

    public List<User> getUsers()
    {
        return users;
    }

    public List<String> getActionType()
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

