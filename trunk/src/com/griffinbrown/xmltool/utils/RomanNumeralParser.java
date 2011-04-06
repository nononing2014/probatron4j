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

package com.griffinbrown.xmltool.utils;

import org.apache.log4j.Logger;

public class RomanNumeralParser
{
    private static Logger logger = Logger.getLogger( RomanNumeralParser.class );

    private static final char UNI_ONE_UPPER = ( char )0x2160;
    private static final char UNI_TWO_UPPER = ( char )0x2161;
    private static final char UNI_THREE_UPPER = ( char )0x2162;
    private static final char UNI_FOUR_UPPER = ( char )0x2163;
    private static final char UNI_FIVE_UPPER = ( char )0x2164;
    private static final char UNI_SIX_UPPER = ( char )0x2165;
    private static final char UNI_SEVEN_UPPER = ( char )0x2166;
    private static final char UNI_EIGHT_UPPER = ( char )0x2167;
    private static final char UNI_NINE_UPPER = ( char )0x2168;
    private static final char UNI_TEN_UPPER = ( char )0x2169;
    private static final char UNI_ELEVEN_UPPER = ( char )0x216A;
    private static final char UNI_TWELVE_UPPER = ( char )0x216B;
    private static final char UNI_FIFTY_UPPER = ( char )0x216C;
    private static final char UNI_HUNDRED_UPPER = ( char )0x216D;

    private static final char UNI_ONE_LOWER = ( char )0x2170;
    private static final char UNI_TWO_LOWER = ( char )0x2171;
    private static final char UNI_THREE_LOWER = ( char )0x2172;
    private static final char UNI_FOUR_LOWER = ( char )0x2173;
    private static final char UNI_FIVE_LOWER = ( char )0x2174;
    private static final char UNI_SIX_LOWER = ( char )0x2175;
    private static final char UNI_SEVEN_LOWER = ( char )0x2176;
    private static final char UNI_EIGHT_LOWER = ( char )0x2177;
    private static final char UNI_NINE_LOWER = ( char )0x2178;
    private static final char UNI_TEN_LOWER = ( char )0x2179;
    private static final char UNI_ELEVEN_LOWER = ( char )0x217A;
    private static final char UNI_TWELVE_LOWER = ( char )0x217B;
    private static final char UNI_FIFTY_LOWER = ( char )0x217C;
    private static final char UNI_HUNDRED_LOWER = ( char )0x217D;

    private static final char[] UNICODE_CHARS_LOWER = new char[] { UNI_ONE_LOWER,
            UNI_TWO_LOWER, UNI_THREE_LOWER, UNI_FOUR_LOWER, UNI_FIVE_LOWER, UNI_SIX_LOWER,
            UNI_SEVEN_LOWER, UNI_EIGHT_LOWER, UNI_NINE_LOWER, UNI_TEN_LOWER, UNI_ELEVEN_LOWER,
            UNI_TWELVE_LOWER, UNI_FIFTY_LOWER, UNI_HUNDRED_LOWER };

    private static final String[] UNICODE_CHARS_LOWER_REPLACEMENTS = new String[] { "i", "ii",
            "iii", "iv", "v", "vi", "vii", "viii", "ix", "x", "xi", "xii", "l", "c" };

    private static final char[] UNICODE_CHARS_UPPER = new char[] { UNI_ONE_UPPER,
            UNI_TWO_UPPER, UNI_THREE_UPPER, UNI_FOUR_UPPER, UNI_FIVE_UPPER, UNI_SIX_UPPER,
            UNI_SEVEN_UPPER, UNI_EIGHT_UPPER, UNI_NINE_UPPER, UNI_TEN_UPPER, UNI_ELEVEN_UPPER,
            UNI_TWELVE_UPPER, UNI_FIFTY_UPPER, UNI_HUNDRED_UPPER };

    private int pos;
    private char[] chars;
    private boolean emitDiagnosticMessages;


    public RomanNumeralParser( boolean emitDiagnosticMessages )
    {
        this.emitDiagnosticMessages = emitDiagnosticMessages;
        pos = 1;
    }


    public Object parse( String s )
    {
        chars = s.toCharArray();
        char tensOrUnits = chars[ 0 ];

        if( logger.isDebugEnabled() )
            logger.debug( "tensOrUnits=" + tensOrUnits );

        if( tensOrUnits == 'i' )
        {
            return units( 1, 0 );
        }
        else if( tensOrUnits == 'v' )
        {
            return units( 5, 0 );
        }
        else if( tensOrUnits == 'x' )
        {
            return tens( 10, 0 );
        }
        else if( tensOrUnits == 'l' )
        {
            return tens( 50, 0 );
        }

        return "NaN";
    }


