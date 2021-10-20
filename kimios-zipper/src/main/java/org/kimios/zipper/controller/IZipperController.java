package org.kimios.zipper.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.security.model.Session;
import org.kimios.kernel.ws.pojo.DMEntityTree;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IZipperController {
    File makeZipWithEntities(Session session, List<Long> dmEntityList)
            throws ConfigException, IOException;

    File makeZipFromEntityTree(Session session, DMEntityTree dmEntityTree)
            throws ConfigException, IOException;

    void markFileDownloaded(File file);
}
