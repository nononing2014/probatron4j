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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Session
{
    static Logger logger = Logger.getLogger( Session.class );

    private boolean physicalLocators = true;
    private String phase;
    private String schemaDoc;
    private SchematronSchema theSchema;
    private UUID uuid;
    private int reportFormat;


    public Session()
    {
        uuid = UUID.randomUUID();
    }


    public ValidationReport doValidation( String candidate ) throws MalformedURLException,
            SAXException, IOException
    {
        ValidationReport vr = null;

        theSchema = new SchematronSchema( new URL( this.schemaDoc ) );

        synchronized( Session.class )
        {
            // gets some metadata about the instance to set a context
            // object used by some XPath extentsion functions
            URL candidateUrl = new URL( candidate );
            ValidationContext vc = analyzeCandidate( candidateUrl );
            vc.setVerbatimName( candidate );
            
            Runtime.setValidationContext( vc );

            vr = theSchema.validateCandidate( candidateUrl );

            if( physicalLocators )
            {
                vr.annotateWithLocators( this, candidateUrl );
            }

            if( getReportFormat() == ValidationReport.REPORT_SVRL_MERGED )
            {
                vr.mergeSvrlIntoCandidate( this, candidateUrl );
            }

            return vr;
        }

    }


    private ValidationContext analyzeCandidate( URL url ) throws SAXException, IOException
    {
        ValidationContext vc = new ValidationContext();
        CandidateAnalyzer ca = new CandidateAnalyzer( vc );

        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler( ca );
        parser.parse( url.toString() );

        return vc;
    }


    public String getPhase()
    {
        return phase;
    }


    public void setPhase( String phase )
    {
        this.phase = phase;
        logger.debug( "Using phase: " + phase );
    }


    public String getSchemaDoc()
    {
        return schemaDoc;
    }


    public void setSchemaDoc( String schemaDoc )
    {
        this.schemaDoc = schemaDoc;
        logger.debug( "Schema document is:" + schemaDoc );
    }


    public boolean usesPhysicalLocators()
    {
        return physicalLocators;
    }


    public void setUsePhysicalLocators( boolean physicalLocators )
    {
        this.physicalLocators = physicalLocators;
        logger.debug( "Setting option (use physical locators): " + physicalLocators );
    }


    public UUID getUuid()
    {
        return uuid;
    }


    public int getReportFormat()
    {
        return reportFormat;
    }


    public void setReportFormat( int reportFormat )
    {
        this.reportFormat = reportFormat;
        logger.debug( "Setting option (report format): " + reportFormat );
    }

}
