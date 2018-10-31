package org.kimios.telemetry.controller;

import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.kimios.telemetry.system.service.KimiosSystemService;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.utils.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class TelemetryController extends AKimiosController implements ITelemetryController {

    public enum ContainerType {
        SPRING("spring"), OSGI("osgi");

        private final String value;

        ContainerType(String type) {
            this.value = type;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(TelemetryController.class);

    private KimiosSystemService kimiosSystemService;
    private DataSource databaseConnection;
    private ISecurityController securityController;
    private IAdministrationController administrationController;
    private IDocumentController documentController;

    private String serverURL;
    private MBeanServerConnection mBeanServerConnection;
    private String uuid;
    private String uuidFile = "uuid";
    private String uuidFilePath;

    public KimiosSystemService getKimiosSystemService() {
        return kimiosSystemService;
    }

    public void setKimiosSystemService(KimiosSystemService systemService) {
        this.kimiosSystemService = systemService;
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

    private void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getUuidFilePath() {
        return uuidFilePath;
    }

    private void setUuidFilePath(String uuidFilePath) {
        this.uuidFilePath = uuidFilePath;
    }

    public TelemetryController(){
        this.serverURL = ConfigurationManager.getValue("dms.telemetry.server.url") != null ?
                ConfigurationManager.getValue("dms.telemetry.server.url") : "";


        logger.info("telemetry url: {}", serverURL);

        this.setMBeanServerConnection(ManagementFactory.getPlatformMBeanServer());

        this.setUuidFilePath(this.computeUuidFileFullPath(this.uuidFile));
        String retrievedUuid = null;
        try {
            retrievedUuid = this.retrieveUuid();
        } catch (IOException e) {
            logger.warn("error while retrieving uuid…");
            logger.warn(e.getMessage());
        }
        try {
            if (retrievedUuid == null) {
                this.generateAndSaveUuid();
                retrievedUuid = this.retrieveUuid();
            }
            this.setUuid(retrievedUuid);
        } catch (IOException e) {
            logger.error("problem while generating and retrieving uuid…");
            logger.error(e.getMessage());
        }
    }

    public TelemetryController(
            KimiosSystemService systemService,
            DataSource databaseConnection,
            ISecurityController securityController,
            IAdministrationController administrationController,
            IDocumentController documentController
    ){
        this();

        this.kimiosSystemService = systemService;
        this.databaseConnection = databaseConnection;
        this.securityController = securityController;
        this.administrationController = administrationController;
        this.documentController = documentController;
    }

    public ContainerType retrieveContainerType() throws IOException {
        ContainerType containerType = null;
        try {
            if (this.getMBeanServerConnection() != null
                    && this.getMBeanServerConnection().queryNames(new ObjectName("osgi.core:type=framework,*"),null) != null
                    && this.getMBeanServerConnection().queryNames(new ObjectName("osgi.core:type=framework,*"),null).size() == 1) {
                containerType = ContainerType.OSGI;
            } else {
                containerType = ContainerType.SPRING;
            }
        } catch (MalformedObjectNameException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return containerType;
    }

    public HashMap<String, String> retrieveOsgiContainerInfo() throws Exception {
        HashMap<String, String> data = new HashMap<>();
        ObjectName karafObjectName = new ObjectName("org.apache.karaf:type=system,name=root");
        if (this.getMBeanServerConnection().queryNames(karafObjectName, null).size() == 1) {
            data = this.retrieveInstanceNameAndVersion();
        }

        return data;
    }

    public HashMap<String, String> retrieveInstanceNameAndVersion() throws Exception {
        HashMap<String, String> data = new HashMap<>();
        data.put("containerName", this.kimiosSystemService.getName() + " (" + this.kimiosSystemService.getFrameworkName() + ")");
        data.put("containerVersion" , this.kimiosSystemService.getVersion());

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

        data.put("kimiosVersion", Version.KIMIOS_VERSION);

        int nbUsers = retrieveKimiosNbUsers(session);
        int nbDocs = retrieveKimiosNbDocs(session);
        data.put("kimiosNbUsers", Integer.toString(nbUsers));
        data.put("kimiosNbDocs", Integer.toString(nbDocs));
        data.put("kimiosNbDocsPerUser", Integer.toString(this.calculateKimiosNbDocsPerUser(nbUsers, nbDocs)));
        data.put("kimiosDistribution", this.kimiosSystemService.getKimiosDistribution());

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

    private int calculateKimiosNbDocsPerUser(int nbUsers, int nbDocs) {
        return nbUsers == 0 ? 0 : nbDocs / nbUsers;
    }

    public void sendToTelemetryPHP(Session session) throws Exception {

        HashMap<String, String> data = new HashMap<>();
        data.putAll(this.retrieveInstanceNameAndVersion());
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
                        + httpResponse.getStatusLine().getStatusCode()
                        + EntityUtils.toString(httpResponse.getEntity()));
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
                                            .add("engine", data.get("containerName") != null ? data.get("containerName") : "UNKNOWN")
                                            .add("version", data.get("containerVersion") != null ? data.get("containerVersion") : "UNKNOWN")))
                            .add("kimios", Json.createObjectBuilder()
                                    .add("default_language", "en_GB")
                                    .add("install_mode", data.get("kimiosDistribution"))
                                    .add("plugins", Json.createArrayBuilder())
                                    .add("usage", Json.createObjectBuilder()
                                            .add("num_users", new Integer(data.get("kimiosNbUsers")))
                                            .add("num_documents", new Integer(data.get("kimiosNbDocs")))
                                            .add("num_documents_per_user", new Integer(data.get("kimiosNbDocsPerUser")))
/*                                            .add("avg_changes", "")
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
                                            .add("notifications_modes", Json.createArrayBuilder())*/
                                    )
                                    .add("uuid", this.getUuid())
                                    .add("version", data.get("kimiosVersion")))
                    );
            object = objectBuilder.build();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return object;
    }

    public void generateAndSaveUuid() throws IOException {
        String uuidStr = UUID.randomUUID().toString();

        FileWriter fw = new FileWriter(this.uuidFilePath);
        fw.write(uuidStr);
        fw.flush();

    }

    public String computeUuidFileFullPath(String uuidFileName) {
        String fullPath = ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH);
        String fileSeparator = System.getProperty("file.separator");
        if (! fullPath.endsWith(fileSeparator)) {
            fullPath += fileSeparator;
        }
        fullPath += uuidFileName;

        return fullPath;
    }

    public String retrieveUuid() throws IOException {
        String uuid = null;

        FileReader fr = new FileReader(this.uuidFilePath);
        fr.read();
        Optional<String> opt = Files.lines(Paths.get(this.uuidFilePath)).findFirst();
        if (opt.isPresent()) {
            uuid = opt.get();
        }

        return uuid;
    }
}

