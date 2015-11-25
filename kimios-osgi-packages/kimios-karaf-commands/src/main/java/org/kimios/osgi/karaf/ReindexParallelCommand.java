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


    @Option(name = "-i",
            aliases = "--ids",
            description = "Index n document by n document",
            required = false, multiValued = true)
    Long[] ids = null;

    @Option(name = "-b",
            aliases = "--blocksize",
            description = "Index n document by n document",
            required = false, multiValued = false)
    Integer blockSize = null;

    @Option(name = "-x",
            description = "--exclude-id",
            required = false, multiValued = true)
    Long[] idsExcluded = null;

    @Option(name = "-e",
            description = "--exclude-extension",
            required = false, multiValued = true)
    String[] extensionExcluded = null;

    @Option(name = "-t",
            description = "--read-timeout",
            required = false, multiValued = false)
    Long readTimeOut = 30L;

    @Option(name = "-g",
            description = "--regenerate-metas-wrapper",
            required = false, multiValued = false)
    boolean regenerateMetaWrapper = false;

    @Option(name = "-nt",
            description = "--no-threading",
            required = false, multiValued = false)
    boolean disableThreading = true;

    @Option(name = "-s",
            description = "--read-timeout-unit",
            required = false, multiValued = false)
    String readTimeOutUnit = "SECONDS";


    @Option(name = "-tps",
            aliases = "--thread-pool-size",
            description = "Thread Pool Size",
            required = false, multiValued = false)
    Integer threadPoolSize = null;

    @Option(name = "-et",
            aliases = "--entity-type",
            description = "Entity Type (Document=3/Folder=2)",
            required = false, multiValued = false)
    Integer entityType = 3;


    @Argument(index = 0, name = "path",
            description = "Kimios Path to reindex",
            required = false, multiValued = true)
    String[] paths = null;


    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {
            int block = blockSize != null && blockSize > 0
                    ? blockSize : 20;

            if(paths != null && paths.length > 0){
                searchManagementController.parallelReindex(
                        this.getCurrentSession(),
                        Arrays.asList(paths),
                        idsExcluded != null ? Arrays.asList(idsExcluded) : new ArrayList<Long>(),
                        extensionExcluded != null ? Arrays.asList(extensionExcluded) : new ArrayList<String>(),
                        block,
                        readTimeOut,
                        readTimeOutUnit != null ? TimeUnit.valueOf(readTimeOutUnit) : null,
                        threadPoolSize != null ? threadPoolSize : 5,
                        regenerateMetaWrapper,
                        disableThreading,
                        entityType
                );
            } else if(ids != null && ids.length > 0){
                searchManagementController.parallelReindex(
                        this.getCurrentSession(),
                        Arrays.asList(ids),
                        block,
                        readTimeOut,
                        readTimeOutUnit != null ? TimeUnit.valueOf(readTimeOutUnit) : null,
                        threadPoolSize != null ? threadPoolSize : 5,
                        regenerateMetaWrapper,
                        disableThreading,
                        entityType
                );
            }

        }
    }
}
