package org.andork.torquescape.control;

import static org.andork.vecmath.DoubleArrayVecmath.cross;
import static org.andork.vecmath.DoubleArrayVecmath.dot3;
import static org.andork.vecmath.DoubleArrayVecmath.invAffine;
import static org.andork.vecmath.DoubleArrayVecmath.invertGeneral;
import static org.andork.vecmath.DoubleArrayVecmath.length3;
import static org.andork.vecmath.DoubleArrayVecmath.mmul;
import static org.andork.vecmath.DoubleArrayVecmath.newIdentityMatrix;
import static org.andork.vecmath.DoubleArrayVecmath.normalize3;
import static org.andork.vecmath.DoubleArrayVecmath.setColumn3;
import static org.andork.vecmath.DoubleArrayVecmath.sub3;

import static org.andork.vecmath.MixedArrayVecmath.*;

import java.nio.ByteBuffer;

import org.andork.torquescape.model.Edge;
import org.andork.vecmath.DoubleArrayVecmath;
import org.andork.vecmath.MixedArrayVecmath;

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
	ByteBuffer		vertBuffer;
	final int[ ]	indices				= new int[ 3 ];
	
	float[ ]		verts				= new float[ 9 ];
	double[ ]		vertsDouble			= new double[ 9 ];
	float[ ]		normals				= new float[ 9 ];
	
	final double[ ]	uVector				= new double[ 3 ];
	final double[ ]	vVector				= new double[ 3 ];
	final double[ ]	nVector				= new double[ 3 ];
	
	final double[ ]	eVector				= new double[ 3 ];
	final double[ ]	fVector				= new double[ 3 ];
	final double[ ]	gVector				= new double[ 3 ];
	
	double			vDotU;
	double			uDotU;
	double			vDotV;
	double			normUCrossV;
	
	final double[ ]	xyzToUVN			= newIdentityMatrix( );
	final double[ ]	uvnToXYZ			= newIdentityMatrix( );
	boolean			xyzToUVNUpToDate	= false;
	
	final double[ ]	xyzToEFG			= newIdentityMatrix( );
	final double[ ]	efgToXYZ			= newIdentityMatrix( );
	boolean			xyzToEFGUpToDate	= false;
	
	final double[ ]	uvnToEFG			= newIdentityMatrix( );
	final double[ ]	efgToUVN			= newIdentityMatrix( );
	boolean			uvnToEFGUpToDate	= false;
	
	public TriangleBasis( )
	{
	}
	
	public void set( ByteBuffer vertBuffer , int i0 , int i1 , int i2 )
	{
		this.vertBuffer = vertBuffer;
		indices[ 0 ] = i0;
		indices[ 1 ] = i1;
		indices[ 2 ] = i2;
		
		vertBuffer.position( i0 );
		verts[ 0 ] = vertBuffer.getFloat( );
		verts[ 1 ] = vertBuffer.getFloat( );
		verts[ 2 ] = vertBuffer.getFloat( );
		normals[ 0 ] = vertBuffer.getFloat( );
		normals[ 1 ] = vertBuffer.getFloat( );
		normals[ 2 ] = vertBuffer.getFloat( );
		
		vertBuffer.position( i1 );
		verts[ 3 ] = vertBuffer.getFloat( );
		verts[ 4 ] = vertBuffer.getFloat( );
		verts[ 5 ] = vertBuffer.getFloat( );
		normals[ 3 ] = vertBuffer.getFloat( );
		normals[ 4 ] = vertBuffer.getFloat( );
		normals[ 5 ] = vertBuffer.getFloat( );
		
		vertBuffer.position( i2 );
		verts[ 6 ] = vertBuffer.getFloat( );
		verts[ 7 ] = vertBuffer.getFloat( );
		verts[ 8 ] = vertBuffer.getFloat( );
		normals[ 6 ] = vertBuffer.getFloat( );
		normals[ 7 ] = vertBuffer.getFloat( );
		normals[ 8 ] = vertBuffer.getFloat( );
		
		MixedArrayVecmath.set( vertsDouble , verts );
		
		sub3( vertsDouble , 3 , vertsDouble , 0 , uVector , 0 );
		sub3( vertsDouble , 6 , vertsDouble , 0 , vVector , 0 );
		cross( uVector , vVector , nVector );
		
		vDotU = dot3( vVector , uVector );
		uDotU = dot3( uVector , uVector );
		vDotV = dot3( vVector , vVector );
		normUCrossV = length3( nVector );
		
		DoubleArrayVecmath.set( eVector , uVector );
		cross( nVector , uVector , fVector );
		normalize3( eVector );
		normalize3( fVector );
		cross( eVector , fVector , gVector );
		
		xyzToUVNUpToDate = false;
		xyzToEFGUpToDate = false;
		uvnToEFGUpToDate = false;
	}
	
	void updateXYZToUVNIfNecessary( )
	{
		if( !xyzToUVNUpToDate )
		{
			setColumn3( uvnToXYZ , 0 , uVector );
			setColumn3( uvnToXYZ , 1 , vVector );
			setColumn3( uvnToXYZ , 2 , nVector );
			setColumn3( uvnToXYZ , 3 , vertsDouble );
			
			invertGeneral( uvnToXYZ , xyzToUVN );
			
			xyzToUVNUpToDate = true;
		}
	}
	
	void updateXYZToEFGIfNecessary( )
	{
		if( !xyzToEFGUpToDate )
		{
			setColumn3( efgToXYZ , 0 , eVector );
			setColumn3( efgToXYZ , 1 , fVector );
			setColumn3( efgToXYZ , 2 , gVector );
			setColumn3( efgToXYZ , 3 , vertsDouble );
			
			invAffine( efgToXYZ , xyzToEFG );
			
			xyzToEFGUpToDate = true;
		}
	}
	
	void updateUVNToEFGIfNecessary( )
	{
		if( !uvnToEFGUpToDate )
		{
			updateXYZToUVNIfNecessary( );
			updateXYZToEFGIfNecessary( );
			
			mmul( xyzToEFG , uvnToXYZ , uvnToEFG );
			mmul( xyzToUVN , efgToXYZ , efgToUVN );
			
			uvnToEFGUpToDate = true;
		}
	}
	
	public void getXYZToUVN( double[ ] result )
	{
		updateXYZToUVNIfNecessary( );
		DoubleArrayVecmath.set( result , xyzToUVN );
	}
	
	public double[ ] getXYZToUVNDirect( )
	{
		updateXYZToUVNIfNecessary( );
		return xyzToUVN;
	}
	
	public void getUVNToXYZ( double[ ] result )
	{
		updateXYZToUVNIfNecessary( );
		DoubleArrayVecmath.set( result , uvnToXYZ );
	}
	
	public double[ ] getUVNToXYZDirect( )
	{
		updateXYZToUVNIfNecessary( );
		return uvnToXYZ;
	}
	
	public void getXYZToEFG( double[ ] result )
	{
		updateXYZToEFGIfNecessary( );
		DoubleArrayVecmath.set( result , xyzToEFG );
	}
	
	public double[ ] getXYZToEFGDirect( )
	{
		updateXYZToEFGIfNecessary( );
		return xyzToEFG;
	}
	
	public void getEFGToXYZ( double[ ] result )
	{
		updateXYZToEFGIfNecessary( );
		DoubleArrayVecmath.set( result , efgToXYZ );
	}
	
	public double[ ] getEFGToXYZDirect( )
	{
		updateXYZToEFGIfNecessary( );
		return efgToXYZ;
	}
	
	public void getUVNToEFG( double[ ] result )
	{
		updateUVNToEFGIfNecessary( );
		DoubleArrayVecmath.set( result , uvnToEFG );
	}
	
	public double[ ] getUVNToEFGDirect( )
	{
		updateUVNToEFGIfNecessary( );
		return uvnToEFG;
	}
	
	public void getEFGToUVN( double[ ] result )
	{
		updateUVNToEFGIfNecessary( );
		DoubleArrayVecmath.set( result , efgToUVN );
	}
	
	public double[ ] getEFGToUVNDirect( )
	{
		updateUVNToEFGIfNecessary( );
		return efgToUVN;
	}
	
	public void interpolateNormals( double u , double v , float[ ] result )
	{
		double f0 = 1 - u - v;
		
		result[ 0 ] = ( float ) ( f0 * normals[ 0 ] + u * normals[ 3 ] + v * normals[ 6 ] );
		result[ 1 ] = ( float ) ( f0 * normals[ 1 ] + u * normals[ 4 ] + v * normals[ 7 ] );
		result[ 2 ] = ( float ) ( f0 * normals[ 2 ] + u * normals[ 5 ] + v * normals[ 8 ] );
	}
	
	public void interpolateNormals( double u , double v , double[ ] result )
	{
		double f0 = 1 - u - v;
		
		result[ 0 ] = f0 * normals[ 0 ] + u * normals[ 3 ] + v * normals[ 6 ];
		result[ 1 ] = f0 * normals[ 1 ] + u * normals[ 4 ] + v * normals[ 7 ];
		result[ 2 ] = f0 * normals[ 2 ] + u * normals[ 5 ] + v * normals[ 8 ];
	}
	
	public Edge createEdge( int index )
	{
		switch( index )
		{
			case 0:
				return new Edge( verts[ 0 ] , verts[ 1 ] , verts[ 2 ] , verts[ 3 ] , verts[ 4 ] , verts[ 5 ] );
			case 1:
				return new Edge( verts[ 3 ] , verts[ 4 ] , verts[ 5 ] , verts[ 6 ] , verts[ 7 ] , verts[ 8 ] );
			case 2:
				return new Edge( verts[ 6 ] , verts[ 7 ] , verts[ 8 ] , verts[ 0 ] , verts[ 1 ] , verts[ 2 ] );
			default:
				throw new IllegalArgumentException( "index must be >= 0 and <= 2" );
		}
	}
	
	public void printInfo( )
	{
		System.out.println( "TriangleBasis:" );
		System.out.println( "[" + indices[ 0 ] + "]: (" + verts[ 0 ] + ", " + verts[ 1 ] + ", " + verts[ 2 ] + ")" );
		System.out.println( "[" + indices[ 1 ] + "]: (" + verts[ 3 ] + ", " + verts[ 4 ] + ", " + verts[ 5 ] + ")" );
		System.out.println( "[" + indices[ 2 ] + "]: (" + verts[ 6 ] + ", " + verts[ 7 ] + ", " + verts[ 8 ] + ")" );
	}
}
