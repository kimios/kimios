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

import org.hibernate.SQLQuery;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.reporting.FactoryInstantiator;
import org.kimios.kernel.reporting.ReportImpl;
import org.kimios.kernel.reporting.report.Cell;
import org.kimios.kernel.reporting.report.Report;
import org.kimios.kernel.reporting.report.Row;

import java.util.List;
import java.util.Vector;

public class EntityInformationsReport extends ReportImpl
{
    private DMEntityImpl dmEntity;

    public String getData() throws ConfigException, DataSourceException
    {
        Vector<Cell> cells = new Vector<Cell>();
        Report report = new Report("EntityInformations");
        report.addColumn("Position");
        report.addColumn("AllVersionsVolume");
        report.addColumn("CurrentVersionsVolume");
        report.addColumn("EntitiesNumber");

        /* get informations about entity */
        String rqInformationsAboutEntity = "SELECT e.dm_entity_path as Position, e.dm_entity_type as EntityType ";
        rqInformationsAboutEntity += "FROM dm_entity e ";
        rqInformationsAboutEntity += "WHERE e.dm_entity_id=:dmEntityUid ";
        SQLQuery sqlInformationsAboutEntity = FactoryInstantiator.getInstance().getDtrFactory().getSession()
                .createSQLQuery(rqInformationsAboutEntity);
        sqlInformationsAboutEntity.addScalar("Position", StringType.INSTANCE);
        sqlInformationsAboutEntity.addScalar("EntityType", IntegerType.INSTANCE);
        sqlInformationsAboutEntity.setLong("dmEntityUid", dmEntity.getUid());

        List<Object[]> reports = sqlInformationsAboutEntity.list();
        for (Object[] r : reports) {
            cells.add(new Cell("Position", (String) r[0]));
        }

        /* get all versions volume */
        String rqAllVersionsVolume = "SELECT SUM(v.version_length) as AllVersionsVolume "
                + "FROM dm_entity e, document_version v " + "WHERE e.dm_entity_id=v.document_id "
                + "AND e.dm_entity_path LIKE :dmEntityPath";
        SQLQuery sqlAllVersionsVolume =
                FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rqAllVersionsVolume);
        sqlAllVersionsVolume.addScalar("AllVersionsVolume", LongType.INSTANCE);
        sqlAllVersionsVolume.setString("dmEntityPath", dmEntity.getPath() + "/%");
        Object allVersionsVolume = sqlAllVersionsVolume.list().get(0);
        if (allVersionsVolume == null) {
            allVersionsVolume = new Long(0);
        }
        cells.add(new Cell("AllVersionsVolume", allVersionsVolume));

        /* get current versions volume */

        String rqCurrentVersionsVolume = "SELECT SUM(v.version_length) as AllVersionsVolume "
                + "FROM document_version v, dm_entity e " + "WHERE v.document_id=e.dm_entity_id "
                + "AND e.dm_entity_path LIKE :dmEntityPath " + "AND v.creation_date IN ( "
                + "SELECT MAX(creation_date) as creationDate " + "FROM document_version v "
                + "GROUP BY document_id " + ")";
        SQLQuery sqlCurrentVersionsVolume =
                FactoryInstantiator.getInstance().getDtrFactory().getSession().createSQLQuery(rqCurrentVersionsVolume);
        sqlCurrentVersionsVolume.addScalar("AllVersionsVolume", LongType.INSTANCE);
        sqlCurrentVersionsVolume.setString("dmEntityPath", dmEntity.getPath() + "/%");
        Object currentVersionsVolume = sqlCurrentVersionsVolume.list().get(0);
        if (currentVersionsVolume == null) {
            currentVersionsVolume = new Long(0);
        }
        cells.add(new Cell("CurrentVersionsVolume", currentVersionsVolume));

        /* get entities number */

        String rqEntitiesNumber = "SELECT COUNT(dm_entity_id) as EntitiesNumber " + "FROM dm_entity e "
                + "WHERE e.dm_entity_path LIKE :dmEntityPath ";
        SQLQuery sqlEntitiesNumber = getSession().createSQLQuery(rqEntitiesNumber);
        sqlEntitiesNumber.addScalar("EntitiesNumber", LongType.INSTANCE);
        sqlEntitiesNumber.setString("dmEntityPath", dmEntity.getPath() + "/%");
        cells.add(new Cell("EntitiesNumber", sqlEntitiesNumber.list().get(0)));

        report.addRow(new Row(cells));
        return report.toXML();
    }

    public DMEntityImpl getDmEntity()
    {
        return dmEntity;
    }
}

