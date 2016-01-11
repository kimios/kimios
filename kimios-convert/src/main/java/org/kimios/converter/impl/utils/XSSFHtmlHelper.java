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

import java.util.Formatter;
        import java.util.Map;

        import org.apache.poi.hssf.util.HSSFColor;
        import org.apache.poi.ss.usermodel.CellStyle;
        import org.apache.poi.xssf.usermodel.XSSFCellStyle;
        import org.apache.poi.xssf.usermodel.XSSFColor;
        import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Implementation of {@link HtmlHelper} for XSSF files.
 *
 * @author Ken Arnold, Industrious Media LLC
 */
public class XSSFHtmlHelper implements HtmlHelper {
    private final XSSFWorkbook wb;

    private static final Map<Integer,HSSFColor> colors = HSSFColor.getIndexHash();

    public XSSFHtmlHelper(XSSFWorkbook wb) {
        this.wb = wb;
    }

    public void colorStyles(CellStyle style, Formatter out) {
        XSSFCellStyle cs = (XSSFCellStyle) style;
        styleColor(out, "background-color", cs.getFillForegroundXSSFColor());
        styleColor(out, "text-color", cs.getFont().getXSSFColor());
    }

    private void styleColor(Formatter out, String attr, XSSFColor color) {
        if (color == null || color.isAuto())
            return;

        byte[] rgb = color.getRgb();
        if (rgb == null) {
            return;
        }

        // This is done twice -- rgba is new with CSS 3, and browser that don't
        // support it will ignore the rgba specification and stick with the
        // solid color, which is declared first
        out.format("  %s: #%02x%02x%02x;%n", attr, rgb[0], rgb[1], rgb[2]);
        byte[] argb = color.getARgb();
        if (argb == null) {
            return;
        }
        out.format("  %s: rgba(0x%02x, 0x%02x, 0x%02x, 0x%02x);%n", attr,
                argb[3], argb[0], argb[1], argb[2]);
    }
}