/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2012  DevLib'
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
package org.kimios.kernel.exception;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ExceptionAnalyser
{
    public static boolean isThrowableCausedBy(Throwable thrown, Class<? extends Throwable> lookedCause)
    {

        /*
           Constitute tree
        */

        List<Throwable> items = new ArrayList<Throwable>();
        Throwable p = thrown;
        while (p != null) {
            items.add(p);
            p = p.getCause();
        }

        boolean integrity = false;
        for (Throwable st : items) {
            if (st.getClass().isAssignableFrom(lookedCause)) {
                integrity = true;
                break;
            }
        }
        return integrity;
    }
}
