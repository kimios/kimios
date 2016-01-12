/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.api;

import org.kimios.api.events.IEventContext;
import org.kimios.api.events.annotations.DmsEventOccur;

import java.lang.reflect.Method;

/**
 * Created by farf on 11/01/16.
 */
public interface EventHandler {
    IEventContext process(Method method, Object[] arguments, DmsEventOccur _when, Object methodReturn,
                          IEventContext ctx) throws Throwable;
}
