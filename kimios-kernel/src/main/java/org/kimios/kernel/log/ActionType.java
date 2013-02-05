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
package org.kimios.kernel.log;

public class ActionType
{
    public final static int CREATE = 0;

    public final static int READ = 1;

    public final static int UPDATE = 2;

    public final static int DELETE = 3;

    public final static int CREATE_DOCUMENT_VERSION = 4;

    public final static int UPDATE_DOCUMENT_VERSION = 5;

    public final static int CREATE_DOCUMENT_COMMENT = 6;

    public final static int UPDATE_DOCUMENT_COMMENT = 7;

    public final static int DELETE_DOCUMENT_COMMENT = 8;

    public final static int ADD_RELATED_DOCUMENT = 9;

    public final static int REMOVE_RELATED_DOCUMENT = 10;

    public final static int CHECKIN = 11;

    public final static int CHECKOUT = 12;

    public final static int WORKFLOW_REQUEST_CREATED = 13;

    public final static int WORKFLOW_REQUEST_APPROVED = 14;

    public final static int WORKFLOW_REQUEST_REJECTED = 15;

    public final static int USER_CONNECTED = 16;

    public final static int USER_DISCONNECTED = 17;

    public final static int KERNEL_START = 18;

    public final static int KERNEL_STOP = 19;

    public final static int CREATE_DOCUMENT_VERSION_FROM_LATEST = 20;
}

