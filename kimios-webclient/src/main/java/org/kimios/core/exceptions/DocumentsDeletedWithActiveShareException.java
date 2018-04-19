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

package org.kimios.core.exceptions;

import java.util.Map;

/**
 *
 * @author Thomas Lornet
 */
public class DocumentsDeletedWithActiveShareException extends DoneWithWarningException {

    private String message = "Several documents deleted with active shares ongoing";

    private Map<Long, String> entityPathList;

    public DocumentsDeletedWithActiveShareException() {
        super();
    }

    public DocumentsDeletedWithActiveShareException(Map<Long, String> entityPathList) {
        super();
        this.entityPathList = entityPathList;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String toString(){
        return "An exception has occured : " + this.message;
    }

    public Map<Long, String> getEntityPathList() {
        return entityPathList;
    }
}

