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

public class ValidationContext
{

    private String verbatimName;
    private String dtdSystemId;
    private String dtdPublicId;


    public String getVerbatimName()
    {
        return verbatimName;
    }


    public void setVerbatimName( String verbatimName )
    {
        this.verbatimName = verbatimName;
    }


    public String getDtdSystemId()
    {
        return dtdSystemId;
    }


    public void setDtdSystemId( String dtdSystemId )
    {
        this.dtdSystemId = dtdSystemId;
    }


    public String getDtdPublicId()
    {
        return dtdPublicId;
    }


    public void setDtdPublicId( String dtdPublicId )
    {
        this.dtdPublicId = dtdPublicId;
    }

}
