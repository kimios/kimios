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
package org.kimios.controller;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.kimios.kernel.ws.pojo.*;
import org.kimios.core.DMEntitySecurityTransformer;
import org.kimios.core.GroupUserTransformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author Fabien Alin
 */
public class SecurityControllerWeb extends Controller {

  public SecurityControllerWeb(Map<String, String> parameters) {
    super(parameters);
  }

  public String execute() throws Exception {
    String jsonResp = null;
    Object[] res = null;

    if (action.equalsIgnoreCase("stdRights")) {
      jsonResp = "[{" + "'canCreateWorkspace':" + Boolean.toString(securityController.canCreateWorkspace(sessionUid)) 
      + ",'isAdmin':" + Boolean.toString(securityController.isAdmin(sessionUid))
      + ",'isStudioUser':"+ Boolean.toString(securityController.hasStudioAccess(sessionUid)) 
//      + ",'isRulesUser':"+ Boolean.toString(new SecurityControllerWeb().hasRulesAccess(sessionUid))
      + ",'isRulesUser':"+false
      + ",'isReportingUser':" + Boolean.toString(securityController.hasReportingAccess(sessionUid))+ "}]";
    }

    if (action.equalsIgnoreCase("getUsers")) {
      String source = parameters.get("sourceUid");
      res = securityController.getUsers(sessionUid, source);
    }
    if (action.equalsIgnoreCase("getGroups")) {
      String source = parameters.get("sourceUid");
      res = securityController.getGroups(sessionUid, source);
    }
    if (action.equalsIgnoreCase("dmEntitySecurity")) {
      String entityUid = parameters.get("dmEntityUid");
      String entityType = parameters.get("dmEntityType");
      res = securityController.getDMEntitySecurities(sessionUid, Long.parseLong(entityUid), Integer
          .parseInt(entityType));
    }

	if (action.equalsIgnoreCase("nodeSecurity")) {
		String dmEntityUid = parameters.get("dmEntityUid");
		String dmEntityType = parameters.get("dmEntityType");
		boolean canRead = false;
		boolean canWrite = false;
		boolean canFullAccess = false;
		try {
			long entityUid = Long.parseLong(dmEntityUid);
			int entityType = Integer.parseInt(dmEntityType);
			canRead = securityController.canRead(sessionUid, entityUid, entityType);
			canWrite = securityController.canWrite(sessionUid, entityUid, entityType);
			canFullAccess = securityController.hasFullAccess(sessionUid, entityUid, entityType);
		} catch (NumberFormatException e) {
			/*
			 * Multiple selection disable rights checking. So it needed to
			 * set all to true.
			 */
			canRead = true;
			canWrite = true;
			canFullAccess = true;
		} finally {
			jsonResp = "[{'read':" + canRead + ",'write':" + canWrite + ",'fullAccess':" + canFullAccess + "}]";
		}
	}

    if (action.equalsIgnoreCase("updateDMEntitySecurities")) {
      updateDMEntitySecurities(parameters);
    }

    if (jsonResp == null)
      jsonResp = new JSONSerializer()
        .transform(new DMEntitySecurityTransformer(), DMEntitySecurity.class)
        .transform(new GroupUserTransformer(), User.class, Group.class)
        .exclude("*.class").serialize(res);
    return jsonResp;
  }

  private void updateDMEntitySecurities(Map<String, String> parameters) throws Exception {
    long uid = Long.parseLong(parameters.get("dmEntityUid"));
    int type = Integer.parseInt(parameters.get("dmEntityType"));
    String dmeJson = parameters.get("securityEntities");

    ArrayList<Map<String, String>> array = (ArrayList<Map<String, String>>) new JSONDeserializer().deserialize(dmeJson);
    Vector<DMEntitySecurity> entitySecurities = new Vector<DMEntitySecurity>();
    
    for (Map map : array) {
      DMEntitySecurity entitySecurity = new DMEntitySecurity();
      entitySecurity.setDmEntityType(Integer.parseInt(String.valueOf(map.get("dmEntityType"))));
      entitySecurity.setDmEntityUid(Long.parseLong(String.valueOf(map.get("dmEntityUid"))));
      entitySecurity.setRead(Boolean.parseBoolean(String.valueOf(map.get("read"))));
      entitySecurity.setWrite(Boolean.parseBoolean(String.valueOf(map.get("write"))));
      entitySecurity.setFullAccess(Boolean.parseBoolean(String.valueOf(map.get("fullAccess"))));
      entitySecurity.setName(String.valueOf(map.get("name")));
      entitySecurity.setSource(String.valueOf(map.get("source")));
      entitySecurity.setType(Integer.parseInt(String.valueOf(map.get("type"))));
      entitySecurities.add(entitySecurity);
    }
    
    securityController.updateDMEntitySecurities(sessionUid, uid, type, false, entitySecurities);
  }

}

