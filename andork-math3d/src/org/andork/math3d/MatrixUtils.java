package org.andork.math3d;

import java.util.Arrays;

import org.andork.util.ArrayUtils;

/**
 * a.k.a. Andy's Magical Matrix Manipulator
 * 
 * Includes:<br>
 * -Stuff for solving linear equations efficiently, adapted from hidden methods of {@link Transform3D}.<br>
 * -Methods for computing gaussian elimination ({@link #gauss(double[], int, int, int[])}) and general solutions of singular matrices (
 * {@link #generalSolution(double[], int, int, int[], boolean, double[], boolean[])})
 */
public class MatrixUtils
{
	public static void main( String[ ] args )
	{
		double[ ][ ] A = {
				{ -4 , 0 , -1 , -8 } ,
				{ 4 , 0 , -1 , -6 } ,
				{ -4 , 0 , 0 , 0 }
		};
		
		gauss( A , new int[ 3 ] );
		
		System.out.println( ArrayUtils.prettyPrint( A , "%9.2f" ) );
	}
	
	/**
	 * Returns the determinant of a 4x4 matrix.
	 */
	public static double determinant4( double[ ] m )
	{
		return m[ 0 ] * ( m[ 5 ] * ( m[ 10 ] * m[ 15 ] - m[ 11 ] * m[ 14 ] ) - m[ 6 ] * ( m[ 9 ] * m[ 15 ] - m[ 11 ] * m[ 13 ] ) + m[ 7 ] * ( m[ 9 ] * m[ 14 ] - m[ 10 ] * m[ 13 ] ) ) - m[ 1 ]
				* ( m[ 4 ] * ( m[ 10 ] * m[ 15 ] - m[ 11 ] * m[ 14 ] ) - m[ 6 ] * ( m[ 8 ] * m[ 15 ] - m[ 11 ] * m[ 12 ] ) + m[ 7 ] * ( m[ 8 ] * m[ 14 ] - m[ 10 ] * m[ 12 ] ) ) + m[ 2 ]
				* ( m[ 4 ] * ( m[ 9 ] * m[ 15 ] - m[ 11 ] * m[ 13 ] ) - m[ 5 ] * ( m[ 8 ] * m[ 15 ] - m[ 11 ] * m[ 12 ] ) + m[ 7 ] * ( m[ 8 ] * m[ 13 ] - m[ 9 ] * m[ 12 ] ) ) - m[ 3 ]
				* ( m[ 4 ] * ( m[ 9 ] * m[ 14 ] - m[ 10 ] * m[ 13 ] ) - m[ 5 ] * ( m[ 8 ] * m[ 14 ] - m[ 10 ] * m[ 12 ] ) + m[ 6 ] * ( m[ 8 ] * m[ 13 ] - m[ 9 ] * m[ 12 ] ) );
	}
	
