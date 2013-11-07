
package com.andork.plot;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

@SuppressWarnings( "serial" )
public class Plot extends JComponent
{
	private final List<IPlotLayer>	layers	= new ArrayList<IPlotLayer>( );
	
	public void addLayer( IPlotLayer layer )
	{
		if( !layers.contains( layer ) )
		{
			layers.add( layer );
		}
	}
	
	protected void paintComponent( Graphics g )
	{
		Graphics2D g2 = ( Graphics2D ) g;
		Rectangle bounds = new Rectangle( getWidth( ) , getHeight( ) );
		
		for( IPlotLayer layer : layers )
		{
			layer.render( g2 , bounds );
		}
	}
}
