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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kimios.client.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if (ex.getMessage().contains("Error 17")) {
                return new DeleteDocumentWithActiveShareException(ex.getMessage());
            }
            if (ex.getMessage().contains("Error 18")) {
                return new DocumentDeletedWithActiveShareException(ex.getMessage());
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
            Pattern p = Pattern.compile("Error (\\d\\d)");
            Matcher m = p.matcher(ex.getMessage());
            if (m.matches()) {
                return Integer.valueOf(m.group(1));
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }
}

