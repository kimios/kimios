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

package org.kimios.webservices.utils;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by farf on 4/30/14.
 */
public class KimiosBusServiceManager extends AbstractFeature {

    private static Logger logger = LoggerFactory.getLogger(KimiosBusServiceManager.class);

    private Bus bus;


    LoggingInInterceptor loggingInInterceptor = null;
    LoggingOutInterceptor loggingOutInterceptor = null;

    private static final int DEFAULT_LIMIT = 64 * 1024;



    public KimiosBusServiceManager() {
        super();
    }

    @Override
    public void initialize(Bus bus) {
        super.initialize(bus);
        this.bus = bus;
        logger.info("initializing Kimios CXF Bus Manager [bus: {}]", bus);


    }

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);
    }


    public void disableLogging(){

        for(Interceptor i: bus.getInInterceptors()){
            logger.info("checking in interceptor {}", i);
            if(i instanceof LoggingInInterceptor){
                loggingInInterceptor = (LoggingInInterceptor)i;
            }
        }

        for(Interceptor i: bus.getOutInterceptors()){
            logger.info("checking out interceptor {}", i);
            if(i instanceof LoggingOutInterceptor){
                loggingOutInterceptor = (LoggingOutInterceptor)i;
            }
        }

        if(loggingInInterceptor != null){
            logger.info("removing in interceptor");
            bus.getInInterceptors().remove(loggingInInterceptor);
        }
        if(loggingOutInterceptor != null){
            logger.info("removing out interceptor");
            bus.getOutInterceptors().remove(loggingOutInterceptor);
        }
    }

    public void enableLogging(){




        for(Interceptor i: bus.getInInterceptors()){
            logger.info("checking in interceptor {}", i);
            if(i instanceof LoggingInInterceptor){
                loggingInInterceptor = (LoggingInInterceptor)i;
            }
        }

        for(Interceptor i: bus.getOutInterceptors()){
            logger.info("checking out interceptor {}", i);
            if(i instanceof LoggingOutInterceptor){
                loggingOutInterceptor = (LoggingOutInterceptor)i;
            }
        }


        if(loggingInInterceptor == null){
            loggingInInterceptor = new LoggingInInterceptor(DEFAULT_LIMIT);
        }
        if(loggingOutInterceptor == null){
            loggingOutInterceptor = new LoggingOutInterceptor(DEFAULT_LIMIT);
        }

        if(loggingInInterceptor != null && !bus.getInInterceptors().contains(loggingInInterceptor)){
            bus.getInInterceptors().add(loggingInInterceptor);
            logger.info("readded log out interceptor");
        }

        if(loggingOutInterceptor != null && !bus.getOutInterceptors().contains(loggingOutInterceptor)){
            bus.getOutInterceptors().add(loggingOutInterceptor);
            logger.info("readded log out interceptor");
        }
    }
}
