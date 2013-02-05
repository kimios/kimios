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
package org.kimios.client.controller.helpers;

import java.util.Vector;

/**
 * This utility class provides methods to parse strings
 */
public class StringTools {

    public static String magicQuotes(String in) {
        if (in != null) {
            return in.replaceAll("\'", "\\\\\'");
        } else {
            return null;
        }
    }

    public static String magicDoubleQuotes(String in) {
        if (in != null) {
            return in.replaceAll("\"", "\'\'");
        } else {
            return null;
        }
    }

    public static String getRequest(Vector select, Vector from, Vector where, Vector other) {
        String request = "SELECT ";
        for (int i = 0; i < select.size(); i++) {
            request += select.elementAt(i);
            if (i != select.size() - 1) {
                request += ",";
            }
        }
        request += " FROM ";
        for (int i = 0; i < from.size(); i++) {
            request += from.elementAt(i);
            if (i != from.size() - 1) {
                request += ",";
            }
        }
        request += " WHERE ";
        for (int i = 0; i < where.size(); i++) {
            request += where.elementAt(i);
            if (i != where.size() - 1) {
                request += " AND ";
            }
        }
        request += " ";
        for (int i = 0; i < other.size(); i++) {
            request += other.elementAt(i);
            if (i != other.size() - 1) {
                request += " ";
            }
        }
        System.out.println(request);
        return request;
    }

    /**
     * Convert simple new line to HTML new line
     */
    public static String nl2br(String str) {
        return str.replaceAll("\n", "<br />");
    }

    public static String HTMLEntityEncode(String s) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                buf.append(c);
            } else {
                buf.append("&#" + (int) c + ";");
            }
        }
        return buf.toString();
    }
}

