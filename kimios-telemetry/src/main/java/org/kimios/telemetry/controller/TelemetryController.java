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
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.management.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.sql.DatabaseMetaData;

import java.util.*;

public class TelemetryController extends AKimiosController implements ITelemetryController {

    public enum ContainerType {
        SPRING("spring"), OSGI("osgi");

        private final String value;

        ContainerType(String type) {
            this.value = type;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(TelemetryController.class);

    private SystemService systemService;
    private DataSource databaseConnection;
    private ISecurityController securityController;
    private IAdministrationController administrationController;
    private IDocumentController documentController;

    private String serverURL;

    private MBeanServerConnection mBeanServerConnection;

    public SystemService getSystemService() {
        return systemService;
    }

    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public DataSource getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(DataSource databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public ISecurityController getSecurityController() {
        return securityController;
    }

    public void setSecurityController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public IAdministrationController getAdministrationController() {
        return administrationController;
    }

    public void setAdministrationController(IAdministrationController administrationController) {
        this.administrationController = administrationController;
    }

    public IDocumentController getDocumentController() {
        return documentController;
    }

    public void setDocumentController(IDocumentController documentController) {
        this.documentController = documentController;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return mBeanServerConnection;
    }

    public void setMBeanServerConnection(MBeanServerConnection mBeanServerConnection) {
        this.mBeanServerConnection = mBeanServerConnection;
    }

    public TelemetryController(){}

    public TelemetryController(
            SystemService systemService,
            DataSource databaseConnection,
            ISecurityController securityController,
            IAdministrationController administrationController,
            IDocumentController documentController
    ){
        this(
                databaseConnection,
                securityController,
                administrationController,
                documentController
        );
        this.systemService = systemService;
    }

    public TelemetryController(
            DataSource databaseConnection,
            ISecurityController securityController,
            IAdministrationController administrationController,
            IDocumentController documentController) {
        this.databaseConnection = databaseConnection;
        this.securityController = securityController;
        this.administrationController = administrationController;
        this.documentController = documentController;

        this.serverURL = ConfigurationManager.getValue("dms.telemetry.server.url") != null ?
                ConfigurationManager.getValue("dms.telemetry.server.url") : "";

        this.setMBeanServerConnection(ManagementFactory.getPlatformMBeanServer());

        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        mBeanServers.size();

        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

        try {
            Set<ObjectName> objectNames = mbsc.queryNames(null, null);
            OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            runtimeMXBean.getName();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ContainerType retrieveContainerType() throws IOException {
        ContainerType containerType = null;
        try {
            if (this.getMBeanServerConnection().queryNames(new ObjectName("osgi.core:type=framework,*"),null )
                    .size() == 1) {
                containerType = ContainerType.OSGI;
            } else {
                containerType = ContainerType.SPRING;
            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        return containerType;
    }

    public HashMap<String, String> retrieveOsgiContainerInfo() throws Exception {
        HashMap<String, String> data = new HashMap<>();
        ObjectName karafObjectName = new ObjectName("org.apache.karaf:type=system,name=root");
        if (this.getMBeanServerConnection().queryNames(karafObjectName, null).size() == 1) {
            data = this.retrieveKarafInstanceNameAndVersion();
        }

        return data;
    }

    public HashMap<String, String> retrieveKarafInstanceNameAndVersion() throws Exception {
        HashMap<String, String> data = new HashMap<>();
        data.put("containerName", this.systemService.getName() + " (" + this.systemService.getFramework().name() + ")");
        data.put("containerVersion" , this.systemService.getVersion());

        return data;
    }

    public HashMap<String, String> retrieveServerInstanceNameAndVersion() throws Exception {
        HashMap<String, String> data = new HashMap<>();


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
        DatabaseMetaData metaData = this.databaseConnection.getConnection().getMetaData();

        HashMap<String, String> data = new HashMap<>();
        data.put("databaseProductName", metaData.getDatabaseProductName());
        data.put("databaseProductVersion", metaData.getDatabaseProductVersion());

        return data;
    }

    public HashMap<String, String> retrieveKimiosInfo(Session session) throws Exception {
        HashMap<String, String> data = new HashMap<>();

        data.put("kimiosVersion", ConfigurationManager.getValue("kimios.version") != null ?
                ConfigurationManager.getValue("kimios.version") : "UNKNOWN");

        data.put("kimiosNbUsers", Integer.toString(retrieveKimiosNbUsers(session)));
        data.put("kimiosNbDocs", Integer.toString(retrieveKimiosNbDocs(session)));
        data.put("kimiosDistribution", ConfigurationManager.getValue("kimios.distribution") != null ?
                ConfigurationManager.getValue("kimios.distribution") : "UNKNOWN");

        return data;
    }

    private int retrieveKimiosNbUsers(Session session) {
        List<AuthenticationSource> authenticationSourceList = this.securityController.getAuthenticationSources();
        int nbUsers = -1;
        try {
            for (AuthenticationSource s : authenticationSourceList) {
                nbUsers += this.administrationController.getUsers(session, s.getName()).size();
            }
        } catch (Exception e) {
            logger.error("Exception raised: " + e.getMessage());
        }

        return nbUsers;
    }

    private int retrieveKimiosNbDocs(Session session) {

        int nbDocs = -1;
        try {
            nbDocs = this.documentController.getDocumentsNumber(session);
        } catch (Exception e) {
            logger.error("Exception raised (" + e.getClass().getName() + "): " + e.getMessage());
        }

        return nbDocs;
    }

    public void sendToTelemetryPHP(Session session) throws Exception {
        ContainerType containerType = this.retrieveContainerType();
        HashMap<String, String> data = new HashMap<>();
        switch (containerType) {
            case OSGI:
                data.putAll(this.retrieveOsgiContainerInfo());
                break;
            case SPRING:
                data.putAll(this.retrieveServerInstanceNameAndVersion());
                break;
        }

        data.putAll(this.retrieveJavaInfo());
        data.putAll(this.retrieveDatabaseInfo());
        data.putAll(this.retrieveKimiosInfo(session));

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
                                            .add("engine", data.get("containerName"))
                                            .add("version", data.get("containerVersion"))))
                            .add("kimios", Json.createObjectBuilder()
                                    .add("default_language", "en_GB")
                                    .add("install_mode", data.get("kimiosDistribution"))
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

