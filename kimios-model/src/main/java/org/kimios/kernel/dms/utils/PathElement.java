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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farf on 11/13/14.
 */
public class PathElement {


    public final static int CREATION_DATE = 0;
    public final static int FIXED_STRING = 1;
    public final static int INDEX_FIELD = 2;

    public final static int VALUE_CURRENT_DATE = 3;


    private boolean isDocumentName;

    private int elementType;

    private String elementValue;

    private String elementFormat;

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public String getElementValue() {
        return elementValue;
    }

    public void setElementValue(String elementValue) {
        this.elementValue = elementValue;
    }

    public String getElementFormat() {
        return elementFormat;
    }

    public void setElementFormat(String elementFormat) {
        this.elementFormat = elementFormat;
    }

    public boolean isDocumentName() {
        return isDocumentName;
    }

    public void setDocumentName(boolean isDocumentName) {
        this.isDocumentName = isDocumentName;
    }

    public static List<PathElement> parseElementsFromStructure(String jsonStructure) throws Exception {
        return new ObjectMapper().readValue(jsonStructure, new TypeReference<ArrayList<PathElement>>(){});
    }

    public static String convertStructureToString(List<PathElement> pathElements) throws Exception{
        String pathStructure = new ObjectMapper().writeValueAsString(pathElements);
        return pathStructure;
    }

}
