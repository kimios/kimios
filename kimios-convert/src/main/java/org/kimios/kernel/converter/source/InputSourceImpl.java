package org.kimios.kernel.converter.source;

public abstract class InputSourceImpl implements InputSource {
    /**
     * A alternative user-friendly document name
     */
    protected String humanName;

    public void setHumanName(String name) {
        this.humanName = name;
    }

    public String getHumanName() {
        return humanName;
    }
}
