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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.controller;

import flexjson.JSONSerializer;
import org.kimios.core.DMSTreeNode;
import org.kimios.core.TreeNodeTransformer;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Folder;
import org.kimios.kernel.ws.pojo.Workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Fabien Alin
 */
public class TreeControllerWeb extends Controller {

    public static String contextPath;

    public TreeControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception {
        int dmEntityType = 1;
        long dmEntityUid = 0;
        List<DMSTreeNode> qNodes = new ArrayList<DMSTreeNode>();
        boolean excLeaf = false;
        if (parameters.get("dmEntityType").equalsIgnoreCase("")
                || parameters.get("nodeUid").equalsIgnoreCase("0")) {
            Workspace[] wNodes = workspaceController
                    .getWorkspaces(sessionUid);
            for (Workspace w : wNodes) {
                qNodes.add(new DMSTreeNode(w));
            }
            excLeaf = true;
        } else {
            dmEntityType = Integer.parseInt(parameters.get("dmEntityType"));
            dmEntityUid = Long.parseLong(parameters.get("nodeUid"));
            if (dmEntityType == 1 || dmEntityType == 2) {
                Folder[] fNodes = folderController.getFolders(sessionUid,
                        dmEntityUid, dmEntityType);
                for (Folder f : fNodes) {
                    qNodes.add(new DMSTreeNode(f));
                }
            }

            if (dmEntityType == 2 && parameters.get("withDoc") != null) {
                Document[] dNodes = documentController.getDocuments(
                        sessionUid, dmEntityUid);
                for (Document d : dNodes)
                    qNodes.add(new DMSTreeNode(d));
            }
        }
        for (DMSTreeNode qn : qNodes)
            qn.setContextPath(TreeControllerWeb.contextPath);

        String jsonResp = "";
        if (!excLeaf)
            jsonResp = new JSONSerializer()
                    .exclude("class")
                    .transform(new TreeNodeTransformer(true),
                            DMSTreeNode.class).serialize(qNodes);
        else
            jsonResp = new JSONSerializer()
                    .exclude("class")
                    .exclude("leaf")
                    .transform(new TreeNodeTransformer(false),
                            DMSTreeNode.class).serialize(qNodes);

        return jsonResp;
    }
}
