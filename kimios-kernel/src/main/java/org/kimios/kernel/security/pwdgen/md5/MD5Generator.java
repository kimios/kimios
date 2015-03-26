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
package org.kimios.kernel.security.pwdgen.md5;

import org.kimios.kernel.security.pwdgen.CredentialsGenerator;

import java.io.UnsupportedEncodingException;

public class MD5Generator implements CredentialsGenerator
{
    public String generatePassword(String clear)
    {

        try {
            MD5 md5 = new MD5();
            md5.Update(clear, "UTF-8");
            return md5.asHex();
        } catch (UnsupportedEncodingException uee) {
            return clear;
        }
    }
}

