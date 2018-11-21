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
package org.kimios.kernel.controller.impl;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.AKimiosController;
import org.kimios.kernel.controller.IServerInformationController;
import org.kimios.exceptions.AccessDeniedException;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.security.model.Session;
import org.kimios.utils.configuration.ConfigurationManager;
import org.kimios.utils.registration.Registration;
import org.kimios.utils.registration.RegistrationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

@Transactional
public class ServerInformationController extends AKimiosController implements IServerInformationController
{

    private static Logger logger = LoggerFactory.getLogger(ServerInformationController.class);

    private static String SERVER_VERSION = "Kimios 1.1";


    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.IServerInformationController#getServerVersion()
    */
    public String getServerVersion()
    {
        return SERVER_VERSION;
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.IServerInformationController#getServerOnlineTime(org.kernel.security.Session)
    */
    public Date getServerOnlineTime(Session session) throws DataSourceException, ConfigException, AccessDeniedException
    {
        throw new AccessDeniedException();
    }

    /* (non-Javadoc)
    * @see org.kimios.kernel.controller.IServerInformationController#getServerName()
    */
    public String getServerName() throws ConfigException
    {
        return ConfigurationManager.getValue(Config.SERVER_NAME);
    }


    @Override
    public String getTelemetryUUID() throws ConfigException {
        try {
            return retrieveUuid();
        }catch (Exception ex){
            throw new ConfigException(ex);
        }
    }



    private String computeUuidFileFullPath(String uuidFileName) {
        String fullPath = ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH);
        String fileSeparator = System.getProperty("file.separator");
        if (! fullPath.endsWith(fileSeparator)) {
            fullPath += fileSeparator;
        }
        fullPath += uuidFileName;

        return fullPath;
    }

    private String retrieveUuid() throws IOException {
        String uuid = null;

        String uuidPath = this.computeUuidFileFullPath("uuid");

        FileReader fr = new FileReader(uuidPath);
        fr.read();
        Optional<String> opt = Files.lines(Paths.get(uuidPath)).findFirst();
        if (opt.isPresent()) {
            uuid = opt.get();
        }

        return uuid;
    }

    @Override
    public void register(RegistrationData data) throws ConfigException {
        try {
            Registration.sendRegistrationRequest(data);
            String fullPath = ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH);
            String fileSeparator = System.getProperty("file.separator");
            if (! fullPath.endsWith(fileSeparator)) {
                fullPath += fileSeparator;
            }
            fullPath += "registered_instance";
            try {
                FileWriter writer = new FileWriter(new File(fullPath));
                writer.write(new Date().toString());
                writer.close();
            } catch (IOException e) {
            }
        }catch (Exception ex){
            logger.error("error during instance registration: {}", data, ex);
        }
    }

    @Override
    public boolean isRegistered() throws ConfigException {
        String fullPath = ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH);
        String fileSeparator = System.getProperty("file.separator");
        if (! fullPath.endsWith(fileSeparator)) {
            fullPath += fileSeparator;
        }
        fullPath += "registered_instance";

        return new File(fullPath).exists();
    }
}

