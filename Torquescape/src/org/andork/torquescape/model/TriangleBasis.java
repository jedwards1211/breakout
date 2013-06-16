package org.andork.torquescape.model;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * Provides methods for transforming between 3 coordinate systems in a triangle:
 * <ul>
 * <li><b>XYZ System</b>: the coordinate system the triangle points are defined in (its basis should be orthogonal).
 * <li><b>UV System</b>: a system where (0, 0) is the triangle point p0, (1, 0) is p1, and (0, 1) is p2 (its basis is not necessarily orthogonal).
 * <li><b>EF System</b>: a system with the same scale as XYZ where (0, 0) is p0, (1, 0) is 1 unit toward p1 from p0, and (0, 1) is 1 unit toward p2 from p0 (its
 * basis is orthogonal as long as XYZ is orthogonal).
 * </ul>
 * In other words, <b>UV</b> and <b>EF</b> are 2-dimensional systems in the triangle plane.<br>
 * <b>UV</b> is ideal for testing if points are inside the triangle / performing intersections with triangle edges.<br>
 * <b>EF</b> is ideal for performing rotations in the triangle plane (because it is orthogonal).<br>
 * <br>
 * In addition, the <b>UVN</b> system is defined as an extension of <b>UV</b>. <b>N</b> is defined as <b>U</b> cross <b>V</b> (a normal to the triangle), where
 * <b>U = p1 - p0</b> and <b>V = p2 - p0</b>. By this definition, (0, 0, 1) in <b>UVN</b> coordinates is p0 + N.
 */
public class TriangleBasis
{
	Triangle		triangle;
	
	final Vector3f	uVector				= new Vector3f( );
	final Vector3f	vVector				= new Vector3f( );
	final Vector3f	nVector				= new Vector3f( );
	
	final Vector3f	eVector				= new Vector3f( );
	final Vector3f	fVector				= new Vector3f( );
	final Vector3f	gVector				= new Vector3f( );
	
	float			vDotU;
	float			uDotU;
	float			vDotV;
	float			normUCrossV;
	
	final Matrix4f	xyzToUVN			= new Matrix4f( );
	final Matrix4f	uvnToXYZ			= new Matrix4f( );
	boolean			xyzToUVNUpToDate	= false;
	
	final Matrix4f	xyzToEFG			= new Matrix4f( );
	final Matrix4f	efgToXYZ			= new Matrix4f( );
	boolean			xyzToEFGUpToDate	= false;
	
	final Matrix4f	uvnToEFG			= new Matrix4f( );
	final Matrix4f	efgToUVN			= new Matrix4f( );
	boolean			uvnToEFGUpToDate	= false;
	
	public TriangleBasis( )
	{
	}
	
	public Triangle getTriangle( )
	{
		return triangle;
	}
	
	public void set( Triangle triangle )
	{
		if( this.triangle != triangle )
		{
			this.triangle = triangle;
			
			uVector.sub( triangle.p1 , triangle.p0 );
			vVector.sub( triangle.p2 , triangle.p0 );
			nVector.cross( uVector , vVector );
			
			vDotU = vVector.dot( uVector );
			uDotU = uVector.lengthSquared( );
			vDotV = vVector.lengthSquared( );
			normUCrossV = nVector.length( );
			
			eVector.set( uVector );
			fVector.cross( nVector , uVector );
			eVector.normalize( );
			fVector.normalize( );
			gVector.cross( eVector , fVector );
			
			xyzToUVNUpToDate = false;
			xyzToEFGUpToDate = false;
			uvnToEFGUpToDate = false;
		}
	}
	
	void updateXYZToUVNIfNecessary( )
	{
		if( !xyzToUVNUpToDate )
		{
			uvnToXYZ.setColumn( 0 , uVector.x , uVector.y , uVector.z , 0 );
			uvnToXYZ.setColumn( 1 , vVector.x , vVector.y , vVector.z , 0 );
			uvnToXYZ.setColumn( 2 , nVector.x , nVector.y , nVector.z , 0 );
			uvnToXYZ.setColumn( 3 , triangle.p0.x , triangle.p0.y , triangle.p0.z , 1 );
			
			xyzToUVN.invert( uvnToXYZ );
			
			xyzToUVNUpToDate = true;
		}
	}
	
	void updateXYZToEFGIfNecessary( )
	{
		if( !xyzToEFGUpToDate )
		{
			efgToXYZ.setColumn( 0 , eVector.x , eVector.y , eVector.z , 0 );
			efgToXYZ.setColumn( 1 , fVector.x , fVector.y , fVector.z , 0 );
			efgToXYZ.setColumn( 2 , gVector.x , gVector.y , gVector.z , 0 );
			efgToXYZ.setColumn( 3 , triangle.p0.x , triangle.p0.y , triangle.p0.z , 1 );
			
			xyzToEFG.invert( efgToXYZ );
			
			xyzToEFGUpToDate = true;
		}
	}
	
	void updateUVNToEFGIfNecessary( )
	{
		if( !uvnToEFGUpToDate )
		{
			updateXYZToUVNIfNecessary( );
			updateXYZToEFGIfNecessary( );
			
			uvnToEFG.mul( xyzToEFG , uvnToXYZ );
			efgToUVN.mul( xyzToUVN , efgToXYZ );
			
			uvnToEFGUpToDate = true;
		}
	}
	
	public void getXYZToUVN( Matrix4f result )
	{
		updateXYZToUVNIfNecessary( );
		result.set( xyzToUVN );
	}
	
	public Matrix4f getXYZToUVNDirect( )
	{
		updateXYZToUVNIfNecessary( );
		return xyzToUVN;
	}
	
	public void getUVNToXYZ( Matrix4f result )
	{
		updateXYZToUVNIfNecessary( );
		result.set( uvnToXYZ );
	}
	
	public Matrix4f getUVNToXYZDirect( )
	{
		updateXYZToUVNIfNecessary( );
		return uvnToXYZ;
	}
	
	public void getXYZToEFG( Matrix4f result )
	{
		updateXYZToEFGIfNecessary( );
		result.set( xyzToEFG );
	}
	
	public Matrix4f getXYZToEFGDirect( )
	{
		updateXYZToEFGIfNecessary( );
		return xyzToEFG;
	}
	
	public void getEFGToXYZ( Matrix4f result )
	{
		updateXYZToEFGIfNecessary( );
		result.set( efgToXYZ );
	}
	
	public Matrix4f getEFGToXYZDirect( )
	{
		updateXYZToEFGIfNecessary( );
		return efgToXYZ;
	}
	
	public void getUVNToEFG( Matrix4f result )
	{
		updateUVNToEFGIfNecessary( );
		result.set( uvnToEFG );
	}
	
	public Matrix4f getUVNToEFGDirect( )
	{
		updateUVNToEFGIfNecessary( );
		return uvnToEFG;
	}
	
	public void getEFGToUVN( Matrix4f result )
	{
		updateUVNToEFGIfNecessary( );
		result.set( efgToUVN );
	}
	
	public Matrix4f getEFGToUVNDirect( )
	{
		updateUVNToEFGIfNecessary( );
		return efgToUVN;
	}
	
	public void interpolateNormals( float u , float v , Vector3f result )
	{
		float f0 = 1 - u - v;
		
		result.scale( f0 , triangle.n0 );
		result.scaleAdd( u , triangle.n1 , result );
		result.scaleAdd( v , triangle.n2 , result );
	}
}
