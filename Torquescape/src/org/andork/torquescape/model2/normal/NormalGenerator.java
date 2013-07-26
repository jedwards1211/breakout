package org.andork.torquescape.model2.normal;

import java.util.HashMap;
import java.util.Map;

public class NormalGenerator
{
	public static void generateNormals( float[ ] verts , int normalOffset , int vertsStride ,
			int[ ] indices , int indicesStart , int indicesEnd )
	{
		Map<Integer, TriangleListNode> connectedTris = new HashMap<Integer, TriangleListNode>( );
		
		float[ ] triNormals = new float[ indicesEnd - indicesStart ];
		
		int normalIndex = 0;
		
		int i = indicesStart;
		while( i < indicesEnd )
		{
			int triangleIndex = i;
			int coord0 = indices[ i++ ];
			int coord1 = indices[ i++ ];
			int coord2 = indices[ i++ ];
			
			// remember that these points are connected to this triangle
			
			connectedTris.put( coord0 , new TriangleListNode( triangleIndex , connectedTris.get( coord0 ) ) );
			connectedTris.put( coord1 , new TriangleListNode( triangleIndex , connectedTris.get( coord1 ) ) );
			connectedTris.put( coord2 , new TriangleListNode( triangleIndex , connectedTris.get( coord2 ) ) );
			
			// compute the normal of the triangle
			
			float ux = verts[ coord1 ] - verts[ coord0 ];
			float uy = verts[ coord1 + 1 ] - verts[ coord0 + 1 ];
			float uz = verts[ coord1 + 2 ] - verts[ coord0 + 2 ];
			float vx = verts[ coord2 ] - verts[ coord0 ];
			float vy = verts[ coord2 + 1 ] - verts[ coord0 + 1 ];
			float vz = verts[ coord2 + 2 ] - verts[ coord0 + 2 ];
			
			triNormals[ normalIndex++ ] = uy * vz - uz * vy;
			triNormals[ normalIndex++ ] = uz * vx - ux * vz;
			triNormals[ normalIndex++ ] = ux * vy - uy * vx;
		}
		
		// set the normal of each point to the normalized sum of the normals of the triangles connected to that point
		
		for( Map.Entry<Integer, TriangleListNode> entry : connectedTris.entrySet( ) )
		{
			int pi = entry.getKey( );
			TriangleListNode node = entry.getValue( );
			
			float x = 0;
			float y = 0;
			float z = 0;
			
			while( node != null )
			{
				int triNormalIndex = node.triangleIndex - indicesStart;
				x += triNormals[ triNormalIndex ];
				y += triNormals[ triNormalIndex + 1 ];
				z += triNormals[ triNormalIndex + 2 ];
				node = node.next;
			}
			
			float normFactor = 1f / ( float ) Math.sqrt( x * x + y * y + z * z );
			normalIndex = pi + normalOffset;
			verts[ normalIndex++ ] = x * normFactor;
			verts[ normalIndex++ ] = y * normFactor;
			verts[ normalIndex++ ] = z * normFactor;
		}
	}
	
	private static class TriangleListNode
	{
		public int				triangleIndex;
		public TriangleListNode	next;
		
		public TriangleListNode( int triangleIndex , TriangleListNode next )
		{
			super( );
			this.triangleIndex = triangleIndex;
			this.next = next;
		}
	}
}
