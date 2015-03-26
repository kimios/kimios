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
import org.hibernate.type.StringType;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.log.ActionType;
import org.kimios.kernel.reporting.FactoryInstantiator;
import org.kimios.kernel.reporting.ReportImpl;
import org.kimios.kernel.reporting.report.Cell;
import org.kimios.kernel.reporting.report.Report;
import org.kimios.kernel.reporting.report.Row;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class DocumentHitsReport extends ReportImpl
{
    private Date dateFrom;

    private Date dateTo;

    private String order;

    private Boolean asc;

    public String getData() throws ConfigException, DataSourceException
    {
        if (order != null && order.length() == 0) {
            order = null;
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

            String rq = "SELECT e.dm_entity_name as DocumentName, count(*) as HitsCount, e.dm_entity_path as Position "
                    + "FROM document d, entity_log dl, dm_entity e "
                    + "WHERE d.id=dl.dm_entity_id  "
                    + "AND d.id=e.dm_entity_id "
                    + "AND dl.dm_entity_type=3  "
                    + "AND dl.action IN ( "
                    + ActionType.READ
                    + ","
                    + ActionType.UPDATE
                    + ","
                    + ActionType.CREATE
                    + ") "
                    + "AND dl.log_time >= :dateFrom "
                    + "AND dl.log_time <= :dateTo "
                    + "GROUP BY e.dm_entity_name,e.dm_entity_path,e.dm_entity_id  "
                    + "ORDER BY "
                    + (order == null ? "HitsCount"
                    : order)
                    + " "
                    + (asc ? "ASC" : "DESC");

            SQLQuery sql = FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rq);
            sql.addScalar("DocumentName", StringType.INSTANCE);
            sql.addScalar("HitsCount", IntegerType.INSTANCE);
            sql.addScalar("Position", StringType.INSTANCE);
            sql.setDate("dateFrom", dtFrom.getTime());
            sql.setDate("dateTo", dtTo.getTime());

            List<Object[]> lReports = sql.list();
            Report report = new Report("DocumentHits");
            report.addColumn("Position");
            report.addColumn("DocumentName");
            report.addColumn("HitsCount");

            for (Object[] r : lReports) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("Position", (String) ((String) r[2])
                        .substring(0, ((String) r[2]).lastIndexOf('/'))));
                cells.add(new Cell("DocumentName", (String) r[0]));
                cells.add(new Cell("HitsCount", (Integer) r[1]));

                report.addRow(new Row(cells));
            }

            return report.toXML();
        } catch (HibernateException he) {
            he.printStackTrace();
            throw he;
        }
    }

    public Date getDateFrom()
    {
        return dateFrom;
    }

    public Date getDateTo()
    {
        return dateTo;
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

