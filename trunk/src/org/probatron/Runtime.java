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

import java.util.HashMap;

import net.sf.saxon.s9api.Processor;

import org.apache.log4j.Logger;
import org.probatron.functions.FileExists;
import org.probatron.functions.GoverningDtdPublicIdentifier;
import org.probatron.functions.GoverningDtdSystemIdentifier;
import org.probatron.functions.IsValidIsbn;
import org.probatron.functions.IsValidIsbn13;
import org.probatron.functions.IsValidIssn;
import org.probatron.functions.RomanNumeralToDecimal;
import org.probatron.functions.SystemId;
import org.probatron.functions.UrlMimeType;

public class Runtime
{
    static Logger logger = Logger.getLogger( Runtime.class );

    private static Processor processor;
    private static HashMap< String, Session > sessionMap = new HashMap< String, Session >();

    static
    {
        processor = new Processor( false );

        // register the Probatron extension functions
        processor.registerExtensionFunction( new IsValidIsbn() );
        processor.registerExtensionFunction( new IsValidIsbn13() );
        processor.registerExtensionFunction( new IsValidIssn() );
        processor.registerExtensionFunction( new SystemId() );
        processor.registerExtensionFunction( new UrlMimeType() );
        processor.registerExtensionFunction( new FileExists() );
        processor.registerExtensionFunction( new GoverningDtdSystemIdentifier() );
        processor.registerExtensionFunction( new GoverningDtdPublicIdentifier() );
        processor.registerExtensionFunction( new RomanNumeralToDecimal() );

    }


    public static void registerSession( Session session )
    {
        sessionMap.put( session.getUuid().toString(), session );
        logger.debug( "Registering session with id " + session.getUuid().toString() );

    }


    public static Session getSession( String sessionId )
    {
        // when getting string parameters from Saxon they seem to get wrapped
        // with quotation marks; hence we ensure here we have a pukka UUID
        sessionId = sessionId.replaceAll( "[^0-9a-zA-Z-]+", "" );
        return sessionMap.get( sessionId );

    }


    public static Processor getSaxonProcessor()
    {
        return processor;
    }

}
