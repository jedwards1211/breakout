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
package org.andork.jogl.old;

import static org.andork.math3d.Vecmath.invAffineToTranspose3x3;
import static org.andork.math3d.Vecmath.mmul3x3;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.newMat4f;





import javax.media.opengl.GL2ES2;

import org.andork.jogl.JoglDrawContext;

public class JOGLXformGroup extends JOGLGroup implements JOGLObject
{
	public float[ ]			xform	= newMat4f( );
	private float[ ]		nxform	= new float[ 9 ];
	private float[ ]		m		= newMat4f( );
	private float[ ]		n		= new float[ 9 ];
	
	public JOGLXformGroup( )
	{
		updateN( );
	}
	
	public void updateN( )
	{
		invAffineToTranspose3x3( xform , nxform );
	}
	
	@Override
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
	{
		mmulAffine( m , xform , this.m );
		mmul3x3( n , nxform , this.n );
		
		super.draw( gl , this.m , this.n , v , p );
	}

	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m, float[ ] n )
	{
		mmulAffine( m , xform , this.m );
		mmul3x3( n , nxform , this.n );
		
		super.draw( context , gl , m, n );
	}
}
