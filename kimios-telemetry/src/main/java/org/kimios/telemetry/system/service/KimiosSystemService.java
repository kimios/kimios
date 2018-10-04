package org.kimios.telemetry.system.service;

import javax.management.*;
import java.io.IOException;

public interface KimiosSystemService {

    public static String DATA_UNKNOWN = "UNKNOWN";

    public String getName() throws MalformedObjectNameException, IOException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException;
    public String getVersion() throws MalformedObjectNameException, IOException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException;
    public String getFrameworkName();
    public String getKimiosDistribution();
}
