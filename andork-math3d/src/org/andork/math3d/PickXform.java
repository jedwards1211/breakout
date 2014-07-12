package org.andork.math3d;

import static org.andork.math3d.Vecmath.calcClippingPlanes;
import static org.andork.math3d.Vecmath.cross;
import static org.andork.math3d.Vecmath.getColumn3;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.mpmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.scaleAdd3;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.math3d.Vecmath.threePointNormal;

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.andork.util.Reparam;

public class PickXform
{
	boolean			perspective	= false;
	final float[ ]	vipi		= newMat4f( );
	final float[ ]	vi			= newMat4f( );
	final float[ ]	btlrnf		= new float[ 6 ];
	
	public void calculate( float[ ] p , float[ ] v )
	{
		perspective = p[ 15 ] == 0;
		if( p[ 15 ] == 0 )
		{
			calcClippingPlanes( p , btlrnf );
			invAffine( v , vi );
		}
		else
		{
			mmulAffine( p , v , vipi );
			invAffine( vipi );
			invAffine( v , vi );
		}
	}
	
	public void xform( float x , float y , float canvasWidth , float canvasHeight , float[ ] originOut , float[ ] directionOut )
	{
		if( perspective )
		{
			getColumn3( vi , 3 , originOut );
			x = btlrnf[ 2 ] + x / canvasWidth * ( btlrnf[ 3 ] - btlrnf[ 2 ] );
			y = btlrnf[ 0 ] + y / canvasHeight * ( btlrnf[ 1 ] - btlrnf[ 0 ] );
			mvmulAffine( vi , x , y , -btlrnf[ 4 ] , directionOut );
		}
		else
		{
			x = Reparam.linear( x , 0 , canvasWidth , -1 , 1 );
			y = Reparam.linear( y , 0 , canvasHeight , -1 , 1 );
			mpmulAffine( vipi , x , y , -1 , originOut );
			mvmulAffine( vi , 0 , 0 , 1 , directionOut );
		}
	}
	
	public void xform( float x , float y , float z , float canvasWidth , float canvasHeight , float[ ] out )
	{
		if( perspective )
		{
			x = Reparam.linear( x , 0 , canvasWidth , btlrnf[ 2 ] , btlrnf[ 3 ] );
			y = Reparam.linear( y , 0 , canvasHeight , btlrnf[ 0 ] , btlrnf[ 1 ] );
			mvmulAffine( vi , x , y , -btlrnf[ 4 ] , out );
			z = Reparam.linear( z , -1 , 1 , -btlrnf[ 4 ] , -btlrnf[ 5 ] );
			scaleAdd3( z / -btlrnf[ 4 ] , out , 0 , vi , 12 , out , 0 );
		}
		else
		{
			x = Reparam.linear( x , 0 , canvasWidth , -1 , 1 );
			y = Reparam.linear( y , 0 , canvasHeight , -1 , 1 );
			mpmulAffine( vipi , x , y , z , out );
		}
	}
	
	public void xform( MouseEvent e , float z , float[ ] out )
	{
		Component canvas = e.getComponent( );
		xform( e.getX( ) , canvas.getHeight( ) - e.getY( ) , z , canvas.getWidth( ) , canvas.getHeight( ) , out );
	}
	
	public void xform( MouseEvent e , float[ ] origin , float[ ] direction )
	{
		Component canvas = e.getComponent( );
		xform( e.getX( ) , canvas.getHeight( ) - e.getY( ) , canvas.getWidth( ) , canvas.getHeight( ) , origin , direction );
	}
	
	public void xform( float x , float y , float z , float[ ] out )
	{
		if( perspective )
		{
			x = Reparam.linear( x , -1 , 1 , btlrnf[ 2 ] , btlrnf[ 3 ] );
			y = Reparam.linear( y , -1 , 1 , btlrnf[ 0 ] , btlrnf[ 1 ] );
			mvmulAffine( vi , x , y , -btlrnf[ 4 ] , out );
			z = Reparam.linear( z , -1 , 1 , -btlrnf[ 4 ] , -btlrnf[ 5 ] );
			scaleAdd3( z / -btlrnf[ 4 ] , out , 0 , vi , 12 , out , 0 );
		}
		else
		{
			mpmulAffine( vipi , x , y , z , out );
		}
	}
	
