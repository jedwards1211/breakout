
package org.andork.math3d.curve;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.andork.math.Spline;

/**
 * A convenience class for representing a spline through 3D points (using one spline for each axis)
 * 
 * @author Andy
 */
public class Spline3d
{
	final double[ ]	mm;
	final Spline	xSpline;
	final Spline	ySpline;
	final Spline	zSpline;
	
	/**
	 * @param m
	 *            curve parameter array (measured depth for wellbores)
	 * @param x
	 *            x coordinates
	 * @param y
	 *            y coordinates
	 * @param z
	 *            z coordinates
	 */
	public Spline3d( double[ ] m , double[ ] x , double[ ] y , double[ ] z )
	{
		this.mm = m;
		xSpline = new Spline( m , x );
		ySpline = new Spline( m , y );
		zSpline = new Spline( m , z );
	}
	
	/**
	 * @param m
	 *            curve parameter (measured depth for wellbores)
	 * @param result
	 *            Point3d to store the result in
	 * @return <code>result</code>
	 */
	public Point3d evaluate( double m , Point3d result )
	{
		result.x = xSpline.evaluate( m );
		result.y = ySpline.evaluate( m );
		result.z = zSpline.evaluate( m );
		return result;
	}
	
	/**
	 * @param m
	 *            curve parameter (measured depth for wellbores)
	 * @param result
	 *            Point3d to store the result in
	 * @return <code>result</code>
	 */
	public Vector3d evaluate( double m , Vector3d result )
	{
		result.x = xSpline.evaluate( m );
		result.y = ySpline.evaluate( m );
		result.z = zSpline.evaluate( m );
		return result;
	}
	
	/**
	 * @param m
	 *            curve parameter (measured depth for wellbores)
	 * @param result
	 *            Point3f to store the result in
	 * @return <code>result</code>
	 */
	public Point3f evaluate( double m , Point3f result )
	{
		result.x = ( float ) xSpline.evaluate( m );
		result.y = ( float ) ySpline.evaluate( m );
		result.z = ( float ) zSpline.evaluate( m );
		return result;
	}
	
	/**
	 * @param m
	 *            curve parameter (measured depth for wellbores)
	 * @param result
	 *            Point3f to store the result in
	 * @return <code>result</code>
	 */
	public Vector3f evaluate( double m , Vector3f result )
	{
		result.x = ( float ) xSpline.evaluate( m );
		result.y = ( float ) ySpline.evaluate( m );
		result.z = ( float ) zSpline.evaluate( m );
		return result;
	}
	
	public boolean isInRange( double m )
	{
		return m >= mm[ 0 ] && m <= mm[ mm.length - 1 ];
	}
}
