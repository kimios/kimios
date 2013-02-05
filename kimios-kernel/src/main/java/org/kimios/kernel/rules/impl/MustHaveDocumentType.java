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
package org.kimios.kernel.rules.impl;

import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentType;
import org.kimios.kernel.dms.DocumentTypeFactory;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.DocumentVersionFactory;
import org.kimios.kernel.dms.FactoryInstantiator;

public class MustHaveDocumentType extends RuleImpl
{
    private String documentTypeName;

    @Override
    public boolean isTrue()
    {
        return true;
    }

    @Override
    public void execute() throws Exception
    {
        DMEntity entity = this.getContext().getEntity();
        if (entity.getType() == 3) {

            //Event type
            switch (ctx.getEvent()) {
                case DOCUMENT_VERSION_CREATE:

                    break;
                case DOCUMENT_VERSION_UPDATE:

                    break;
                case META_VALUE_UPDATE:

                    break;
                default:
                    //do nothing
            }

            Document doc = (Document) entity;
            DocumentVersion version = (DocumentVersion) ctx.getParameters().get("version");
            DocumentTypeFactory dtFactory = FactoryInstantiator.getInstance().getDocumentTypeFactory();
            DocumentVersionFactory dvFactory = FactoryInstantiator.getInstance().getDocumentVersionFactory();

            DocumentType dt = null;
            if (version == null) {
                version = dvFactory.getLastDocumentVersion(doc);
            }
            if (version != null && version.getDocumentType() == null) {
                // load document type
                dt = dtFactory.getDocumentTypeByName(documentTypeName);
                version.setDocumentType(dt);
                //set values
            }
            //check dt
            if (dt.getName().equalsIgnoreCase(documentTypeName)) {

            } else {

            }
        }
    }
}

