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

package org.probatron.functions;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.functions.ExtensionFunctionCall;
import net.sf.saxon.functions.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SingletonIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

import org.probatron.Utils;

import com.griffinbrown.xmltool.utils.ISBN;
import com.griffinbrown.xmltool.utils.MalformedISBNException;

@SuppressWarnings("serial")
public class IsValidIsbn extends ExtensionFunctionDefinition
{
    private static StructuredQName funcName = new StructuredQName( "pr",
            Utils.PROBATRON_FUNCTION_NAME, "is-valid-isbn" );


    public StructuredQName getFunctionQName()
    {
        return funcName;
    }


    public int getMinimumNumberOfArguments()
    {
        return 1;
    }


    public int getMaximumNumberOfArguments()
    {
        return 1;
    }


    public SequenceType[] getArgumentTypes()
    {
        return new SequenceType[] { SequenceType.SINGLE_STRING };
    }


    public SequenceType getResultType( SequenceType[] suppliedArgumentTypes )
    {
        return SequenceType.SINGLE_BOOLEAN;
    }


    public ExtensionFunctionCall makeCallExpression()
    {
        return new IsValidIsbnCall();
    }

    private static class IsValidIsbnCall extends ExtensionFunctionCall
    {

        public SequenceIterator call( SequenceIterator[] arguments, XPathContext context )
                throws XPathException
        {
            boolean value = true;

            ISBN isbn = null;
            try
            {
                SequenceIterator iter = arguments[ 0 ];
                String candidate = iter.next().getStringValue();

                isbn = new ISBN( candidate );
            }
            catch( MalformedISBNException m )
            {
                value = false;
            }

            if( isbn != null && !isbn.hasCorrectChecksum() )
            {
                value = false;
            }

            return SingletonIterator.makeIterator( value ? BooleanValue.TRUE
                    : BooleanValue.FALSE );
        }

    }

}
