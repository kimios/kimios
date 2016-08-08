package org.kimios.osgi.karaf;

import java.util.List;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.apache.karaf.shell.api.console.Completer;
import org.kimios.kernel.security.model.Session;

/**
 */
public abstract class KimiosCompleter implements Completer
{
    protected Session getCurrentSession(org.apache.karaf.shell.api.console.Session commandSession)
    {
        if (commandSession != null) {
            Session session = (Session) commandSession.get(KimiosCommand.KIMIOS_SESSION);
            return session;
        }
        return null;
    }

    //public abstract int complete(String buffer, int cursor, List<String> candidates);
}
