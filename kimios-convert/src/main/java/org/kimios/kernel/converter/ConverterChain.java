package org.kimios.kernel.converter;

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
