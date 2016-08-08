package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

/**
 */
@Service
@Command(
        scope = "kimios",
        name = "search-fields",
        description = "List Solr Schema Available Fields")
public class SearchFieldsCommand extends KimiosCommand
{
    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        if (this.isConnected()) {
              List<String> stringList = searchController.listAvailableFields(this.getCurrentSession());
            if (stringList.size() > 0) {

                for (String d : stringList) {
            /*        System.out.println(String.format(lockTpl, doc.getUid(), doc.getPath(),
                            doc.getLock().getUser() + "@" + doc.getLock().getUserSource()));*/
                    System.out.println(" > " + d);
                }
            } else {
                System.out.println("No fields available.");
            }
        }
    }
}
