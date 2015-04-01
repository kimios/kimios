package org.kimios.osgi.karaf;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.kimios.kernel.index.ReindexerOsgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 */
@Command(
        scope = "kimios",
        name = "reindexer",
        description = "Launch reindex process")
public class ReindexOsgiCommand extends KimiosCommand {


    @Option(name = "-b",
            aliases = "--blocksize",
            description = "Index n document by n document",
            required = false, multiValued = false)
    Integer blockSize = null;

    @Argument(index = 0, name = "path",
            description = "Kimios Path to reindex",
            required = false, multiValued = true)
    String[] paths = null;

    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {


            int block = blockSize != null && blockSize > 0
                    ? blockSize : 20;


            ExecutorService executor = Executors.newFixedThreadPool(8);


            /*ThreadFactory threadFactory = Executors.defaultThreadFactory();
            //creating the ThreadPoolExecutor
            ThreadPoolExecutor executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory);*/


            List<Future<ReindexerOsgi.ReindexResult>> list = new ArrayList<Future<ReindexerOsgi.ReindexResult>>();
            Map<String, ReindexerOsgi> items = new HashMap<String, ReindexerOsgi>();
            for (String u : paths) {
                ReindexerOsgi osgiReindexer = new ReindexerOsgi(
                        indexManager,
                        pathController,
                        u,
                        block
                );
                Future<ReindexerOsgi.ReindexResult> future = executor.submit(osgiReindexer);
                list.add(future);
                items.put(u, osgiReindexer);
            }
            executor.shutdown();

            Thread.sleep(2000);
            while(!executor.isTerminated()){
                //display log about indexing
                Thread.sleep(1000);
                for(String path: items.keySet()){
                    log.info("Indexing "
                            + path
                            + " : " + items.get(path).getReindexProgression() + " %. "
                            + ". Indexed "
                            + items.get(path).getReindexResult().getReindexedCount() + " on "
                            +  items.get(path).getReindexResult().getEntitiesCount());
                }
            }

            for(Future<ReindexerOsgi.ReindexResult> e: list){
                log.info("Ended: " + e.get());
            }

        }
    }
}
