/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2013  DevLib'
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.reflections.vfs.Vfs;

import com.google.common.collect.AbstractIterator;

/**
 *
 *  @author Fabien Alin <fabien.alin@gmail.com>
 */
public class BundleUrlType implements Vfs.UrlType
{
    public static final String BUNDLE_PROTOCOL = "bundle";

    private final Bundle bundle;

    public BundleUrlType()
    {
        this.bundle = FrameworkUtil.getBundle(BundleUrlType.class);
    }

    public Vfs.Dir createDir(URL url)
    {
        return new BundleDir(bundle, url);
    }

    public boolean matches(URL url)
    {
        return BUNDLE_PROTOCOL.equals(url.getProtocol());
    }

    public static class BundleDir implements Vfs.Dir
    {
        private String path;

        private final Bundle bundle;

        private static String urlPath(Bundle bundle, URL url)
        {
            return ("/" + url.getFile());
        }

        public BundleDir(Bundle bundle, URL url)
        {
            //this(bundle, url.getPath());
            this(bundle, url.getPath());
        }

        public BundleDir(Bundle bundle, String p)
        {
            this.bundle = bundle;
            this.path = p;
            if (path.startsWith(BUNDLE_PROTOCOL + ":")) {
                path = path.substring((BUNDLE_PROTOCOL + ":").length());
            }
        }

        public String getPath()
        {
            return path;
        }

        public Iterable<Vfs.File> getFiles()
        {
            return new Iterable<Vfs.File>()
            {
                public Iterator<Vfs.File> iterator()
                {
                    return new AbstractIterator<Vfs.File>()
                    {
                        final Enumeration<URL> entries = bundle.findEntries(path, "*.class", true);

                        protected Vfs.File computeNext()
                        {
                            return entries.hasMoreElements() ? new BundleFile(BundleDir.this, entries.nextElement()) :
                                    endOfData();
                        }
                    };
                }
            };
        }

        public void close()
        {
        }
    }

    public static class BundleFile implements Vfs.File
    {
        private final BundleDir dir;

        private final String name;

        private final URL url;

        public BundleFile(BundleDir dir, URL url)
        {
            this.dir = dir;
            this.url = url;
            String path = url.getFile();
            this.name = path.substring(path.lastIndexOf("/") + 1);
        }

        public String getName()
        {
            return name;
        }

        public String getRelativePath()
        {
            return getFullPath().substring(dir.getPath().length());
        }

        public String getFullPath()
        {
            return url.getFile();
        }

        public InputStream openInputStream() throws IOException
        {
            return url.openStream();
        }
    }
}
