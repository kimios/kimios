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
package org.kimios.controller;

import flexjson.JSONSerializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Fabien Alin <fabien.alin@gmail.com>
 *
 *         Class Handling Icon Css Class generation
 */
public class CssController extends HttpServlet
{
    StringBuilder cssContainer;



    private static List<String> availablesIcons = new ArrayList<String>(  );

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {

        if (cssContainer == null) {
            cssContainer = new StringBuilder();

            String cssTpl = ".%s {background-image: url(%s/images/fileicons/%s.png) !important;" +
                    "background-repeat: no-repeat;" +
                    "background-position: center right}";

            /*
               List extension
            */

            String fileIconsPath = this.getServletContext().getRealPath("/images/fileicons");

            File iconsDirectory = new File(fileIconsPath);

            FilenameFilter pngFilter = new FilenameFilter()
            {
                public boolean accept(File file, String s)
                {
                    return s.endsWith(".png");
                }
            };

            List<File> pngFiles = Arrays.asList(iconsDirectory.listFiles(pngFilter));
            for (File f : pngFiles) {



                String fileName = f.getName().substring(0,
                        f.getName().lastIndexOf(".")
                );
                availablesIcons.add( fileName );
                cssContainer.append(
                        String.format(cssTpl, fileName, this.getServletContext().getContextPath(), fileName)
                );
                cssContainer.append("\n");
            }
        }

        if(req.getRequestURL().toString().endsWith( "icons" )){
            resp.setContentType("text/css");
            resp.getWriter().write(cssContainer.toString());
        } else {
            resp.setContentType( "application/json" );
            resp.getWriter().write( new JSONSerializer()
                                        .exclude( "class" )
                                        .serialize( availablesIcons ) );
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doGet(req, resp);
    }
}
