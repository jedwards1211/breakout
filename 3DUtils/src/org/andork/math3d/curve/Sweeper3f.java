package org.andork.math3d.curve;

import java.util.Collection;
import java.util.Iterator;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.TransformComputer3f;

/**
 * A class for computing sweep transforms, e.g. transforming from (depth,offset) sweep geometry to 3d coordinates.
 */
public class Sweeper3f
{
	private Point3f				pf1			= new Point3f( );
	private Vector3f			vf1			= new Vector3f( );
	private Vector3f			vf2			= new Vector3f( );
	private Vector3f			vf3			= new Vector3f( );
	private TransformComputer3f	tc			= new TransformComputer3f( );
	
	private float				lastAngle	= 0;
	private float				lastxf		= 1;
	private float				lastyf		= 0;
	
	/**
	 * Transforms a depth/offset point on the sweep into 3D coordinates, storing them in <code>result</code>
	 * 
	 * @return whether the operation was successful or not.
	 */
	public void apply( ICurveWithNormals3f sweep , float depth , float lateralOffset , float angle , Point3f result )
	{
		sweep.getPoint( depth , result );
		sweep.getBinormal( depth , vf1 );
		sweep.getNormal( depth , vf2 );
		
		if( angle != lastAngle )
		{
			lastAngle = angle;
			lastxf = ( float ) Math.cos( angle );
			lastyf = ( float ) Math.sin( angle );
		}
		result.scaleAdd( lateralOffset * lastxf , vf1 , result );
		result.scaleAdd( lateralOffset * lastyf , vf2 , result );
	}
	
	/**
	 * Transforms a collection of depth/offset points on the sweep into 3D world coordinates, storing them in the result collection. The result must be the same
	 * size as the data and contain no null elements.
	 * 
	 * @return whether the operation was successful or not.
	 */
	public <D extends Collection<Point2f>, T extends Collection<Point3f>> void apply( ICurveWithNormals3f sweep , D data , float lateralOffset , float angle , T result )
	{
		if( data.size( ) != result.size( ) )
		{
			throw new IllegalArgumentException( "result must be the same size as data" );
		}
		
		if( angle != lastAngle )
		{
			lastAngle = angle;
			lastxf = ( float ) Math.cos( angle );
			lastyf = ( float ) Math.sin( angle );
		}
		
		final Iterator<Point2f> d = data.iterator( );
		final Iterator<Point3f> t = result.iterator( );
		
		float depth = 0;
		sweep.getPoint( depth , pf1 );
		sweep.getBinormal( depth , vf1 );
		sweep.getNormal( depth , vf2 );
		
		while( d.hasNext( ) && t.hasNext( ) )
		{
			final Point2f datum = d.next( );
			final Point3f trans = t.next( );
			
			if( depth != datum.x )
			{
				depth = datum.x;
				sweep.getPoint( depth , pf1 );
				sweep.getBinormal( depth , vf1 );
				sweep.getNormal( depth , vf2 );
			}
			
			// trans.scaleAdd( datum.y , vf1 , pf1 );
			float offset = datum.y + lateralOffset;
			trans.scaleAdd( offset * lastxf , vf1 , pf1 );
			trans.scaleAdd( offset * lastyf , vf2 , trans );
		}
	}
	
	/**
	 * Transforms a collection of depth/offset points on the sweep into 3D world coordinates, storing them in the result array. The number of coordinates must
	 * be equal to the number of data points or an exception will be thrown.
	 * 
	 * @param data
	 *            an array of size 2*N containing data points in depth-value pairs
	 * @param angle
	 *            sweep angle
	 * @param coords
	 *            an array of size 3*N to store the 3D coordinates into
	 * @return whether the operation was successful or not.
	 */
	public void apply( ICurveWithNormals3f sweep , float[ ] data , float lateralOffset , float angle , float[ ] coords )
	{
		if( data.length * 3 / 2 != coords.length )
		{
			throw new IllegalArgumentException( "result length must be 3/2 times data length" );
		}
		
		if( angle != lastAngle )
		{
			lastAngle = angle;
			lastxf = ( float ) Math.cos( angle );
			lastyf = ( float ) Math.sin( angle );
		}
		
		float depth = 0;
		sweep.getPoint( depth , pf1 );
		sweep.getBinormal( depth , vf1 );
		sweep.getNormal( depth , vf2 );
		
		int d = 0;
		int r = 0;
		for( d = 0 , r = 0 ; d < data.length && r < coords.length ; d += 2 , r += 3 )
		{
			if( depth != data[ d ] )
			{
				depth = data[ d ];
				sweep.getPoint( depth , pf1 );
				sweep.getBinormal( depth , vf1 );
				sweep.getNormal( depth , vf2 );
			}
			
			float offset = data[ d + 1 ] + lateralOffset;
			
			// vf3.scaleAdd( data[ d + 1 ] , vf1 , pf1 );
			vf3.scaleAdd( offset * lastxf , vf1 , pf1 );
			vf3.scaleAdd( offset * lastyf , vf2 , vf3 );
			
			coords[ r ] = vf3.x;
			coords[ r + 1 ] = vf3.y;
			coords[ r + 2 ] = vf3.z;
		}
	}
	