	/**
	 * Given a 4x4 array "matrix0", this function replaces it with the LU decomposition of a row-wise permutation of itself. The input parameters are "matrix0"
	 * and "dimen". The array "matrix0" is also an output parameter. The vector "row_perm[4]" is an output parameter that contains the row permutations
	 * resulting from partial pivoting. The output parameter "even_row_xchg" is 1 when the number of row exchanges is even, or -1 otherwise. Assumes data type
	 * is always double.
	 * 
	 * This function is similar to luDecomposition, except that it is tuned specifically for 4x4 matrices.
	 * 
	 * @return true if the matrix is nonsingular, or false otherwise.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 40-45.
	//
	public static boolean luDecomposition4x4( double[ ] matrix0 , int[ ] row_perm )
	{
		
		// Can't re-use this temporary since the method is static.
		double row_scale[] = new double[ 4 ];
		
		// Determine implicit scaling information by looping over rows
		{
			int i, j;
			int ptr, rs;
			double big, temp;
			
			ptr = 0;
			rs = 0;
			
			// For each row ...
			i = 4;
			while( i-- != 0 )
			{
				big = 0.0;
				
				// For each column, find the largest element in the row
				j = 4;
				while( j-- != 0 )
				{
					temp = matrix0[ ptr++ ];
					temp = Math.abs( temp );
					if( temp > big )
					{
						big = temp;
					}
				}
				
				// Is the matrix singular?
				if( big == 0.0 )
				{
					return false;
				}
				row_scale[ rs++ ] = 1.0 / big;
			}
		}
		
		{
			int j;
			int mtx;
			
			mtx = 0;
			
			// For all columns, execute Crout's method
			for( j = 0 ; j < 4 ; j++ )
			{
				int i, imax, k;
				int target, p1, p2;
				double sum, big, temp;
				
				// Determine elements of upper diagonal matrix U
				for( i = 0 ; i < j ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = i;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
					}
					matrix0[ target ] = sum;
				}
				
				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0.0;
				imax = -1;
				for( i = j ; i < 4 ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = j;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
					}
					matrix0[ target ] = sum;
					
					// Is this the best pivot so far?
					if( ( temp = row_scale[ i ] * Math.abs( sum ) ) >= big )
					{
						big = temp;
						imax = i;
					}
				}
				
				if( imax < 0 )
				{
					return false;
				}
				
				// Is a row exchange necessary?
				if( j != imax )
				{
					// Yes: exchange rows
					k = 4;
					p1 = mtx + ( 4 * imax );
					p2 = mtx + ( 4 * j );
					while( k-- != 0 )
					{
						temp = matrix0[ p1 ];
						matrix0[ p1++ ] = matrix0[ p2 ];
						matrix0[ p2++ ] = temp;
					}
					
					// Record change in scale factor
					row_scale[ imax ] = row_scale[ j ];
				}
				
				// Record row permutation
				row_perm[ j ] = imax;
				
				// Is the matrix singular
				if( matrix0[ ( mtx + ( 4 * j ) + j ) ] == 0.0 )
				{
					return false;
				}
				
				// Divide elements of lower diagonal matrix L by pivot
				if( j != ( 4 - 1 ) )
				{
					temp = 1.0 / ( matrix0[ ( mtx + ( 4 * j ) + j ) ] );
					target = mtx + ( 4 * ( j + 1 ) ) + j;
					i = 3 - j;
					while( i-- != 0 )
					{
						matrix0[ target ] *= temp;
						target += 4;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Solves a set of linear equations. The input parameters "matrix1", and "row_perm" come from luDecompostionD4x4 and do not change here. The parameter
	 * "vectors" is a list of 4-coordinate vectors, one after another. The procedure takes every 4 elements of "vectors" in turn and treats it as the right-hand
	 * side of the matrix equation Ax = LUx = b. The solution vector replaces the cooresponding elements of "vectors".
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 44-45.
	//
	public static void luBacksubstitution4x4( double[ ] matrix1 , int[ ] row_perm , double[ ] vectors )
	{
		
		int i, ii, ip, j, k;
		int rp;
		int rv;
		
		// rp = row_perm;
		rp = 0;
		
		for( k = 0 ; k < vectors.length ; k += 4 )
		{
			// cv = &(matrix2[0][k]);
			ii = -1;
			
			// Forward substitution
			for( i = 0 ; i < 4 ; i++ )
			{
				double sum;
				
				ip = row_perm[ rp + i ];
				sum = vectors[ k + ip ];
				vectors[ k + ip ] = vectors[ k + i ];
				if( ii >= 0 )
				{
					// rv = &(matrix1[i][0]);
					rv = i * 4;
					for( j = ii ; j <= i - 1 ; j++ )
					{
						sum -= matrix1[ rv + j ] * vectors[ k + j ];
					}
				}
				else if( sum != 0.0 )
				{
					ii = i;
				}
				vectors[ k + i ] = sum;
			}
			
			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 3 * 4;
			vectors[ k + 3 ] /= matrix1[ rv + 3 ];
			
			rv -= 4;
			vectors[ k + 2 ] = ( vectors[ k + 2 ] - matrix1[ rv + 3 ] * vectors[ k + 3 ] ) / matrix1[ rv + 2 ];
			
			rv -= 4;
			vectors[ k + 1 ] = ( vectors[ k + 1 ] - matrix1[ rv + 2 ] * vectors[ k + 2 ] - matrix1[ rv + 3 ] * vectors[ k + 3 ] ) / matrix1[ rv + 1 ];
			
			rv -= 4;
			vectors[ k + 0 ] = ( vectors[ k + 0 ] - matrix1[ rv + 1 ] * vectors[ k + 1 ] - matrix1[ rv + 2 ] * vectors[ k + 2 ] - matrix1[ rv + 3 ] * vectors[ k + 3 ] ) / matrix1[ rv + 0 ];
		}
	}
	
	/**
	 * Solves the system Ax = b (in 4 variables), modifying A in the process and placing the result in b. Multiple values of b can be given, and the systems
	 * will be solved more efficiently than with individual calls to solve4.
	 * 
	 * @param A
	 *            an array of 16 values representing the 4x4 matrix A
	 * @param temp
	 *            a temporary array of 4 integers
	 * @param b
	 *            an array of 4*n values representing n 4-coordinate vectors for b
	 */
	public static void solve4( double[ ] A , int[ ] temp , double[ ] b )
	{
		if( temp == null || temp.length != 4 )
		{
			temp = new int[ 4 ];
		}
		
		if( !luDecomposition4x4( A , temp ) )
		{
			throw new SingularMatrixException( "Can't solve the given system" );
		}
		
		luBacksubstitution4x4( A , temp , b );
	}
	
	/**
	 * Returns the determinant of a 3x3 matrix.
	 */
	public static double determinant3( double[ ] m )
	{
		return m[ 0 ] * ( m[ 4 ] * m[ 8 ] - m[ 5 ] * m[ 7 ] ) + m[ 1 ] * ( m[ 5 ] * m[ 6 ] - m[ 3 ] * m[ 8 ] ) + m[ 2 ] * ( m[ 3 ] * m[ 7 ] - m[ 4 ] * m[ 6 ] );
	}
	
