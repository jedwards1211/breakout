package org.andork.torquescape.model2.meshing;

public class MeshingUtils
{
	public static int[ ] generateDefaultMeshing( int npoints )
	{
		int[ ] result = new int[ npoints * 6 ];
		
		int k = 0;
		for( int i = 0 ; i < npoints ; i++ )
		{
			result[k++] = i;
			result[k++] = (i + 1) % npoints;
			result[k++] = i + npoints;
			result[k++] = ((i + 1) % npoints) + npoints;
			result[k++] = i + npoints;
			result[k++] = (i + 1) % npoints;
		}
		
		return result;
	}
}
