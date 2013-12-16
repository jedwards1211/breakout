package org.andork.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.andork.awt.layout.Corner;
import org.andork.awt.layout.Side;

public class GradientFillBorder extends FillBorder
{
	Side	side1;
	Corner	corner1;
	Color	color1;
	Side	side2;
	Corner	corner2;
	Color	color2;
	
	public GradientFillBorder( Side side1 , Color color1 , Side side2 , Color color2 )
	{
		super( );
		this.side1 = side1;
		this.color1 = color1;
		this.side2 = side2;
		this.color2 = color2;
	}
	
	public GradientFillBorder( Corner corner1 , Color color1 , Corner corner2 , Color color2 )
	{
		super( );
		this.corner1 = corner1;
		this.color1 = color1;
		this.corner2 = corner2;
		this.color2 = color2;
	}
	
	public GradientFillBorder( Side side1 , Color color1 , Corner corner2 , Color color2 )
	{
		super( );
		this.side1 = side1;
		this.color1 = color1;
		this.corner2 = corner2;
		this.color2 = color2;
	}
	
	public GradientFillBorder( Corner corner1 , Color color1 , Side side2 , Color color2 )
	{
		super( );
		this.corner1 = corner1;
		this.color1 = color1;
		this.side2 = side2;
		this.color2 = color2;
	}
	
	public GradientFillBorder( Side side1 , Color color1 , Color color2 )
	{
		super( );
		this.side1 = side1;
		this.color1 = color1;
		this.color2 = color2;
	}
	
	public GradientFillBorder( Corner corner1 , Color color1 , Color color2 )
	{
		super( );
		this.corner1 = corner1;
		this.color1 = color1;
		this.color2 = color2;
	}
	
	protected static Point2D Point2D( Point p )
	{
		return new Point2D.Double( p.x , p.y );
	}
	
	protected Point2D getPoint1( int x , int y , int width , int height )
	{
		Rectangle bounds = getFillBounds( x , y , width , height );
		if( side1 == null )
		{
			if( corner1 == null )
			{
				return new Point2D.Double( bounds.getCenterX( ) , bounds.getCenterY( ) );
			}
			return Point2D( corner1.location( bounds ) );
		}
		return Point2D( side1.center( bounds ) );
	}
	
	protected Color getColor1( )
	{
		return color1;
	}
	
	protected Point2D getPoint2( int x , int y , int width , int height )
	{
		Rectangle bounds = getFillBounds( x , y , width , height );
		if( side2 == null )
		{
			if( corner2 == null )
			{
				return new Point( ( int ) bounds.getCenterX( ) , ( int ) bounds.getCenterY( ) );
			}
			return Point2D( corner2.location( bounds ) );
		}
		return Point2D( side2.center( bounds ) );
	}
	
	protected Color getColor2( )
	{
		return color2;
	}
	
	protected Rectangle getFillBounds( int x , int y , int width , int height )
	{
		return new Rectangle( x , y , width , height );
	}
	
	@Override
	public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
	{
		Graphics2D g2 = ( Graphics2D ) g;
		Paint prevPaint = g2.getPaint( );
		
		g2.setPaint( new GradientPaint(
				getPoint1( x , y , width , height ) ,
				getColor1( ) ,
				getPoint2( x , y , width , height ) ,
				getColor2( ) ) );
		
		g2.fill( getFillBounds( x , y , width , height ) );
		
		g2.setPaint( prevPaint );
	}
}
