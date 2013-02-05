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
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.user.AuthenticationSourceFactory;
import org.kimios.kernel.user.Group;
import org.kimios.kernel.user.User;
import org.kimios.kernel.user.UserFactory;
import org.kimios.kernel.utils.ClientInformationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager extends HFactory implements ISessionManager
{
    private static SessionManager instance;

    protected SessionManager()
    {
    }

    public static synchronized SessionManager getInstance()
    {
        try {
            if (instance == null) {
                instance = new SessionManager();
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected AuthenticationSourceFactory authenticationSourceFactory;

    private AuthenticatedServiceFactory authenticatedServiceFactory;

    public AuthenticationSourceFactory getAuthenticationSourceFactory()
    {
        return authenticationSourceFactory;
    }

    public void setAuthenticationSourceFactory(
            AuthenticationSourceFactory authenticationSourceFactory)
    {
        this.authenticationSourceFactory = authenticationSourceFactory;
    }

    public AuthenticatedServiceFactory getAuthenticatedServiceFactory()
    {
        return authenticatedServiceFactory;
    }

    public void setAuthenticatedServiceFactory(AuthenticatedServiceFactory authenticatedServiceFactory)
    {
        this.authenticatedServiceFactory = authenticatedServiceFactory;
    }

    public Session startSession(String uid, String password, String userSource)
            throws DataSourceException, ConfigException
    {
        UserFactory uf = authenticationSourceFactory.getAuthenticationSource(userSource).getUserFactory();
        if (uf.authenticate(uid, password)) {
            Session s = createSession(uid, userSource);
            s.setMetaDatas(ClientInformationUtil.getInfos());
            return s;
        } else {
            /*
                            Try to authenticate based on session Uid Content for securized Call (like from Bonita, or Portal)
            */

            log.debug("Trying to authenticate with " + uid + "@" + userSource + "  through " + password);
            try {
                String secData[] = password.split("\\|\\|\\|");
                String serviceId = secData[0];
                String serviceKey = secData[1];
                AuthenticatedService authService =
                        authenticatedServiceFactory.loadServiceByIdAndKey(serviceId, serviceKey);

                if (authService != null) {
                    log.debug("Starting session from service " + serviceId + " for " + uid + "@" + userSource);
                    return startSession(uid, userSource);
                } else {
                    log.debug("Auth service not found");
                    return null;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                return null;
            } catch (Exception e) {
                log.error("Error while auth on service", e);
                return null;
            }
        }
    }

    public Session startSession(String uid, String userSource) throws DataSourceException, ConfigException
    {
        UserFactory uf = authenticationSourceFactory.getAuthenticationSource(userSource).getUserFactory();
        if (uf.getUser(uid) != null) {
            Session s = createSession(uid, userSource);
            s.setMetaDatas(ClientInformationUtil.getInfos());
            return s;
        } else {
            return null;
        }
    }

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);

    private final static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<String, Session>();

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
            throw new DataSourceException(he, he.getMessage());
        }
    }

    public Session getSession(String sessionUid) throws DataSourceException, ConfigException
    {
        try {
            Session sUser = sessionUid != null ? (Session) sessions.get(sessionUid) : null;
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

    public void removeSession(String sessionUid) throws DataSourceException, ConfigException
    {
        sessions.remove(sessionUid);
    }

    public void removeSessions(String userName, String userSource) throws DataSourceException, ConfigException
    {
        for (String sessionUid : sessions.keySet()) {
            Session s = sessions.get(sessionUid);
            if (s.getUserName().equals(userName) && s.getUserSource().equals(userSource)) {
                sessions.remove(sessionUid);
            }
        }
    }

    protected String generateSessionUid()
    {
        String[] hash = {
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "I",
                "J",
                "K",
                "L",
                "M",
                "N",
                "O",
                "P",
                "Q",
                "R",
                "S",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z",
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9"
        };
        String sessionUID = "";
        for (int i = 0; i < 20; i++) {
            sessionUID += hash[(int) (Math.random() * (hash.length))];
        }
        return sessionUID;
    }
}

