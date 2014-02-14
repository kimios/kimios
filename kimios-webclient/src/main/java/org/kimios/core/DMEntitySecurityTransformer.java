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

package org.kimios.core;

import flexjson.transformer.AbstractTransformer;
import org.kimios.kernel.ws.pojo.DMEntitySecurity;

public class DMEntitySecurityTransformer extends AbstractTransformer {

  public void transform(Object arg0) {
      getContext().writeOpenObject();
       DMEntitySecurity sec = (DMEntitySecurity) arg0;
      
      // Write out the fields
        getContext().writeName("dmEntityType");
        getContext().transform(sec.getDmEntityType());
        getContext().writeComma();
        getContext().writeName("dmEntityUid");
        getContext().transform(sec.getDmEntityUid());
        getContext().writeComma();
        getContext().writeName("type");
        getContext().transform(sec.getType());
        getContext().writeComma();
        getContext().writeName("source");
        getContext().transform(sec.getSource());
        getContext().writeComma();
        getContext().writeName("name");
        getContext().transform(sec.getName());
        getContext().writeComma();
        getContext().writeName("fullName");
        getContext().transform(sec.getFullName());
        getContext().writeComma();
        getContext().writeName("read");
        getContext().transform(sec.isRead());
        getContext().writeComma();
        getContext().writeName("write");
        getContext().transform(sec.isWrite());
        getContext().writeComma();
        getContext().writeName("fullAccess");
        getContext().transform(sec.isFullAccess());
        getContext().writeCloseObject();
        
  }

}
