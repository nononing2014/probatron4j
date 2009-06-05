/*  This file is part of the source of
 * 
 *  Probatron4J - a Schematron validator for Java(tm)
 * 
 *  Copyright (C) 2009 Griffin Brown Digitial Publishing Ltd
 *   
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.probatron;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

public class SchematronSchema
{
    private byte[] schemaAsBytes;
    static Logger logger = Logger.getLogger( SchematronSchema.class );


    public SchematronSchema( URL url )
    {
        logger.debug( "Constructing from URL: " + url.toString() );
        this.schemaAsBytes = Utils.derefUrl( url );
    }


    public SchematronSchema( InputStream is )
    {
        logger.debug( "Constructing from InpuStream" );
        try
        {
            this.schemaAsBytes = Utils.getBytesToEndOfStream( is, false );
        }
        catch( IOException e )
        {
            logger.error( e.getMessage() );
        }
    }


    public ValidationReport validateCandidate( URL url )
    {
        byte[] ba = Utils.derefUrl( url );
        ByteArrayInputStream bis = new ByteArrayInputStream( ba );
        ValidationReport vr = validateCandidate( bis );
        Utils.streamClose( bis );
        return vr;
    }


    private ValidationReport validateCandidate( InputStream is )
    {
        JarUriResolver jur = new JarUriResolver();
        TransformerFactory tf = Utils.getTransformerFactory();
        tf.setURIResolver( jur );

        ValidationReport vr = null;

        Transformer t = null;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] interim = null;

            // Step 1. pre-process schema
            logger.debug( "Running pre-process transform #1" );
            Source xsltSource = jur.resolve( "iso_dsdl_include.xsl", null );
            t = tf.newTransformer( xsltSource );
            t.transform( new StreamSource( new ByteArrayInputStream( this.schemaAsBytes ) ),
                    new StreamResult( baos ) );
            interim = baos.toByteArray();
            baos.reset();

            // Step 2. second pre-proc
            logger.debug( "Running pre-process transform #2" );
            xsltSource = jur.resolve( "iso_abstract_expand.xsl", null );
            t = tf.newTransformer( xsltSource );
            t.transform( new StreamSource( new ByteArrayInputStream( interim ) ),
                    new StreamResult( baos ) );
            interim = baos.toByteArray();
            baos.reset();

            // Step 3. compile schema to XSLT
            logger.debug( "Transforming schema to XSLT" );
            xsltSource = jur.resolve( "iso_svrl_for_xslt2.xsl", null );
            t = tf.newTransformer( xsltSource );
            t.setParameter( "full-path-notation", "4" );
            t.transform( new StreamSource( new ByteArrayInputStream( interim ) ),
                    new StreamResult( baos ) );
            interim = baos.toByteArray();
            baos.reset();

            // Step 4. Apply XSLT to candidate
            logger.debug( "Applying XSLT to candidate" );
            xsltSource = new StreamSource( new ByteArrayInputStream( interim ) );
            t = tf.newTransformer( xsltSource );
            t.transform( new StreamSource( is ), new StreamResult( baos ) );
            vr = new ValidationReport( baos.toByteArray() );

        }
        catch( Exception e )
        {
            logger.fatal( e.getMessage() );
            throw new RuntimeException(
                    "Cannot instantiate XSLT transformer, or transformation failure: "
                            + e.getMessage(), e );
        }

        return vr;

    }

}
