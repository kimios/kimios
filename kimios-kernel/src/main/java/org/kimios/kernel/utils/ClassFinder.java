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
package org.kimios.kernel.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassFinder
{
    private static Logger log = LoggerFactory.getLogger(ClassFinder.class);

    public static <T> Collection<Class<? extends T>> findImplement(String pkg, Class<T> impl)
    {
        try {
            Vfs.addDefaultURLTypes(new BundleUrlType());
        } catch (RuntimeException e) {
        } catch (Exception e) {
        } catch (LinkageError e){
        }

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage(pkg))
        );
        return reflections.getSubTypesOf(impl);
    }

    public static Vector<Class<?>> findnames(String pckgname, Class<?> tosubclass)
    {
        try {
            String packagePath = pckgname.replace('.', '/');
            URLClassLoader cLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
            URL[] classpath = cLoader.getURLs();
            Vector<Class<?>> result = new Vector<Class<?>>();
            for (URL url : classpath) {
                File file = new File(url.toURI());
                if (file.getPath().endsWith(".jar")) {
                    JarFile jarFile = new JarFile(file);
                    for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                        String entryName = (entries.nextElement()).getName();
                        if (entryName.matches(packagePath + "/\\w*\\.class")) { // get only class files in package dir
                            String className = entryName.replace('/', '.').substring(0, entryName.lastIndexOf('.'));
                            Class<?> clazz = Class.forName(className);
                            try {
                                Object o = clazz.newInstance();
                                if (tosubclass.isInstance(o)) {
                                    result.add(clazz);
                                }
                            } catch (InstantiationException iex) {
                                iex.printStackTrace();
                            } catch (IllegalAccessException iaex) {
                                iaex.printStackTrace();
                            }
                        }
                    }
                } else { // directory
                    File packageDirectory = new File(file.getPath() + "/" + packagePath);
                    if (packageDirectory.exists()) {
                        for (File f : packageDirectory.listFiles()) {
                            if (f.getPath().endsWith(".class")) {
                                String className =
                                        pckgname + "." + f.getName().substring(0, f.getName().lastIndexOf('.'));
                                Class<?> clazz = Class.forName(className);
                                try {
                                    Object o = clazz.newInstance();
                                    if (tosubclass.isInstance(o)) {
                                        result.add(clazz);
                                    }
                                } catch (InstantiationException iex) {
                                    iex.printStackTrace();
                                } catch (IllegalAccessException iaex) {
                                    iaex.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error during class search", e);
            return null;
        }
    }
}
