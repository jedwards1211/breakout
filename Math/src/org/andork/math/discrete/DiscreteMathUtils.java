package org.andork.math.discrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class DiscreteMathUtils
{
	public static void main( String[ ] args )
	{
		for( int[ ] m : generateMonomials( 3 , 3 ) )
		{
			System.out.println( Arrays.toString( m ) );
		}
	}
	
	public static int[ ][ ] generateMonomials( int powerSum , int variables )
	{
		List<int[ ]> result = new ArrayList<int[ ]>( );
		Stack<Integer> stack = new Stack<Integer>( );
		generateMonomials( result , stack , powerSum - 1 , variables );
		return result.toArray( new int[ result.size( ) ][ ] );
	}
	
	private static void generateMonomials( List<int[ ]> result , Stack<Integer> stack , int remainingPowerSum , int variables )
	{
		if( stack.size( ) + 1 == variables )
		{
			int[ ] monomial = new int[ variables ];
			for( int i = 0 ; i < variables - 1 ; i++ )
			{
				monomial[ i ] = stack.get( i );
			}
			monomial[ variables - 1 ] = remainingPowerSum;
			result.add( monomial );
		}
		else
		{
			for( int i = remainingPowerSum ; i >= 0 ; i-- )
			{
				stack.push( i );
				generateMonomials( result , stack , remainingPowerSum - i , variables );
				stack.pop( );
			}
		}
	}
}
