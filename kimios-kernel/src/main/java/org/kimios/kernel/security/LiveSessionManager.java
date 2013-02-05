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
package org.kimios.kernel.security;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.HibernateException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.utils.ClientInformationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveSessionManager extends SessionManager
{
    public LiveSessionManager()
    {
        super();
    }

    private static Logger log = LoggerFactory.getLogger(LiveSessionManager.class);

    private final static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<String, Session>();

    @Override
    protected Session createSession(String userName, String userSource) throws DataSourceException, ConfigException
    {
        try {
            String uid = generateSessionUid();
            Session temp = sessions.get(uid);
            while (temp != null) {
                uid = generateSessionUid();
                temp = sessions.get(uid);
            }
            Date startTime = new Date();
            User u = authenticationSourceFactory.getAuthenticationSource(userSource).getUserFactory().getUser(userName);
            Session sUser = new Session(uid, u.getUid(), userSource, startTime, null);
            Vector<Group> grps = new Vector<Group>();
            for (Group g : u.getGroups()) {
                grps.add(g);
            }
            sUser.setGroups(grps);
            sessions.put(sUser.getUid(), sUser);
            return sUser;
        } catch (Exception he) {
            throw new DataSourceException(he);
        }
    }

    @Override
    public Session getSession(String sessionUid) throws DataSourceException, ConfigException
    {
        try {
            Session sUser = (Session) sessions.get(sessionUid);
            if (sUser != null) {
                sUser.setLastUse(new Date());
                sUser.setGroups(
                        authenticationSourceFactory.getAuthenticationSource(sUser.getUserSource()).getGroupFactory()
                                .getGroups(sUser.getUserName()));

                sUser.setMetaDatas(ClientInformationUtil.getInfos());
            }
            return sUser;
        } catch (HibernateException he) {
            throw new DataSourceException(he);
        }
    }

    @Override
    public synchronized void cleanSessionContext(long sessionExpire)
    {
        try {
            Date cleanDate = new Date();
            cleanDate.setTime((new Date().getTime()) - sessionExpire);
            Vector<String> toDelete = new Vector<String>();
            for (Entry<String, Session> s : sessions.entrySet()) {
                Timestamp test = new Timestamp(cleanDate.getTime());
                if (s.getValue().getLastUse().compareTo(test) < 0) {
                    toDelete.add(s.getKey());
                }
            }

            for (String d : toDelete) {
                log.info("Cleaning Session: " + d);
                sessions.remove(d);
            }
        } catch (Exception e) {
            log.error("Error while cleaning session", e);
        }
    }

    @Override
    public void closeSessionContext() throws DataSourceException, ConfigException
    {
        try {
            int nbSessions = 0;
            for (Entry<String, Session> s : sessions.entrySet()) {
                getSession().save(s.getValue());
                nbSessions++;
            }
            log.info("[kimios - SESSION MANAGER Closing:" + nbSessions + " have been saved!");
        } catch (HibernateException he) {
            log.error("Error while closing session manager", he);
            throw new DataSourceException(he);
        }
    }

    @Override
    public void initSessionContext() throws DataSourceException, ConfigException
    {
        try {
            int nbSessions = 0;
            List<Session> lSessions = getSession().createCriteria(Session.class).list();
            for (Session s : lSessions) {
                User u =
                        authenticationSourceFactory.getAuthenticationSource(s.getUserSource()).getUserFactory().getUser(
                                s.getUserName());
                try {
                    u.getGroups();
                    Vector<Group> grps = new Vector<Group>();
                    for (Group g : u.getGroups()) {
                        grps.add(g);
                    }
                    s.setGroups(grps);
                    sessions.put(s.getUid(), s);
                    getSession().delete(s);
                    nbSessions++;
                } catch (NullPointerException e) {
                }
            }
            log.info("[kimios - SESSION MANAGER Initialization:" + nbSessions + " have been re-loaded!");
        } catch (HibernateException he) {
            log.error("Error while loading session manager", he);
            throw new DataSourceException(he);
        }
    }

    @Override
    public Collection<User> getConnectedUsers() throws DataSourceException, ConfigException
    {
        Collection<Session> e = sessions.values();
        Iterator<Session> it = e.iterator();
        Map<String, User> m = new HashMap<String, User>();
        while (it.hasNext()) {
            Session s = it.next();
            User user = new User(s.getUserName(), null, null, null, s.getUserSource());
            m.put(s.getUserName() + "@" + s.getUserSource(), user);
        }
        return m.values();
    }

    @Override
    public List<Session> getSessions() throws DataSourceException, ConfigException
    {
        Collection<Session> e = sessions.values();
        Iterator<Session> i = e.iterator();
        List<Session> list = new ArrayList<Session>();
        while (i.hasNext()) {
            Session s = i.next();
            list.add(s);
        }
        return list;
    }

    @Override
    public List<Session> getSessions(String userName, String userSource) throws DataSourceException, ConfigException
    {
        Collection<Session> e = sessions.values();
        Iterator<Session> i = e.iterator();
        List<Session> list = new ArrayList<Session>();
        while (i.hasNext()) {
            Session s = i.next();
            if (s.getUserName().equals(userName) && s.getUserSource().equals(userSource)) {
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public void removeSession(String sessionUid) throws DataSourceException, ConfigException
    {
        sessions.remove(sessionUid);
    }

    @Override
    public void removeSessions(String userName, String userSource) throws DataSourceException, ConfigException
    {
        for (String sessionUid : sessions.keySet()) {
            Session s = sessions.get(sessionUid);
            if (s.getUserName().equals(userName) && s.getUserSource().equals(userSource)) {
                sessions.remove(sessionUid);
            }
        }
    }
}

