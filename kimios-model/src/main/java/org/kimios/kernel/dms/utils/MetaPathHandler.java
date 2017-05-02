/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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

package org.kimios.kernel.dms.utils;

import org.kimios.kernel.dms.model.DocumentType;
import org.kimios.kernel.dms.model.MetaType;
import org.kimios.kernel.dms.model.MetaValue;
import org.kimios.kernel.dms.model.PathTemplate;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by farf on 11/13/14.
 */
public class MetaPathHandler {

    private static Logger logger = LoggerFactory.getLogger(MetaPathHandler.class);

    public String path(Date creationDate,
                       Session session,
                       List<PathElement> pathElements,
                       DocumentType documentType,
                       List<MetaValue> metas,
                       String mimeTypeOrExtension) {
        StringBuffer buffer = new StringBuffer();

        for (PathElement e : pathElements) {
            switch (e.getElementType()) {

                case PathElement.USERNAME:
                    try {
                        buffer.append("/" + PathUtils.cleanDmEntityName(session.getUserName()));
                    } catch (Exception ex) {

                    }
                    break;
                case PathElement.USERSOURCE:
                    try {
                        buffer.append("/" + PathUtils.cleanDmEntityName(session.getUserSource()));
                    } catch (Exception ex) {

                    }
                    break;

                case PathElement.USER_ID:
                    try {
                        buffer.append("/" + PathUtils.cleanDmEntityName(session.getUserName()
                                                                            + "@" +
                                                                            session.getUserSource()));
                    } catch (Exception ex) {

                    }
                    break;

                case PathElement.DOCUMENT_EXTENSION:
                    try {
                        buffer.append("/" + PathUtils.cleanDmEntityName(mimeTypeOrExtension));
                    } catch (Exception ex) {

                    }
                    break;

                case PathElement.DOCUMENT_MIME_TYPE:
                    try {
                        buffer.append("/" + PathUtils.cleanDmEntityName(mimeTypeOrExtension));
                    } catch (Exception ex) {

                    }
                    break;

                case PathElement.CREATION_DATE:
                    try {
                        buffer.append("/" + PathUtils.cleanDmEntityName(new SimpleDateFormat(e.getElementFormat()).format(creationDate)));
                    } catch (Exception ex) {

                    }
                    break;

                case PathElement.FIXED_STRING:
                            buffer.append(PathUtils.cleanDmEntityName(e.getElementValue()));
                    break;

                case PathElement.TYPE_FIELD:
                    if(documentType != null){
                        buffer.append(documentType.getName());
                    }
                    break;

                case PathElement.INDEX_FIELD:
                    boolean useId = false;
                    long id = -1;
                    try {
                        id = Long.parseLong(e.getElementValue());
                        useId = true;
                    } catch (Exception ex) {

                    }

                    for (MetaValue value : metas) {
                        if ((useId && value.getMetaUid() == id) || value.getMeta().getName().equals(e.getElementValue())) {
                            buffer.append(parseMeta(value, e));
                            break;
                        }
                    }
                    break;
            }

            if(e.isDocumentName() && mimeTypeOrExtension != null && mimeTypeOrExtension.length() > 0){
                buffer.append("." + mimeTypeOrExtension);
            }
        }
        logger.debug("generated path {}", buffer);
        return buffer.toString();
    }



    private static String parseMeta(MetaValue value, PathElement e){
            if (value.getMeta().getMetaType() == MetaType.DATE) {
                try {
                    return  "/" + new SimpleDateFormat(e.getElementFormat()).format((Date)value.getValue());
                } catch (Exception ex) {
                    return "";
                }
            } else  if (value.getMeta().getMetaType() == MetaType.LIST){

                List<String> valueList = (List)value.getValue();
                Collections.sort(valueList);
                return "/" + PathUtils.cleanDmEntityName((valueList.get(0)));
            } else
                return "/" + PathUtils.cleanDmEntityName((value.getValue().toString()));
    }

    public static PathTemplate defaultPathModel(){

        PathTemplate pathTemplate = new PathTemplate();
        List<PathElement> pathElements = new ArrayList<>();

        PathElement p = new PathElement();
        p.setElementType(PathElement.FIXED_STRING);
        p.setElementValue("Documents");
        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.TYPE_FIELD);
        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("yyyy");
        pathElements.add(p);


        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("MM");
        pathElements.add(p);


        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("dd");
        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("yyyy-MM-dd-hh-mm-ss");
        p.setDocumentName(true);
        pathElements.add(p);

        pathTemplate.setTemplateName("default");
        pathTemplate.setPathElements(pathElements);
        return pathTemplate;
    }

    public static PathTemplate defaultUserPathModel(){

        PathTemplate pathTemplate = new PathTemplate();
        List<PathElement> pathElements = new ArrayList<>();

        PathElement p = new PathElement();
        p.setElementType(PathElement.FIXED_STRING);
        p.setElementValue("Documents");
        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.USER_ID);
        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("yyyy");
        pathElements.add(p);


        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("MM");
        pathElements.add(p);


        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("dd");
        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("yyyy-MM-dd-hh-mm-ss");
        p.setDocumentName(true);
        pathElements.add(p);

        pathTemplate.setTemplateName("default");
        pathTemplate.setPathElements(pathElements);
        return pathTemplate;
    }


}
