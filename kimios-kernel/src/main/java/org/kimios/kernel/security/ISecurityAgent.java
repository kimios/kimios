package org.kimios.kernel.security;

import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.user.Group;

import java.util.List;
import java.util.Vector;

/**
 * Created by farf on 01/04/15.
 */
public interface ISecurityAgent {
    <T extends DMEntity> List<T> areReadable(List<T> entities, String userName, String userSource,
                                                 Vector<Group> groups) throws ConfigException,
            DataSourceException;

    <T extends DMEntity> List<T> areWritable(List<T> entities, String userName, String userSource,
                                                 Vector<Group> groups) throws ConfigException, DataSourceException;

    <T extends DMEntity> List<T> areFullAccess(List<T> entities, String userName, String userSource,
                                                   Vector<Group> groups) throws ConfigException, DataSourceException;

    boolean isReadable(DMEntity dm, String userName, String userSource, Vector<Group> groups)
                                    throws ConfigException, DataSourceException;

    boolean isWritable(DMEntity dm, String userName, String userSource, Vector<Group> groups)
                                            throws ConfigException, DataSourceException;

    /*
        *  Check if one child element is not writable (this method don't check the checked out documents)
        */
    boolean hasAnyChildNotWritable(DMEntity dm, String userName, String userSource, Vector<Group> groups)
            throws ConfigException, DataSourceException;

    boolean hasAnyChildNotFullAccess(DMEntity dm, String userName, String userSource, Vector<Group> groups)
                    throws ConfigException, DataSourceException;

    boolean hasAnyChildCheckedOut(DMEntity dm, String userName, String userSource)
                            throws ConfigException, DataSourceException;

    boolean isFullAccess(DMEntity dm, String userName, String userSource, Vector<Group> groups)
                                    throws ConfigException, DataSourceException;

    boolean isAdmin(String userName, String userSource) throws ConfigException, DataSourceException;

    boolean canCancelWorkFlow(Document doc, String userName, String userSource, Vector<Group> groups)
                                            throws ConfigException, DataSourceException;

    boolean isDocumentOutOfWorkflow(Document doc) throws ConfigException, DataSourceException;
}
