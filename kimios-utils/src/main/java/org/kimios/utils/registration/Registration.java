/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.utils.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Registration {


    private static Logger logger = LoggerFactory.getLogger(Registration.class);

    private static final String SERVICE_URL = "http://registration.kimios.com/services/registration/registerUser";

    public static void sendRegistrationRequest(RegistrationData data) throws Exception {
        String rebuiltUrl = SERVICE_URL;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(rebuiltUrl);
        String registrationRequest = new ObjectMapper().writeValueAsString(data);
        StringEntity input = new StringEntity(registrationRequest, "UTF-8");
        logger.debug("sending registration request {}", registrationRequest);
        input.setContentType("application/json");
        postRequest.setEntity(input);
        HttpResponse response = httpClient.execute(postRequest);
        if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode()
                    + EntityUtils.toString(response.getEntity()));
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));


        String e = null;
        String fullResp = "";
        while ((e = br.readLine()) != null) {
            fullResp += e;
        }

        logger.debug("returned reg data {}", fullResp);

        httpClient.getConnectionManager().shutdown();
    }

}
