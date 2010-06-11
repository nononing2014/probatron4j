/*
 * Created on 30-Jun-2006
 */
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

/*
 * The procedure for calculating the check digit, which may be carried out automatically in a
 * computer, is as follows:
 * 
 * 1. Take the first seven digits of the ISSN (the check digit is the eighth and last digit): 0
 * 3 1 7 8 4 7 2. Take the weighting factors associated with each digit : 8 7 6 5 4 3 2 3.
 * Multiply each digit in turn by its weighting factor: 0 21 6 35 32 12 14 4. Add these numbers
 * together: 0+21+6+35+32+12+14 = 120 5. Divide this sum by the modulus 11: 120:11 =10 remainder
 * 10 6. Substract the remainder from 11: 11-10 = 1 7. Add the remainder, which is the check
 * digit, to the extreme right (low order) position of the base number of the ISSN: 0317-8471
 * 
 * If the remainder is 10, substitute an upper case X in the check digit position. If there is
 * no remainder, put a zero in the check digit position.
 */

public class ISSN
{
    private String issn;
    private static Logger logger = Logger.getLogger( ISSN.class );


    public ISSN( String issn ) throws MalformedISSNException
    {
        this.issn = issn.replace( "-", "" ); // remove hyphens
        if( this.issn == null || this.issn.length() != 8 )
        {
            logger.debug( "ISSN length=" + this.issn.length() );
            throw new MalformedISSNException( "Bad ISSN length:" + this.issn );
        }
    }


    public ISSN( int issn )
    {}


    public int getCheckSum()
    {
        int sum = 0;
        int weight = 0;

        for( int i = 0; i < 7; i++ )
        {
            switch( i ){
            case 0:
                weight = 8;
                break;
            case 1:
                weight = 7;
                break;
            case 2:
                weight = 6;
                break;
            case 3:
                weight = 5;
                break;
            case 4:
                weight = 4;
                break;
            case 5:
                weight = 3;
                break;
            case 6:
                weight = 2;
                break;
            }

            char c = issn.charAt( i );
            int n = Character.digit( c, 10 ); // convert character to digit
            sum += ( n * weight );
        }

        int cSum = sum % 11;

        return cSum == 0 ? cSum : 11 - cSum;
    }


    /**
     * @return whether this ISBN has a correct checksum
     */
    public boolean hasCorrectChecksum()
    {
        char checksumChar;
        int checksum = getCheckSum(); // get the calculated check sum digit

        if( checksum == 10 )
        {
            checksumChar = 'X';
        }
        else
        {
            checksumChar = Character.forDigit( checksum, 10 ); // convert digit to character
        }

        char fileDigit = issn.charAt( 7 ); // check sum char

        return checksumChar == fileDigit;
    }

}
