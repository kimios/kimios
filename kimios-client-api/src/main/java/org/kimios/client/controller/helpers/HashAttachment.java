package org.kimios.client.controller.helpers;

import org.apache.cxf.attachment.ByteDataSource;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.kimios.client.controller.helpers.HashCalculator;
import org.kimios.client.controller.helpers.HashInputStream;

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