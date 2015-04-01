package org.kimios.osgi.karaf;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;

import java.util.List;

/**
 */
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
