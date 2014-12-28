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
package org.andork.jogl.neu2;

import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.ortho;

import org.andork.jogl.PerspectiveProjection;
import org.andork.jogl.Projection;
import org.andork.math3d.PickXform;
import org.andork.math3d.Vecmath;

public class DefaultJoglCamera implements JoglCamera
{
	/**
	 * The view matrix.
	 */
	protected final float[ ]	v			= newMat4f( );

	/**
	 * The inverse of the view matrix.
	 */
	protected final float[ ]	vi			= newMat4f( );

	/**
	 * The projection matrix.
	 */
	protected final float[ ]	p			= newMat4f( );

	/**
	 * Transforms from pixel space coordinates to clipping coordinates.
	 */
	protected final float[ ]	screenXform	= newMat4f( );

	protected final float[ ]	pixelScale	= new float[ 2 ];

	private Projection			projection	= new PerspectiveProjection( ( float ) Math.PI / 2 , 1f , 1e7f );

	private int					width , height;

	private PickXform			pickXform	= new PickXform( );

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.jogl.neu2.JoglCamera#updatePickXform()
	 */
	@Override
	public void update( int width , int height )
	{
		this.width = width;
		this.height = height;

		ortho( screenXform , 0 , width , 0 , height , 1 , -1 );
		pixelScale[ 0 ] = screenXform[ 0 ];
		pixelScale[ 1 ] = screenXform[ 5 ];

		projection.calculate( this , p );

		pickXform.calculate( p , v );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.jogl.neu2.JoglCamera#getViewXform(float[])
	 */
	@Override
	public void getViewXform( float[ ] out )
	{
		System.arraycopy( v , 0 , out , 0 , 16 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.jogl.neu2.JoglCamera#setViewXform(float[])
	 */
	@Override
	public void setViewXform( float[ ] v )
	{
		if( Vecmath.hasNaNsOrInfinites( v ) )
		{
			throw new IllegalArgumentException( "v must not contain NaN or Infinite values" );
		}

		System.arraycopy( v , 0 , this.v , 0 , 16 );
		invAffine( v , vi );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.jogl.neu2.JoglCamera#pickXform()
	 */
	@Override
	public PickXform pickXform( )
	{
		return pickXform;
	}

	public int getWidth( )
	{
		return width;
	}

	public int getHeight( )
	{
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.andork.jogl.neu2.JoglCamera#setProjectionCalculator(org.andork.jogl
	 * .ProjectionCalculator)
	 */
	@Override
	public void setProjection( Projection projection )
	{
		this.projection = projection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.jogl.neu2.JoglCamera#getProjectionCalculator()
	 */
	@Override
	public Projection getProjection( )
	{
		return projection;
	}

	@Override
	public float[ ] inverseViewXform( )
	{
		return vi;
	}

	@Override
	public float[ ] viewXform( )
	{
		return v;
	}

	@Override
	public float[ ] projXform( )
	{
		return p;
	}

	@Override
	public float[ ] screenXform( )
	{
		return screenXform;
	}

	@Override
	public float[ ] pixelScale( )
	{
		return pixelScale;
	}
}
