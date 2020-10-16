package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.kimios.kernel.security.model.Session;

/**
 */
@Service
@Command(description = "Kimios Administration Command", name = "admin", scope = "kimios")
public class AdminCommand extends KimiosCommand
{
    @Option(name = "-l",
            aliases = "--login",
            description = "Kimios Admin Login: <login>@<domain>",
            required = true, multiValued = false)
    String login = null;

    @Option(name = "-p",
            aliases = "--password",
            description = "Kimios Admin Password",
            required = true, multiValued = false)
    String password = null;

    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        String[] loginArg = login.split("@");
        if (loginArg.length != 2) {
            this.karafSession.getConsole().println("login must be <USERID>@<SOURCE>");
            return;
        }
        String user = loginArg[0];
        String domain = loginArg[1];
        Session kimiosSession = null;
        try {
            kimiosSession = securityController.startSession(user, domain, password);
        } catch (Exception e) {
            this.karafSession.getConsole().println("login failed");
        }
        if (kimiosSession == null) {
            this.karafSession.getConsole().println("login failed");
        } else {
            this.karafSession.getConsole().println("login ok");
            this.karafSession.put(KIMIOS_SESSION, kimiosSession);
        }
    }
}
