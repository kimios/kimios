package org.kimios.kernel.security;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 10/10/13
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Authenticator {


    public boolean authenticate(String user, String password, Map<String, Object> additionnalParameters) throws Exception;

    public String authenticate(String externalToken) throws Exception;

    public boolean disconnect(String token) throws Exception;
}
