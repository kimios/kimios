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

package org.kimios.tests.helpers;

import org.kimios.tests.helpers.WorkflowStatusDefinition;
import org.kimios.kernel.ws.pojo.WorkflowStatus;
import org.kimios.kernel.ws.pojo.WorkflowStatusManager;

import java.util.Vector;

/**
 * Test Helpers
 */
public class XMLDescriptionGenerators {

    /**
     * Return workflow XML description from workflow UID and workflow status definition list
     */
    public static String getWorkflowXMLDescriptor(long wfUid, Vector<WorkflowStatusDefinition> vStatus) {
        String xmlStream = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xmlStream += "<workflow " + (wfUid > 0 ? "uid=\"" + wfUid + "\"" : "") + ">";
        for (WorkflowStatusDefinition statusDef : vStatus) {
            WorkflowStatus status = statusDef.getWorkflowStatus();
            WorkflowStatusManager[] lWfStatusManagers = statusDef.getWorkflowStatusManagers();
            xmlStream +=
                    "<status uid=\"" + status.getUid() + "\" successor-uid=\"" + status.getSuccessorUid() + "\"><name>"
                            + cleanString(status.getName()) + "</name>";
            for (WorkflowStatusManager wfm : lWfStatusManagers) {
                xmlStream += "<manager type=\"" + wfm.getSecurityEntityType() + "\" uid=\"" +
                        wfm.getSecurityEntityName() +
                        "\" source=\"" +
                        wfm.getSecurityEntitySource() + "\" />";

            }
            xmlStream += "</status>";
        }
        xmlStream += "</workflow>";
        return xmlStream;
    }

    /**
     * Clean string and return it
     */
    public static String cleanString(String str) {
        String r = "";
        for (int g = 0; g < str.length(); g++) {
            int i = (int) str.charAt(g);
            if (i >= 48 && i <= 57 || i >= 65 && i <= 90 || i >= 97 && i <= 122) {
                r += str.charAt(g);
            } else {
                r += "&#" + i + ";";
            }
        }
        return r;
    }
}
