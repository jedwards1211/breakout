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
package com.andork.plot;

import java.awt.CompositeContext;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class AlphaDepthCompositeContext implements CompositeContext
{
	@Override
	public void dispose( )
	{
		
	}
	
	@Override
	public void compose( Raster src , Raster dstIn , WritableRaster dstOut )
	{
		Rectangle r = src.getBounds( );
		r = r.intersection( dstIn.getBounds( ) );
		r = r.intersection( dstOut.getBounds( ) );
		
		int[ ] srcPixels = src.getPixels( r.x , r.y , r.width , r.height , ( int[ ] ) null );
		int[ ] dstPixels = dstIn.getPixels( r.x , r.y , r.width , r.height , ( int[ ] ) null );
		
		for( int i = 0 ; i < srcPixels.length ; i++ )
		{
			int srcAlpha = srcPixels[ i ] >> 24;
			int dstAlpha = dstPixels[ i ] >> 24;
			
			dstPixels[ i ] = dstAlpha >= srcAlpha ? dstPixels[ i ] : srcPixels[ i ];
		}
		
		dstOut.setPixels( r.x , r.y , r.width , r.height , dstPixels );
	}
}