    /**
     * @param s
     * @param runningTotal
     * @param startIndex
     * @return
     * @throws FunctionCallException
     */
    private Object units( int runningTotal, int start )
    {
        if( logger.isDebugEnabled() )
            logger.debug( "units runningTotal=" + runningTotal );

        int initVal = 0;
        char first = chars[ start ];
        if( first == 'i' )
            initVal = 1;
        else if( first == 'v' )
            initVal = 5;

        int total = runningTotal;

        if( logger.isDebugEnabled() )
            logger.debug( "units s=" + new String( chars ) );

        if( logger.isDebugEnabled() )
            logger.debug( "units initVal=" + initVal );

        int iCount = initVal == 1 ? 1 : 0; // no of consecutive i's
        boolean done = false;

        while( pos < chars.length )
        {
            if( logger.isDebugEnabled() )
            {
                logger.debug( "total=" + total );
                logger.debug( "pos=" + pos );
            }

            if( done )
                return nan( "error parsing roman numeral: no more chars expected at position "
                        + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

            char c = chars[ pos ];

            switch( c ){
            case 'i':
                iCount++;
                if( logger.isDebugEnabled() )
                    logger.debug( "i at pos " + pos + " iCount=" + iCount );
                if( iCount == 3 )
                {
                    done = true;
                }
                total += 1;
                if( logger.isDebugEnabled() )
                    logger.debug( "breaking with total=" + total );
                break;
            case 'v':
                if( initVal == 1 )
                {
                    if( runningTotal == 1 )
                        total = 4; // iv = 4
                    else
                        total += 3; // ...iv
                    done = true;
                    break;
                }
                else
                    return nan( "error parsing roman numeral: no more chars expected at position "
                            + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

                // whitespace
            case '\n':
            case '\r':
            case '\t':
            case ' ':
                return nan( "error parsing roman numeral: illegal whitespace char at position "
                        + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

            case 'x':
                if( logger.isDebugEnabled() )
                    logger.debug( "running total=" + runningTotal );
                if( total == 1 || ( runningTotal - 1 ) % 10 == 0 ) // 1 or mod 10
                {
                    total += 8;
                    done = true;
                    break;
                }
                return nan( "error parsing roman numeral: invalid x char at position "
                        + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

            case 'l':
                return nan( "error parsing roman numeral: invalid l char at position "
                        + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

            default:
                return nan( "error parsing roman numeral: unrecognised char at position "
                        + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );
            }

            pos++;

            if( logger.isDebugEnabled() )
                logger.debug( "total=" + total + " at pos " + pos );
        }
        return "" + total;
    }


    /**
     * @param s
     * @param runningTotal
     * @param startIndex
     * @return
     * @throws FunctionCallException
     */
    private Object tens( int runningTotal, int start )
    {
        int initVal = 0;
        char first = chars[ start ];

        if( first == 'x' )
            initVal = 10;
        else if( first == 'l' )
            initVal = 50;

        int total = runningTotal;

        if( logger.isDebugEnabled() )
            logger.debug( "tens init=" + initVal );

        int xCount = initVal == 10 ? 1 : 0; // no of consecutive x's
        boolean done = false;

        while( pos < chars.length )
        {
            char c = chars[ pos ];

            switch( c ){
            case 'x':
                xCount++;
                if( done )
                    return nan( "error parsing roman numeral: no more chars expected at position "
                            + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );
                else if( xCount == 3 )
                {
                    done = true;
                }
                total += 10;
                break;

            case 'l':
                if( initVal == 10 && total == 10 ) // started with x
                {
                    total = 40; // xl = 40
                    break;
                }
                else
                    return nan( "error parsing roman numeral: invalid l char at position "
                            + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

            case 'c':
                if( initVal == 10 && total == 10 ) // started with x
                {
                    total = 90; // xc = 40
                    break;
                }
                else
                    return nan( "error parsing roman numeral: invalid c char at position "
                            + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

                // whitespace
            case '\n':
            case '\r':
            case '\t':
            case ' ':
                return nan( "error parsing roman numeral: illegal whitespace char at position "
                        + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );

            case 'i':
                pos++;
                return units( total + 1, pos - 1 );
            case 'v':
                pos++;
                return units( total + 5, pos - 1 );

            default:
                return nan( "error parsing roman numeral: unrecognised char at position "
                        + ( pos + 1 ) + ": '" + new String( chars, 0, pos + 1 ) + "'" );
            }

            pos++;
        }

        return "" + total;
    }


    private Object hundreds( int runningTotal, int start )
    {
        return "NaN"; // TODO
    }


    /**
     * Always returns the string "NaN", optionally emitting a diagnostic message.
     * 
     * @param msg
     *            the message to emit (optionally)
     * @return the string "NaN"
     * @throws FunctionCallException
     */
    private String nan( String msg )
    {
        if( logger.isDebugEnabled() )
            logger.debug( msg + " " + emitDiagnosticMessages );

        if( emitDiagnosticMessages )
            throw new IllegalArgumentException( msg );

        return "NaN";
    }
}
