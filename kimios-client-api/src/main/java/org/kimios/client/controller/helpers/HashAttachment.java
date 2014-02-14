/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

package org.kimios.client.controller.helpers;

import org.apache.cxf.attachment.ByteDataSource;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.activation.DataHandler;
import java.io.IOException;
import java.security.MessageDigest;

/**
 */
public class HashAttachment
    extends Attachment
{
    private HashInputStream sourceStreamForHash;

    private String hashName;

    public HashAttachment( String id, DataHandler dataHandler, HashInputStream sourceStreamForHash, String hashName )
    {
        super( id, dataHandler, null );
        /*
            On garde une référence à l'attachement portant le document
         */
        this.sourceStreamForHash = sourceStreamForHash;
        this.hashName = hashName;
    }


    @Override
    public DataHandler getDataHandler()
    {
        try
        {
            for ( MessageDigest md : sourceStreamForHash.getDigests() )
            {
                if ( md.getAlgorithm().equals( hashName ) )
                {
                    String hash = HashCalculator.buildHexaString( md.digest() ).replaceAll( " ", "" );
                    System.out.println( " > Hash found " + hashName + ". Value: " + hash + ". Creating datahandler" );
                    DataHandler dhHash = new DataHandler( new ByteDataSource( (byte[]) hash.getBytes( "UTF-8" ) ) );
                    return dhHash;
                }
            }
            throw new RuntimeException( "No Hash Found" );
        }
        catch ( IOException e )
        {
            System.out.println( "> Error while accessing source stream " + e.getMessage() );
            e.printStackTrace( System.out );
            throw new RuntimeException( e );
        }
    }
}