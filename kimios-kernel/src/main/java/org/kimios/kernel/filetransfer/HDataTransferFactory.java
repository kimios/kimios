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
package org.kimios.kernel.filetransfer;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.filetransfer.model.DataTransfer;
import org.kimios.kernel.hibernate.HFactory;

public class HDataTransferFactory extends HFactory implements DataTransferFactory
{
    /* (non-Javadoc)
    * @see org.kimios.kernel.filetransfer.DataTransferFactory#addDataTransfer(org.kimios.kernel.filetransfer.DataTransfer)
    */
    public long addDataTransfer(DataTransfer transfer)
            throws DataSourceException
    {
        try {
            getSession().save(transfer);
            getSession().flush();
            return transfer.getUid();
        } catch (HibernateException he) {
            boolean integrity = he instanceof ConstraintViolationException;
            throw new DataSourceException(he, he.getMessage());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.filetransfer.DataTransferFactory#getDataTransfer(long)
    */
    public DataTransfer getDataTransfer(long uid) throws DataSourceException
    {
        try {
            DataTransfer transfer = (DataTransfer) getSession().get(DataTransfer.class, new Long(uid));
            return transfer;
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.filetransfer.DataTransferFactory#removeDataTransfer(org.kimios.kernel.filetransfer.DataTransfer)
    */
    public void removeDataTransfer(DataTransfer transfer)
            throws DataSourceException
    {
        try {
            getSession().delete(transfer);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.filetransfer.DataTransferFactory#updateDataTransfer(org.kimios.kernel.filetransfer.DataTransfer)
    */
    public void updateDataTransfer(DataTransfer transfer)
            throws DataSourceException
    {
        try {
            getSession().update(transfer);
            //getSession().flush();
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public DataTransfer getUploadDataTransferByDocumentId(long uid)
            throws DataSourceException
    {
        try {
            String query =
                    "from DataTransfer where documentVersionUid in (select dv.uid from " +
                            "DocumentVersion dv where documentUid = :docId order by dv.uid desc) and " +
                            " transferMode = :transferMode";
            return (DataTransfer) getSession().createQuery(query)
                    .setLong("docId", uid)
                    .setInteger("transferMode", DataTransfer.UPLOAD)
                    .uniqueResult();
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public DataTransfer getUploadDataTransferByDocumentToken(String token)
            throws DataSourceException
    {
        try {
            String query =
                    "from DataTransfer where downloadToken = :token and " +
                            " transferMode = :transferMode";
            DataTransfer dataTransfer = (DataTransfer) getSession().createQuery(query)
                    .setString("token", token)
                    .setInteger("transferMode", DataTransfer.TOKEN)
                    .uniqueResult();
            Hibernate.initialize(dataTransfer.getShare());
            return dataTransfer;
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

