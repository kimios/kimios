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

package org.kimios.converter.impl.docx4j.osgi;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.common.Exporter;
import org.docx4j.convert.out.html.HTMLExporterVisitor;
import org.docx4j.convert.out.html.HTMLExporterXslt;
import org.docx4j.events.EventFinished;
import org.docx4j.events.StartEvent;
import org.docx4j.events.WellKnownProcessSteps;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.kimios.converter.impl.docx4j.osgi.HTMLExporterOsgi;

import java.io.OutputStream;

/**
 * Created by farf on 09/01/16.
 */
public class Docx4jOsgi extends Docx4J {




    /**
     *  Convert the document to HTML
     */
    public static void toHTML(HTMLSettings settings, OutputStream outputStream, int flags) throws Docx4JException {

        StartEvent startEvent = new StartEvent( settings.getWmlPackage(), WellKnownProcessSteps.HTML_OUT );
        startEvent.publish();

        Exporter<HTMLSettings> exporter = getHTMLExporter(flags);
        exporter.export(settings, outputStream);

        new EventFinished(startEvent).publish();
    }

    protected static Exporter<HTMLSettings> getHTMLExporter(int flags) {
        switch (flags) {
            case FLAG_EXPORT_PREFER_NONXSL:
                return HTMLExporterVisitor.getInstance();
            case FLAG_EXPORT_PREFER_XSL:
            default:
                return HTMLExporterOsgi.getInstance();
        }
    }
}