	public void xform( float x , float y , float canvasWidth , float canvasHeight , float[ ] originOut , int originOuti , float[ ] directionOut , int directionOuti )
	{
		if( perspective )
		{
			getColumn3( vi , 3 , originOut , originOuti );
			x = btlrnf[ 2 ] + x / canvasWidth * ( btlrnf[ 3 ] - btlrnf[ 2 ] );
			y = btlrnf[ 0 ] + y / canvasHeight * ( btlrnf[ 1 ] - btlrnf[ 0 ] );
			mvmulAffine( vi , x , y , -btlrnf[ 4 ] , directionOut , directionOuti );
		}
		else
		{
			x = Reparam.linear( x , 0 , canvasWidth , -1 , 1 );
			y = Reparam.linear( y , 0 , canvasHeight , -1 , 1 );
			mpmulAffine( vipi , x , y , -1 , originOut , originOuti );
			mvmulAffine( vi , 0 , 0 , 1 , directionOut , directionOuti );
		}
	}
	
	public void exportViewVolume( PlanarHull3f hull , float viewWidth , float viewHeight )
	{
		exportViewVolume( hull , 0 , 0 , viewWidth , viewHeight , viewWidth , viewHeight );
	}
	
	public void exportViewVolume( PlanarHull3f hull , float[ ] viewBounds , float viewWidth , float viewHeight )
	{
		exportViewVolume( hull , viewBounds[ 0 ] , viewBounds[ 1 ] , viewBounds[ 2 ] , viewBounds[ 3 ] , viewWidth , viewHeight );
	}
	
	public void exportViewVolume( PlanarHull3f hull , MouseEvent e , float radius )
	{
		Component c = e.getComponent( );
		int y = c.getHeight( ) - e.getY( );
		exportViewVolume( hull , e.getX( ) - radius , y - radius , e.getX( ) + radius , y + radius ,
				c.getWidth( ) , c.getHeight( ) );
	}
	
	public void exportViewVolume( PlanarHull3f hull , float minX , float minY , float maxX , float maxY , float viewWidth , float viewHeight )
	{
		if( hull.origins.length != 6 )
		{
			throw new IllegalArgumentException( "hull must have exactly 6 sides" );
		}
		if( hull.vertices.length != 8 )
		{
			throw new IllegalArgumentException( "hull must have exactly 8 vertices" );
		}
		
		xform( minX , minY , -1 , viewWidth , viewHeight , hull.vertices[ 0 ] );
		xform( maxX , minY , -1 , viewWidth , viewHeight , hull.vertices[ 1 ] );
		xform( minX , maxY , -1 , viewWidth , viewHeight , hull.vertices[ 2 ] );
		xform( maxX , maxY , -1 , viewWidth , viewHeight , hull.vertices[ 3 ] );
		xform( minX , minY , 1 , viewWidth , viewHeight , hull.vertices[ 4 ] );
		xform( maxX , minY , 1 , viewWidth , viewHeight , hull.vertices[ 5 ] );
		xform( minX , maxY , 1 , viewWidth , viewHeight , hull.vertices[ 6 ] );
		xform( maxX , maxY , 1 , viewWidth , viewHeight , hull.vertices[ 7 ] );
		
		setf( hull.origins[ 0 ] , hull.vertices[ 0 ] );
		setf( hull.origins[ 1 ] , hull.vertices[ 3 ] );
		setf( hull.origins[ 2 ] , hull.vertices[ 0 ] );
		setf( hull.origins[ 3 ] , hull.vertices[ 3 ] );
		xform( (minX + maxX) * 0.5f , (minY + maxY) * 0.5f , -1 , viewWidth , viewHeight , hull.origins[ 4 ] );
		xform( (minX + maxX) * 0.5f , (minY + maxY) * 0.5f , 1 , viewWidth , viewHeight , hull.origins[ 5 ] );
		
		threePointNormal( hull.vertices[ 0 ] , hull.vertices[ 4 ] , hull.vertices[ 2 ] , hull.normals[ 0 ] );
		threePointNormal( hull.vertices[ 1 ] , hull.vertices[ 3 ] , hull.vertices[ 5 ] , hull.normals[ 1 ] );
		threePointNormal( hull.vertices[ 0 ] , hull.vertices[ 1 ] , hull.vertices[ 4 ] , hull.normals[ 2 ] );
		threePointNormal( hull.vertices[ 2 ] , hull.vertices[ 6 ] , hull.vertices[ 3 ] , hull.normals[ 3 ] );
		threePointNormal( hull.vertices[ 0 ] , hull.vertices[ 2 ] , hull.vertices[ 1 ] , hull.normals[ 4 ] );
		threePointNormal( hull.vertices[ 4 ] , hull.vertices[ 5 ] , hull.vertices[ 6 ] , hull.normals[ 5 ] );
		
		normalize3( hull.normals[ 0 ] );
		normalize3( hull.normals[ 1 ] );
		normalize3( hull.normals[ 2 ] );
		normalize3( hull.normals[ 3 ] );
		normalize3( hull.normals[ 4 ] );
		normalize3( hull.normals[ 5 ] );
	}
}
