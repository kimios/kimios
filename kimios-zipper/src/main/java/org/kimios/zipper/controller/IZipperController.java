package org.kimios.zipper.controller;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.kernel.security.model.Session;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IZipperController {
    File makeZipWithEntities(Session session, List<DMEntityImpl> dmEntityList)
            throws ConfigException, IOException;
}
