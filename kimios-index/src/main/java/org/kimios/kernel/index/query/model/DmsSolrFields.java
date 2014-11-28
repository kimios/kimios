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

package org.kimios.kernel.index.query.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Fabien Alin <fabien.alin@gmail.com>
 *
 *
 *
 */
public class DmsSolrFields
{

    public static Map<String, Class> sortFieldMapping = new HashMap<String, Class>();

    static {
        sortFieldMapping.put("DocumentCreationDate", Date.class);
        sortFieldMapping.put("DocumentUpdateDate", Date.class);
        sortFieldMapping.put("DocumentCreationDate", Date.class);
        sortFieldMapping.put("DocumentVersionUpdateDate", Date.class);
        sortFieldMapping.put("DocumentExtension", String.class);
        sortFieldMapping.put("DocumentOwner", String.class);
        sortFieldMapping.put("DocumentVersionLength", Number.class);
        sortFieldMapping.put("DocumentName", String.class);
        sortFieldMapping.put("DocumentTypeName", String.class);
        sortFieldMapping.put("DocumentTypeUid", Number.class);
        sortFieldMapping.put("DocumentUid", Number.class);
        sortFieldMapping.put("DocumentVersionId", Number.class);
        sortFieldMapping.put("DocumentVersionOwner", String.class);
        sortFieldMapping.put("DocumentVersionOwnerId", String.class);
        sortFieldMapping.put("DocumentVersionOwnerSource", String.class);
        sortFieldMapping.put("DocumentVersionHash", String.class);
        sortFieldMapping.put("Attribute_SEARCHTAG", String.class);

    }

    public static Map<String, Class> sortMetaFieldMapping = new HashMap<String, Class>();

    static {
        sortMetaFieldMapping.put("MetaDataDate", Date.class);
        sortMetaFieldMapping.put("MetaDataNumber", Number.class);
        sortMetaFieldMapping.put("MetaDataString", String.class);
        sortMetaFieldMapping.put("MetaDataList", String.class);
    }


}
