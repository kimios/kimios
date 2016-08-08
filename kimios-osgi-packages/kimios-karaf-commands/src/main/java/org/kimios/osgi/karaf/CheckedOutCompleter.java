package org.kimios.osgi.karaf;

import java.util.List;

import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.dms.model.Document;

/**
 * Created with IntelliJ IDEA. User: farf Date: 1/10/13 Time: 1:15 PM To change this template use File | Settings | File
 * Templates.
 */
@Service
public class CheckedOutCompleter extends KimiosCompleter
{

    @Reference
    IAdministrationController administrationController;

    public int complete(Session session, CommandLine commandLine, List<String> candidates){
        StringsCompleter delegate = new StringsCompleter();
        List<Document> locked = administrationController.getCheckedOutDocuments(this.getCurrentSession(session));
        String lockTpl = "ID: %d\tPath: %s\t\t\tLock Owner: %s";

        if (locked.size() > 0) {
            for (Document doc : locked) {
                delegate.getStrings().add(String.valueOf(doc.getUid()));
            }
        } else {
            delegate.getStrings().add("No document locked");
        }
        return delegate.complete(session, commandLine, candidates);
    }

    public IAdministrationController getAdministrationController()
    {
        return administrationController;
    }

    public void setAdministrationController(IAdministrationController administrationController)
    {
        this.administrationController = administrationController;
    }
}
