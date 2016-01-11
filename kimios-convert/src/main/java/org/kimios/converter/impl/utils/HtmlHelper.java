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

package org.kimios.converter.impl.utils;

import org.apache.poi.ss.usermodel.CellStyle;

        import java.util.Formatter;

/**
 * This interface is used where code wants to be independent of the workbook
 * formats.  If you are writing such code, you can add a method to this
 * interface, and then implement it for both HSSF and XSSF workbooks, letting
 * the driving code stay independent of format.
 *
 * @author Ken Arnold, Industrious Media LLC
 */
public interface HtmlHelper {
    /**
     * Outputs the appropriate CSS style for the given cell style.
     *
     * @param style The cell style.
     * @param out   The place to write the output.
     */
    void colorStyles(CellStyle style, Formatter out);
}