	/**
	 * Given a 4x4 array "matrix0", this function replaces it with the LU decomposition of a row-wise permutation of itself. The input parameters are "matrix0"
	 * and "dimen". The array "matrix0" is also an output parameter. The vector "row_perm[4]" is an output parameter that contains the row permutations
	 * resulting from partial pivoting. The output parameter "even_row_xchg" is 1 when the number of row exchanges is even, or -1 otherwise. Assumes data type
	 * is always double.
	 * 
	 * This function is similar to luDecomposition, except that it is tuned specifically for 4x4 matrices.
	 * 
	 * @return true if the matrix is nonsingular, or false otherwise.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 40-45.
	//
	public static boolean luDecomposition3x3( double[ ] matrix0 , int[ ] row_perm )
	{
		
		// Can't re-use this temporary since the method is static.
		double row_scale[] = new double[ 3 ];
		
		// Determine implicit scaling information by looping over rows
		{
			int i, j;
			int ptr, rs;
			double big, temp;
			
			ptr = 0;
			rs = 0;
			
			// For each row ...
			i = 3;
			while( i-- != 0 )
			{
				big = 0.0;
				
				// For each column, find the largest element in the row
				j = 3;
				while( j-- != 0 )
				{
					temp = matrix0[ ptr++ ];
					temp = Math.abs( temp );
					if( temp > big )
					{
						big = temp;
					}
				}
				
				// Is the matrix singular?
				if( big == 0.0 )
				{
					return false;
				}
				row_scale[ rs++ ] = 1.0 / big;
			}
		}
		
		{
			int j;
			int mtx;
			
			mtx = 0;
			
			// For all columns, execute Crout's method
			for( j = 0 ; j < 3 ; j++ )
			{
				int i, imax, k;
				int target, p1, p2;
				double sum, big, temp;
				
				// Determine elements of upper diagonal matrix U
				for( i = 0 ; i < j ; i++ )
				{
					target = mtx + ( 3 * i ) + j;
					sum = matrix0[ target ];
					k = i;
					p1 = mtx + ( 3 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 3;
					}
					matrix0[ target ] = sum;
				}
				
				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0.0;
				imax = -1;
				for( i = j ; i < 3 ; i++ )
				{
					target = mtx + ( 3 * i ) + j;
					sum = matrix0[ target ];
					k = j;
					p1 = mtx + ( 3 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 3;
					}
					matrix0[ target ] = sum;
					
					// Is this the best pivot so far?
					if( ( temp = row_scale[ i ] * Math.abs( sum ) ) >= big )
					{
						big = temp;
						imax = i;
					}
				}
				
				if( imax < 0 )
				{
					return false;
				}
				
				// Is a row exchange necessary?
				if( j != imax )
				{
					// Yes: exchange rows
					k = 3;
					p1 = mtx + ( 3 * imax );
					p2 = mtx + ( 3 * j );
					while( k-- != 0 )
					{
						temp = matrix0[ p1 ];
						matrix0[ p1++ ] = matrix0[ p2 ];
						matrix0[ p2++ ] = temp;
					}
					
					// Record change in scale factor
					row_scale[ imax ] = row_scale[ j ];
				}
				
				// Record row permutation
				row_perm[ j ] = imax;
				
				// Is the matrix singular
				if( matrix0[ ( mtx + ( 3 * j ) + j ) ] == 0.0 )
				{
					return false;
				}
				
				// Divide elements of lower diagonal matrix L by pivot
				if( j != ( 3 - 1 ) )
				{
					temp = 1.0 / ( matrix0[ ( mtx + ( 3 * j ) + j ) ] );
					target = mtx + ( 3 * ( j + 1 ) ) + j;
					i = 2 - j;
					while( i-- != 0 )
					{
						matrix0[ target ] *= temp;
						target += 3;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Solves a set of linear equations. The input parameters "matrix1", and "row_perm" come from luDecompostionD3x3 and do not change here. The parameter
	 * "vectors" is a list of 3-coordinate vectors, one after another. The procedure takes every 3 elements of "vectors" in turn and treats it as the right-hand
	 * side of the matrix equation Ax = LUx = b. The solution vector replaces the cooresponding elements of "vectors".
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 44-45.
	//
	public static void luBacksubstitution3x3( double[ ] matrix1 , int[ ] row_perm , double[ ] vectors )
	{
		
		int i, ii, ip, j, k;
		int rp;
		int rv;
		
		// rp = row_perm;
		rp = 0;
		
		for( k = 0 ; k < vectors.length ; k += 3 )
		{
			// cv = &(matrix2[0][k]);
			ii = -1;
			
			// Forward substitution
			for( i = 0 ; i < 3 ; i++ )
			{
				double sum;
				
				ip = row_perm[ rp + i ];
				sum = vectors[ k + ip ];
				vectors[ k + ip ] = vectors[ k + i ];
				if( ii >= 0 )
				{
					// rv = &(matrix1[i][0]);
					rv = i * 3;
					for( j = ii ; j <= i - 1 ; j++ )
					{
						sum -= matrix1[ rv + j ] * vectors[ k + j ];
					}
				}
				else if( sum != 0.0 )
				{
					ii = i;
				}
				vectors[ k + i ] = sum;
			}
			
			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 2 * 3;
			vectors[ k + 2 ] /= matrix1[ rv + 2 ];
			
			rv -= 3;
			vectors[ k + 1 ] = ( vectors[ k + 1 ] - matrix1[ rv + 2 ] * vectors[ k + 2 ] ) / matrix1[ rv + 1 ];
			
			rv -= 3;
			vectors[ k + 0 ] = ( vectors[ k + 0 ] - matrix1[ rv + 1 ] * vectors[ k + 1 ] - matrix1[ rv + 2 ] * vectors[ k + 2 ] ) / matrix1[ rv + 0 ];
		}
	}
	
	/**
	 * Solves the system Ax = b (in 3 variables), modifying A in the process and placing the result in b. Multiple values for b can be specified, and the
	 * systems will be solved more efficiently than by individual calls to solve3.
	 * 
	 * @param A
	 *            an array of 9 values representing the 3x3 matrix A
	 * @param temp
	 *            a temporary array of 3 integers
	 * @param b
	 *            an array of 3 * n values representing n 3-coordinate vectors for b
	 */
	public static void solve3( double[ ] A , int[ ] temp , double[ ] b )
	{
		if( temp == null || temp.length != 3 )
		{
			temp = new int[ 3 ];
		}
		
		if( !luDecomposition3x3( A , temp ) )
		{
			throw new SingularMatrixException( "Can't solve the given system" );
		}
		
		luBacksubstitution3x3( A , temp , b );
	}
	
