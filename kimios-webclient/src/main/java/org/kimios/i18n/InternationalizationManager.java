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

package org.kimios.i18n;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Fabien Alin
 */
public class InternationalizationManager {
    
    
    private static InternationalizationManager instance;
    private String values = "";
    private InternationalizationManager(){
        
    }
    
    public static InternationalizationManager getInstance(String loc){
        if(instance != null)
            return instance;
        
        InternationalizationManager im = new InternationalizationManager();
        ResourceBundle rb = ResourceBundle.getBundle("org.kimios.i18n.client", new Locale(loc));
        Enumeration<String> en = rb.getKeys();
        im.values += "<script type=\"text/javascript\" language=\"javascript\">";
        while(en.hasMoreElements()){
            String it = en.nextElement();
            im.values += "var jsVar" + it + "='" + rb.getString(it) + "';";
        }
        im.values += "</script>";
        instance = im;
        return instance;
    }
    
    public static String getValues(String loc){
        if(instance == null)
            getInstance(loc);
            
         return instance.values;
    }


    public static String getSingleValue(String loc, String key){
        ResourceBundle rb = ResourceBundle.getBundle("org.kimios.i18n.client", new Locale(loc));
        return  rb.getString(key);
    }


}

