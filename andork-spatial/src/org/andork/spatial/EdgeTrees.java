package org.andork.spatial;

public class EdgeTrees
{
	public static boolean isInPolygon( float[ ] point , RNode<float[ ], float[ ]> edgeTree )
	{
		Integer intersections = RTraversal.traverse( edgeTree ,
				node -> {
					float[ ] mbr = node.mbr( );
					return mbr[ 0 ] <= point[ 0 ] && mbr[ 1 ] <= point[ 1 ] && mbr[ 3 ] >= point[ 1 ];
				} ,
				leaf -> {
					float[ ] p = point;
					float[ ] edge = leaf.object( );
					float[ ] mbr = leaf.mbr( );
					if( p[ 1 ] == mbr[ 1 ] )
					{
						return 0;
					}
					if( p[ 0 ] > mbr[ 2 ] )
					{
						return 1;
					}
					// note; edge[1] != edge[3] because in that case we would have returned at p[1] == mbr[1],
					// since p[1] is guaranteed to be between mbr[1] and mbr[3]
					return p[ 0 ] > edge[ 0 ] + ( p[ 1 ] - edge[ 1 ] ) * ( edge[ 2 ] - edge[ 0 ] ) / ( edge[ 3 ] - edge[ 1 ] ) ? 1 : 0;
				} ,
				( ) -> 0 ,
				( Integer a , Integer b ) -> a + b );
		
		return intersections != null && ( intersections % 2 ) == 1;
	}
}
