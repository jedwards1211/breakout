package org.andork.math3d;

import java.util.Arrays;

import org.andork.math3d.RowMajorVecmath;
import org.andork.math3d.Vecmath;
import org.junit.Assert;
import org.junit.Test;

public class ColumnMajorVecmathTest
{
	public double[ ] createTestMatrix( )
	{
		double[ ] mat = RowMajorVecmath.newMat4d( );
		
		double[ ] axis = { 1 , 0.5 , 0.25 };
		RowMajorVecmath.normalize3( axis );
		
		RowMajorVecmath.setRotation( mat , axis , 2.0 );
		
		RowMajorVecmath.setColumn3( mat , 3 , 2.0 , 5.0 , 16.0 );
		
		return mat;
	}
	
	@Test
	public void testInvertGeneral( )
	{
		double[ ] mat = createTestMatrix( );
		
		double[ ] copy = Arrays.copyOf( mat , 16 );
		RowMajorVecmath.transpose( copy , copy );
		
		RowMajorVecmath.invertGeneral( mat );
		Vecmath.invertGeneral( copy );
		RowMajorVecmath.transpose( copy , copy );
		
		Assert.assertArrayEquals( mat , copy , 0.0 );
	}
	
	@Test
	public void testInvAffine( )
	{
		double[ ] mat = createTestMatrix( );
		
		double[ ] copy = Arrays.copyOf( mat , 16 );
		RowMajorVecmath.transpose( copy , copy );
		
		RowMajorVecmath.invAffine( mat , mat );
		Vecmath.invAffine( copy , copy );
		RowMajorVecmath.transpose( copy , copy );
		
		Assert.assertArrayEquals( mat , copy , 0.0 );
	}
	
	@Test
	public void testInvAffineToTranspose3x3( )
	{
		double[ ] mat = createTestMatrix( );
		
		double[ ] copy = Arrays.copyOf( mat , 16 );
		RowMajorVecmath.transpose( copy , copy );
		
		RowMajorVecmath.invAffineToTranspose3x3( mat , mat );
		Vecmath.invAffineToTranspose3x3( copy , copy );
		RowMajorVecmath.transpose( copy , copy );
		
		Assert.assertArrayEquals( mat , copy , 0.0 );
	}
}
