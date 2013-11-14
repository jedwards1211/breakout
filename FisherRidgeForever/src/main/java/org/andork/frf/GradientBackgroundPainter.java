package org.andork.frf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Map;

public class GradientBackgroundPainter
{
	IGradientMap	gradientMap;
	Orientation		orientation;
	
	public GradientBackgroundPainter( Orientation orientation , IGradientMap gradientMap )
	{
		super( );
		this.orientation = orientation;
		this.gradientMap = gradientMap;
	}
	
	public static enum Orientation
	{
		HORIZONTAL , VERTICAL;
	}
	
	public void paint( Component c , Graphics2D g2 )
	{
		Point2D start = new Point2D.Double( 0 , 0 );
		Point2D end = orientation == Orientation.HORIZONTAL ? new Point2D.Double( c.getWidth( ) , 0 ) : new Point2D.Double( 0 , c.getHeight( ) );
		
		float[ ] fractions = new float[ gradientMap.size( ) ];
		Color[ ] colors = new Color[ gradientMap.size( ) ];
		
		double min = gradientMap.firstKey( );
		double max = gradientMap.lastKey( );
		
		int k = 0;
		for( Map.Entry<Double, Color> entry : gradientMap.entries( ) )
		{
			fractions[ k ] = ( float ) ( ( entry.getKey( ).floatValue( ) - min ) / ( max - min ) );
			colors[ k ] = entry.getValue( );
			k++;
		}
		
		Paint prevPaint = g2.getPaint( );
		
		g2.setPaint( new LinearGradientPaint( start , end , fractions , colors ) );
		g2.fillRect( 0 , 0 , c.getWidth( ) , c.getHeight( ) );
		
		g2.setPaint( prevPaint );
	}
}
