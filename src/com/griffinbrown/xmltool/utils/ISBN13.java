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

import java.util.regex.Pattern;

/**
 * @author andrews Class to represent a <strong>13-digit</strong> ISBN. For details, refer to
 *         5th edn of ISBN Users' Manual (2005), especially section 4.5. $Id: ISBN13.java,v 1.1
 *         2006/07/12 11:02:15 GBDP\andrews Exp $
 */
public class ISBN13
{
    private String isbn;
    private int checkDigit; // 13th digit of string passed in
    private int checkSum;
    private static final Pattern PRODUCTION = Pattern.compile( "[0-9]{13}" ); // 13 digits
    private int expectedCheckDigit = -1; // as derived from checksum


    /**
     * @param isbn
     * @throws MalformedISBNException
     */
    public ISBN13( String isbn ) throws MalformedISBNException
    {
        this.isbn = isbn;

        if( isbn == null )
        {
            throw new MalformedISBNException( "null ISBN" );
        }
        else if( !PRODUCTION.matcher( isbn ).matches() )
        {
            throw new MalformedISBNException( "ISBN must be 13 digits" );
        }

        this.checkDigit = Character.digit( isbn.charAt( 12 ), 10 );
        this.checkSum = setCheckSum();
        this.expectedCheckDigit = calculateCheckDigit();
    }


    /**
     * @param isbn
     */
    public ISBN13( int isbn )
    {

    }


    public int getCheckSum()
    {
        return this.checkSum;
    }


    /**
     * "Each of the first 12 digits of the ISBN is alternately multiplied by 1 and 3."
     * 
     * @return the checksum for this ISBN
     */
    private int setCheckSum()
    {
        char c;
        int n;
        int sum = 0; // sum of the weighted product
        for( int i = 0; i <= 11; i++ )
        {
            c = isbn.charAt( i );
            n = Character.digit( c, 10 ); // convert character to digit
            sum += ( i % 2 == 0 ? n : n * 3 ); // multiply alternately by 1 and 3
        }
        return sum;
    }


    /**
     * "The sum of the weighted products of the first 12 digits plus the check digit must be
     * divisible by 10 without a remainder for the ISBN to be valid."
     * 
     * @return whether the checksum plus the calculated check digit modulus 10 is equal to 0
     */
    public boolean hasCorrectChecksum()
    {
        return ( this.checkSum + this.expectedCheckDigit ) % 10 == 0;
    }


    /**
     * Whether the actual check digit equals its expected value.
     * 
     * @return whether the check digit in the isbn passed in equals the value calculated from
     *         the isbn's checksum
     */
    public boolean hasCorrectCheckDigit()
    {
        return this.checkDigit == this.expectedCheckDigit;
    }


    /**
     * "The check digit is equal to 10 minus the remainder resulting from dividing the sum of
     * the weighted products of the first 12 digits by 10 with one exception. If this
     * calculation results in an apparent check digit of 10, the check digit is 0."
     * 
     * @return expected check digit
     */
    private int calculateCheckDigit()
    {
        int cd = 10 - ( this.checkSum % 10 );
        if( cd == 10 )
            cd = 0;
        return cd;
    }


    public boolean isValid()
    {
        return ( hasCorrectCheckDigit() && hasCorrectChecksum() );
    }
}