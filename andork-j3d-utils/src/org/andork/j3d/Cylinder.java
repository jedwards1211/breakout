package org.andork.j3d;
/**
 * 
 */


import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.TransformComputer3f;

/**
 * @author brian.kamery
 * @author andy.edwards
 */
public class Cylinder extends Primitive
{
	protected com.sun.j3d.utils.geometry.Cylinder	m_primative;
	protected TransformComputer3f					m_tc;
	
	public Cylinder( float radius , float length , TransformComputer3f tc , Color3f color )
	{
		this( radius , new Point3f( 0 , -length * .5f , 0 ) , new Point3f( 0 , length * .5f , 0 ) , tc , color );
	}
	
	/**
	 * creates a cylinder with center endcap at p1 and center endcap at p2
	 * 
	 * @param p1
	 * @param p2
	 */
	public Cylinder( float radius , Point3f p1 , Point3f p2 , TransformComputer3f tc , Color3f color )
	{
		length = p1.distance( p2 );
		if( length == 0 )
		{
			throw new IllegalArgumentException( "Points must be different!" );
		}
		
		this.radius = radius;
		
		m_tc = tc;
		
		// the extra tacked on is ONLY for the bore model ...needs to go elsewhere
		m_primative = new com.sun.j3d.utils.geometry.Cylinder( 1 , 1 );
		
		Appearance appearance = new Appearance( );
		
		Material mat = new Material( );
		mat.setAmbientColor( color );
		mat.setDiffuseColor( color );
		mat.setSpecularColor( color );
		// mat.setShininess(1.0f);
		appearance.setMaterial( mat );
		appearance.setCapability( Appearance.ALLOW_MATERIAL_WRITE );
		m_primative.setAppearance( appearance );
		m_primative.setCapability( com.sun.j3d.utils.geometry.Cylinder.ENABLE_APPEARANCE_MODIFY );
		
		this.setCapability( BranchGroup.ALLOW_DETACH );
		m_transformGroup.addChild( m_primative );
		
		m_transformGroup.setCapability( Group.ALLOW_CHILDREN_EXTEND );
		m_transformGroup.setCapability( Group.ALLOW_CHILDREN_READ );
		m_transformGroup.setCapability( Group.ALLOW_CHILDREN_WRITE );
		m_transformGroup.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		
		setEndpoints( p1 , p2 );
	}
	
	/**
	 * creates a cylinder with center endcap at p1 and center endcap at p2
	 * 
	 * @param p1
	 * @param p2
	 */
	public Cylinder( float radius , Point3f p1 , Point3f p2 , Vector3f normal , TransformComputer3f tc , Color3f color )
	{
		length = p1.distance( p2 );
		if( length == 0 )
		{
			throw new IllegalArgumentException( "Points must be different!" );
		}
		
		this.radius = radius;
		
		m_tc = tc;
		
		// the extra tacked on is ONLY for the bore model ...needs to go elsewhere
		m_primative = new com.sun.j3d.utils.geometry.Cylinder( 1 , 1 );
		
		Appearance appearance = new Appearance( );
		
		Material mat = new Material( );
		mat.setAmbientColor( color );
		mat.setDiffuseColor( color );
		mat.setSpecularColor( color );
		// mat.setShininess(1.0f);
		appearance.setMaterial( mat );
		appearance.setCapability( Appearance.ALLOW_MATERIAL_WRITE );
		m_primative.setAppearance( appearance );
		m_primative.setCapability( com.sun.j3d.utils.geometry.Cylinder.ENABLE_APPEARANCE_MODIFY );
		
		this.setCapability( BranchGroup.ALLOW_DETACH );
		m_transformGroup.addChild( m_primative );
		
		m_transformGroup.setCapability( Group.ALLOW_CHILDREN_EXTEND );
		m_transformGroup.setCapability( Group.ALLOW_CHILDREN_READ );
		m_transformGroup.setCapability( Group.ALLOW_CHILDREN_WRITE );
		m_transformGroup.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		
		setEndpoints( p1 , p2 , normal );
	}
	
	/**
	 * Sets the radius of the existing cylinder this assumes that the calling entity has detached the BranchGroup somewhere in the heirarchy
	 * 
	 * @param radius
	 */
	public void setRadius( float radius )
	{
		if( radius != this.radius )
		{
			this.radius = radius;
			createTransform( m_transform );
			apply( );
		}
	}
	
	public float getRadius( )
	{
		return radius;
	}
	
	public float getLength( )
	{
		return length;
	}
	
	public void setEndpoints( Point3f p1 , Point3f p2 )
	{
		length = p1.distance( p2 );
		createTransform( p1 , p2 , m_transform );
		apply( );
	}
	
