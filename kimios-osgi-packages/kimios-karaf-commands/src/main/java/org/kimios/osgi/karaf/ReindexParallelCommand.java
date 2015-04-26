package org.kimios.osgi.karaf;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.kimios.kernel.index.ReindexerProcess;

import java.util.*;
import java.util.concurrent.*;


/**
 */
@Command(
        scope = "kimios",
        name = "parallel-reindexer",
        description = "Launch multiple reindex process")
public class ReindexParallelCommand extends KimiosCommand {


    @Option(name = "-b",
            aliases = "--blocksize",
            description = "Index n document by n document",
            required = false, multiValued = false)
    Integer blockSize = null;

    @Option(name = "-x",
            description = "--exclude",
            required = false, multiValued = true)
    Long[] idsExcluded = null;

    @Argument(index = 0, name = "path",
            description = "Kimios Path to reindex",
            required = false, multiValued = true)
    String[] paths = null;



    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {
            int block = blockSize != null && blockSize > 0
                    ? blockSize : 20;
            searchManagementController.parallelReindex(
                    this.getCurrentSession(),
                    Arrays.asList(paths),
                    idsExcluded != null ? Arrays.asList(idsExcluded) : new ArrayList<Long>(),
                    block
            );
        }
    }
}
