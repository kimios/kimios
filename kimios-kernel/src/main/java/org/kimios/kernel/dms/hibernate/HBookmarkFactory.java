/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
package org.kimios.kernel.dms.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;

import java.util.List;
import java.util.Vector;

public class HBookmarkFactory extends HFactory implements BookmarkFactory
{
    public void addBookmark(String userName, String userSource,
            long dmentityUid, int dmentityType) throws ConfigException,
            DataSourceException
    {
        try {
            getSession().save(new Bookmark(userName, userSource, dmentityUid, dmentityType));
            getSession().flush();
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }

    public Vector<DMEntity> getBookmarks(String userName, String userSource)
            throws ConfigException, DataSourceException
    {
        try {
            FactoryInstantiator fc = FactoryInstantiator.getInstance();
            Vector<DMEntity> bookmarks = new Vector<DMEntity>();
            List<Bookmark> lBookmarks = (List<Bookmark>) getSession().createCriteria(Bookmark.class)
                    .add(Restrictions.eq("owner", userName))
                    .add(Restrictions.eq("ownerSource", userSource))
                    .list();
            DMEntity t = null;
            for (Bookmark b : lBookmarks) {
                switch (b.getType()) {
                    case DMEntityType.WORKSPACE:
                        t = fc.getWorkspaceFactory().getWorkspace(b.getUid());
                        break;
                    case DMEntityType.FOLDER:
                        t = fc.getFolderFactory().getFolder(b.getUid());
                        break;
                    case DMEntityType.DOCUMENT:
                        t = fc.getDocumentFactory().getDocument(b.getUid());
                        break;
                }
                if (t != null) {
                    bookmarks.add(t);
                } else {
                    removeBookmark(userName, userSource, b.getUid(), b.getType());
                }
            }
            return bookmarks;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void removeBookmark(String userName, String userSource,
            long dmentityUid, int dmentityType) throws ConfigException,
            DataSourceException
    {
        try {
            Bookmark b = new Bookmark(userName, userSource, dmentityUid, dmentityType);
            try {
                b = (Bookmark) getSession().merge(b);
            } catch (Exception e) {
            }
            getSession().delete(b);
            getSession().flush();
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

