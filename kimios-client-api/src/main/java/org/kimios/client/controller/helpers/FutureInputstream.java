package org.kimios.client.controller.helpers;

import org.apache.cxf.attachment.ByteDataSource;
import org.kimios.client.controller.helpers.HashCalculator;
import org.kimios.client.controller.helpers.HashInputStream;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 *
 *  @author Fabien Alin <f@devlib.fr>
 *
 *  Custom inpustream, with content built on demand, based on HashInputstream (@see HashInputStream)
 *
 *
 */
public class FutureInputstream
    extends ByteArrayInputStream
{

    private ByteArrayInputStream futureStream;

    private HashInputStream hashInputStreamSource;

    private String hashName;

    public FutureInputstream( byte[] buf )
    {
        super( buf );
    }


    public FutureInputstream( String hashName, HashInputStream hashInputStreamSource )
    {
        super( new byte[]{ } );
        this.hashName = hashName;
        this.hashInputStreamSource = hashInputStreamSource;


    }

    private void checkFutureStream(){
        if ( futureStream == null )
        {
            String hash = null;
            for ( MessageDigest md : hashInputStreamSource.getDigests() )
            {
                if ( md.getAlgorithm().equals( hashName ) )
                {
                    hash = HashCalculator.buildHexaString( md.digest() ).replaceAll( " ", "" );
                    futureStream = new ByteArrayInputStream(hash.getBytes());
                    return;
                }
            }
            throw new RuntimeException( "No Hash Found" );
        }


    }

    @Override
    public synchronized int read( byte[] b, int off, int len )
    {

        checkFutureStream();
        return futureStream.read( b, off,
                           len );    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public synchronized int read()
    {
        checkFutureStream();
        return futureStream.read();
    }
}