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

package org.kimios.deployer.core;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.List;

public class Restarter {
    public static void restartApplication(Runnable runBeforeRestart) throws IOException {
        try {
            // java binary
            String java = System.getProperty("java.home") + "/bin/java";
            // vm arguments
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuffer vmArgsOneLine = new StringBuffer();
            for (String arg : vmArguments) {
                // if it's the agent argument : we ignore it otherwise the
                // address of the old application and the new one will be in conflict
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
            // init the command to execute, add the vm args
            final StringBuffer cmd = new StringBuffer(java + " " + vmArgsOneLine);
            // program main and program arguments (be careful a sun property. might not be supported by all JVM)
            String[] mainCommand = System.getProperty("sun.java.command").split(" ");
            // program main is a jar
            if (mainCommand[0].endsWith(".jar")) {
                // if it's a jar, add -jar mainJar
                cmd.append("-jar " + new File(mainCommand[0]).getPath());
            } else {
                // else it's a .class, add the classpath and mainClass
                cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
            }
            // finally add program arguments
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }
            // execute the command in a shutdown hook, to be sure that all the
            // resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("running " + cmd.toString());
                        Process p = Runtime.getRuntime().exec(cmd.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            // execute some custom code before restarting
            if (runBeforeRestart != null) {
                runBeforeRestart.run();
            }
            // exit
            System.exit(0);
        } catch (Exception e) {
            // something went wrong
            throw new IOException("Error while trying to restart the application", e);
        }
    }
}
