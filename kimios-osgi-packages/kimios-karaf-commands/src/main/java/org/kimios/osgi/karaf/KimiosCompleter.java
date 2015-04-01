package org.kimios.osgi.karaf;

import java.util.List;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.CommandSessionHolder;
import org.apache.karaf.shell.console.Completer;
import org.kimios.kernel.security.Session;

/**
 */
public abstract class KimiosCompleter implements Completer
{
    protected Session getCurrentSession()
    {
        CommandSession commandSession = CommandSessionHolder.getSession();
        if (commandSession != null) {

            Session session = (Session) commandSession.get(KimiosCommand.KIMIOS_SESSION);
            return session;
        }
        return null;
    }

    public abstract int complete(String buffer, int cursor, List<String> candidates);
}
