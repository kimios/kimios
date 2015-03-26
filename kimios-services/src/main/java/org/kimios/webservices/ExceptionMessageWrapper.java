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

package org.kimios.webservices;

import org.codehaus.jackson.annotate.JsonIgnore;

public class ExceptionMessageWrapper {

    private String message;

    private int code;

    private StackTraceElement[] stacktrace;


    @JsonIgnore
    private String name;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ExceptionMessageWrapper(){}


    public ExceptionMessageWrapper(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public ExceptionMessageWrapper(String message, int code, String name) {
        this.message = message;
        this.code = code;
        this.name = name;
    }

    public ExceptionMessageWrapper(String message, String name) {
        this.message = message;
        this.name = name;
    }

    public StackTraceElement[] getStackTrace() {
        return stacktrace;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stacktrace = stackTrace;
    }
}
