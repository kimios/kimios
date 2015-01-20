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
package org.kimios.kernel.security;

import org.hibernate.HibernateException;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.user.*;
import org.kimios.kernel.utils.ClientInformationUtil;
import org.kimios.utils.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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

        AuthenticationSource authenticationSource = null;
        UserFactory uf = null;
        try{
            authenticationSource = authenticationSourceFactory.getAuthenticationSource(userSource);
           uf = authenticationSource.getUserFactory();
        } catch (NullPointerException nullException){
            log.error(userSource + " user source doesn't exist");
            return null;
        }
        User authenticatingUser = null;
        if(authenticationSource.getEnableAuthByEmail() && authenticationSource.getEnableAuthByEmail()){
            authenticatingUser = uf.getUserByEmail(uid);
        }
        if(authenticatingUser == null){
            authenticatingUser = uf.getUser(uid);
        }
        if(authenticatingUser != null){
            if (uf.authenticate(authenticatingUser.getUid(), password)) {
                Session s = createSession(authenticatingUser.getUid(), userSource);
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

        }  else
            return  null;

    }

    public Session startSession(String uid, String userSource) throws DataSourceException, ConfigException
    {
        AuthenticationSource authenticationSource = authenticationSourceFactory.getAuthenticationSource(userSource);
        UserFactory uf = authenticationSourceFactory.getAuthenticationSource(userSource).getUserFactory();
        User userAuthenticating = null;
        if(authenticationSource.getEnableAuthByEmail()){
            userAuthenticating = uf.getUserByEmail(uid);
        }
        if(userAuthenticating == null){
            userAuthenticating = uf.getUser(uid);
        }
        if (userAuthenticating != null) {
            Session s = createSession(userAuthenticating.getUid(), userSource);
            s.setMetaDatas(ClientInformationUtil.getInfos());
            return s;
        } else {
            return null;
        }
    }


    public Session startSession(String externalToken) throws DataSourceException, ConfigException
    {
        /* Load Authenticator */
        Map<String, Authenticator> authenticators = ApplicationContextProvider.loadBeans(Authenticator.class);
        Session session = null;
        for(Authenticator authenticator: authenticators.values()){
            log.info("Attempt to log on " + authenticator.getClass().getMethods() + " authenticator");
            try{
                String userName = authenticator.authenticate(externalToken);
                if(userName != null){
                    /*
                        Start Session
                     */
                        for(AuthenticationSource as: authenticationSourceFactory.getAuthenticationSources()){
                            if(as.getEnableSSOCheck() != null && as.getEnableSSOCheck()){
                                User user = as.getUserFactory().getUser(userName);
                                log.info("Found user on " + as.getName() + " authentication source.");
                                session =  startSession(user.getUid(), user.getAuthenticationSourceName());
                                break;
                            }
                        }
                    }
                    break;
            }   catch (Exception e){
                log.error("Login attempt failed");
                return null;
            }
        }
        log.info("Session Manager External Token Session : " + session);
        return session;
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
            he.printStackTrace();
            throw new DataSourceException(he, he.getMessage());
        }
    }

    @Transactional
    public Session getSession(String sessionUid) throws DataSourceException, ConfigException
    {
        try {
            log.debug("getting session with id {}", sessionUid);
            Session sUser = sessionUid != null ? (Session) sessions.get(sessionUid) : null;
            if (sUser != null) {
                log.debug("session found {} {}", sUser, sUser.getUserName());
                sUser.setLastUse(new Date());
                sUser.setGroups(
                        authenticationSourceFactory.getAuthenticationSource(sUser.getUserSource()).getGroupFactory()
                                .getGroups(sUser.getUserName()));

                sUser.setMetaDatas(ClientInformationUtil.getInfos());
            }
            return sUser;
        } catch (HibernateException he) {
            log.error("error while loading session", he);
            throw new DataSourceException(he);
        }
    }

    @Transactional
    @DmsEvent(eventName = DmsEventName.SESSION_STOP, when = DmsEventOccur.AFTER)
    public synchronized void cleanSessionContext(long sessionExpire)
    {
        try {
            Date cleanDate = new Date();
            cleanDate.setTime((new Date().getTime()) - sessionExpire);
            List<Session> sessionList = new ArrayList<Session>();
            for (Entry<String, Session> s : sessions.entrySet()) {
                Timestamp test = new Timestamp(cleanDate.getTime());
                if (s.getValue().getLastUse().compareTo(test) < 0) {
                    sessionList.add(s.getValue());
                }
            }

            for (Session d : sessionList) {
                log.debug("Cleaning Session: " + d.getUid());
                sessions.remove(d.getUid());
            }
            EventContext.getParameters().put("sessions", sessionList);
        } catch (Exception e) {
            log.error("Error while cleaning session", e);
        }
    }

    @Transactional
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

    @Transactional
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
            User user = new User(s.getUserName(), s.getUserSource());
            m.put(s.getUserName() + "@" + s.getUserSource(), user);
        }
        return m.values();
    }

    @Transactional
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

    @Transactional
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


    @DmsEvent(eventName = DmsEventName.SESSION_STOP, when = DmsEventOccur.AFTER)
    public void removeSession(String sessionUid) throws DataSourceException, ConfigException
    {
        Session s = sessions.get(sessionUid);
        sessions.remove(sessionUid);
        List<Session> items = new ArrayList<Session>();
        items.add(s);
        EventContext.getParameters().put("sessions", items);
    }


    @DmsEvent(eventName = DmsEventName.SESSION_STOP, when = DmsEventOccur.AFTER)
    public void removeSessions(String userName, String userSource) throws DataSourceException, ConfigException
    {

        List<Session> items = new ArrayList<Session>();
        for (String sessionUid : sessions.keySet()) {
            Session s = sessions.get(sessionUid);
            if (s.getUserName().equals(userName) && s.getUserSource().equals(userSource)) {
                items.add(s);
                sessions.remove(sessionUid);
            }
        }
        EventContext.getParameters().put("sessions", items);
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

