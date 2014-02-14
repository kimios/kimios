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
package org.kimios.kernel.reporting.impl;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.reporting.FactoryInstantiator;
import org.kimios.kernel.reporting.ReportImpl;
import org.kimios.kernel.reporting.report.Cell;
import org.kimios.kernel.reporting.report.Report;
import org.kimios.kernel.reporting.report.Row;

import java.util.List;
import java.util.Vector;

public class DocumentTransactionsReport extends ReportImpl
{
    private String order;

    private Boolean asc;

    public String getData() throws ConfigException, DataSourceException
    {
        if (order != null && order.length() == 0) {
            order = null;
        }
        try {
            String rq = "SELECT " + "e.dm_entity_name as DocumentName, "
                    + "t.transfer_mode as TransferMode, t.username as UserName, "
                    + "t.user_source as UserSource, t.last_activity_date as LastActivityDate, "
                    + "e.dm_entity_path as Position, t.id as TransactionUid "
                    + "FROM dm_entity e, document d, document_version v, data_transaction t "
                    + "WHERE d.id=v.document_id AND v.id=t.document_version_id AND d.id=e.dm_entity_id "
                    + "ORDER BY " + (order == null ? "LastActivityDate" : order) + " "
                    + (asc ? "ASC" : "DESC");

            SQLQuery sql = FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rq);
            sql.addScalar("DocumentName", StringType.INSTANCE);
            sql.addScalar("TransferMode", IntegerType.INSTANCE);
            sql.addScalar("UserName", StringType.INSTANCE);
            sql.addScalar("LastActivityDate", StringType.INSTANCE);
            sql.addScalar("Position", StringType.INSTANCE);
            sql.addScalar("TransactionUid", StringType.INSTANCE);

            List<Object[]> lReports = sql.list();
            Report report = new Report("DocumentTransactions");
            report.addColumn("TransactionUid");
            report.addColumn("Position");
            report.addColumn("DocumentName");
            report.addColumn("TransferMode");
            report.addColumn("UserName");
            report.addColumn("LastActivityDate");

            for (Object[] r : lReports) {
                Vector<Cell> cells = new Vector<Cell>();
                cells.add(new Cell("DocumentName", (String) r[0]));
                cells.add(new Cell("TransferMode", (String) ((Integer) r[1] == 1 ? "Upload"
                        : (Integer) r[1] == 2 ? "Download" : "Unknown?")));
                cells.add(new Cell("UserName", (String) r[2]));
                cells.add(new Cell("LastActivityDate", (String) r[3]));
                cells
                        .add(new Cell("Position", (String) ((String) r[4]).substring(0, ((String) r[4])
                                .lastIndexOf('/'))));
                cells.add(new Cell("TransactionUid", (Long) r[5]));
                report.addRow(new Row(cells));
            }

            return report.toXML();
        } catch (HibernateException he) {
            he.printStackTrace();
            throw he;
        }
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

