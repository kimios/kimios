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
kimios.Cookies = {
  setCookie : function(name, value, days) {
    var expire = new Date();
    expire.setTime(expire.getTime() + (24 * 60 * 60 * 1000) * days);
    document.cookie = name + '=' + escape(value) + '; expires=' + expire.toGMTString();
  },
  getCookie : function(name){
    var startIndex = document.cookie.indexOf(name);
    if (startIndex != -1) {
      var endIndex = document.cookie.indexOf(';', startIndex);
      if (endIndex == -1) endIndex = document.cookie.length;
      return unescape(document.cookie.substring(startIndex+name.length+1, endIndex));
    }
    else {
      return null;
    }
  },
  deleteCookie : function(name) {
       var expire = new Date();
       expire.setTime(expire.getTime() - (24 * 60 * 60 * 1000));
       document.cookie = name + "=; expires=" + expire.toGMTString();
  }
};
