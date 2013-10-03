package org.andork.j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.vecmath.VecmathUtils;

public class DebugVector extends BranchGroup
{
	private final Shape3D			shape	= new Shape3D( );
	
	private static final Object		lock	= new Object( );
	private static final Vector3f	v1		= new Vector3f( );
	private static final Vector3f	v2		= new Vector3f( );
	private static final Vector3f	v3		= new Vector3f( );
	private static final Point3f	p1		= new Point3f( );
	private static final Point3f	p2		= new Point3f( );
	
	public DebugVector( Point3f location , Vector3f extent , Color3f color )
	{
		ColoringAttributes ca = new ColoringAttributes( );
		ca.setCapability( ColoringAttributes.ALLOW_COLOR_WRITE );
		RenderingAttributes ra = new RenderingAttributes( );
		ra.setCapability( RenderingAttributes.ALLOW_VISIBLE_WRITE );
		TransparencyAttributes ta = new TransparencyAttributes( );
		ta.setTransparency( 0.3f );
		Appearance app = new Appearance( );
		app.setColoringAttributes( ca );
		app.setCapability( Appearance.ALLOW_COLORING_ATTRIBUTES_READ );
		app.setRenderingAttributes( ra );
		app.setCapability( Appearance.ALLOW_RENDERING_ATTRIBUTES_READ );
		shape.setAppearance( app );
		shape.setCapability( Shape3D.ALLOW_APPEARANCE_READ );
		shape.setCapability( Shape3D.ALLOW_GEOMETRY_WRITE );
		
		setVector( location , extent );
		setColor( color );
		
		addChild( shape );
	}
	
	public void setVisible( boolean visible )
	{
		shape.getAppearance( ).getRenderingAttributes( ).setVisible( visible );
	}
	
	public void setVector( Point3f location , Vector3f extent )
	{
		VecmathUtils.checkReal( location );
		VecmathUtils.checkReal( extent );
		VecmathUtils.checkNonzero( extent );
		
		if( extent.length( ) == 0 )
		{
			throw new IllegalArgumentException( "extent must be nonzero" );
		}
		
		LineArray newArray = new LineArray( 6 , LineArray.COORDINATES );
		
		synchronized( lock )
		{
			newArray.setCoordinate( 0 , location );
			p1.add( extent , location );
			newArray.setCoordinate( 1 , p1 );
			newArray.setCoordinate( 3 , p1 );
			newArray.setCoordinate( 5 , p1 );
			// newArray.setCoordinate( 7 , p1 );
			// newArray.setCoordinate( 9 , p1 );
			
			v3.scale( -0.25f , extent );
			p1.add( v3 );
			
			if( v3.x == 0 && v3.y == 0 )
			{
				v1.set( 1 , 0 , 0 );
			}
			else
			{
				float xy = ( float ) Math.sqrt( v3.x * v3.x + v3.y * v3.y );
				v1.x = -v3.z * v3.x / xy;
				v1.y = -v3.z * v3.y / xy;
				v1.z = xy;
			}
			
			v2.cross( v1 , v3 );
			v2.scale( v3.length( ) / v2.length( ) );
			
			p2.add( p1 , v1 );
			newArray.setCoordinate( 2 , p2 );
			p2.scaleAdd( -1 , v1 , p1 );
			newArray.setCoordinate( 4 , p2 );
		}
		
		shape.setGeometry( newArray );
	}
	
	public void setColor( Color3f color )
	{
		shape.getAppearance( ).getColoringAttributes( ).setColor( color );
	}
}