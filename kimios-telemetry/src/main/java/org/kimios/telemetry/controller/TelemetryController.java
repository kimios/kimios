package org.kimios.telemetry.controller;

import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.karaf.system.SystemService;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.sql.DatabaseMetaData;

import java.util.HashMap;

public class TelemetryController extends AKimiosController implements ITelemetryController {

    private static Logger logger = LoggerFactory.getLogger(TelemetryController.class);

    private SystemService systemService;
    private DataSource dataSource;
    private String serverURL;

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

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public TelemetryController(){}

    public TelemetryController(
            SystemService systemService,
            DataSource dataSource

    ){
        this.systemService = systemService;
        this.dataSource = dataSource;

        this.serverURL = ConfigurationManager.getValue("dms.telemetry.server.url") != null ?
                ConfigurationManager.getValue("dms.telemetry.server.url") : "";
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

    public void sendToTelemetryPHP(Session session) throws Exception {
        HashMap<String, String> data = this.retrieveKarafInstanceNameAndVersion();
        data.putAll(this.retrieveJavaInfo());
        data.putAll(this.retrieveDatabaseInfo());

        try {
            JsonObject dataJson = hashMapToTelemetryPhpJson(data);
            this.sendToServer(dataJson);
        } catch (Exception e) {
            logger.error("Error during telemetry data sending to server");
            logger.error(e.getMessage());
        }

    }

    public void sendToServer(JsonObject dataJson) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost postRequest = new HttpPost(this.serverURL);
            StringEntity input = new StringEntity(dataJson.toString(), ContentType.APPLICATION_JSON);
            postRequest.setEntity(input);
            HttpResponse httpResponse = httpClient.execute(postRequest);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (MalformedURLException e) {
            throw e;
        }

    }

    public JsonObject hashMapToTelemetryPhpJson(HashMap<String, String> data) {
        JsonObject object = null;
        try {
            JsonBuilderFactory factory = Json.createBuilderFactory(null);
            JsonObjectBuilder objectBuilder = factory.createObjectBuilder()
                    .add("id", "http://kimios.com")
                    .add("data", Json.createObjectBuilder()
                            .add("system", Json.createObjectBuilder()
                                    .add("db", Json.createObjectBuilder()
                                            .add("engine", data.get("databaseProductName"))
                                            .add("version", data.get("databaseProductVersion"))
                                            .add("log_size", "-1")
                                            .add("size", "-1")
                                            .add("sql_mode", "-1"))
                                    .add("os", Json.createObjectBuilder()
                                            .add("distribution", "-1")
                                            .add("family", data.get("os.name"))
                                            .add("version", data.get("os.version")))
                                    .add("php", Json.createObjectBuilder()
                                            .add("modules", Json.createArrayBuilder())
                                            .add("setup",  Json.createObjectBuilder()
                                                    .add("max_execution_time", "-1")
                                                    .add("memory_limit", "-1")
                                                    .add("post_max_size", "-1")
                                                    .add("safe_mode", JsonValue.FALSE)
                                                    .add("session", "-1")
                                                    .add("upload_max_filesize", "-1"))
                                            .add("version", data.get("java.version")))
                                    .add("web_server", Json.createObjectBuilder()
                                            .add("engine", "Apache Karaf")
                                            .add("version", data.get("karafVersion"))))
                            .add("kimios", Json.createObjectBuilder()
                                    .add("default_language", "en_GB")
                                    .add("install_mode", "Karaf Distribution")
                                    .add("plugins", Json.createArrayBuilder())
                                    .add("usage", Json.createObjectBuilder()
                                            .add("avg_changes", "")
                                            .add("avg_computers", "")
                                            .add("avg_entities", "")
                                            .add("avg_groups", "")
                                            .add("avg_networkequipments", "")
                                            .add("avg_problems", "")
                                            .add("avg_projects", "")
                                            .add("avg_tickets", "")
                                            .add("avg_users", "")
                                            .add("ldap_enabled", JsonValue.FALSE)
                                            .add("mailcollector_enabled", JsonValue.FALSE)
                                            .add("notifications_modes", Json.createArrayBuilder()))
                                    .add("uuid", "GENERATED_BAD_UUID")
                                    .add("version", "VERSION")
                            )

                    );
            object = objectBuilder.build();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return object;
    }
}

