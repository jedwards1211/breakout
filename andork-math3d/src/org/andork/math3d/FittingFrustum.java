package org.andork.math3d;

import static org.andork.math3d.Vecmath.*;

import java.util.Arrays;

public class FittingFrustum
{
	public static void main( String[ ] args )
	{
		FittingFrustum frustum = new FittingFrustum( );
		
		frustum.init( new float[ ] { -1 , 0 , 0 } ,
				new float[ ] { -1 , 0 , -1 } ,
				new float[ ] { -1 , 0 , 1 } ,
				new float[ ] { -1 , 1 , 0 } ,
				new float[ ] { -1 , -1 , 0 } );
		
		frustum.addPoint( 0 , 0 , 1 );
		frustum.addPoint( 1 , 0 , 5 );
		
		float[ ] origin = new float[ 3 ];
		frustum.calculateOrigin( origin );
		
		System.out.println( Arrays.toString( origin ) );
	}
	
	final float[ ]		direction	= new float[ 3 ];
	
	final float[ ]		left		= new float[ 4 ];
	final float[ ]		right		= new float[ 4 ];
	final float[ ]		top			= new float[ 4 ];
	final float[ ]		bottom		= new float[ 4 ];
	
	final float[ ]		horizontal	= new float[ 4 ];
	final float[ ]		vertical	= new float[ 4 ];
	
	final float[ ]		horizontal2	= new float[ 4 ];
	final float[ ]		vertical2	= new float[ 4 ];
	
	final float[ ][ ]	matrix1		= new float[ ][ ] { left , right , horizontal };
	final float[ ][ ]	matrix2		= new float[ ][ ] { top , bottom , vertical };
	final float[ ][ ]	matrix3		= new float[ ][ ] { left , right , vertical2 };
	final float[ ][ ]	matrix4		= new float[ ][ ] { top , bottom , horizontal2 };
	
	final float[ ]		p0			= new float[ 3 ];
	final float[ ]		p1			= new float[ 3 ];
	
	final int[ ]		row_perms	= new int[ 3 ];
	
	final float			EPS			= 1e-4f;
	
	public void init( float[ ] direction , float[ ] left , float[ ] right , float[ ] top , float[ ] bottom )
	{
		setf( this.direction , direction );
		setf( this.left , left );
		setf( this.right , right );
		setf( this.top , top );
		setf( this.bottom , bottom );
		
		furtherInit( );
	}
	
	public void init( PickXform xform )
	{
		xform.xform( 1f , 1f , 2f , 2f , horizontal , 0 , direction , 0 );
		
		xform.xform( 0f , 1f , 2f , 2f , horizontal , 0 , left , 0 );
		xform.xform( 2f , 1f , 2f , 2f , horizontal , 0 , right , 0 );
		xform.xform( 1f , 0f , 2f , 2f , horizontal , 0 , top , 0 );
		xform.xform( 1f , 2f , 2f , 2f , horizontal , 0 , bottom , 0 );
		
		furtherInit( );
	}
	
	public void furtherInit( )
	{
		normalize3( left );
		normalize3( right );
		normalize3( top );
		normalize3( bottom );
		
		cross( right , left , vertical );
		cross( bottom , top , horizontal );
		cross( vertical , left , left );
		cross( right , vertical , right );
		cross( horizontal , top , top );
		cross( bottom , horizontal , bottom );
		
		left[ 3 ] = right[ 3 ] = top[ 3 ] = bottom[ 3 ] = Float.NaN;
		horizontal[ 3 ] = vertical[ 3 ] = 0f;
		
		setf( horizontal2 , horizontal );
		setf( vertical2 , vertical );
	}
	
	public void addPoint( float x , float y , float z )
	{
		float dl = x * left[ 0 ] + y * left[ 1 ] + z * left[ 2 ];
		if( Float.isNaN( left[ 3 ] ) || dl > left[ 3 ] )
		{
			left[ 3 ] = dl;
		}
		
		float dr = x * right[ 0 ] + y * right[ 1 ] + z * right[ 2 ];
		if( Float.isNaN( right[ 3 ] ) || dr > right[ 3 ] )
		{
			right[ 3 ] = dr;
		}
		
		float dt = x * top[ 0 ] + y * top[ 1 ] + z * top[ 2 ];
		if( Float.isNaN( top[ 3 ] ) || dt > top[ 3 ] )
		{
			top[ 3 ] = dt;
		}
		
		float db = x * bottom[ 0 ] + y * bottom[ 1 ] + z * bottom[ 2 ];
		if( Float.isNaN( bottom[ 3 ] ) || db > bottom[ 3 ] )
		{
			bottom[ 3 ] = db;
		}
	}
	
	void checkZeros( float[ ] row )
	{
		for( int i = 0 ; i < 3 ; i++ )
		{
			float f = row[ i ];
			if( Float.isNaN( f ) || Float.isInfinite( f ) || Math.abs( f ) > EPS )
			{
				throw new RuntimeException( "Malformed input or floating-point error: " + Arrays.toString( row ) );
			}
		}
	}
	
	public void calculateOrigin( float[ ] out )
	{
		reduce( matrix1 , 2 , null );
		checkZeros( horizontal );
		horizontal2[ 3 ] = -horizontal[ 3 ];
		
		reduce( matrix2 , 2 , null );
		checkZeros( vertical );
		vertical2[ 3 ] = -vertical[ 3 ];
		
		reduce( matrix3 , 3 , row_perms );
		for( int i = 0 ; i < 3 ; i++ )
		{
			p0[ i ] = matrix3[ row_perms[ i ] ][ 3 ];
		}
		
		reduce( matrix4 , 3 , row_perms );
		for( int i = 0 ; i < 3 ; i++ )
		{
			p1[ i ] = matrix4[ row_perms[ i ] ][ 3 ];
		}
		
		setf( out , dot3( p0 , direction ) < dot3( p1 , direction ) ? p0 : p1 );
	}
	
	void reduce( float[ ][ ] A , int rows , int[ ] row_perms )
	{
		for( int i = 0 ; i < rows ; i++ )
		{
			// find the largest pivot (first nonzero column) in row i
			int j;
			for( j = 0 ; j < 3 ; j++ )
			{
				if( A[ i ][ j ] != 0 )
				{
					break;
				}
			}
			
			if( j == 3 )
			{
				throw new RuntimeException( "zero rows not allowed" );
			}
			
			if( row_perms != null )
			{
				row_perms[ i ] = j;
			}
			
			// divide all values in row i by the pivot
			for( int k = j + 1 ; k < 4 ; k++ )
			{
				A[ i ][ k ] /= A[ i ][ j ];
			}
			A[ i ][ j ] = 1f;
			
			// reduce the other rows by row i
			for( int h = 0 ; h < 3 ; h++ )
			{
				if( h == i )
				{
					continue;
				}
				float f = -A[ h ][ j ];
				A[ h ][ j ] = 0f;
				
				for( int k = j + 1 ; k < 4 ; k++ )
				{
					A[ h ][ k ] += f * A[ i ][ k ];
				}
			}
		}
	}
}
