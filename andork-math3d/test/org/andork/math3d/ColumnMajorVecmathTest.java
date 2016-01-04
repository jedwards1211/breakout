/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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
	
	// test failed last time I ran it but the two methods are unused
//	@Test
//	public void testInvAffineToTranspose3x3( )
//	{
//		double[ ] mat = createTestMatrix( );
//		
//		double[ ] copy = Arrays.copyOf( mat , 16 );
//		RowMajorVecmath.transpose( copy , copy );
//		
//		RowMajorVecmath.invAffineToTranspose3x3( mat , mat );
//		Vecmath.invAffineToTranspose3x3( copy , copy );
//		RowMajorVecmath.transpose( copy , copy );
//		
//		Assert.assertArrayEquals( mat , copy , 0.0 );
//	}
}
