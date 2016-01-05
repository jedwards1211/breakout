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
package org.andork.jogl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL2ES2;

public class JoglScene implements JoglDrawable , JoglResourceManager
{
	private final LinkedList<JoglResource>	needDispose				= new LinkedList<JoglResource>( );
	private final LinkedList<JoglResource>	needInitialize			= new LinkedList<JoglResource>( );
	private List<JoglDrawable>				drawables				= new ArrayList<JoglDrawable>( );
	private List<JoglDrawable>				unmodifiableDrawables	= Collections.unmodifiableList( drawables );

	public void add( JoglDrawable drawable )
	{
		drawables.add( drawable );
	}

	public void remove( JoglDrawable drawable )
	{
		drawables.remove( drawable );
	}

	public void setDrawablesDirect( List<JoglDrawable> drawables )
	{
		this.drawables = drawables;
		unmodifiableDrawables = Collections.unmodifiableList( drawables );
	}

	public List<JoglDrawable> getDrawablesDirect( )
	{
		return this.drawables;
	}

	public List<JoglDrawable> getDrawables( )
	{
		return unmodifiableDrawables;
	}

	public void initLater( JoglResource resource )
	{
		needInitialize.add( resource );
	}

	public void disposeLater( JoglResource resource )
	{
		needDispose.add( resource );
	}

	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
	{
		JoglResource resource;

		while( ( resource = needInitialize.poll( ) ) != null )
		{
			resource.init( gl );
		}
		while( ( resource = needDispose.poll( ) ) != null )
		{
			resource.dispose( gl );
		}

		for( JoglDrawable drawable : drawables )
		{
			drawable.draw( context , gl , m , n );
		}
	}
}
