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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class EnrichmentFilter extends XMLFilterImpl
{
    static Logger logger = Logger.getLogger( EnrichmentFilter.class );
    private ValidationReport rpt;
    private Stack<String> ancestors = new Stack<String>();


    public EnrichmentFilter( ValidationReport rpt )
    {
        this.rpt = rpt;
    }


    public void startElement( String namespaceURI, String localName, String qualifiedName,
            Attributes atts ) throws SAXException
    {
        ancestors.push( localName );

        if( namespaceURI.equals( Utils.SVRL_NAME )
                && ( localName.equals( "successful-report" ) || localName
                        .equals( "failed-assert" ) ) )
        {
            String pseudo = atts.getValue( "location" );
            String fixed = this.rpt.nsMap.fixupXpath( pseudo );
            AttributesImpl newAttributes = new AttributesImpl( atts );
            newAttributes.removeAttribute( newAttributes.getIndex( "location" ) );
            newAttributes.addAttribute( "", "location", "location", "CDATA", fixed );
            atts = newAttributes;

            if( Driver.physicalLocators )
            {
                PhysicalLocation loc = rpt.locMap.get( Utils.trimAttributePart( pseudo ) );

                if( loc == null )
                {
                    logger.warn( "Cannot find location of psuedo-XPath: <" + pseudo + ">" );
                }

                atts = loc.addAsAttributes( newAttributes );
            }
        }
        else if( namespaceURI.equals( Utils.SVRL_NAME )
                && ( localName.equals( "ns-prefix-in-attribute-values" ) ) )
        {
            return; // having done nothing
        }
        else if( Driver.compact && namespaceURI.equals( Utils.SVRL_NAME )
                && ( localName.equals( "fired-rule" ) || localName.equals( "active-pattern" ) ) )
        {
            return; // having done nothing
        }

        super.startElement( namespaceURI, localName, qualifiedName, atts );

        if( namespaceURI.equals( Utils.SVRL_NAME )
                && ( localName.equals( "schematron-output" ) ) )
        {
            // insert a bunch of synthesis namespace/prefix mappings
            ArrayList<AttributesImpl> attsArray = this.rpt.nsMap.asAttributes();
            Iterator<AttributesImpl> iter = attsArray.iterator();

            while( iter.hasNext() )
            {

                AttributesImpl atts2 = iter.next();
                logger
                        .debug( "Creating SVRL mapping for namespace: "
                                + atts2.getValue( "uri" ) );
                getContentHandler()
                        .startElement( Utils.SVRL_NAME, "ns-prefix-in-attribute-values",
                                "ns-prefix-in-attribute-values", atts2 );
                getContentHandler().endElement( Utils.SVRL_NAME,
                        "ns-prefix-in-attribute-values", "ns-prefix-in-attribute-values" );
            }
        }
    }


    @Override
    public void characters( char[] ch, int start, int length ) throws SAXException
    {
        
        // we only strip ws children of the root element
        if( ! Driver.compact || ! ancestors.peek().equals( "schematron-output" ) )
        {
            super.characters( ch, start, length );
            return;
        }

        // if there's any non-ws here, call the superclass
        for( int i = start; i < start + length; i++ )
        {
            char c = ch[ i ];
            if( ! ( c == ' ' || c == '\t' || c == '\n' ) )
            {

                super.characters( ch, start, length );
                return;
            }
        }

        return; // doing nothing

    }


    public void endElement( String namespaceURI, String localName, String qualifiedName )
            throws SAXException
    {
        ancestors.pop();
        if( namespaceURI.equals( Utils.SVRL_NAME )
                && ( localName.equals( "ns-prefix-in-attribute-values" ) ) )
        {
            return; // having done nothing
        }
        else if( Driver.compact && namespaceURI.equals( Utils.SVRL_NAME )
                && ( localName.equals( "fired-rule" ) || localName.equals( "active-pattern" ) ) )
        {
            return; // having done nothing
        }

        // else
        super.endElement( namespaceURI, localName, qualifiedName );
    }
}
