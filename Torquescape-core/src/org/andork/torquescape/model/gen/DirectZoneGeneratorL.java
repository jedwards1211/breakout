package org.andork.torquescape.model.gen;

public class DirectZoneGeneratorL extends DirectZoneGenerator
{
	DirectZoneGeneratorL( )
	{
		
	}
	
	@Override
	public void visit( double d )
	{
		visit( Double.doubleToRawLongBits( d ) );
	}
	
	@Override
	public void visit( float f )
	{
//		visit( Float.floatToRawIntBits( f ) );
		buffer.putFloat( f );
		
	}
	
	@Override
	public void visit( long l )
	{
		buffer.put( ( byte ) l );
		buffer.put( ( byte ) ( l >> 8 ) );
		buffer.put( ( byte ) ( l >> 16 ) );
		buffer.put( ( byte ) ( l >> 24 ) );
		buffer.put( ( byte ) ( l >> 32 ) );
		buffer.put( ( byte ) ( l >> 40 ) );
		buffer.put( ( byte ) ( l >> 48 ) );
		buffer.put( ( byte ) ( l >> 56 ) );
	}
	
	@Override
	public void visit( int i )
	{
//		buffer.put( ( byte ) i );
//		buffer.put( ( byte ) ( i >> 8 ) );
//		buffer.put( ( byte ) ( i >> 16 ) );
//		buffer.put( ( byte ) ( i >> 24 ) );
		buffer.putInt( i );
	}
	
	@Override
	public void visit( char c )
	{
		buffer.put( ( byte ) c );
		buffer.put( ( byte ) ( c >> 8 ) );
	}
	
	@Override
	public void visit( short s )
	{
		buffer.put( ( byte ) s );
		buffer.put( ( byte ) ( s >> 8 ) );
	}
	
	@Override
	public void visit( byte b )
	{
		buffer.put( b );
	}
}
