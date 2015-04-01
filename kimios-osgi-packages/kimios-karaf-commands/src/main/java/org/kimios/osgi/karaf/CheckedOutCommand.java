package org.kimios.osgi.karaf;

import java.util.List;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.kimios.kernel.dms.Document;

/**
 */
@Command(
        scope = "kimios",
        name = "list-locked-documents",
        description = "List Kimios locked documents")
public class CheckedOutCommand extends KimiosCommand
{
    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        if (this.isConnected()) {
            List<Document> locked = administrationController.getCheckedOutDocuments(this.getCurrentSession());

            String lockTpl = "ID: %d\tPath: %s\t\t\tLock Owner: %s";

            if (locked.size() > 0) {
                for (Document doc : locked) {
                    System.out.println(String.format(lockTpl, doc.getUid(), doc.getPath(),
                            doc.getLock().getUser() + "@" + doc.getLock().getUserSource()));
                }
            } else {
                System.out.println("No document locked");
            }
        }
    }
}
