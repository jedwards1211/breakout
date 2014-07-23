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
package org.andork.vecmath;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class EdgeNormalComputer
{
	private final Vector3f	v1	= new Vector3f( );
	private final Vector3f	v2	= new Vector3f( );
	private final Vector3f	v3	= new Vector3f( );
	
	public Vector3f edgeNormal( Point3f e1 , Point3f e2 , Point3f c1 , Point3f c2 , Vector3f result )
	{
		v2.sub( e2 , e1 );
		v3.sub( c1 , e1 );
		v1.cross( v2 , v3 );
		v1.normalize( );
		
		v3.sub( c2 , e1 );
		v2.cross( v3 , v2 );
		v2.normalize( );
		
		result.add( v1 , v2 );
		result.normalize( );
		
		return result;
	}
}