	public void setEndpoints( Point3f p1 , Point3f p2 , Vector3f normal )
	{
		length = p1.distance( p2 );
		createTransform( p1 , p2 , normal , m_transform );
		apply( );
	}
	
	/**
	 * Appearance modifiable
	 * 
	 * @param color
	 */
	public void setColor( Color3f color )
	{
		Material mat = new Material( );
		mat.setAmbientColor( color );
		mat.setDiffuseColor( color );
		mat.setSpecularColor( color );
		
		m_primative.getAppearance( ).setMaterial( mat );
	}
	
	public void setAppearance( Appearance app )
	{
		m_primative.setAppearance( app );
	}
	
	public Appearance getAppearance( )
	{
		return m_primative.getAppearance( );
	}
	
	public void getEndpoints( Point3f p1 , Point3f p2 )
	{
		p1.set( 0 , -.5f , 0 );
		p2.set( 0 , .5f , 0 );
		m_transform.transform( p1 );
		m_transform.transform( p2 );
	}
	
	protected void createTransform( Point3f p1 , Point3f p2 , Transform3D result )
	{
		m_tc.p1.set( 0 , 0 , 0 );
		m_tc.v1.set( 1 , 0 , 0 );
		m_tc.v2.set( 0 , .5f , 0 );
		m_tc.v3.set( 0 , 0 , 1 );
		
		m_tc.p2.interpolate( p1 , p2 , .5f );
		m_tc.v5.sub( p2 , m_tc.p2 );
		
		if( m_tc.v5.x == 0 && m_tc.v5.y == 0 && m_tc.v5.z == 0 )
		{
			throw new IllegalArgumentException( "p1 and p2 must not be the same or infinitesimally close together" );
		}
		
		if( m_tc.v5.x == 0 && m_tc.v5.y == 0 )
		{
			m_tc.v4.set( 0 , radius , 0 );
			m_tc.v6.set( radius , 0 , 0 );
		}
		else
		{
			m_tc.v6.set( 0 , 0 , 1 );
			m_tc.v4.cross( m_tc.v5 , m_tc.v6 );
			m_tc.v4.scale( radius / m_tc.v4.length( ) );
			m_tc.v6.cross( m_tc.v4 , m_tc.v5 );
			m_tc.v6.scale( radius / m_tc.v6.length( ) );
		}
		
		m_tc.shear( result );
	}
	
	protected void createTransform( Point3f p1 , Point3f p2 , Vector3f normal , Transform3D result )
	{
		m_tc.p1.set( 0 , 0 , 0 );
		m_tc.v1.set( 1 , 0 , 0 );
		m_tc.v2.set( 0 , .5f , 0 );
		m_tc.v3.set( 0 , 0 , 1 );
		
		m_tc.p2.interpolate( p1 , p2 , .5f );
		m_tc.v5.sub( p2 , m_tc.p2 );
		
		if( m_tc.v5.x == 0 && m_tc.v5.y == 0 && m_tc.v5.z == 0 )
		{
			throw new IllegalArgumentException( "p1 and p2 must not be the same or infinitesimally close together" );
		}
		
		m_tc.v4.cross( m_tc.v5 , normal );
		if( m_tc.v4.x == 0 && m_tc.v4.y == 0 && m_tc.v4.z == 0 )
		{
			throw new IllegalArgumentException( "normal must not be parallel to vector from p1 to p2" );
		}
		m_tc.v4.scale( radius / m_tc.v4.length( ) );
		m_tc.v6.cross( m_tc.v4 , m_tc.v5 );
		m_tc.v6.scale( radius / m_tc.v6.length( ) );
		
		m_tc.shear( result );
	}
	
	protected void createTransform( Transform3D result )
	{
		m_tc.p1.set( 0 , 0 , 0 );
		m_tc.v1.set( 1 , 0 , 0 );
		m_tc.v2.set( 0 , .5f , 0 );
		m_tc.v3.set( 0 , 0 , 1 );
		
		m_transform.transform( m_tc.p1 , m_tc.p2 );
		m_transform.transform( m_tc.v1 , m_tc.v4 );
		m_transform.transform( m_tc.v2 , m_tc.v5 );
		m_transform.transform( m_tc.v3 , m_tc.v6 );
		
		m_tc.v4.scale( radius / m_tc.v4.length( ) );
		m_tc.v6.scale( radius / m_tc.v6.length( ) );
		
		m_tc.shear( result );
	}
	
	public void setAppearanceOverrideEnable( boolean flag )
	{
		for( int i = 0 ; i < 3 ; i++ )
		{
			m_primative.getShape( i ).setAppearanceOverrideEnable( flag );
		}
	}
	
	private float	radius;
	private float	length;
}
