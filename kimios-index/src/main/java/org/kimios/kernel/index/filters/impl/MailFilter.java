package org.kimios.kernel.index.filters.impl;

import org.kimios.kernel.dms.Document;
import org.kimios.kernel.index.FileFilterException;
import org.kimios.kernel.index.filters.Filter;

import java.io.InputStream;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 7/5/13
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailFilter
    implements Filter
{

    private static String MSG_EXTENSION = "msg";

    private static String EML_EXTENSION = "eml";

    private static String[] HANDLED_MIME_TYPES = null;

    private static String[] HANDLED_EXTENSIONS = new String[]{ MSG_EXTENSION, EML_EXTENSION };

    public String[] handledMimeTypes()
    {
        return HANDLED_MIME_TYPES;
    }

    public String[] handledExtensions()
    {
        return HANDLED_EXTENSIONS;
    }

    public Object getFileBody( Document document, InputStream inputStream )
        throws FileFilterException
    {
        if ( document.getExtension().toLowerCase().endsWith( MSG_EXTENSION ) )
        {
            /*
                Extract body part
             */
        }
        else if ( document.getExtension().toLowerCase().endsWith( EML_EXTENSION ) )
        {
            /*
                Extract body part
             */




        }

        return  "";
    }

    public Map<String, Object> getMetaDatas()
        throws FileFilterException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
