package org.kimios.kernel.plugin.model;

import javax.persistence.*;

@Entity
@Table(name = "plugin")
@SequenceGenerator(allocationSize = 1, name = "seq", sequenceName = "plugin_id_seq")
public class Plugin {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private long id;

    @Column(name = "code_name", unique = true, nullable = false)
    private String codeName;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PluginStatus pluginStatus;

    @Column(name = "started", nullable = false)
    private boolean started;

    public Plugin() {
    }

    public Plugin(String codeName, String name, String version, PluginStatus pluginStatus, boolean started) {
        this.codeName = codeName;
        this.name = name;
        this.version = version;
        this.pluginStatus = pluginStatus;
        this.started = started;
    }

    public long getId() {
        return id;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public PluginStatus getPluginStatus() {
        return pluginStatus;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
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
    
    public org.kimios.kernel.ws.pojo.Plugin toPojo() {
        return new org.kimios.kernel.ws.pojo.Plugin(
                this.getCodeName(),
                this.getName(),
                this.getVersion(),
                this.getPluginStatus(),
                this.isStarted()
        );
    }
}
