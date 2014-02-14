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

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.kimios.client.controller.RuleController;
import org.kimios.client.controller.helpers.XMLGenerators;
import org.kimios.client.controller.helpers.rules.Rule;
import org.kimios.client.controller.helpers.rules.RuleParameter;

import java.util.List;
import java.util.Map;

/**
 * @author Fabien Alin
 */
public class RuleControllerWeb extends Controller {

    public RuleControllerWeb(Map<String, String> parameters) {
        super(parameters);
    }

    public String execute() throws Exception {
        if (action.equalsIgnoreCase("rulesAvailable")) {
            return rulesAvailable(parameters);
        }
        if (action.equalsIgnoreCase("rulesParams")) {
            return rulesParameters(parameters);
        }
        if (action.equalsIgnoreCase("createRule")) {
            createRule(parameters);
        }
        return "";
    }

    public void createRule(Map<String, String> parameters) throws Exception {
        List<Map<String, String>> eventsMap = null;
        List<Map<String, String>> parametersMap = null;
        if (parameters.get("events") != null)
            eventsMap = (List<Map<String, String>>) new JSONDeserializer()
                    .deserialize(parameters.get("events"));
        if (parameters.get("parameters") != null)
            parametersMap = (List<Map<String, String>>) new JSONDeserializer()
                    .deserialize(parameters.get("parameters"));
        new RuleController()
                .createRule(sessionUid, parameters.get("conditionJavaClass"),
                        parameters.get("ruleName"), parameters.get("path"),
                        XMLGenerators.getRulesXMLDescriptor(eventsMap,
                                parametersMap));
    }

    public String rulesAvailable(Map<String, String> parameters)
            throws Exception {
        String[] availablesRules = ruleController
                .getAvailablesRules(sessionUid);
        String resp = "{list:"
                + new JSONSerializer().serialize(availablesRules) + "}";
        return resp;
    }

    public String rulesParameters(Map<String, String> parameters)
            throws Exception {
        String typeName = parameters.get("javaClassName");
        String xmlParams = ruleController
                .getRuleParametersDescription(sessionUid, typeName);

        Rule rule = XMLGenerators.unserializeRule(xmlParams);

        /*
           * Serialize back to display;
           */

        String metaDatas = "{fields:[";

        for (RuleParameter key : rule.getParams()) {
            metaDatas += "{name:'" + key.getName() + "'," + "type:'"
                    + key.getType() + "'," + "}";
        }

        metaDatas = metaDatas.substring(0, metaDatas.lastIndexOf(","));
        metaDatas += "]}";

        return metaDatas;
    }

}
