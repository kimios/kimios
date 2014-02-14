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

import org.kimios.kernel.security.sso.CasUtils;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 10/10/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class CasPlayer {


    public static void main(String[] args) throws Exception{
        String username = "admin";
        String password = "admin";
        new CasUtils("https://localhost:9011").validateAuthentication(username, password, "http://localhost:8080");
    }
}
