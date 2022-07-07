package org.kimios.kernel.ws.pojo;

import org.kimios.kernel.plugin.model.PluginStatus;

public class Plugin {
    private String codeName;
    private String name;
    private String version;
    private PluginStatus pluginStatus;
    private boolean started;

    public Plugin(String codeName, String name, String version, PluginStatus pluginStatus, boolean started) {
        this.codeName = codeName;
        this.name = name;
        this.version = version;
        this.pluginStatus = pluginStatus;
        this.started = started;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public PluginStatus getPluginStatus() {
        return pluginStatus;
    }

    public void setPluginStatus(PluginStatus pluginStatus) {
        this.pluginStatus = pluginStatus;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
