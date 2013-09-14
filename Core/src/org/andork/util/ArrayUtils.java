package org.andork.util;

public class ArrayUtils
{
	
	/**
	 * Changes the block size of an array, i.e. pads or truncates blocks of elements.
	 */
	public static float[ ] changeBlockSize( float[ ] src , int srcBlockSize , int destBlockSize )
	{
		if( ( src.length % srcBlockSize ) != 0 )
		{
			throw new IllegalArgumentException( "src does not have a block size of " + srcBlockSize );
		}
		final float[ ] dest = new float[ src.length * destBlockSize / srcBlockSize ];
		final int minBlockSize = Math.min( srcBlockSize , destBlockSize );
		final int srcPad = srcBlockSize - destBlockSize;
		final int destPad = destBlockSize - srcBlockSize;
		
		int s = 0, d = 0;
		if( srcPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize + srcPad;
				d += minBlockSize;
			}
		}
		else if( destPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize;
				d += minBlockSize + destPad;
			}
		}
		else
		{
			System.arraycopy( src , 0 , dest , 0 , src.length );
		}
		
		return dest;
	}
	
	/**
	 * Changes the block size of an array, i.e. pads or truncates blocks of elements.
	 */
	public static double[ ] changeBlockSize( double[ ] src , int srcBlockSize , int destBlockSize )
	{
		if( ( src.length % srcBlockSize ) != 0 )
		{
			throw new IllegalArgumentException( "src does not have a block size of " + srcBlockSize );
		}
		final double[ ] dest = new double[ src.length * destBlockSize / srcBlockSize ];
		final int minBlockSize = Math.min( srcBlockSize , destBlockSize );
		final int srcPad = srcBlockSize - destBlockSize;
		final int destPad = destBlockSize - srcBlockSize;
		
		int s = 0, d = 0;
		if( srcPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize + srcPad;
				d += minBlockSize;
			}
		}
		else if( destPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize;
				d += minBlockSize + destPad;
			}
		}
		else
		{
			System.arraycopy( src , 0 , dest , 0 , src.length );
		}
		
		return dest;
	}
	
	/**
	 * Changes the block size of an array, i.e. pads or truncates blocks of elements.
	 */
	public static byte[ ] changeBlockSize( byte[ ] src , int srcBlockSize , int destBlockSize )
	{
		if( ( src.length % srcBlockSize ) != 0 )
		{
			throw new IllegalArgumentException( "src does not have a block size of " + srcBlockSize );
		}
		final byte[ ] dest = new byte[ src.length * destBlockSize / srcBlockSize ];
		final int minBlockSize = Math.min( srcBlockSize , destBlockSize );
		final int srcPad = srcBlockSize - destBlockSize;
		final int destPad = destBlockSize - srcBlockSize;
		
		int s = 0, d = 0;
		if( srcPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize + srcPad;
				d += minBlockSize;
			}
		}
		else if( destPad > 0 )
		{
			while( s < src.length )
			{
				System.arraycopy( src , s , dest , d , minBlockSize );
				s += minBlockSize;
				d += minBlockSize + destPad;
			}
		}
		else
		{
			System.arraycopy( src , 0 , dest , 0 , src.length );
		}
		
		return dest;
	}
	
	public static int max( int[ ] values )
	{
		int max = Integer.MIN_VALUE;
		for( final int value : values )
		{
			max = Math.max( max , value );
		}
		return max;
		
	}
	
	public static String prettyPrintAsNumbers( char[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns - 1 && i < end ; col++ , i++ )
			{
				sb.append( String.format( elemFormat , ( int ) a[ i ] ) ).append( "  " );
			}
			if( i < end )
			{
				sb.append( String.format( elemFormat , ( int ) a[ i++ ] ) ).append( '\n' );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( int[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns - 1 && i < end ; col++ , i++ )
			{
				sb.append( String.format( elemFormat , a[ i ] ) ).append( "  " );
			}
			if( i < end )
			{
				sb.append( String.format( elemFormat , a[ i++ ] ) ).append( '\n' );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( float[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns - 1 && i < end ; col++ , i++ )
			{
				sb.append( String.format( elemFormat , a[ i ] ) ).append( "  " );
			}
			if( i < end )
			{
				sb.append( String.format( elemFormat , a[ i++ ] ) ).append( '\n' );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( double[ ] a , int columns , int start , int end , int newlineInterval , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		int rows = 0;
		
		int i = start;
		while( i < end )
		{
			sb.append( '[' ).append( String.format( indexFormat , i ) ).append( "] " );
			for( int col = 0 ; col < columns - 1 && i < end ; col++ , i++ )
			{
				sb.append( String.format( elemFormat , a[ i ] ) ).append( "  " );
			}
			if( i < end )
			{
				sb.append( String.format( elemFormat , a[ i++ ] ) ).append( '\n' );
			}
			
			if( ++rows == newlineInterval )
			{
				rows = 0;
				sb.append( '\n' );
			}
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( double[ ][ ] a , String elemFormat )
	{
		StringBuffer sb = new StringBuffer( );
		
		int indexWidth = ( int ) Math.log10( a.length ) + 1;
		String indexFormat = "%" + indexWidth + "d";
		
		for( int row = 0 ; row < a.length ; row++ )
		{
			sb.append( '[' ).append( String.format( indexFormat , row ) ).append( "] " );
			
			for( int col = 0 ; col < a[ row ].length ; col++ )
			{
				sb.append( String.format( elemFormat , a[ row ][ col ] ) ).append( "  " );
			}
			
			sb.append( '\n' );
		}
		
		return sb.toString( );
	}
}
