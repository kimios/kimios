/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.client.controller.helpers;

import org.kimios.client.exception.ConfigException;

import java.io.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class about file compression
 */
public class FileCompressionHelper
{

    /**
     * @param tmpFilePath
     * @param sessionUid
     * @return
     * @throws ConfigException
     * @throws java.io.IOException
     */
    public static String getTempFilePath( String tmpFilePath, String sessionUid )
        throws ConfigException, IOException
    {
        File path = new File( tmpFilePath );
        if ( !path.exists() )
        {
            path.mkdirs();
        }
        String tmpFileName = "/" + sessionUid + ( new Date() ).getTime();
        File tmp = new File( tmpFilePath + tmpFileName );
        tmp.createNewFile();
        return tmpFileName;
    }

    /**
     * Get an uncompressed file from compressed file
     *
     * @param toUncompress
     * @return
     * @throws Exception
     * @throws ConfigException
     * @throws java.io.IOException
     */
    public static InputStream getUncompressedFile( File toUncompress )
        throws Exception, ConfigException, IOException
    {
        FileInputStream toRead = new FileInputStream( toUncompress );
        ZipInputStream zis = new ZipInputStream( new BufferedInputStream( toRead ) );
        if ( zis.getNextEntry() != null )
        {
            return zis;
        }
        else
        {
            throw new Exception( "File Error" );
        }
    }

    /**
     * Get an uploadable compressed version from input stream
     *
     * @param in
     * @param sessionUid
     * @param tmpFilePath
     * @return
     * @throws ConfigException
     * @throws java.io.IOException
     */
    public static File getUploadableCompressedVersion( InputStream in, String sessionUid, String tmpFilePath )
        throws ConfigException, IOException
    {
        File toCompress = new File( getTempFilePath( tmpFilePath, sessionUid ) );
        FileOutputStream fos = new FileOutputStream( toCompress );
        int bufferSize = 1024 * 512;
        byte[] j = new byte[bufferSize];
        int readByte = 0;
        while ( ( readByte = in.read( j, 0, bufferSize ) ) != -1 )
        {
            fos.write( j, 0, readByte );
        }
        fos.flush();
        fos.close();
        File tmp = new File( tmpFilePath + toCompress.getName() );
        FileOutputStream dest = new FileOutputStream( tmp );
        ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream( dest ) );
        byte[] data = new byte[2048];
        BufferedInputStream original = new BufferedInputStream( new FileInputStream( toCompress ), 2048 );
        ZipEntry entry = new ZipEntry( toCompress.getName() );
        out.putNextEntry( entry );
        int count;
        while ( ( count = original.read( data, 0, 2048 ) ) != -1 )
        {
            out.write( data, 0, count );
        }
        out.close();
        //Delete Temporary File
        toCompress.delete();
        return tmp;
    }
}

