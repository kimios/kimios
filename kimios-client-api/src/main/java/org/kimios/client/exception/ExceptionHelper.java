/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kimios.client.exception;

/**
 *
 * @author Fabien Alin
 */
public class ExceptionHelper {

    public Exception convertException(Exception ex) {
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Error 01")) {
                return new InvalidSessionException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 02")) {
                return new ConfigException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 03")) {
                return new DataSourceException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 04")) {
                return new AccessDeniedException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 05")) {
                return new RepositoryException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 06")) {
                return new XMLException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 07")) {
                return new TransferIntegrityException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 08")) {
                return new IndexException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 09")) {
                return new TransferIntegrityException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 10")) {
                return new MetaValueTypeException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 11")) {
                return new WorkflowException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 16")) {
                return new ReportingException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 00")) {
                return new DMSException(ex.getMessage(), ex);
            }
            return ex;
        } else {
            return ex;
        }
    }

    public static int getCode(Exception ex) {
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Error 01")) {
                return 1;
            }
            if (ex.getMessage().contains("Error 02")) {
                return 2;
            }
            if (ex.getMessage().contains("Error 03")) {
                return 3;
            }
            if (ex.getMessage().contains("Error 04")) {
                return 4;
            }
            if (ex.getMessage().contains("Error 05")) {
                return 5;
            }
            if (ex.getMessage().contains("Error 06")) {
                return 6;
            }
            if (ex.getMessage().contains("Error 07")) {
                return 7;
            }
            if (ex.getMessage().contains("Error 08")) {
                return 8;
            }
            if (ex.getMessage().contains("Error 09")) {
                return 9;
            }
            if (ex.getMessage().contains("Error 10")) {
                return 10;
            }
            if (ex.getMessage().contains("Error 11")) {
                return 11;
            }
            if (ex.getMessage().contains("Error 00")) {
                return 0;
            }
            return 0;
        } else {
            return -1;
        }
    }
}

