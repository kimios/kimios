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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.core;

/**
 * @author Fabien Alin
 */
public class ThreadLocalWeb extends ThreadLocal<WebContext> {
    public long nbThreadInstance = 0;

    @Override
    public WebContext get() {
        return super.get();
    }

    public long count() {
        return nbThreadInstance;
    }

    @Override
    public void remove() {
        nbThreadInstance--;
        super.set(null);
        super.remove();
    }

    @Override
    public void set(WebContext value) {
        nbThreadInstance++;
        super.set(value);
    }
}

