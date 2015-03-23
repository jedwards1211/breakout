package org.breakout.parse;

import org.andork.i18n.I18n;
import org.andork.util.ArrayUtils;

public class ParseExpectedMessage implements ParseErrorMessage
{
	private final Object[ ] expectedItems;

	public ParseExpectedMessage( Object ... expectedItems )
	{
		super( );
		this.expectedItems = expectedItems;
	}

	@Override
	public String getLocalizedMessage( )
	{
		if( expectedItems.length == 1 )
		{
			return I18n.forClass( ParseExpectedMessage.class ).getFormattedString( "message.singular" ,
				expectedItems[ 0 ] );
		}
		return I18n.forClass( ParseExpectedMessage.class ).getFormattedString( "message.plural" ,
			System.lineSeparator( ) + "  " + ArrayUtils.join( System.lineSeparator( ) + "  " , expectedItems ) + System.lineSeparator( ) );
	}
}
