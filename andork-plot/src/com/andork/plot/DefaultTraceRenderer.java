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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class DefaultTraceRenderer implements ITraceRenderer
{
	private Graphics2D		g2;
	private Stroke			stroke		= new BasicStroke( 1f , BasicStroke.CAP_ROUND , BasicStroke.JOIN_ROUND );
	private double			pointRadius	= 3;
	private Color			pointColor	= Color.RED;
	private Color			lineColor	= Color.RED;
	private Color			fillColor	= new Color( 255 , 0 , 0 , 128 );
	
	private final Ellipse2D	ellipse		= new Ellipse2D.Double( );
	
	public DefaultTraceRenderer( )
	{
		this( null );
	}
	
	public DefaultTraceRenderer( Graphics2D g2 )
	{
		setGraphics( g2 );
	}
	
	public void setGraphics( Graphics2D g2 )
	{
		this.g2 = g2;
	}
	
	public void drawPoint( Point2D point )
	{
		drawPoint( point , pointColor );
	}
	
	@Override
	public void drawPoint( Point2D point , Color color )
	{
		ellipse.setFrame( point.getX( ) - pointRadius / 2.0 , point.getY( ) - pointRadius / 2.0 , pointRadius , pointRadius );
		
		Paint prevPaint = g2.getPaint( );
		
		g2.setColor( color );
		g2.fill( ellipse );
		
		g2.setPaint( prevPaint );
	}
	
	@Override
	public void drawLine( Path2D line )
	{
		Paint prevPaint = g2.getPaint( );
		Stroke prevStroke = g2.getStroke( );
		g2.setColor( lineColor );
		g2.setStroke( stroke );
		g2.draw( line );
		g2.setPaint( prevPaint );
		g2.setStroke( prevStroke );
	}
	
	@Override
	public void drawFill( Path2D fill )
	{
		Paint prevPaint = g2.getPaint( );
		g2.setColor( fillColor );
		g2.fill( fill );
		g2.setPaint( prevPaint );
	}
	
	public Stroke getStroke( )
	{
		return stroke;
	}
	
	public void setStroke( Stroke stroke )
	{
		this.stroke = stroke;
	}
	
	public Color getLineColor( )
	{
		return lineColor;
	}
	
	public void setLineColor( Color lineColor )
	{
		this.lineColor = lineColor;
	}
	
	public Color getPointColor( )
	{
		return pointColor;
	}
	
	public void setPointColor( Color pointColor )
	{
		this.pointColor = pointColor;
	}
	
	public Color getFillColor( )
	{
		return fillColor;
	}
	
	public void setFillColor( Color fillColor )
	{
		this.fillColor = fillColor;
	}
}
