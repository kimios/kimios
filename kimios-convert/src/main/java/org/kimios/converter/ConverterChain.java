/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.converter;

import java.util.ArrayList;
import java.util.List;

public class ConverterChain {
    private String name;
    private List<ConverterBean> converterBeans;

    public ConverterChain() {
        converterBeans = new ArrayList<ConverterBean>();
    }

    public ConverterChain(List<ConverterBean> beans) {
        this.converterBeans = beans;
    }

    public void addConverterBean(ConverterBean bean) {
        converterBeans.add(bean);
    }

    public void addConverterBeans(List<ConverterBean> beans) {
        converterBeans.addAll(beans);
    }

    public void setConverterBeans(List<ConverterBean> beans) {
        converterBeans = beans;
    }

    public List<ConverterBean> getConverterBeans() {
        return converterBeans;
    }

//    public ConverterType getSourceType() {
//        return converterBeans.get(0).getSourceType();
//    }
//
//    public ConverterType getTargetType() {
//        if (converterBeans == null || converterBeans.size() == 0)
//            return null;
//        return converterBeans.get(converterBeans.size() - 1).getTargetType();
//    }

}
