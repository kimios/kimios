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

package org.kimios.kernel.converter;

public class ConverterBean {

    /**
     * Short description of converter
     */
    private String name;

//    /**
//     * Converter source type
//     */
//    private ConverterType sourceType;

//    /**
//     * Converter target type
//     */
//    private ConverterType targetType;

    /**
     * Implementation class name
     */
    private Class implClass;

    /**
     * Enable or disable this converter
     */
    private Boolean enabled;


    public ConverterBean(Class implClass) {
        this.implClass = implClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public ConverterType getSourceType() {
//        return sourceType;
//    }
//
//    public void setSourceType(ConverterType sourceType) {
//        this.sourceType = sourceType;
//    }
//
//    public ConverterType getTargetType() {
//        return targetType;
//    }
//
//    public void setTargetType(ConverterType targetType) {
//        this.targetType = targetType;
//    }

    public Class getImplClass() {
        return implClass;
    }

    public void setImplClass(Class implClass) {
        this.implClass = implClass;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
