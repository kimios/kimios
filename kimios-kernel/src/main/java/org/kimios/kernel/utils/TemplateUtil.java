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
package org.kimios.kernel.utils;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

public class TemplateUtil
{
    private static Logger log = LoggerFactory.getLogger(TemplateUtil.class);

    private static Properties p;

    static {
        p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty("input.encoding", "UTF-8");
        p.setProperty("output.encoding", "UTF-8");
    }

    public static String generateContent(Map<String, Object> datas, String templatePath, String encoding)
            throws Exception
    {
        Velocity.init(p);
        StringWriter w = new StringWriter();
        VelocityContext context = new VelocityContext();
        for (String it : datas.keySet()) {
            log.debug("Set " + it + " --> " + datas.get(it));
            context.put(it, datas.get(it));
        }
        boolean merging = Velocity.mergeTemplate(templatePath, (encoding == null ? "UTF-8" : encoding), context, w);
        if (log.isDebugEnabled()) {
            log.debug("Merge Result: " + merging + " --> " + w.toString());
        }
        return w.toString();
    }
}

