package org.kimios.kernel.security;

import java.util.Collection;
import java.util.List;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.AuthenticationSourceFactory;
import org.kimios.kernel.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA. User: farf Date: 1/2/13 Time: 9:28 PM To change this template use File | Settings | File
 * Templates.
 */
public interface ISessionManager
{
    AuthenticationSourceFactory getAuthenticationSourceFactory();

    void setAuthenticationSourceFactory(
            AuthenticationSourceFactory authenticationSourceFactory);

    AuthenticatedServiceFactory getAuthenticatedServiceFactory();

    void setAuthenticatedServiceFactory(AuthenticatedServiceFactory authenticatedServiceFactory);

    Session startSession(String uid, String password, String userSource)
                    throws DataSourceException, ConfigException;

    Session startSession(String uid, String userSource) throws DataSourceException, ConfigException;

    Session getSession(String sessionUid) throws DataSourceException, ConfigException;

    void cleanSessionContext(long sessionExpire);

    void closeSessionContext() throws DataSourceException, ConfigException;

    void initSessionContext() throws DataSourceException, ConfigException;

    Collection<User> getConnectedUsers() throws DataSourceException, ConfigException;

    List<Session> getSessions() throws DataSourceException, ConfigException;

    List<Session> getSessions(String userName, String userSource) throws DataSourceException, ConfigException;

    void removeSession(String sessionUid) throws DataSourceException, ConfigException;

    void removeSessions(String userName, String userSource) throws DataSourceException, ConfigException;
}
