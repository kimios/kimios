package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

/**
 */
@Service
@Command(
        scope = "kimios",
        name = "reindex",
        description = "Launch reindex process")
public class ReindexCommand extends KimiosCommand
{

    @Argument(index = 0, name = "path",
            description = "Kimios Path to reindex",
            required = false, multiValued = false)
    String path = null;

    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        if (this.isConnected()) {
            searchManagementController.reindex(this.getCurrentSession(), this.path);
        }
    }
}
