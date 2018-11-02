/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
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
package org.kimios.deployer.core;

public class InstallerProgress
{
    private static InstallerProgress instance;

    private static long progression;

    /**
     * -1: undefined progression
     *
     * 0: installation finish
     *
     * other value: known progression
     */
    private static long maxProgression;

    private static String status = "";

    private static boolean isHtml = false;

    private InstallerProgress()
    {
        init();
    }

    public synchronized void init()
    {
        init(-1);
    }

    public synchronized void init(long maxProgression)
    {
        InstallerProgress.progression = 0;
        InstallerProgress.maxProgression = maxProgression;
        InstallerProgress.status = "";
    }

    public String getStatus()
    {
        return status;
    }

    public synchronized void setStatus(String customMessage, Object _status)
    {
        if (_status instanceof Exception) {
            if (isHtml) {
                status = getException(customMessage, ((Exception) _status));
            } else {
                status = ((Exception) _status).getMessage();
            }
        } else {
            String message = customMessage != null ? customMessage : "";
            InstallerProgress.status = message;
        }
    }

    public synchronized static InstallerProgress getInstance()
    {
        if (instance == null) {
            instance = new InstallerProgress();
        }
        return instance;
    }

    public synchronized void addProgression(long progression)
    {
        setProgression(InstallerProgress.progression + progression);
    }

    public synchronized void setCompleted()
    {
        InstallerProgress.progression = InstallerProgress.maxProgression;
    }

    public synchronized void setProgression(long progression)
    {
        if (progression > maxProgression) {
            InstallerProgress.progression = maxProgression;
        } else {
            InstallerProgress.progression = progression;
        }
    }

    public synchronized void setMaxProgression(long maxProgression)
    {
        InstallerProgress.maxProgression = maxProgression;
    }

    public long getProgression()
    {
        return InstallerProgress.progression;
    }

    public long getMaxProgression()
    {
        return InstallerProgress.maxProgression;
    }

    public boolean isFinish()
    {
        return InstallerProgress.progression == InstallerProgress.maxProgression;
    }

    public static String getException(String action, Exception e)
    {
        return ("<b>Exception occurred while " + action + ".</b><br/><br/>"
                + e.getMessage() + "<br/><br/>" + getTrace(e))
                .replace("\"", "\\\"").replace("\n", "<br/>").replace("\r", "");
    }

    private static String getTrace(Exception e)
    {
        StackTraceElement[] elems = e.getStackTrace();
        String trace = "<small>";
        if (elems.length != 0) {
            trace += "<font color=gray>";
            for (StackTraceElement elem : elems) {
                trace += elem.toString() + "<br/>";
            }
            trace += "</font><br/>";
        }
        trace += "<b>OS:</b> " + System.getProperty("os.name") + " ("
                + System.getProperty("os.version") + ") "
                + System.getProperty("os.arch") + "<br/>";
        trace += "<b>Java:</b> " + System.getProperty("java.version") + " ("
                + System.getProperty("java.vendor") + ")<br/>";
        trace += "<b>Catalina:</b> "
                + System.getProperty("catalina.home").replaceAll("\\\\", "/")
                + "<br/>";
        return trace;
    }
}

