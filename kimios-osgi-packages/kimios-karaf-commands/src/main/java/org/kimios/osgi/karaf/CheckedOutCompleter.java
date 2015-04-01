package org.kimios.osgi.karaf;

import java.util.List;

import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.kimios.kernel.controller.IAdministrationController;
import org.kimios.kernel.controller.ISecurityController;
import org.kimios.kernel.dms.Document;

/**
 * Created with IntelliJ IDEA. User: farf Date: 1/10/13 Time: 1:15 PM To change this template use File | Settings | File
 * Templates.
 */
public class CheckedOutCompleter extends KimiosCompleter
{
    IAdministrationController administrationController;

    @Override public int complete(String s, int i, List<String> strings)
    {



        StringsCompleter delegate = new StringsCompleter();
        List<Document> locked = administrationController.getCheckedOutDocuments(this.getCurrentSession());

        String lockTpl = "ID: %d\tPath: %s\t\t\tLock Owner: %s";

        if (locked.size() > 0) {
            for (Document doc : locked) {
                delegate.getStrings().add(String.valueOf(doc.getUid()));
            }
        } else {
            delegate.getStrings().add("No document locked");
        }
        return delegate.complete(s, i, strings);
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
