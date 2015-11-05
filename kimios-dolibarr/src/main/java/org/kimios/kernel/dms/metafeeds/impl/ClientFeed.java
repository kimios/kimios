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

package org.kimios.kernel.dms.metafeeds.impl;

import org.kimios.kernel.dms.model.MetaFeedImpl;
import org.kimios.kernel.exception.MetaFeedSearchException;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(value = "org.kimios.kernel.dms.metafeeds.impl.ClientFeed")
public class ClientFeed extends MetaFeedImpl {



    @Transient
    private String dbPrefix = "llx_";

    @Transient
    private String dbUser = "root";

    @Transient
    private String dbPassword = "root";

    @Transient
    private String dbDriver = "com.mysql.jdbc.Driver";

    @Transient
    private String dbUrl = "jdbc:mysql://localhost:3306/dolibarr";


    @Transient
    private String orderBy = "code_client";

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ClientFeed.class);



    @Transient
    private Connection cnx;

    private void initCnx() throws ClassNotFoundException, SQLException {
            /*
                Try to load from preference
            */
            for(String key: preferences.keySet()){
                try{
                    Field f = this.getClass().getDeclaredField(key);
                    f.set(this, preferences.get(key));
                    logger.info("Init of " + key + " ==> " + preferences.get(key));
                }   catch (Exception e){
                    logger.error("Error while metafeed preferences init: " + key, e);
                }
            }
            Class.forName(dbDriver);
            cnx = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    private void closeCnx(){
        try{
        if(cnx != null)
            cnx.close();
        }catch (Exception e){
            logger.error("Error while closing Dolibarr cnx", e);
        }
    }

    @Override
    public List<String> getValues() {
        try{
            initCnx();
            PreparedStatement p = cnx.prepareStatement("select * from " + dbPrefix + "societe order by " + orderBy);
            logger.info("Meta feed statement prepared");
            ResultSet resultSet = p.executeQuery();
            List<String> values = new ArrayList<String>();
            while(resultSet.next()){
                values.add(resultSet.getString("code_client") + " (" + resultSet.getString("nom") + ")");
                logger.info("Added " + resultSet.getString("code_client"));
            }


            return values;


        }catch (Exception e){
            logger.error("Dolibarr feed exception", e);
        }
        finally {
            closeCnx();
        }

        return null;
    }

    @Override
    public String[] search(String criteria) throws MetaFeedSearchException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
