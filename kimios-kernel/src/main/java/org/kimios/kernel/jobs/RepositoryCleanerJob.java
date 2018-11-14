package org.kimios.kernel.jobs;

import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.controller.IDocumentVersionController;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class RepositoryCleanerJob extends JobImpl<Integer> implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RepositoryCleanerJob.class);

    private IDocumentVersionController versionController;

    public RepositoryCleanerJob(IDocumentVersionController versionController) {
        //generate task id
        super( UUID.randomUUID().toString() );

        this.versionController = versionController;
    }


    @Override
    public void run() {
        try {
            this.execute();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public Integer execute() throws Exception {
        List<DocumentVersion> versions = versionController.getOprhansDocumentVersion();
        int deleted = 0;
        for (DocumentVersion v: versions) {
            if(logger.isDebugEnabled()){
                logger.debug("removing version #" + v.getUid() + " -> " + v.getStoragePath());
            }
            new File(ConfigurationManager.getValue(Config.DEFAULT_REPOSITORY_PATH) +
                    v.getStoragePath()).delete();
            versionController.deleteDocumentVersion(v.getUid());
            deleted++;
        }
        return deleted;
    }
}
