/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.kernel.index.filters.impl;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.index.FileFilterException;
import org.kimios.kernel.index.filters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GlobalFilter
    implements Filter
{

    private static Logger logger = LoggerFactory.getLogger( GlobalFilter.class );

    public String[] handledExtensions()
    {
        return new String[0];
    }


    private Map<String, Object> metaDatas;

    public String[] handledMimeTypes()
    {
        return new String[0];
    }

    public Object getFileBody( Document document, InputStream inputStream )
        throws Throwable
    {
        try
        {


            Parser parser = new AutoDetectParser(); // Should auto-detect!
            /*
                create content handler with unlimited content length
             */
            ContentHandler contentHandler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            parser.parse( inputStream, contentHandler, metadata, context );


            metaDatas = new HashMap<String, Object>(  );
            for(String m: metadata.names()){

                metaDatas.put( m, metadata.isMultiValued( m ) ? metadata.getValues( m ) : metadata.get( m ) );
            }

            String val = contentHandler.toString();
            if(logger.isDebugEnabled()){
                for(String m: metaDatas.keySet()){
                    logger.debug( "Metadata {} --> {}", m, metaDatas.get( m ) );
                }
                logger.debug( val );
            }
            return val;

        }
        catch ( Exception e )
        {
            throw new FileFilterException( e );
        }
        finally
        {
            inputStream.close();
        }
    }

    public Map<String, Object> getMetaDatas()
        throws FileFilterException
    {
        return metaDatas;
    }
}
