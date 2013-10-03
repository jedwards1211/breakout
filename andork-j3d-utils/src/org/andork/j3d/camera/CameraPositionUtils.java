
package org.andork.j3d.camera;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class CameraPositionUtils
{
	/**
	 * Finds the optimal camera position for a given view azimuth (with no tilt) that is as close to a group of points as possible without any being
	 * outside the field of view.
	 * 
	 * @param vsAzimuth
	 *            the vertical section azimuth
	 * @param fov
	 *            the camera's field of view angle in radians
	 * @param aspectRatio
	 *            the aspect ratio of the canvas (width / height)
	 * @param points
	 *            the points to fit to screen
	 * @param result
	 *            the {@link CameraPosition} to set
	 * @return {@code result} after setting it to the optimal position.
	 */
	public static CameraPosition fitToScreen( double vsAzimuth , double fov , double aspectRatio , Iterable<Point3d> points , CameraPosition result )
	{
		result.setPanTilt( vsAzimuth - Math.PI / 2 , 0 );
		return fitToScreen( result , fov , aspectRatio , points , result );
	}
	
	/**
	 * Finds the optimal camera location for a given orientation (view direction) that is as close to a group of points as possible without any being
	 * outside the field of view.
	 * 
	 * @param initOrientation
	 *            the initial camera orientation to use. Only the right, down, and forward vectors will be used; the location will be ignored.
	 * @param vsAzimuth
	 *            the vertical section azimuth
	 * @param fov
	 *            the camera's field of view angle in radians
	 * @param aspectRatio
	 *            the aspect ratio of the canvas (width / height)
	 * @param points
	 *            the points to fit to screen
	 * @param result
	 *            the {@link CameraPosition} to set
	 * @return {@code result} after setting it to the optimal position.
	 */
	public static CameraPosition fitToScreen( CameraPosition initOrientation , double fov , double aspectRatio , Iterable<Point3d> points , CameraPosition result )
	{
		Vector3d rightVector = new Vector3d( );
		Vector3d forwardVector = new Vector3d( );
		Vector3d downVector = new Vector3d( );
		
		initOrientation.getRight( rightVector );
		initOrientation.getForward( forwardVector );
		initOrientation.getDown( downVector );
		
		double fovratio = Math.tan( fov / 2 );
		
		double rightRatio;
		double downRatio;
		
		rightRatio = fovratio;
		downRatio = fovratio / aspectRatio;
		
		Vector3d toPoint = new Vector3d( );
		
		// compute the optimal "downing location" where all points fit optimally within the top/bottom field of view
		// but not necessarily the left/right field of view.
		
		Vector3d downingLoc = new Vector3d( );
		
		int count = 0;
		
		for( Point3d p : points )
		{
			if( count++ == 0 )
			{
				downingLoc.set( p );
			}
			else
			{
				double forwarding;
				toPoint.sub( p , downingLoc );
				
				double downing = downVector.dot( toPoint );
				forwarding = forwardVector.dot( toPoint );
				
				// is the point vertically within view?
				if( forwarding <= 0 || Math.abs( downing / forwarding ) > downRatio )
				{
					//translate the camera backward and up/down along the view frustrum planes to where the point is just within view
					double extraDowning = downRatio * forwarding;
					
					double base = Math.abs( downing ) + extraDowning;
					double height = base / 2 / downRatio;
					
					double backUp = height - forwarding;
					
					downingLoc.scaleAdd( -backUp , forwardVector , downingLoc );
					downingLoc.scaleAdd( backUp * downRatio * Math.signum( downing ) , downVector , downingLoc );
				}
			}
		}
		
		// compute the optimal "righting location" where all points fit optimally within the left/right field of view
		// but not necessarily the top/bottom field of view.
		
		Vector3d rightingLoc = new Vector3d( );
		count = 0;
		
		for( Point3d p : points )
		{
			if( count++ == 0 )
			{
				rightingLoc.set( p );
			}
			else
			{
				
				toPoint.sub( p , rightingLoc );
				
				double righting = rightVector.dot( toPoint );
				double forwarding = forwardVector.dot( toPoint );
				
				// is the point horizontally within view?
				if( forwarding <= 0 || Math.abs( righting / forwarding ) > rightRatio )
				{
					//translate the camera backward and left/right along the view frustrum planes to where the point is just within view
					double extraRighting = rightRatio * forwarding;
					
					double base = Math.abs( righting ) + extraRighting;
					double height = base / 2 / rightRatio;
					
					double backUp = height - forwarding;
					
					rightingLoc.scaleAdd( -backUp , forwardVector , rightingLoc );
					rightingLoc.scaleAdd( backUp * rightRatio * Math.signum( righting ) , rightVector , rightingLoc );
				}
			}
		}
		
		// combine the two into one location where the points fit within the whole field of view
		// use the righting location for left/right position, downing location for up/down position, and whichever is farther back for the
		// forward/backward position
		
		double righting = rightingLoc.dot( rightVector );
		double downing = downingLoc.dot( downVector );
		double forwarding = Math.min( rightingLoc.dot( forwardVector ) , downingLoc.dot( forwardVector ) );
		
		Point3d loc = new Point3d( );
		loc.scale( righting , rightVector );
		loc.scaleAdd( downing , downVector , loc );
		loc.scaleAdd( forwarding , forwardVector , loc );
		
		// compute lookAt using forward vector
		Point3d lookAt = new Point3d( );
		lookAt.add( loc , forwardVector );
		
		result.setLocation( loc );
		result.lookAt( lookAt );
		
		return result;
	}
}
