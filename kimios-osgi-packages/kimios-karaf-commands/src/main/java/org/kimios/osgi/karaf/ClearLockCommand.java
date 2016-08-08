package org.kimios.osgi.karaf;

import java.util.List;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;

import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.kimios.kernel.dms.model.Document;

/**
 * Created with IntelliJ IDEA. User: farf Date: 1/10/13 Time: 1:38 PM To change this template use File | Settings | File
 * Templates.
 */
@Service
@Command(
        scope = "kimios",
        name = "clear-locked-documents",
        description = "Remove lock from Kimios Documents")
public class ClearLockCommand extends KimiosCommand
{
    @Argument(index = 0, name = "ids",
            description = "document Ids",
            required = true, multiValued = true)
    @Completion(CheckedOutCompleter.class)
    String[] ids = null;

    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        if (this.isConnected()) {
            List<Document> locked = administrationController.getCheckedOutDocuments(getCurrentSession());
            if (locked.size() > 0) {
                for (String docId : ids) {
                    System.out.println("Unlocking: " + docId);
                    administrationController.clearLock(getCurrentSession(), Long.parseLong(docId));
                    System.out.println("Lock removed.");
                }
            } else {
                System.out.println("No document locked");
            }
        }
    }
}
