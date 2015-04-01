package org.kimios.osgi.karaf;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;

/**
 * Created with IntelliJ IDEA. User: farf Date: 1/10/13 Time: 4:07 PM To change this template use File | Settings | File
 * Templates.
 */
@Command(name = "end", description = "Close Kimios Admin Session", scope = "kimios")
public class EndCommand extends KimiosCommand
{
    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        this.session.put(KIMIOS_SESSION, null);
    }
}
