/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

package org.kimios.utils.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by farf on 5/1/14.
 */
public class LoggerManager {


    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LoggerManager.class);

    static public Map<String,String> listLoggers(){

        org.slf4j.Logger rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if(rootLogger instanceof Logger){
            LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
            HashMap<String, String> loggersList = new HashMap<String, String>();
            for(Logger l: loggerContext.getLoggerList()){
                loggersList.put(l.getName(), l.getEffectiveLevel().levelStr);
            }


            return loggersList;
        } else {
            logger.info("Root logger isn't logback...");
        }

        return null;

    }

    static public void setLevel(String loggerName, String levelName){

        org.slf4j.Logger logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if(logger instanceof Logger){

            LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
            loggerContext.getLogger(loggerName).setLevel(Level.toLevel(levelName));
        }


    }


}
