/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.webservices.impl;


import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.kimios.kernel.index.query.model.Criteria;
import org.kimios.webservices.DMServiceException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityType;
import org.kimios.kernel.dms.Folder;
import org.kimios.kernel.dms.Workspace;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.QueryParam;

@WebService(targetNamespace = "http://kimios.org", serviceName = "SearchService", name = "SearchService")
public class SearchServiceImpl extends CoreService implements SearchService
{


    private static Logger log  = LoggerFactory.getLogger(SearchService.class);

    public Document[] quickSearch(String sessionUid, String query, long dmEntityUid, int dmEntityType) throws DMServiceException {
        try {
            Session s = getHelper().getSession(sessionUid);
            DMEntity entity = null;
            switch (dmEntityType) {
                case DMEntityType.WORKSPACE:
                    entity = new Workspace(dmEntityUid, "", "", "", null);
                    break;
                case DMEntityType.FOLDER:
                    entity = new Folder(dmEntityUid, "", "", "", null, -1, -1);
                    break;
            }
            List<Document> v = new Vector<Document>();
            if (entity != null) {
                v = searchController.quickSearchPojos(s, query, entity);
            } else {
                if (dmEntityUid == -1)
                    v = searchController.quickSearchPojos(s, query, null);
            }
            Document[] d = new Document[v.size()];
            for (int i = 0; i < v.size(); i++) {
                d[i] = v.get(i);
                v.set(i, null);
            }
            v = null;
            return d;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    /**
     * @param sessionUid
     * @param xmlStream
     * @return
     * @throws DMServiceException
     */
    public Document[] advancedSearch(String sessionUid, String xmlStream, long dmEntityUid, int dmEntityType) throws DMServiceException {
        try {
            Session s = getHelper().getSession(sessionUid);
            DMEntity entity = null;
            switch (dmEntityType) {
                case DMEntityType.WORKSPACE:
                    entity = new Workspace(dmEntityUid, "", "", "", null);
                    break;
                case DMEntityType.FOLDER:
                    entity = new Folder(dmEntityUid, "", "", "", null, -1, -1);
                    break;
            }
            if (entity != null)
                System.out.println(entity.getUid());
            List<Document> v = searchController.advancedSearchPojos(s, xmlStream, entity);
            Document[] d = new Document[v.size()];
            s = null;
            for (int i = 0; i < v.size(); i++) {
                d[i] = v.get(i);
                v.set(i, null);
            }
            v = null;
            return d;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public org.kimios.kernel.ws.pojo.DMEntity getDMentityFromPath(String sessionUid, String path) throws DMServiceException {
        try {
            Session s = getHelper().getSession(sessionUid);
            org.kimios.kernel.ws.pojo.DMEntity entity = null;
            DMEntity r = pathController.getDMEntityFromPath(s, path);
            entity = new org.kimios.kernel.ws.pojo.DMEntity(r.getUid(), r.getType(),
                    r.getName(), r.getCreationDate(), r.getOwner(), r.getOwnerSource(), r.getPath());
            return entity;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public String getPathFromDMEntity(String sessionUid, long entityUid, int entityType) throws DMServiceException {
        try {
            Session s = getHelper().getSession(sessionUid);
            String r = pathController.getPathFromDMEntity(s, entityUid, entityType);
            return r;
        } catch (Exception e) {
            throw getHelper().convertException(e);
        }
    }

    public String saveSearchQuery(String sessionId,
                                  String name,
                                  List<Criteria> criterias)
    throws DMServiceException {

        try{

            log.info(" >> " + criterias.toString());

            return "WHAT";
        }   catch (Exception e){
            throw getHelper().convertException(e);
        }



    }
}

