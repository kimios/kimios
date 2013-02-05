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

package org.kimios.core;

import flexjson.transformer.AbstractTransformer;
import org.kimios.kernel.ws.pojo.Group;
import org.kimios.kernel.ws.pojo.User;

public class GroupUserTransformer extends AbstractTransformer {

	public void transform(Object arg0) {
		if (arg0 instanceof User) {
			User sec = (User) arg0;
			// Write out the fields
			getContext().writeOpenObject();
			getContext().writeName("uid");
			getContext().transform(sec.getUid());
			getContext().writeComma();
			getContext().writeName("name");
			getContext().transform(sec.getName());
			getContext().writeComma();
			getContext().writeName("mail");
			getContext().transform(sec.getMail());
			getContext().writeComma();
			getContext().writeName("source");
			getContext().transform(sec.getSource());
			getContext().writeComma();
			getContext().writeName("lastLogin");
			getContext().transform(
					sec.getLastLogin() != null ? sec.getLastLogin().getTime()
							: null);
			getContext().writeCloseObject();
		} else if (arg0 instanceof Group) {
			getContext().writeOpenObject();
			Group sec = (Group) arg0;
			// Write out the fields
			getContext().writeName("gid");
			getContext().transform(sec.getGid());
			getContext().writeComma();
			getContext().writeName("name");
			getContext().transform(sec.getName());
			getContext().writeComma();
			getContext().writeName("source");
			getContext().transform(sec.getSource());
			getContext().writeCloseObject();
		}
	}

}
