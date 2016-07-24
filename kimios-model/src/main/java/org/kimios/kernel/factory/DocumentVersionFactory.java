/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.kernel.factory;

import com.github.zafarkhaja.semver.Version;
import org.kimios.api.VersionIncrementor;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.DocumentType;
import org.kimios.kernel.dms.model.DocumentVersion;
import org.kimios.kernel.dms.utils.SemVerIncrementor;
import org.kimios.utils.configuration.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by farf on 22/07/16.
 */
public class DocumentVersionFactory {

    private static Logger logger = LoggerFactory.getLogger(DocumentVersionFactory.class);

    public static DocumentVersion createDocumentVersion(long uid, String author, String authorSource, Date creationDate, Date modificationDate,
                                                        Document document, String previousVersion, long length, DocumentType documentType) {
        //Document factory
        DocumentVersion documentVersion = new DocumentVersion(uid, author, authorSource, creationDate, modificationDate, document.getUid(), length, documentType);
        //check if should automatically create version
        if(ConfigurationManager.getValue(Config.VERSION_AUTOMATIC_INCREMENT) != null
                && ConfigurationManager.getValue(Config.VERSION_AUTOMATIC_INCREMENT).toLowerCase().equals("true")){

            String incrementor = ConfigurationManager.getValue(Config.VERSION_ID_INCREMENTOR) != null
                     ? ConfigurationManager.getValue(Config.VERSION_ID_INCREMENTOR) : SemVerIncrementor.class.getName();
            String version = null;
            logger.debug("try to generata version id automatically with {}", incrementor);
            try{
                Class<VersionIncrementor> versionIncrementor = (Class<VersionIncrementor>)Class.forName(incrementor);
                VersionIncrementor incrementor1 = versionIncrementor.newInstance();
                if(previousVersion == null){
                    version = incrementor1.defaultVersion(null);
                } else {
                    version = incrementor1.nextVersion(previousVersion, null);
                }
                logger.debug("document {}: automatic incrementor generated version {}", document, version);
                documentVersion.setCustomVersion(version);
            }catch (Exception ex){
                logger.error("an error happen during automatic version incr for doc {}, version {}",
                        document, documentVersion, ex);
            }

        }
        return documentVersion;
    }
}
