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

import com.griffinbrown.xmltool.utils.ISBN13;
import com.griffinbrown.xmltool.utils.MalformedISBNException;

@SuppressWarnings("serial")
public class IsValidIsbn13 extends ExtensionFunctionDefinition
{
    private static StructuredQName funcName = new StructuredQName( "pr",
            Utils.PROBATRON_FUNCTION_NAME, "is-valid-isbn-13" );


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
        return new IsValidIsbn13Call();
    }

    private static class IsValidIsbn13Call extends ExtensionFunctionCall
    {

        public SequenceIterator call( SequenceIterator[] arguments, XPathContext context )
                throws XPathException
        {
            boolean value = true;

            ISBN13 isbn = null;
            try
            {
                SequenceIterator iter = arguments[ 0 ];
                String candidate = iter.next().getStringValue();

                isbn = new ISBN13( candidate );
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
