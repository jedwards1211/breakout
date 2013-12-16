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
