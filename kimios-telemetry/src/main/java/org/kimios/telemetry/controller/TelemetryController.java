package org.kimios.telemetry.controller;

import com.google.common.collect.Lists;
import org.apache.karaf.system.SystemService;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.security.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.util.HashMap;

public class TelemetryController extends AKimiosController implements ITelemetryController {

    private static Logger logger = LoggerFactory.getLogger(TelemetryController.class);

    private SystemService systemService;
    private DataSource dataSource;

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public TelemetryController(){}

    public TelemetryController(
            SystemService systemService,
            DataSource dataSource
    ){
        this.systemService = systemService;
        this.dataSource = dataSource;
    }

    public HashMap<String, String> retrieveKarafInstanceNameAndVersion() throws Exception {
        HashMap<String, String> data = new HashMap<>();
        data.put("karafName", this.systemService.getName());
        data.put("karafVersion" , this.systemService.getVersion());
        data.put("karafFramework", this.systemService.getFramework().name());

        return data;
    }

    public HashMap<String, String> retrieveJavaInfo() throws Exception {
        HashMap<String, String> data = new HashMap<>();

        data.put("runtimeMaxMemory", String.valueOf(Runtime.getRuntime().maxMemory()));
        data.put("runtimeAvailableProcessors", String.valueOf(Runtime.getRuntime().availableProcessors()));

        String[] wantedPropertyNames = {
                "java.version",
                "java.runtime.name",
                "java.specification.name",
                "java.specification.vendor",
                "java.vm.name",
                "java.class.version",
                "java.vm.specification.version",
                "java.vm.version",
                "java.runtime.version",
                "os.version",
                "os.arch",
                "os.name",
        };

        Lists.newArrayList(wantedPropertyNames).forEach(p -> {
            String prop = System.getProperty(p);
            if (prop != null) {
                data.put(p, prop);
            }
        });

        return data;
    }

    public HashMap<String, String> retrieveDatabaseInfo() throws Exception {
        DatabaseMetaData metaData = this.dataSource.getConnection().getMetaData();

        HashMap<String, String> data = new HashMap<>();
        data.put("databaseProductName", metaData.getDatabaseProductName());
        data.put("databaseProductVersion", metaData.getDatabaseProductVersion());

        return data;
    }

    @Override
    public void sendToTelemetryPHP(Session session) throws Exception {
        HashMap<String, String> karafInfo = this.retrieveKarafInstanceNameAndVersion();
        HashMap<String, String> javaInfo = this.retrieveJavaInfo();
        HashMap<String, String> dbInfo = this.retrieveDatabaseInfo();

        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.debug("Karaf info:");
        karafInfo.forEach((k, v) -> logger.debug(k + " : " + v));
        logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.debug("Java info:");
        javaInfo.forEach((k, v) -> logger.debug(k + " : " + v));
        logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
