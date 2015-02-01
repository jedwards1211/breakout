package org.breakout.wallsimport;

import org.andork.unit.Angle;
import org.andork.unit.UnitizedDouble;

public enum CardinalDirection
{
	NORTH( new UnitizedDouble<>( 0.0 , Angle.degrees ) )
	{
		public CardinalDirection opposite( )
		{
			return SOUTH;
		}
	},
	EAST( new UnitizedDouble<>( 90.0 , Angle.degrees ) )
	{
		public CardinalDirection opposite( )
		{
			return WEST;
		}
	},
	SOUTH( new UnitizedDouble<>( 180.0 , Angle.degrees ) )
	{
		public CardinalDirection opposite( )
		{
			return NORTH;
		}
	},
	WEST( new UnitizedDouble<>( 270.0 , Angle.degrees ) )
	{
		public CardinalDirection opposite( )
		{
			return EAST;
		}
	},
	;

	public final UnitizedDouble<Angle>	angle;

	private CardinalDirection( UnitizedDouble<Angle> angle )
	{
		this.angle = angle;
	}

	public abstract CardinalDirection opposite( );

	public static CardinalDirection fromCharacter( String s )
	{
		if( s.length( ) == 0 )
		{
			throw new IllegalArgumentException( "invalid cardinal direction: " + s );
		}
		return fromCharacter( s.charAt( 0 ) );
	}

	public static CardinalDirection fromCharacter( char c )
	{
		switch( c )
		{
		case 'n':
		case 'N':
			return CardinalDirection.NORTH;
		case 's':
		case 'S':
			return CardinalDirection.SOUTH;
		case 'e':
		case 'E':
			return CardinalDirection.EAST;
		case 'w':
		case 'W':
			return CardinalDirection.WEST;
		default:
			throw new IllegalArgumentException( "Invalid cardinal direction character: " + c );
		}
	}
}
