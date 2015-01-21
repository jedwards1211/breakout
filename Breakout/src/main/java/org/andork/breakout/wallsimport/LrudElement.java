package org.andork.breakout.wallsimport;

import java.util.HashMap;
import java.util.Map;

public enum LrudElement
{
	L
	{
		@Override
		void visit( LrudElementVisitor visitor )
		{
			visitor.visitLeft( );
		}
	},
	R
	{
		@Override
		void visit( LrudElementVisitor visitor )
		{
			visitor.visitRight( );
		}
	},
	U
	{
		@Override
		void visit( LrudElementVisitor visitor )
		{
			visitor.visitUp( );
		}
	},
	D
	{
		@Override
		void visit( LrudElementVisitor visitor )
		{
			visitor.visitDown( );
		}
	};

	private static Map<String, LrudElement>	lookup	= new HashMap<>( );

	static
	{
		for( LrudElement elem : values( ) )
		{
			lookup.put( elem.name( ).toLowerCase( ) , elem );
		}
	}

	/**
	 * @return the {@link LrudElement} with the given {@code name}, case insensitive, or {@code null} if none has the
	 *         given name.
	 */
	public LrudElement forName( String name )
	{
		return lookup.get( name );
	}

	abstract void visit( LrudElementVisitor visitor );
}
