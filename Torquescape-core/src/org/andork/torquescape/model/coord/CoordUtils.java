package org.andork.torquescape.model.coord;

public class CoordUtils
{
	/**
	 * Generates a polygon with one point per corner, so that the generated normals will make the corners appear smooth.
	 * 
	 * @param npoints
	 *            the number of polygon points.
	 * @param rad
	 *            the radius of the polygon.
	 * @return an x,y,z array of polygon points.
	 */
	public static float[ ] generateSmoothPolygon( int npoints , float rad )
	{
		float[ ] result = new float[ npoints * 3 ];
		int i = 0;
		int a = 0;
		while( a < npoints )
		{
			float angle = ( float ) Math.PI * 2 * a / npoints;
			result[ i++ ] = rad * ( float ) Math.cos( angle );
			result[ i++ ] = rad * ( float ) Math.sin( angle );
			i++ ;
			a++ ;
		}
		
		return result;
	}
	
	/**
	 * Generates a polygon with two points per corner, so that the generated normals will make the corners appear sharp.
	 * 
	 * @param npoints
	 *            the number of polygon points.
	 * @param rad
	 *            the radius of the polygon.
	 * @return an x,y,z array of polygon points.
	 */
	public static float[ ] generateSharpPolygon( int npoints , float rad )
	{
		float[ ] result = new float[ npoints * 6 ];
		int i = 0;
		int a = 0;
		while( a < npoints )
		{
			float angle = ( float ) Math.PI * 2 * a / npoints;
			float x = result[ i++ ] = rad * ( float ) Math.cos( angle );
			float y = result[ i++ ] = rad * ( float ) Math.sin( angle );
			i++ ;
			result[ i++ ] = x;
			result[ i++ ] = y;
			i++ ;
			a++ ;
		}
		
		return result;
	}
	
	public static float[ ] reverse3( float[ ] coords )
	{
		float[ ] result = new float[ coords.length ];
		for( int i = 0 , j = coords.length - 3 ; i < coords.length && j >= 0 ; i += 3 , j -= 3 )
		{
			result[ i ] = coords[ j ];
			result[ i + 1 ] = coords[ j + 1 ];
			result[ i + 2 ] = coords[ j + 2 ];
		}
		return result;
	}
}
