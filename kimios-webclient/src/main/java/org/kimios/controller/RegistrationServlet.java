package org.kimios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kimios.utils.registration.Registration;
import org.kimios.utils.registration.RegistrationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Date;

public class RegistrationServlet extends HttpServlet {
    //registration.
    private static final String SERVICE_URL = "http://registration.kimios.com/cxf/rest/registration/registerUser";
    private static final String REGISTRED_INSTANCE_FILE_NAME = "REGISTRED_INSTANCE";

    private HttpServletRequest request;
    private HttpServletResponse response;


    private static Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        request = req;
        response = resp;
        boolean result = false;

        if ("check".equals(request.getParameter("action"))) {
            result = check();
        } else if ("register".equals(request.getParameter("action"))) {
            result = register();
        } else if ("unregister".equals(request.getParameter("action"))) {
            result = unregister();
        }

        response.setContentType("application/json");
        response.getWriter().write("{result:[{success:" + result + "}]}");
    }

    /**
     * Check if instance has been already registred.
     * Return true if exists, else false.
     */
    private boolean check() {
        return getFile().exists();
    }

    /**
     * Unregister LOCALLY
     */
    private boolean unregister() {
        return getFile().delete();
    }

    private String streamToString(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int i; (i = in.read(b)) != -1; ) {
            out.append(new String(b, 0, i));
        }
        return out.toString();
    }

    /**
     * Try to register current instance.
     * True if creation success, else false.
     */
    private boolean register() {
        try {
            File file = getFile();
            if (!file.exists()) {
                String rebuiltUrl = SERVICE_URL;
                try {

                    ObjectMapper mapper = new ObjectMapper();
                    RegistrationData data = mapper.readValue(request.getParameter("content"), RegistrationData.class);


                    logger.info("registration data {}", data);

                    //load telemetry uuid file





                    Registration.sendRegistrationRequest(data);



                    return createFile(file);  // true if file created, else false.

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

            } else {
                System.err.println("Cannot register: the named file " + REGISTRED_INSTANCE_FILE_NAME + " already exists!");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private File getFile() {
        ServletContext context = this.getServletContext();
        String realPath = context.getRealPath("/WEB-INF");
        return new File(realPath + "/" + REGISTRED_INSTANCE_FILE_NAME);
    }

    private boolean createFile(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(new Date().toString());
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
