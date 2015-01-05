package org.andork.breakout.table;

import org.andork.q2.QObject;
import org.andork.q2.QSpec;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class Shot extends QSpec
{
	public static final Property<Integer>		index		= new ShotProperty<>( "index" , ShotPropertyType.INTEGER );
	public static final Property<String>		from		= new ShotProperty<>( "from" , ShotPropertyType.STRING );
	public static final Property<String>		to			= new ShotProperty<>( "to" , ShotPropertyType.STRING );
	public static final Property<ShotVector>	vector		= new ShotProperty<>( "vector" , ShotPropertyType.VECTOR );
	public static final Property<CrossSection>	fromXsect	= new ShotProperty<>( "fromXsect" ,
																ShotPropertyType.CROSS_SECTION );
	public static final Property<CrossSection>	toXsect		= new ShotProperty<>( "toXsect" ,
																ShotPropertyType.CROSS_SECTION );
	public static final Property<Unit<Length>>	distUnit	= new ShotProperty<>( "distUnit" , ShotPropertyType.UNIT );
	public static final Property<Unit<Angle>>	angleUnit	= new ShotProperty<>( "angleUnit" , ShotPropertyType.UNIT );

	public static final Shot					spec		= new Shot( );

	private Shot( )
	{
		super( index , from , to , vector , fromXsect , toXsect , distUnit , angleUnit );
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
