package org.kimios.kernel.index.filters;

import org.kimios.kernel.dms.Document;
import org.kimios.kernel.index.FileFilterException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 7/5/13
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Filter
{

    public String[] handledExtensions();

    public String[] handledMimeTypes();

    public Object getFileBody( Document document, InputStream inputStream )
        throws FileFilterException, IOException;

    public Map<String, Object> getMetaDatas()
        throws FileFilterException;
}
