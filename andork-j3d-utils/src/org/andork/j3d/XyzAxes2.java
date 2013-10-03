
package org.andork.j3d;

/**
 * 
 */

import java.awt.Color;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import org.andork.j3d.math.TransformComputer3f;

/**
 * @author brian.kamery
 * 
 */
public class XyzAxes2 extends BranchGroup
{
	TransformComputer3f	m_tc3f	= new TransformComputer3f( );
	/**
	 * 
	 * @param radius
	 * @param length
	 */
	public XyzAxes2( float radius , float length )
	{
		
		Cylinder x = new Cylinder( radius , new Point3f( ) , new Point3f( 1 , 0 , 0 ) , m_tc3f , new Color3f( Color.RED ) );
		Cylinder y = new Cylinder( radius , new Point3f( ) , new Point3f( 0 , 1 , 0 ) , m_tc3f , new Color3f( Color.GREEN ) );
		Cylinder z = new Cylinder( radius , new Point3f( ) , new Point3f( 0 , 0 , 1 ) , m_tc3f , new Color3f( Color.BLUE ) );
		
		this.addChild( x );
		this.addChild( y );
		this.addChild( z );
		
	}
}
