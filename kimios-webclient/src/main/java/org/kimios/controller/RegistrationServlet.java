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
        }

        response.setContentType("application/json");
        response.getWriter().write("{result:[{success:" + result + "}]}");
    }

    /**
     * Check if instance has been already registred.
     * Return true if exists, else false.
     */
    private boolean check() {
        try {
            return Controller.getServerInformationController().isRegistered();
        } catch (Exception ex) {
            return true;
        }
    }


    /**
     * Try to register current instance.
     * True if creation success, else false.
     */
    private boolean register() {
        try {
            try {

                ObjectMapper mapper = new ObjectMapper();
                RegistrationData data = mapper.readValue(request.getParameter("content"), RegistrationData.class);

                //get uuid

                try {
                    String uuid = Controller.getServerInformationController().getTelemetryUUID();
                    logger.info("server telemetry uuid {}", uuid);
                    data.setTelemetryUuid(uuid);
                } catch (Exception ex) {
                    logger.error("error while getting uuid", ex);
                }
                logger.info("registration data {}", data);

                Controller.getServerInformationController().register(data);
                return true;
            } catch (Exception ex) {
                logger.error("reg error: ", ex);
                return false;
            }

        } catch (Exception e) {
            logger.error("error while registering instance", e);
            return false;
        }
    }
}
