/*
 * This file is part of the source of
 * 
 * Probatron4J - a Schematron validator for Java(tm)
 * 
 * Copyright (C) 2010 Griffin Brown Digitial Publishing Ltd
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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class CandidateAnalyzer implements LexicalHandler, ContentHandler
{
    private ValidationContext validationContext;


    public CandidateAnalyzer( ValidationContext vc )
    {
        this.validationContext = vc;
    }


    public void comment( char[] ch, int start, int length ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void endCDATA() throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void endDTD() throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void endEntity( String name ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void startCDATA() throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void startDTD( String name, String publicId, String systemId ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void startEntity( String name ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void characters( char[] ch, int start, int length ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void endDocument() throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void endElement( String uri, String localName, String qName ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void endPrefixMapping( String prefix ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void ignorableWhitespace( char[] ch, int start, int length ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void processingInstruction( String target, String data ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void setDocumentLocator( Locator locator )
    {
    // TODO Auto-generated method stub

    }


    public void skippedEntity( String name ) throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void startDocument() throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void startElement( String uri, String localName, String qName, Attributes atts )
            throws SAXException
    {
    // TODO Auto-generated method stub

    }


    public void startPrefixMapping( String prefix, String uri ) throws SAXException
    {
    // TODO Auto-generated method stub

    }

}
