package org.andork.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.andork.awt.layout.Corner;
import org.andork.awt.layout.Side;

public abstract class GradientFillBorder extends FillBorder
{
	protected GradientFillBorder( )
	{
		
	}
	
	protected static Point2D Point2D( Point p )
	{
		return new Point2D.Double( p.x , p.y );
	}
	
	protected abstract Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height );
	
	protected abstract Color getColor1( Component c , Graphics g );
	
	protected abstract Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height );
	
	protected abstract Color getColor2( Component c , Graphics g );
	
	@Override
	public Paint getPaint( Component c , Graphics g , int x , int y , int width , int height )
	{
		return new GradientPaint(
				getPoint1( c , g , x , y , width , height ) ,
				getColor1( c , g ) ,
				getPoint2( c , g , x , y , width , height ) ,
				getColor2( c , g ) );
	}
	
	public static From from( final Side side1 )
	{
		return new From( )
		{
			@Override
			protected Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height )
			{
				return Point2D( side1.center( new Rectangle( x , y , width , height ) ) );
			}
		};
	}
	
	public static From from( final Corner corner1 )
	{
		return new From( )
		{
			@Override
			protected Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height )
			{
				return corner1.location( new Rectangle( x , y , width , height ) );
			}
		};
	}
	
	public static From fromCenter( )
	{
		return new From( )
		{
			@Override
			protected Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height )
			{
				return new Point2D.Double( x + width / 2.0 , y + height / 2.0 );
			}
		};
	}
	
	public static From fromAbs( final double x , final double y )
	{
		return new From( )
		{
			@Override
			protected java.awt.geom.Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height )
			{
				return new Point2D.Double( x , y );
			}
		};
	}
	
	public static From fromRel( final double xf , final double yf )
	{
		return new From( )
		{
			@Override
			protected Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height )
			{
				return new Point2D.Double( x + width * xf , y + height * yf );
			}
		};
	}
	
	public static abstract class From
	{
		private From( )
		{
			
		}
		
		protected abstract Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height );
		
		public To to( final Side side2 )
		{
			return new To( this )
			{
				@Override
				protected java.awt.geom.Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height )
				{
					return Point2D( side2.center( new Rectangle( x , y , width , height ) ) );
				}
			};
		}
		
		public To to( final Corner corner2 )
		{
			return new To( this )
			{
				@Override
				protected java.awt.geom.Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height )
				{
					return Point2D( corner2.location( new Rectangle( x , y , width , height ) ) );
				}
			};
		}
		
		public To toCenter( )
		{
			return new To( this )
			{
				@Override
				protected java.awt.geom.Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height )
				{
					return new Point2D.Double( x + width / 2.0 , y + height / 2.0 );
				}
			};
		}
		
		public To toAbs( final double x , final double y )
		{
			return new To( this )
			{
				@Override
				protected java.awt.geom.Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height )
				{
					return new Point2D.Double( x , y );
				}
			};
		}
		
		public To toRel( final double xf , final double yf )
		{
			return new To( this )
			{
				@Override
				protected java.awt.geom.Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height )
				{
					return new Point2D.Double( x + width * xf , y + height * yf );
				}
			};
		}
	}
	
	public static abstract class To
	{
		From	from;
		
		private To( From from )
		{
			this.from = from;
		}
		
		protected abstract Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height );
		
		public GradientFillBorder colors( final Color color1 , final Color color2 )
		{
			return new GradientFillBorder( )
			{
				
				@Override
				protected java.awt.geom.Point2D getPoint1( Component c , Graphics g , int x , int y , int width , int height )
				{
					return from.getPoint1( c , g , x , y , width , height );
				}
				
				@Override
				protected Color getColor1( Component c , Graphics g )
				{
					return color1;
				}
				
				@Override
				protected java.awt.geom.Point2D getPoint2( Component c , Graphics g , int x , int y , int width , int height )
				{
					return To.this.getPoint2( c , g , x , y , width , height );
				}
				
				@Override
				protected Color getColor2( Component c , Graphics g )
				{
					return color2;
				}
			};
		}
	}
}
