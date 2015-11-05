package org.kimios.osgi.karaf;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.kimios.kernel.security.model.Session;

/**
 */
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
        String user = login.split("@")[0];
        String domain = login.split("@")[1];
        Session kimiosSession = securityController.startSession(user, domain, password);
        this.session.put(KIMIOS_SESSION, kimiosSession);
    }
}
