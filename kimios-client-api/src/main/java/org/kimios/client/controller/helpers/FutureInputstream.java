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

package org.kimios.client.controller.helpers;

import java.io.ByteArrayInputStream;
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