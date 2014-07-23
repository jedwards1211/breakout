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
