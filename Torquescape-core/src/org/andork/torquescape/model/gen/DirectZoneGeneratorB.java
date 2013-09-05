package org.andork.torquescape.model.gen;


public class DirectZoneGeneratorB extends DirectZoneGenerator
{
	DirectZoneGeneratorB( )
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
		visit( Float.floatToRawIntBits( f ) );
		
	}
	
	@Override
	public void visit( long l )
	{
		buffer.put( ( byte ) ( l >> 56 ) );
		buffer.put( ( byte ) ( l >> 48 ) );
		buffer.put( ( byte ) ( l >> 40 ) );
		buffer.put( ( byte ) ( l >> 32 ) );
		buffer.put( ( byte ) ( l >> 24 ) );
		buffer.put( ( byte ) ( l >> 16 ) );
		buffer.put( ( byte ) ( l >> 8 ) );
		buffer.put( ( byte ) l );
	}
	
	@Override
	public void visit( int i )
	{
		buffer.put( ( byte ) ( i >> 24 ) );
		buffer.put( ( byte ) ( i >> 16 ) );
		buffer.put( ( byte ) ( i >> 8 ) );
		buffer.put( ( byte ) i );
	}
	
	@Override
	public void visit( char c )
	{
		buffer.put( ( byte ) ( c >> 8 ) );
		buffer.put( ( byte ) c );
	}
	
	@Override
	public void visit( short s )
	{
		buffer.put( ( byte ) ( s >> 8 ) );
		buffer.put( ( byte ) s );
	}
	
	@Override
	public void visit( byte b )
	{
		buffer.put( b );
	}
}
