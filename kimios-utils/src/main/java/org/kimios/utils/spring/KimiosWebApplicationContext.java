package org.kimios.utils.spring;

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 9/6/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class KimiosWebApplicationContext extends XmlWebApplicationContext {



    public static String KIMIOS_HOME = "kimios.home";


    private static String KIMIOS_APP_ATTRIBUTE_NAME = "kimios.app.settings";

    @Override
    protected String[] getDefaultConfigLocations() {
        String kimiosHomeDirectory = System.getProperty(KimiosWebApplicationContext.KIMIOS_HOME);
        if (kimiosHomeDirectory != null) {

            /*
                Load servlet context attribute (for client or server)
             */

            String kimiosAppConfDirectory = this.getServletContext().getInitParameter(KIMIOS_APP_ATTRIBUTE_NAME);


            File kimiosHome = new File(kimiosHomeDirectory + "/" + kimiosAppConfDirectory);
            if (kimiosHome.exists() && kimiosHome.isDirectory()) {
                /*
                    Start Spring loading
                 */
                File springConf = new File(kimiosHome, "conf");
                if (springConf.exists()) {
                    return new String[]{kimiosHome.getAbsolutePath() + "/conf/ctx-kimios.xml"};
                }
            }
        }
        throw new RuntimeException("Kimios Home Not found. Please check kimios.home value, and target directory");
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new FileSystemResource(path);
    }
}