	// public static class test3x3
	// {
	// public static void main( String[ ] args )
	// {
	// double[ ] A = { 1.0 , 0.0 , 1.0 , 1.0 , 1.0 , 0.0 , 0.0 , 1.0 , 1.0 };
	//
	// double[ ] b = { 3.0 , 4.0 , 5.0 , 6.0 , 8.0 , 10.0 };
	//
	// solve3( A , new int[ 3 ] , b );
	//
	// System.out.println( Arrays.toString( b ) );
	// }
	// }
	
	/**
	 * Performs gaussian elimination on the m by n matrix A.
	 */
	public static void gauss( double[ ] A , int m , int n )
	{
		int i = 0;
		int j = 0;
		
		while( i < m && j < n )
		{
			int rowstart = i * n;
			int maxi = i;
			double maxpivot = A[ rowstart + j ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < m ; k++ )
			{
				double newpivot = A[ k * n + j ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				int count = n - j;
				
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					double[ ] temp = new double[ count ];
					System.arraycopy( A , rowstart + j , temp , 0 , count );
					System.arraycopy( A , maxi * n + j , A , rowstart + j , count );
					System.arraycopy( temp , 0 , A , maxi * n + j , count );
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ rowstart + k ] /= maxpivot;
				}
				
				// subtract row i from the rows below
				for( int u = i + 1 ; u < m ; u++ )
				{
					int rowstart2 = u * n;
					double multiplier = A[ rowstart2 + j ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ rowstart2 + k ] -= multiplier * A[ rowstart + k ];
					}
				}
				i += 1;
			}
			j += 1;
		}
	}
	
	// public static class test3x3
	// {
	// public static void main( String[ ] args )
	// {
	// double[ ] A = { 1.0 , 0.0 , 1.0 , 1.0 , 1.0 , 0.0 , 0.0 , 1.0 , 1.0 };
	//
	// double[ ] b = { 3.0 , 4.0 , 5.0 , 6.0 , 8.0 , 10.0 };
	//
	// solve3( A , new int[ 3 ] , b );
	//
	// System.out.println( Arrays.toString( b ) );
	// }
	// }
	
	/**
	 * Performs gaussian elimination on the m by n matrix A. This method is faster than {@link #gauss(double[], int, int)} because it doesn't actually swap
	 * rows. Instead of exchanging rows, row_perms is used to mark the positions of the rows in the reduced matrix. Row <code>i</code> of the reduced matrix is
	 * row <code>row_perms[ i ]</code> of A.
	 */
	public static void gauss( double[ ] A , int m , int n , int[ ] row_perms )
	{
		int i = 0;
		int j = 0;
		
		for( int k = 0 ; k < row_perms.length ; k++ )
		{
			row_perms[ k ] = k;
		}
		
		while( i < m && j < n )
		{
			int rowstart = row_perms[ i ] * n;
			int maxi = i;
			double maxpivot = A[ rowstart + j ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < m ; k++ )
			{
				double newpivot = A[ row_perms[ k ] * n + j ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					int temp = row_perms[ i ];
					row_perms[ i ] = row_perms[ maxi ];
					row_perms[ maxi ] = temp;
					
					rowstart = row_perms[ i ] * n;
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ rowstart + k ] /= maxpivot;
				}
				
				// subtract row i from the rows below
				for( int u = i + 1 ; u < m ; u++ )
				{
					int rowstart2 = row_perms[ u ] * n;
					double multiplier = A[ rowstart2 + j ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ rowstart2 + k ] -= multiplier * A[ rowstart + k ];
					}
				}
				i += 1;
			}
			j += 1;
		}
	}
	
	/**
	 * Performs gaussian elimination on the m by n matrix A. Instead of exchanging rows, row_perms is used to mark the positions of the rows in the reduced
	 * matrix. Row <code>i</code> of the reduced matrix is row <code>row_perms[ i ]</code> of A.
	 */
	public static void gauss( double[ ][ ] A , int[ ] row_perms )
	{
		int i = 0;
		int j = 0;
		
		int m = A.length;
		int n = A.length == 0 ? 0 : A[ 0 ].length;
		
		if( row_perms.length != m )
		{
			throw new IllegalArgumentException( "row_perms.length must equal A.length" );
		}
		
		for( int k = 0 ; k < row_perms.length ; k++ )
		{
			row_perms[ k ] = k;
		}
		
		while( i < m && j < n )
		{
			int maxi = i;
			double maxpivot = A[ row_perms[ i ] ][ j ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < m ; k++ )
			{
				double newpivot = A[ row_perms[ k ] ][ j ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					int temp = row_perms[ i ];
					row_perms[ i ] = row_perms[ maxi ];
					row_perms[ maxi ] = temp;
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ row_perms[ i ] ][ k ] /= maxpivot;
				}
				
				// subtract row i from the rows below
				for( int u = i + 1 ; u < m ; u++ )
				{
					double multiplier = A[ row_perms[ u ] ][ j ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ row_perms[ u ] ][ k ] -= multiplier * A[ row_perms[ i ] ][ k ];
					}
				}
				i++ ;
			}
			j++ ;
		}
	}
	
	/**
	 * Performs gaussian elimination on the m by n matrix A. Instead of exchanging rows, row_perms is used to mark the positions of the rows in the reduced
	 * matrix. Row <code>i</code> of the reduced matrix is row <code>row_perms[ i ]</code> of A.
	 */
	public static void gauss( float[ ][ ] A , int[ ] row_perms )
	{
		int i = 0;
		int j = 0;
		
		int m = A.length;
		int n = A.length == 0 ? 0 : A[ 0 ].length;
		
		if( row_perms.length != m )
		{
			throw new IllegalArgumentException( "row_perms.length must equal A.length" );
		}
		
		for( int k = 0 ; k < row_perms.length ; k++ )
		{
			row_perms[ k ] = k;
		}
		
		while( i < m && j < n )
		{
			int maxi = i;
			float maxpivot = A[ row_perms[ i ] ][ j ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < m ; k++ )
			{
				float newpivot = A[ row_perms[ k ] ][ j ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					int temp = row_perms[ i ];
					row_perms[ i ] = row_perms[ maxi ];
					row_perms[ maxi ] = temp;
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ row_perms[ i ] ][ k ] /= maxpivot;
				}
				
				// subtract row i from the rows below
				for( int u = i + 1 ; u < m ; u++ )
				{
					float multiplier = A[ row_perms[ u ] ][ j ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ row_perms[ u ] ][ k ] -= multiplier * A[ row_perms[ i ] ][ k ];
					}
				}
				i++ ;
			}
			j++ ;
		}
	}
	
	/**
	 * Performs partial gaussian elimination on the m by n matrix A. Instead of exchanging rows, row_perms is used to mark the positions of the rows in the
	 * reduced matrix. Row <code>i</code> of the reduced matrix is row <code>row_perms[ i ]</code> of A.
	 * 
	 * @param maxNumToReduce
	 *            only the topmost {@code maxNumToReduce} rows will be fully reduced
	 */
	public static void partialGauss( float[ ][ ] A , int maxNumToReduce , int[ ] row_perms )
	{
		int i = 0;
		int j = 0;
		
		int m = A.length;
		int n = A.length == 0 ? 0 : A[ 0 ].length;
		
		if( row_perms.length != m )
		{
			throw new IllegalArgumentException( "row_perms.length must equal A.length" );
		}
		
		for( int k = 0 ; k < row_perms.length ; k++ )
		{
			row_perms[ k ] = k;
		}
		
		while( i < maxNumToReduce && i < m && j < n )
		{
			int maxi = i;
			float maxpivot = A[ row_perms[ i ] ][ j ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < maxNumToReduce ; k++ )
			{
				float newpivot = A[ row_perms[ k ] ][ j ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					int temp = row_perms[ i ];
					row_perms[ i ] = row_perms[ maxi ];
					row_perms[ maxi ] = temp;
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ row_perms[ i ] ][ k ] /= maxpivot;
				}
				
				// subtract row i from the rows below
				for( int u = i + 1 ; u < m ; u++ )
				{
					float multiplier = A[ row_perms[ u ] ][ j ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ row_perms[ u ] ][ k ] -= multiplier * A[ row_perms[ i ] ][ k ];
					}
				}
				i++ ;
			}
			j++ ;
		}
	}
	
	public static void backsubstitute( double[ ] A , int m , int n , int[ ] row_perms , double[ ] coefficients )
	{
		if( m + 1 != n )
		{
			throw new IllegalArgumentException( "This method only supports matrices where m + 1 == n" );
		}
		
		for( int i = m - 1 ; i >= 0 ; i-- )
		{
			int rowstart = row_perms[ i ] * n;
			coefficients[ i ] = -A[ rowstart + m ];
			for( int j = i + 1 ; j < m ; j++ )
			{
				coefficients[ i ] -= A[ rowstart + j ] * coefficients[ j ];
			}
		}
	}
	
	/**
	 * Computes the general solution of a linear system. An original algorithm by Andy Edwards!
	 * 
	 * @param A
	 *            a matrix that has been row-reduced by {@link #gauss(double[], int, int, int[])}
	 * @param m
	 *            the number of rows in A
	 * @param n
	 *            the number of columns in A
	 * @param row_perms
	 *            the row permuations of A from {@link #gauss(double[], int, int, int[])}
	 * @param aug
	 *            <code>true</code> if A is an augmented matrix
	 * @param soln
	 *            output parameter: coefficients for each variable in the system (and constants for non-homogeneous systems). E.g. for a 3 variable augmented
	 *            matrix with x3 free, the output will be of the form
	 * 
	 *            <pre>
	 * [  0,  0, a3, a0,
	 *    0,  0, b3, b0,
	 *    0,  0,  1,  0 ]
	 * </pre>
	 * 
	 *            Represents the solutions
	 * 
	 *            <pre>
	 * x1 = 0 * x1 + 0 * x2 + a3 * x3 + a0
	 * x2 = 0 * x1 + 0 * x2 + b3 * x3 + b0
	 * x3 = 0 * x1 + 0 * x2 +  1 * x3 +  0
	 * </pre>
	 * 
	 *            Only the free variables will have nonzero coefficients for themselves. For a 3 variable homogeneous matrix, the output would lack the
	 *            constants a0, b0, and c0.<br>
	 * 
	 *            The basis for the solution space will be the set of all nonzero "columns" of this output, excluding the last for augmented matrices which is
	 *            the offset from the origin.
	 * @param free
	 *            Output parameter: an array of booleans identifying which variables are free.
	 * @return <code>true</code> if the system was successfully solved, <code>false</code> if the system is inconsistent.
	 */
	public static boolean generalSolution( double[ ] A , int m , int n , int[ ] row_perms , boolean aug , double[ ] soln , boolean[ ] free )
	{
		int nvars = aug ? n - 1 : n;
		
		Arrays.fill( soln , 0 );
		Arrays.fill( free , true );
		
		// for all rows...
		for( int i = 0 ; i < m ; i++ )
		{
			int rowstart = row_perms != null ? row_perms[ i ] * n : i * n;
			
			// find pivot column/variable no. in row
			int j;
			for( j = i ; j < nvars ; j++ )
			{
				if( A[ rowstart + j ] != 0 )
				{
					break;
				}
			}
			
			if( j == nvars )
			{
				// check for inconsistent system
				if( aug && A[ rowstart + j ] != 0 )
				{
					return false;
				}
			}
			else
			{
				free[ j ] = false;
				
				// set the coefficents for the solution equation of the current pivot variable
				double pivot = A[ rowstart + j ];
				for( int k = j + 1 ; k < nvars ; k++ )
				{
					soln[ n * j + k ] = -A[ rowstart + k ] / pivot;
				}
				if( aug )
				{
					soln[ n * j + n - 1 ] = A[ rowstart + n - 1 ] / pivot;
				}
			}
		}
		
		double[ ] temp = new double[ n ];
		
		// for free variables, mark a coefficient of 1 for the variable itself in its
		// solution equation
		int var = 0;
		for( var = 0 ; var < nvars ; var++ )
		{
			if( free[ var ] )
			{
				int rowstart = var * n;
				soln[ rowstart + var ] = 1;
			}
		}
		
		for( var = nvars - 1 ; var >= 0 ; var-- )
		{
			if( !free[ var ] )
			{
				int rowstart = var * n;
				
				// substitute free variable equations into pivot variable equation
				
				Arrays.fill( temp , 0 );
				
				for( int j = var ; j < nvars ; j++ )
				{
					double coef = soln[ rowstart + j ];
					if( coef != 0 )
					{
						int rowstart2 = j * n;
						
						for( int k = j ; k < n ; k++ )
						{
							temp[ k ] += soln[ rowstart2 + k ] * coef;
						}
					}
				}
				
				System.arraycopy( temp , 0 , soln , rowstart , nvars );
				
				// handle constant coefficient
				if( aug )
				{
					soln[ rowstart + n - 1 ] += temp[ n - 1 ];
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Changes variable from a free variable to another variable defined only in terms of the free variable.
	 * 
	 * @param soln
	 *            the solution equation array for the variables from {@link #generalSolution(double[], int, int, int[], boolean, double[], boolean[])}
	 * @param nvars
	 *            the number of variables in the system
	 * @param aug
	 *            <code>true</code> if the system is non-homogeneous
	 * @param free
	 *            free variable flags from {@link #generalSolution(double[], int, int, int[], boolean, double[], boolean[])}
	 * @param oldvar
	 *            a current free variable.
	 * @param newvar
	 *            a variable currently defined only in terms of the free variable.
	 */
	public static void changeVariableFast( double[ ] soln , int nvars , boolean aug , boolean[ ] free , int oldvar , int newvar )
	{
		if( !free[ oldvar ] )
		{
			throw new IllegalArgumentException( "oldVar must be free" );
		}
		
		if( free[ newvar ] )
		{
			throw new IllegalArgumentException( "newVar must not be free" );
		}
		
		int rowsize = aug ? nvars + 1 : nvars;
		
		if( soln[ rowsize * newvar + oldvar ] == 0 )
		{
			throw new IllegalArgumentException( "newVar must be defined in terms of oldVar" );
		}
		
		int oldrowstart = rowsize * oldvar;
		int newrowstart = rowsize * newvar;
		
		for( int var = 0 ; var < nvars ; var++ )
		{
			if( ( soln[ newrowstart + var ] != 0 ) != ( var == oldvar ) )
			{
				throw new IllegalArgumentException( "This method does not currently support changing to a varable that is defined in terms of multiple variables." );
			}
		}
		
		// change of variable:
		
		// newVar = a * oldVar + b
		// oldVar = newVar / a - b / a
		
		// otherVar = x + c * oldVar + d = x + newVar * (c / a) - (b * (c / a) + d)
		
		double a = soln[ newrowstart + oldvar ];
		double b = 0;
		if( aug )
		{
			b = soln[ newrowstart + nvars ];
		}
		
		// change equation for old variable
		soln[ oldrowstart + newvar ] = 1 / a;
		if( aug )
		{
			soln[ oldrowstart + nvars ] = -b / a;
		}
		soln[ oldrowstart + oldvar ] = 0;
		
		// change equation for new variable
		soln[ newrowstart + newvar ] = 1;
		soln[ newrowstart + oldvar ] = 0;
		if( aug )
		{
			soln[ newrowstart + nvars ] = 0;
		}
		
		// change equations for other variables
		int rowstart = 0;
		for( int var = 0 ; var < nvars ; var++ )
		{
			if( var != oldvar && var != newvar )
			{
				double c = soln[ rowstart + oldvar ];
				if( c != 0 )
				{
					double ca = c / a;
					soln[ rowstart + newvar ] = ca;
					if( aug )
					{
						soln[ rowstart + nvars ] -= b * ca;
					}
				}
				soln[ rowstart + oldvar ] = 0;
			}
			rowstart += rowsize;
		}
		
		free[ oldvar ] = false;
		free[ newvar ] = true;
	}
	
	/**
	 * Changes variable from a free variable to another variable defined in terms of the free variable.
	 * 
	 * @param soln
	 *            the solution equation array for the variables from {@link #generalSolution(double[], int, int, int[], boolean, double[], boolean[])}
	 * @param nvars
	 *            the number of variables in the system
	 * @param aug
	 *            <code>true</code> if the system is non-homogeneous
	 * @param free
	 *            free variable flags from {@link #generalSolution(double[], int, int, int[], boolean, double[], boolean[])}
	 * @param oldvar
	 *            a current free variable.
	 * @param newvar
	 *            a variable currently defined in terms of the free variable (and possibly other free variables).
	 */
	public static void changeVariableFull( double[ ] soln , int nvars , boolean aug , boolean[ ] free , int oldvar , int newvar )
	{
		if( !free[ oldvar ] )
		{
			throw new IllegalArgumentException( "oldVar must be free" );
		}
		
		if( free[ newvar ] )
		{
			throw new IllegalArgumentException( "newVar must not be free" );
		}
		
		int rowsize = aug ? nvars + 1 : nvars;
		
		if( soln[ rowsize * newvar + oldvar ] == 0 )
		{
			throw new IllegalArgumentException( "newVar must be defined in terms of oldVar" );
		}
		
		int oldrowstart = rowsize * oldvar;
		int newrowstart = rowsize * newvar;
		
		// change of variable:
		
		// free vars = f1, f2, ..., ff
		// fo = oldVar
		// fn = newvar
		
		// fn = c1 * f1 + c2 * f2 + ... + co * fo + ... + cf * ff + c0
		// fo = (-c1 / co) * f1 + (-c2 / co) * f2 + ... + (1 / co) * fn + ... + (-cf / co) * ff + (-c0 / co)
		
		// fother = d1 * f1 + d2 * f2 + ... + do * fo + ... + df * ff + d0
		// = (d1 - do * c1 / co) * f1 + (d2 - do * c2 / co) * f2 + ... + (do / co) * fn + ... + (df - do * cf / co) * ff - (do * c0 / co)
		
		double co = soln[ newrowstart + oldvar ];
		
		for( int var = 0 ; var < nvars ; var++ )
		{
			if( var == newvar )
			{
				soln[ oldrowstart + var ] = 1 / co;
			}
			else if( free[ var ] && var != oldvar )
			{
				soln[ oldrowstart + var ] = -soln[ newrowstart + var ] / co;
			}
			else
			{
				soln[ oldrowstart + var ] = 0;
			}
		}
		
		if( aug )
		{
			soln[ oldrowstart + nvars ] = -soln[ newrowstart + nvars ] / co;
		}
		
		// update free flags
		free[ oldvar ] = false;
		free[ newvar ] = true;
		
		// change equations for other variables
		int rowstart = 0;
		for( int var = 0 ; var < nvars ; var++ )
		{
			double d_o = soln[ rowstart + oldvar ];
			
			if( var != oldvar && var != newvar )
			{
				for( int othervar = 0 ; othervar < nvars ; othervar++ )
				{
					if( free[ othervar ] )
					{
						soln[ rowstart + othervar ] += d_o * soln[ oldrowstart + othervar ];
					}
					else
					{
						soln[ rowstart + othervar ] = 0;
					}
				}
				
				if( aug )
				{
					soln[ rowstart + nvars ] += d_o * soln[ oldrowstart + nvars ];
				}
			}
			rowstart += rowsize;
		}
		
		// change equation for newvar
		for( int i = 0 ; i < rowsize ; i++ )
		{
			soln[ newrowstart + i ] = i == newvar ? 1 : 0;
		}
	}
	
	/**
	 * Backsubstitutes fixed values for free variables into a general solution
	 * 
	 * @param soln
	 *            the general solution of a system of linear equations from {@link #generalSolution(double[], int, int, int[], boolean, double[], boolean[])}
	 * @param nvars
	 *            the number of variables in the system
	 * @param aug
	 *            <code>true</code> if the system is non-homogeneous
	 * @param free
	 *            the free variable flags from {@link #generalSolution(double[], int, int, int[], boolean, double[], boolean[])}
	 * @param values
	 *            input/output parameter: inputs the values for the free variables, and outputs the specific solution for all variables
	 */
	public static void backsubstitute( double[ ] soln , int nvars , boolean aug , boolean[ ] free , double[ ] values )
	{
		int rowsize = aug ? nvars + 1 : nvars;
		
		int rowstart = 0;
		for( int var = 0 ; var < nvars ; var++ )
		{
			if( !free[ var ] )
			{
				values[ var ] = 0;
				
				for( int coef = 0 ; coef < nvars ; coef++ )
				{
					if( free[ coef ] )
					{
						values[ var ] += soln[ rowstart + coef ] * values[ coef ];
					}
				}
				
				if( aug )
				{
					values[ var ] += soln[ rowstart + nvars ];
				}
			}
			rowstart += rowsize;
		}
	}
	
	public static String toString( double[ ] A , int m , int n , int[ ] row_perms )
	{
		StringBuffer sb = new StringBuffer( );
		
		for( int i = 0 ; i < m ; i++ )
		{
			int rowstart = row_perms != null ? row_perms[ i ] * n : i * n;
			
			for( int j = 0 ; j < n ; j++ )
			{
				sb.append( A[ rowstart + j ] );
				if( j < n - 1 )
				{
					sb.append( ", " );
				}
			}
			sb.append( '\n' );
		}
		
		return sb.toString( );
	}
	
	// public static void main( String[ ] args )
	// {
	// // double[ ] A = new double[ ] { 2 , 1 , -1 , 8 , -3 , -1 , 2 , -11 , -2 , 1 , 2 , -3 };
	// // double[ ] A = new double[ ] { 2 , 1 , -1 , 8 , -3 , -1 , 2 , -11 , 0 , 0 , 0 , 0 };
	// // double[ ] A = new double[ ] { 0 , 2 , 0 , 0 , 1 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 2 , 1 };
	// double[ ] A = new double[ ] { 0 , 2 , 0 , 0 , 1 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 };
	// int m = 3;
	// int n = 5;
	// int nvars = 4;
	//
	// int[ ] row_perms = new int[ m ];
	//
	// gauss( A , m , n , row_perms );
	//
	// double[ ] eqs = new double[ n * nvars ];
	// boolean[ ] free = new boolean[ nvars ];
	//
	// generalSolution( A , m , n , row_perms , true , eqs , free );
	//
	// System.out.println( "Reduced form of A:" );
	// System.out.println( toString( A , m , n , row_perms ) );
	//
	// System.out.println( "General solution:" );
	// System.out.println( toString( eqs , nvars , n , null ) );
	//
	// System.out.println( "Free vars:" );
	// System.out.println( Arrays.toString( free ) );
	// }
}
