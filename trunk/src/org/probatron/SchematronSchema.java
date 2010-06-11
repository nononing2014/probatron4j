/*
 * This file is part of the source of
 * 
 * Probatron4J - a Schematron validator for Java(tm)
 * 
 * Copyright (C) 2009 Griffin Brown Digitial Publishing Ltd
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

package org.probatron;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.megginson.sax.XMLWriter;

public class SchematronSchema
{
    private byte[] schemaAsBytes;
    static Logger logger = Logger.getLogger( SchematronSchema.class );
    private URL schemaUrl;
    private JarUriResolver jur = new JarUriResolver();


    public SchematronSchema(  URL url )
    {
        assert this.schemaUrl != null : "null schema URL";

      
        logger.debug( "Constructing from URL: " + url.toString() );
        this.schemaAsBytes = Utils.derefUrl( url );
        this.schemaUrl = url;

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


    public ValidationReport validateCandidate( URL candidateUrl )
    {
        ValidationReport vr = null;
        InputStream is = null; // the stream for the candidate

        try
        {
            URLConnection conn = candidateUrl.openConnection();
            conn.connect();
            is = conn.getInputStream();
            vr = validateCandidate( is );
        }
        catch( IOException e )
        {
            logger.fatal( e.getMessage() );
        }
        finally
        {
            Utils.streamClose( is );
        }

        return vr;
    }


    static void doIncludePreprocess( byte[] schemaAsBytes, ByteArrayOutputStream baos )
    {
    // do nothing
    }


    private ValidationReport validateCandidate( InputStream candidateStream )
    {

        TransformerFactory jarAwareTransformerFactory = Utils.getTransformerFactory();
        jarAwareTransformerFactory.setURIResolver( jur );

        ValidationReport vr = null;
        Transformer t = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] interim = null;

            // Step 1. do the inclusion
            logger.debug( "Performing inclusion ..." );
            XMLReader reader = XMLReaderFactory.createXMLReader();
            IncludingFilter filter = new IncludingFilter( schemaUrl, true );
            filter.setParent( reader );
            filter.setContentHandler( new XMLWriter( new OutputStreamWriter( baos ) ) );
            filter.parse( new InputSource( new ByteArrayInputStream( this.schemaAsBytes ) ) );
            interim = baos.toByteArray();
            baos.reset();

            Source xsltSource = null;

            // optimisation - if no abstract patterns exist, this step can be skipped
            if( filter.foundAbstractPatterns )
            {
                // Step 2. for abstract template processing
                logger.debug( "Running abstract template expansion transform ..." );
                xsltSource = jur.resolve( "iso_abstract_expand.xsl", null );
                t = jarAwareTransformerFactory.newTransformer( xsltSource );
                t.transform( new StreamSource( new ByteArrayInputStream( interim ) ),
                        new StreamResult( baos ) );
                interim = baos.toByteArray();
                baos.reset();
            }

           // Utils.writeBytesToFile( interim, "interim.xml" );

            // Step 3. compile schema to XSLT
            interim = compileToXslt( interim );

            // Step 4. Apply XSLT to candidate
            interim = applyXsltSchema( candidateStream, interim );

            // Generate the Validation report from the raw SVRL thus created
            vr = new ValidationReport( interim );

        }
        catch( TransformerException e )
        {
            logger.fatal( e.getMessage() );
            throw new RuntimeException(
                    "Cannot instantiate XSLT transformer, or transformation failure: " + e, e );
        }
        catch( IOException e )
        {
            logger.fatal( e.getMessage() );
            throw new RuntimeException( "IOException: " + e, e );
        }
        catch( SAXException e )
        {
            logger.fatal( e.getMessage() );
            throw new RuntimeException( "SAXException: " + e, e );
        }
        catch( SaxonApiException e )
        {
            logger.fatal( e.getMessage() );
            throw new RuntimeException( "SaxonApiException: " + e, e );

        }

        return vr;

    }


    private byte[] applyXsltSchema( InputStream candidateStream, byte[] interim )
            throws SaxonApiException
    {
        logger.debug( "Applying XSLT version of schema to candidate" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XsltCompiler comp = Runtime.getSaxonProcessor().newXsltCompiler();

        Source ss = new StreamSource( new ByteArrayInputStream( interim ) );
        XsltExecutable xx = comp.compile( ss );
        XsltTransformer transformer = xx.load();
        transformer.setSource( new StreamSource( candidateStream, schemaUrl.toExternalForm() ) );

        Serializer ser = new Serializer();
        ser.setOutputStream( baos );
        transformer.setDestination( ser );

        transformer.transform();

        return baos.toByteArray();
    }


    private byte[] compileToXslt( byte[] interim ) throws TransformerException,
            SaxonApiException
    {
        logger.debug( "Transforming schema to XSLT ..." );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XsltCompiler comp = Runtime.getSaxonProcessor().newXsltCompiler();
        comp.setURIResolver( jur );
        Source ss = jur.resolve( "iso_svrl_for_xslt2.xsl", null );
        XsltExecutable xx = comp.compile( ss );
        XsltTransformer transformer = xx.load();

        transformer.setSource( new StreamSource( new ByteArrayInputStream( interim ) ) );

        Serializer ser = new Serializer();
        ser.setOutputStream( baos );
        transformer.setDestination( ser );

        transformer.transform();

        interim = baos.toByteArray();

        // Utils.writeBytesToFile( interim, "interim.xml" );

        return interim;
    }


    public URL getUrl()
    {
        return schemaUrl;
    }

}
