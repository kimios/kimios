/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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

package org.kimios.kernel.security.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class CasUtils {


    private static Logger logger = LoggerFactory.getLogger(CasUtils.class);

    private String casServerUrl;

    public CasUtils(){ }

    public CasUtils(String casServerUrl){
        this.casServerUrl = casServerUrl;
    }

    public void setCasServerUrl(String casServerUrl) {
        this.casServerUrl = casServerUrl;
    }

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });
    }


    static HttpsURLConnection openConn(String urlk)  throws  IOException
    {
        URL url = new URL(urlk);
        HttpsURLConnection hsu = (HttpsURLConnection) url.openConnection();
        hsu.setDoInput(true);
        hsu.setDoOutput(true);
        hsu.setRequestMethod("POST");
        return hsu;
    }


    static void closeConn(HttpsURLConnection c)
    {
        c.disconnect();
    }


    public void disconnect(String tgt) throws IOException {
    }


    public boolean validateTicket(String tgt, String serviceURL) throws IOException {

        if(tgt != null)
        {
            logger.debug("Cas TGT: {}", tgt);
            OutputStreamWriter out;
            BufferedWriter bwr;
            String encodedServiceURL = URLEncoder.encode("service","utf-8") +"=" + URLEncoder.encode(serviceURL,"utf-8");
            logger.debug("Service url is : " + encodedServiceURL);
            String myURL = getCasServerUrl() + "/"+ tgt ;
            logger.debug(myURL);
            HttpsURLConnection cnx = openConn(myURL);
            out = new OutputStreamWriter(cnx.getOutputStream());
            bwr = new BufferedWriter(out);
            bwr.write(encodedServiceURL);
            bwr.flush();
            bwr.close();
            out.close();

            logger.debug("Response code is:  " + cnx.getResponseCode());
            BufferedReader isr = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
            String line;
            while ((line = isr.readLine()) != null) {
                logger.debug(line);
            }
            isr.close();
            cnx.disconnect();
            return true;

        }
        else
        {
            return false;
        }



    }


    public String getCasServerUrl(){
        return casServerUrl + "/v1/tickets";
    }

    public HttpsURLConnection getCasConnection() throws IOException {
        return openConn(getCasServerUrl());
    }


    public boolean validateAuthentication(String userName, String password, String serviceURL) throws Exception {

        try
        {
            HttpsURLConnection hsu =  getCasConnection();
            String s =   URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName,"UTF-8");
            s+="&" +URLEncoder.encode("password","UTF-8") + "=" + URLEncoder.encode(password,"UTF-8");
            logger.debug(s);
            OutputStreamWriter out = new OutputStreamWriter(hsu.getOutputStream());
            BufferedWriter bwr = new BufferedWriter(out);
            bwr.write(s);
            bwr.flush();
            bwr.close();
            out.close();

            String tgt = hsu.getHeaderField("location");
            logger.debug(hsu.getResponseCode() + "");
            BufferedReader reader = new BufferedReader(new InputStreamReader(hsu.getInputStream()));
            String l;
            while(( l = reader.readLine()) != null)
                logger.debug(l);

            if(tgt != null && hsu.getResponseCode() == 201){
                logger.debug("Tgt is : " + tgt.substring( tgt.lastIndexOf("/") +1));
                tgt = tgt.substring( tgt.lastIndexOf("/") +1);
                bwr.close();
                closeConn(hsu);
                //return validateTicket(tgt, serviceURL);

                validateTicket(tgt, serviceURL);

                /*
                AttributePrincipal principal = null;
                Cas20ProxyTicketValidator sv = new Cas20ProxyTicketValidator(casServerUrl);
                sv.setAcceptAnyProxy(true);
                String legacyServerServiceUrl = "http://otherserver/legacy/service";
                Assertion a = sv.validate(tgt, legacyServerServiceUrl);
                principal = a.getPrincipal();



                System.out.println("p: " + principal.getName());
                 */


                return true;

            } else {
                return false;
            }

        }
        catch(MalformedURLException mue)
        {
            logger.error("exception while validating CAS auth", mue);
            throw mue;
        }
    }

}
