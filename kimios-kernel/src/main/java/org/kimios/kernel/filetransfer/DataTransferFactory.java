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
package org.kimios.kernel.filetransfer;

import org.kimios.kernel.exception.DataSourceException;

public interface DataTransferFactory
{
    public DataTransfer getDataTransfer(long uid) throws DataSourceException;

    public long addDataTransfer(DataTransfer transfer) throws DataSourceException;

    public void removeDataTransfer(DataTransfer transfer) throws DataSourceException;

    public void updateDataTransfer(DataTransfer transfer) throws DataSourceException;

    public DataTransfer getUploadDataTransferByDocumentId(long uid) throws DataSourceException;

    public DataTransfer getUploadDataTransferByDocumentToken(String token) throws DataSourceException;
}

