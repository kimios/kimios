package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.kimios.api.controller.IServiceWithThreadPoolExecutorManager;
import org.kimios.api.controller.ServiceWithThreadPoolExecutorManagerState;

import java.util.*;
import java.util.stream.IntStream;


/**
 */
@Service
@Command(
        scope = "kimios",
        name = "threads",
        description = "Start / stop threads")
public class ThreadsCommand extends KimiosCommand {

    @Reference
    protected IServiceWithThreadPoolExecutorManager threadsManagerController;

    @Argument(index = 0, name = "action",
            description = "start-all / stop-all / status-all / start <index> / stop <index>",
            required = true, multiValued = false)
    String action = "";

    @Argument(index = 1, name = "serviceIndex",
            description = "service index in list",
            required = false, multiValued = false)
    String serviceIndex = "";

    private static String SERVICE_MAP_NAME = "IManageableServiceController_map";

    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {
            if (this.threadsManagerController != null) {
                switch (action) {
                    case "start-all":
                        threadsManagerController.startAll();
                        break;
                    case "stop-all":
                        threadsManagerController.stopAll();
                        break;
                    case "status-all":
                        Map<Integer, Integer> servicesMap = new HashMap<>();
                        List<Integer> servicesIndexList = new ArrayList<>();
                        List<String> displayList = new ArrayList<>();
                        Map<Integer, AbstractMap.SimpleEntry<String, ServiceWithThreadPoolExecutorManagerState>> statusesMap =
                                this.threadsManagerController.statusAll();

                        statusesMap.keySet().forEach(hashCode -> {
                            AbstractMap.SimpleEntry<String, ServiceWithThreadPoolExecutorManagerState> entry = statusesMap.get(hashCode);
                            String controllerName = entry.getKey();
                            String status = entry.getValue().getValue();
                            servicesIndexList.add(hashCode);
                            displayList.add(controllerName + " status is " + status);
                        });

                        IntStream.range(0, displayList.size()).forEach(i -> {
                            System.out.println(i + " : " + displayList.get(i));
                            servicesMap.put(i, servicesIndexList.get(i));
                        });
                        this.karafSession.put(SERVICE_MAP_NAME, servicesMap);
                        break;
                    case "start":
                        if (this.karafSession.get(SERVICE_MAP_NAME) == null) {
                            this.action = "status-all";
                            doExecuteKimiosCommand();
                        } else {
                            if (serviceIndex.isEmpty()) {
                                String usage = "start <SERVICE_INDEX>";
                                this.karafSession.getConsole().println(usage);
                            } else {
                                try {
                                    Map<Integer, Integer> servicesMapInSession =
                                            (Map<Integer, Integer>) this.karafSession.get(SERVICE_MAP_NAME);
                                    this.threadsManagerController.startServiceThreadPoolExecutor(
                                            servicesMapInSession.get(Integer.parseInt(serviceIndex)));
                                } catch (Exception e) {
                                    this.karafSession.getConsole().println("this service is not available any more");
                                }
                            }
                        }
                        break;
                    case "stop":
                        if (this.karafSession.get(SERVICE_MAP_NAME) == null) {
                            this.action = "status-all";
                            doExecuteKimiosCommand();
                        } else {
                            if (serviceIndex.isEmpty()) {
                                String usage = "stop <SERVICE_INDEX>";
                                this.karafSession.getConsole().println(usage);
                            } else {
                                try {
                                    Map<Integer, Integer> servicesMapInSession =
                                            (Map<Integer, Integer>) this.karafSession.get(SERVICE_MAP_NAME);
                                    this.threadsManagerController.stopServiceThreadPoolExecutor(
                                            servicesMapInSession.get(Integer.parseInt(serviceIndex)));
                                } catch (Exception e) {
                                    this.karafSession.getConsole().println("this service is not available any more");
                                }
                            }
                        }
                        break;
                    default:
                        System.out.println("action must be 'start-all', 'stop-all', 'status-all', 'start', 'stop'");
                }
            } else {
                System.out.println("");
            }
        }
    }

    public IServiceWithThreadPoolExecutorManager getThreadsManagerController() {
        return threadsManagerController;
    }

    public void setThreadsManagerController(IServiceWithThreadPoolExecutorManager threadsManagerController) {
        this.threadsManagerController = threadsManagerController;
    }
}
