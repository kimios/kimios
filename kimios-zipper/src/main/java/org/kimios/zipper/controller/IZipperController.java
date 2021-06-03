package org.kimios.zipper.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.security.model.Session;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IZipperController {
    File makeZipWithEntities(Session session, List<Long> dmEntityList)
            throws ConfigException, IOException;
}
