package org.andork.breakout.table;

import org.andork.q2.QObject;
import org.andork.q2.QSpec;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class Shot extends QSpec
{
	public static final Property<Integer>		index		= property( "index" , Integer.class );
	public static final Property<String>		from		= property( "from" , String.class );
	public static final Property<String>		to			= property( "to" , String.class );
	public static final Property<ShotVector>	vector		= property( "vector" , ShotVector.class );
	public static final Property<CrossSection>	fromXsect	= property( "fromXsect" , CrossSection.class );
	public static final Property<CrossSection>	toXsect		= property( "toXsect" , CrossSection.class );
	public static final Property<Unit<Length>>	distUnit	= property( "distUnit" , Unit.class );
	public static final Property<Unit<Angle>>	angleUnit	= property( "angleUnit" , Unit.class );

	public static final Shot					spec		= new Shot( );

	private Shot( )
	{
		super( index , from , to , vector , fromXsect , toXsect , distUnit , angleUnit );
	}

	protected Shot( Property<?> ... properties )
	{
		super( spec , properties );
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	public boolean equals( QObject a , Object b )
	{
		return a == b;
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	public int hashCode( QObject o )
	{
		return System.identityHashCode( o );
	}
}