	/**
	 * Transforms a collection of depth/offset points on the sweep into 3D world coordinates and normals, storing them in the result arrays. The number of
	 * coordinates and normals must be equal to the number of data points or an exception will be thrown.
	 * 
	 * @param data
	 *            an array of size 2*N containing data points in depth-value pairs
	 * @param angle
	 *            sweep angle
	 * @param coords
	 *            an array of size 3*N to store the 3D coordinates into
	 * @param normals
	 *            an array of size 3*N to store the 3D normals into
	 * @return whether the operation was successful or not.
	 */
	public void apply( ICurveWithNormals3f sweep , float[ ] data , float lateralOffset , float angle , float[ ] coords , float[ ] normals )
	{
		if( data.length * 3 / 2 != coords.length )
		{
			throw new IllegalArgumentException( "result length must be 3/2 times data length" );
		}
		if( coords.length != normals.length )
		{
			throw new IllegalArgumentException( "coords must be the same length as normals" );
		}
		
		if( angle != lastAngle )
		{
			lastAngle = angle;
			lastxf = ( float ) Math.cos( angle );
			lastyf = ( float ) Math.sin( angle );
		}
		
		float xf90 = -lastyf;
		float yf90 = lastxf;
		
		float depth = 0;
		sweep.getPoint( depth , pf1 );
		sweep.getBinormal( depth , vf1 );
		sweep.getNormal( depth , vf2 );
		
		int d = 0;
		int r = 0;
		for( d = 0 , r = 0 ; d < data.length && r < coords.length ; d += 2 , r += 3 )
		{
			if( depth != data[ d ] )
			{
				depth = data[ d ];
				sweep.getPoint( depth , pf1 );
				sweep.getBinormal( depth , vf1 );
				sweep.getNormal( depth , vf2 );
			}
			
			float offset = data[ d + 1 ] + lateralOffset;
			
			// vf3.scaleAdd( data[ d + 1 ] , vf1 , pf1 );
			vf3.scaleAdd( offset * lastxf , vf1 , pf1 );
			vf3.scaleAdd( offset * lastyf , vf2 , vf3 );
			coords[ r ] = vf3.x;
			coords[ r + 1 ] = vf3.y;
			coords[ r + 2 ] = vf3.z;
			
			vf3.scale( xf90 , vf1 );
			vf3.scaleAdd( yf90 , vf2 , vf3 );
			normals[ r ] = vf3.x;
			normals[ r + 1 ] = vf3.y;
			normals[ r + 2 ] = vf3.z;
		}
	}
	
	/**
	 * Transforms a collection of depth/offset points on the sweep into 3D world coordinates, storing them in the result collection. The result must be the same
	 * size as the data and contain no null elements.
	 * 
	 * @return whether the operation was successful or not.
	 */
	public void apply( ICurveWithNormals3f sweep , Point2f data[] , float lateralOffset , float angle , Point3f trans[] )
	{
		if( data.length != trans.length )
		{
			throw new IllegalArgumentException( "result must be the same size as data" );
		}
		
		for( int i = 0 ; i < data.length ; i++ )
		{
			apply( sweep , data[ i ].x , data[ i ].y + lateralOffset , angle , trans[ i ] );
		}
	}
	
	public Transform3D createOrientTransform( ICurveWithNormals3f sweep , Point3f origin , Vector3f tangent , Vector3f normal , float depth , Transform3D result )
	{
		sweep.getPoint( depth , pf1 );
		sweep.getTangent( depth , vf1 );
		sweep.getBinormal( depth , vf2 );
		tc.orient( origin , tangent , normal , pf1 , vf1 , vf2 , result );
		return result;
	}
	
	public Transform3D createOrientTransform( ICurveWithNormals3f sweep , Point3f origin , Vector3f tangent , Vector3f normal , float depth , float angle , Transform3D result )
	{
		sweep.getPoint( depth , pf1 );
		sweep.getTangent( depth , vf1 );
		sweep.getBinormal( depth , vf2 );
		sweep.getNormal( depth , vf3 );
		
		// cache sin & cos calculations
		if( angle != lastAngle )
		{
			lastAngle = angle;
			lastxf = ( float ) Math.cos( angle );
			lastyf = ( float ) Math.sin( angle );
		}
		vf2.scale( lastxf );
		vf2.scaleAdd( lastyf , vf3 , vf2 );
		tc.orient( origin , tangent , normal , pf1 , vf1 , vf2 , result );
		return result;
	}
}
