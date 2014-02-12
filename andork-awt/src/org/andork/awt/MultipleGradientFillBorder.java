package org.andork.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;

import org.andork.awt.layout.Corner;
import org.andork.awt.layout.Side;

public class MultipleGradientFillBorder
{
	protected static Point2D Point2D( Point p )
	{
		return new Point2D.Double( p.x , p.y );
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
		
		public FillBorder linear( final float[ ] fractions , final Color[ ] colors )
		{
			return new FillBorder( )
			{
				@Override
				protected Paint getPaint( Component c , Graphics g , int x , int y , int width , int height )
				{
					return new LinearGradientPaint( from.getPoint1( c , g , x , y , width , height ) ,
							getPoint2( c , g , x , y , width , height ) , fractions , colors );
				}
			};
		}
		
		public FillBorder linear( final float[ ] fractions , final Color[ ] colors , final CycleMethod cycleMethod )
		{
			return new FillBorder( )
			{
				@Override
				protected Paint getPaint( Component c , Graphics g , int x , int y , int width , int height )
				{
					return new LinearGradientPaint( from.getPoint1( c , g , x , y , width , height ) ,
							getPoint2( c , g , x , y , width , height ) , fractions , colors , cycleMethod );
				}
			};
		}
	}
}
