package org.andork.jogl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class BufferHelper
{
	private final ArrayList<Object>	result	= new ArrayList<Object>( );
	
	public Object get( int offset )
	{
		return result.get( offset );
	}
	
	public Object getBackward( int offset )
	{
		return result.get( result.size( ) - 1 + offset );
	}
	
	public int count( )
	{
		return result.size( );
	}
	
	public BufferHelper put( byte ... values )
	{
		for( byte f : values )
		{
			result.add( f );
		}
		return this;
	}
	
	public BufferHelper put( short ... values )
	{
		for( short f : values )
		{
			result.add( f );
		}
		return this;
	}
	
	public BufferHelper put( char ... values )
	{
		for( char f : values )
		{
			result.add( f );
		}
		return this;
	}
	
	public BufferHelper put( int ... values )
	{
		for( int f : values )
		{
			result.add( f );
		}
		return this;
	}
	
	public BufferHelper put( long ... values )
	{
		for( long f : values )
		{
			result.add( f );
		}
		return this;
	}
	
	public BufferHelper put( float ... values )
	{
		for( float f : values )
		{
			result.add( f );
		}
		return this;
	}
	
	public BufferHelper put( double ... values )
	{
		for( double f : values )
		{
			result.add( f );
		}
		return this;
	}
	
	public BufferHelper putAsFloats( double ... values )
	{
		for( double d : values )
		{
			result.add( ( float ) d );
		}
		return this;
	}
	
	public BufferHelper putInts( int ... values )
	{
		for( int i : values )
		{
			result.add( i );
		}
		return this;
	}
	
	public void writeTo( ByteBuffer buffer )
	{
		for( Object n : result )
		{
			if( n instanceof Byte )
			{
				buffer.put( ( Byte ) n );
			}
			else if( n instanceof Short )
			{
				buffer.putShort( ( Short ) n );
			}
			else if( n instanceof Character )
			{
				buffer.putChar( ( Character ) n );
			}
			else if( n instanceof Integer )
			{
				buffer.putInt( ( Integer ) n );
			}
			else if( n instanceof Long )
			{
				buffer.putLong( ( Long ) n );
			}
			else if( n instanceof Float )
			{
				buffer.putFloat( ( Float ) n );
			}
			else if( n instanceof Double )
			{
				buffer.putDouble( ( Double ) n );
			}
			else if( n instanceof Short )
			{
				buffer.putShort( ( Short ) n );
			}
		}
	}
	
	public ByteBuffer toByteBuffer( )
	{
		int capacity = 0;
		
		for( Object n : result )
		{
			if( n instanceof Byte )
			{
				capacity++ ;
			}
			else if( n instanceof Short || n instanceof Character )
			{
				capacity += 2;
			}
			else if( n instanceof Integer || n instanceof Float )
			{
				capacity += 4;
			}
			else if( n instanceof Long || n instanceof Double )
			{
				capacity += 8;
			}
		}
		
		ByteBuffer buffer = ByteBuffer.allocateDirect( capacity );
		buffer.order( ByteOrder.nativeOrder( ) );
		writeTo( buffer );
		buffer.position( 0 );
		return buffer;
	}
}
