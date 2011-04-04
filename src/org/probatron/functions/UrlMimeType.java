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

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.functions.ExtensionFunctionCall;
import net.sf.saxon.functions.ExtensionFunctionDefinition;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SingletonIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import org.probatron.Utils;

import com.griffinbrown.xmltool.utils.HeuriSniff;

@SuppressWarnings("serial")
public class UrlMimeType extends ExtensionFunctionDefinition
{
    private static StructuredQName funcName = new StructuredQName( "pr",
            Utils.PROBATRON_FUNCTION_NAME, "url-mime-type" );


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
        return 2;
    }


    public SequenceType[] getArgumentTypes()
    {
        return new SequenceType[] { SequenceType.SINGLE_STRING, SequenceType.OPTIONAL_NODE };
    }


    public SequenceType getResultType( SequenceType[] suppliedArgumentTypes )
    {
        return SequenceType.SINGLE_STRING;
    }


    public ExtensionFunctionCall makeCallExpression()
    {
        return new UrlMimeTypeCall();
    }

    private static class UrlMimeTypeCall extends ExtensionFunctionCall
    {

        public SequenceIterator call( SequenceIterator[] arguments, XPathContext context )
                throws XPathException
        {
            SequenceIterator iter = arguments[ 0 ];
            String url = iter.next().getStringValue();
            String baseUri = null;

            NodeInfo item = ( NodeInfo )iter.next();
            if( item != null )
            {
                baseUri = item.getBaseURI();
            }
            else
            // no second arg
            {
                baseUri = ( ( NodeInfo )context.getContextItem() ).getBaseURI();
            }

            String mime = "";

            try
            {
                mime = HeuriSniff.sniffMimeTypeFromUrl( new URL( new URL( baseUri ), url ) );

            }
            catch( MalformedURLException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return SingletonIterator.makeIterator( new StringValue( mime ) );

        }

    }

}
