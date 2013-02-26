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
