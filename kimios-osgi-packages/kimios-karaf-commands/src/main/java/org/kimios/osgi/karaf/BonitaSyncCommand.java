package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;


/**
 */
@Service
@Command(
        scope = "kimios",
        name = "bonita-sync-users",
        description = "Launch Users Synchronisation Process on Bonita")
public class BonitaSyncCommand extends KimiosCommand {



    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {

                bonitaUsersSynchronizer.synchronize();

        }
    }
}
