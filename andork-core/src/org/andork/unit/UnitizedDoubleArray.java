package org.andork.unit;

public class UnitizedDoubleArray<T extends UnitType<T>>
{
	private final double[ ]	values;
	public final Unit<T>	unit;
	
	public UnitizedDoubleArray( int length , Unit<T> unit )
	{
		super( );
		this.values = new double[ length ];
		this.unit = unit;
	}
	
	public static <T extends UnitType<T>> void copy( UnitizedDoubleArray<T> src , int srcIndex ,
			UnitizedDoubleArray<T> dest , int destIndex , int length )
	{
		for( int i = 0 ; i < length ; i++ )
		{
			dest.set( destIndex + i , src.values[ srcIndex + i ] , src.unit );
		}
	}
	
	public int length( )
	{
		return values.length;
	}
	
	public void set( int index , UnitizedDouble<T> value )
	{
		values[ index ] = value.doubleValue( unit );
	}
	
	public void set( int index , double value , Unit<T> unit )
	{
		if( unit == this.unit )
		{
			values[ index ] = value;
		}
		else
		{
			values[ index ] = this.unit.type.convert( value , unit , this.unit );
		}
	}
	
	public double get( int index , Unit<T> unit )
	{
		if( unit == this.unit )
		{
			return values[ index ];
		}
		return this.unit.type.convert( values[ index ] , this.unit , unit );
	}
	
	public static <T extends UnitType<T>> String toString( UnitizedDoubleArray<T> array )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( '[' );
		for( int i = 0 ; i < array.length( ) ; i++ )
		{
			if( i > 0 )
			{
				sb.append( ", " );
			}
			
			sb.append( array.get( i , array.unit ) );
		}
		
		if( array.length( ) > 0 )
		{
			sb.append( ' ' );
		}
		sb.append( array.unit );
		sb.append( ']' );
		return sb.toString( );
	}
}