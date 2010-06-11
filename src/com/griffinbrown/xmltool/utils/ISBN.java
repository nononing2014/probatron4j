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

/**
 * @author andrews
 *
 * Class to represent a *10-digit* ISBN.
 */
public class ISBN
{
	private String isbn = null;
	
	/**
	 * Constructor.
	 */
	public ISBN( String isbn ) throws MalformedISBNException
	{
		this.isbn = isbn;
		if( isbn == null || isbn.length() != 10 )
		{
			throw new MalformedISBNException( "Bad ISBN length" );
		}
	}
	
	/**
	 * Integer constructor.
	 * @param isbn the isbn to be parsed
	 */
	public ISBN( int isbn )
	{}
	
	/**
	 * @param isbn string of the ISBN to be parsed
	 * @return checksum for this ISBN 
	 */
	public int getCheckSum()
	{
	   int	sum = 0;
	   int	weight = 0;

		for( int i = 0; i <= 8; i++ )
		{
		   switch( i )
		   {
			   case 0: weight = 10;
							   break;
			   case 1: weight = 9;
							   break;
			   case 2: weight = 8;
							   break;
			   case 3: weight = 7;
							   break;
			   case 4: weight = 6;
							   break;
			   case 5: weight = 5;
							   break;
			   case 6: weight = 4;
							   break;
			   case 7: weight = 3;
							   break;
			   case 8: weight = 2;
							   break;
			}

		   char c = isbn.charAt( i );
		   int n = Character.digit( c, 10 ); //convert character to digit
		   sum += ( n * weight );
		}
		
		int cSum = sum % 11;
		
		return cSum == 0 ? cSum : 11 - ( sum % 11 );
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
			checksumChar='X';
		}			
		else
		{
			checksumChar = Character.forDigit( checksum, 10 ); // convert digit to character
		}			

		char fileDigit = isbn.charAt( 9 ); // check sum char

		return checksumChar == fileDigit;
   }
   
   public String toString()
   {
		return this.isbn;
   }
   
}
