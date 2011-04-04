/*
 * This file is part of the source of
 * 
 * Probatron4J - a Schematron validator for Java(tm)
 * 
 * Copyright (C) 2010-2011 Griffin Brown Digitial Publishing Ltd
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.griffinbrown.xmltool.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A heuristic "sniffer" of bytes to determine MIME type.
 */
public class HeuriSniff
{
    private final static int READ_BUFFER_SIZE = 10;


    private static byte[] derefUrlPartial( URL url )
    {
        byte[] ba = null;

        InputStream is = null;
        try
        {
            // get what's at the url location and put it into a byte array
            URLConnection conn = url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            ba = getSomeBytes( is ); // does not lose

        }
        catch( IOException e )
        {
            return null;
        }
        finally
        {
            streamClose( is );
        }

        return ba;

    }


    /**
     * Attempt to an InputStream. N.B. If an exception is raised, it is ignored.
     * 
     * @param is
     */
    public static void streamClose( InputStream is )
    {
        try
        {
            if( is != null )
            {
                is.close();
            }
        }
        catch( Exception e )
        {
            // do nowt
        }
    }


    private static byte[] getSomeBytes( InputStream in ) throws IOException
    {

        byte[] buf = new byte[ READ_BUFFER_SIZE + 1 ];
        long n = in.read( buf, 0, READ_BUFFER_SIZE );

        if( n <= 0 )
        {
            return null;
        }

        byte[] ba = new byte[ ( int )n ];
        for( int i = 0; i < n; i++ )
        {
            ba[ i ] = buf[ i ];
        }

        return buf;
    }


    /**
     * <p>
     * Given a URL returns the MIME type of the resolved resource if it one of a selection of
     * common types:
     * </p>
     * <ul>
     * <li>application/pdf</li>
     * <li>application/postscript</li>
     * <li>application/xml</li>
     * <li>application/zip</li>
     * <li>image/gif</li>
     * <li>image/jpeg</li>
     * <li>image/png</li>
     * <li>image/tiff</li>
     * <li>text/html</li>
     * </ul>
     * <p>
     * If the format is not recognisable as one of these, the MIME type <tt>binary/plain</tt> is
     * reported.
     * </p>
     * 
     * @param url
     *            the URL to test
     * @return a String containing the MIME type; null if resource is not viable
     */
    public static String sniffMimeTypeFromUrl( URL url )
    {
        byte[] ba = derefUrlPartial( url );
        String mt = ba == null ? null : sniffMimeTypeFromBytes( ba );
        return mt;

    }


    /**
     * Based on info at http://www.martinreddy.net/gfx/2d-hi.html
     * 
     * @param ba
     *            bytes to sniff
     * @return mime type
     */
    private static String sniffMimeTypeFromBytes( byte[] ba )
    {
        String mt = null;

        if( ba.length >= 3 && ba[ 0 ] == 'G' && ba[ 1 ] == 'I' && ba[ 2 ] == 'F' )
        {
            mt = "image/gif"; // GIF
        }
        else if( ba.length >= 2 && ( ba[ 0 ] == 'I' && ba[ 1 ] == 'I' )
                || ( ba[ 0 ] == 'M' && ba[ 1 ] == 'M' ) )
        {
            mt = "image/tiff"; // TIFF
        }
        else if( ba.length >= 2 && ba[ 0 ] == ( byte )0xFF && ba[ 1 ] == ( byte )0xD8 )
        {
            mt = "image/jpeg"; // JPEG / JFIF
        }
        else if( ba.length >= 5 && ba[ 0 ] == '<' && ba[ 1 ] == '?' && ba[ 2 ] == 'x'
                && ba[ 3 ] == 'm' && ba[ 4 ] == 'l' )
        {
            mt = "application/xml"; // XML (UTF-8 or ASCII)
        }
        else if( ba.length >= 8 && ba[ 0 ] == ( byte )0xFF && ba[ 1 ] == ( byte )0xFE
                && ba[ 2 ] == '<' && ba[ 3 ] == ( byte )0x00 && ba[ 4 ] == '?'
                && ba[ 5 ] == ( byte )0x00 && ba[ 6 ] == 'x' && ba[ 7 ] == ( byte )0x00 )
        {
            mt = "application/xml"; // XML (little-endian UTF-16)
        }

        else if( ba.length >= 6 && ba[ 0 ] == '<' && ba[ 1 ] == ( byte )0x00 && ba[ 2 ] == '?'
                && ba[ 3 ] == ( byte )0x00 && ba[ 4 ] == 'x' && ba[ 5 ] == ( byte )0x00 )
        {
            mt = "application/xml"; // XML (little-endian UTF-16), *no BOM*
        }

        else if( ba.length >= 8 && ba[ 0 ] == ( byte )0xFE && ba[ 1 ] == ( byte )0xFF
                && ba[ 2 ] == 0x00 && ba[ 3 ] == '<' && ba[ 4 ] == 0x00 && ba[ 5 ] == '?'
                && ba[ 6 ] == 0x00 && ba[ 7 ] == 'x' )
        {
            mt = "application/xml"; // XML (big-endian UTF-16)
        }

        else if( ba.length >= 6 && ba[ 0 ] == ( byte )0x00 && ba[ 1 ] == '<'
                && ba[ 2 ] == ( byte )0x00 && ba[ 3 ] == '?' && ba[ 4 ] == ( byte )0x00
                && ba[ 5 ] == 'x' )
        {
            mt = "application/xml"; // XML (big-endian UTF-16), *no BOM*
        }

        else if( ba.length >= 4 && ba[ 0 ] == '%' && ba[ 1 ] == 'P' && ba[ 2 ] == 'D'
                && ba[ 3 ] == 'F' )
        {
            mt = "application/pdf"; // PDF
        }
        else if( ba.length >= 4 && ba[ 0 ] == '%' && ba[ 1 ] == '!' && ba[ 2 ] == 'P'
                && ba[ 3 ] == 'S' )
        {
            mt = "application/postscript"; // Postscript
        }
        // else if( ba.length >= 5 && ba[ 0 ] == '<' && ba[ 1 ] == 'h' && ba[ 2 ] == 't'
        // && ba[ 3 ] == 'm' && ba[ 4 ] == 'l' )
        // {
        // mt = "text/html"; // HTML (UTF-8 or ASCII)
        // }
        // else if( ba.length >= 5 && ba[ 0 ] == '<' && ba[ 1 ] == 'H' && ba[ 2 ] == 'T'
        // && ba[ 3 ] == 'M' && ba[ 4 ] == 'L' )
        // {
        // mt = "text/html"; // HTML (UTF-8 or ASCII)
        // }
        else if( ba.length >= 4 && ba[ 0 ] == -119 && ba[ 1 ] == 'P' && ba[ 2 ] == 'N'
                && ba[ 3 ] == 'G' )
        {
            mt = "image/png"; // PNG
        }
        else if( ba.length >= 2 && ba[ 0 ] == 'P' && ba[ 1 ] == 'K' )
        {
            mt = "application/zip"; // ZIP
        }

        return mt;

    }

}