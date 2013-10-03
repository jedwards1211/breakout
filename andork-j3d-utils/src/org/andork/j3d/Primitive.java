/**
 * 
 */

package org.andork.j3d;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 * @author brian.kamery
 * @author andy.edwards
 */
public abstract class Primitive extends BranchGroup
{
	protected TransformGroup	m_transformGroup	= new TransformGroup( );
	private Vector3f			m_location			= new Vector3f( );
	protected Transform3D		m_transform			= new Transform3D( );
	
	public Primitive( )
	{
		this.addChild( m_transformGroup );
	}
	
	public Transform3D getTransform( )
	{
		return m_transform;
	}
	
	public void setTransform( Transform3D t , boolean applyNow )
	{
		m_transform = t;
		if( applyNow )
		{
			apply( );
		}
	}
	
	public void translate( float x , float y , float z , boolean applyNow )
	{
		m_transform.get( m_location );
		m_location.x += x;
		m_location.y += y;
		m_location.z += z;
		m_transform.setTranslation( m_location );
		if( applyNow )
		{
			apply( );
		}
	}
	
	/**
	 * applies the
	 */
	public void apply( )
	{
		m_transformGroup.setTransform( m_transform );
	}
	
	// public abstract Appearance getAppearance();
}
