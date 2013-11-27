package org.andork.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.SwingConstants;

public enum Axis
{
	X
	{
		@Override
		public int size( Dimension dim )
		{
			return dim.width;
		}
		
		@Override
		public void setSize( Dimension dim , int size )
		{
			dim.width = size;
		}
		
		@Override
		public int lower( Rectangle bounds )
		{
			return bounds.x;
		}
		
		@Override
		public void setLower( Rectangle bounds , int lower )
		{
			bounds.x = lower;
		}
		
		@Override
		public void setSize( Rectangle bounds , int size )
		{
			bounds.width = size;
		}
		
		@Override
		public int lower( Insets insets )
		{
			return insets.left;
		}
		
		@Override
		public void setLower( Insets insets , int lower )
		{
			insets.left = lower;
		}
		
		@Override
		public int upper( Insets insets )
		{
			return insets.right;
		}
		
		@Override
		public void setUpper( Insets insets , int upper )
		{
			insets.right = upper;
		}
		
		@Override
		public int swingConstant( )
		{
			return SwingConstants.HORIZONTAL;
		}
		
		@Override
		public Side lowerSide( )
		{
			return Side.LEFT;
		}
		
		@Override
		public Side upperSide( )
		{
			return Side.RIGHT;
		}
		
		@Override
		public Axis opposite( )
		{
			return Y;
		}
		
		@Override
		public int get( Point p )
		{
			return p.x;
		}
		
		@Override
		public void set( Point p , int value )
		{
			p.x = value;
		}
		
		@Override
		public double get( Point2D p )
		{
			return p.getX( );
		}
		
		@Override
		public void set( Point2D p , double value )
		{
			p.setLocation( value , p.getY( ) );
		}
	} ,
	Y
	{
		@Override
		public int size( Dimension dim )
		{
			return dim.height;
		}
		
		@Override
		public void setSize( Dimension dim , int size )
		{
			dim.height = size;
		}
		
		@Override
		public int lower( Rectangle bounds )
		{
			return bounds.y;
		}
		
		@Override
		public void setLower( Rectangle bounds , int lower )
		{
			bounds.y = lower;
		}
		
		@Override
		public void setSize( Rectangle bounds , int size )
		{
			bounds.height = size;
		}
		
		@Override
		public int lower( Insets insets )
		{
			return insets.top;
		}
		
		@Override
		public void setLower( Insets insets , int lower )
		{
			insets.top = lower;
		}
		
		@Override
		public int upper( Insets insets )
		{
			return insets.bottom;
		}
		
		@Override
		public void setUpper( Insets insets , int upper )
		{
			insets.bottom = upper;
		}
		
		@Override
		public int swingConstant( )
		{
			return SwingConstants.VERTICAL;
		}
		
		@Override
		public Side lowerSide( )
		{
			return Side.TOP;
		}
		
		@Override
		public Side upperSide( )
		{
			return Side.BOTTOM;
		}
		
		@Override
		public Axis opposite( )
		{
			return X;
		}
		
		@Override
		public int get( Point p )
		{
			return p.y;
		}
		
		@Override
		public void set( Point p , int value )
		{
			p.y = value;
		}
		
		@Override
		public double get( Point2D p )
		{
			return p.getY( );
		}
		
		@Override
		public void set( Point2D p , double value )
		{
			p.setLocation( p.getX( ) , value );
		}
	};
	
	public static Axis fromSwingConstant( int swingConstant )
	{
		switch( swingConstant )
		{
			case SwingConstants.HORIZONTAL:
				return X;
			case SwingConstants.VERTICAL:
				return Y;
			default:
				throw new IllegalArgumentException( "swingConstant must be SwingConstants.HORIZONTAL or SwingConstants.VERTICAL" );
		}
	}
	
	public abstract int swingConstant( );
	
	public abstract Side lowerSide( );
	
	public abstract Side upperSide( );
	
	public abstract Axis opposite( );
	
	public abstract int size( Dimension dim );
	
	public abstract void setSize( Dimension dim , int size );
	
	public abstract int lower( Rectangle bounds );
	
	public abstract int get( Point p );
	
	public abstract void set( Point p , int value );
	
	public abstract double get( Point2D p );
	
	public abstract void set( Point2D p , double value );
	
	public abstract void setLower( Rectangle bounds , int lower );
	
	public int size( Rectangle bounds )
	{
		return size( bounds.getSize( ) );
	}
	
	public abstract void setSize( Rectangle bounds , int size );
	
	public void setBounds( Rectangle bounds , int lower , int size )
	{
		setLower( bounds , lower );
		setSize( bounds , size );
	}
	
	public void grow( Rectangle bounds , int amount )
	{
		setSize( bounds , size( bounds ) + amount );
	}
	
	public int upper( Rectangle bounds )
	{
		return lower( bounds ) + size( bounds );
	}
	
	public int center( Rectangle bounds )
	{
		return lower( bounds ) + size( bounds ) / 2;
	}
	
	public int center( Component comp )
	{
		return center( comp.getBounds( ) );
	}
	
	public int insetLocalCenter( Container parent )
	{
		return ( lowerSide( ).insetLocalLocation( parent ) + upperSide( ).insetLocalLocation( parent ) ) / 2;
	}
	
	public boolean contains( Rectangle bounds , int value )
	{
		int size = size( bounds );
		int lower = lower( bounds );
		
		return size > 0 && ( lower + size < size || lower + size > value );
	}
	
	public int size( Component comp )
	{
		return size( comp.getSize( ) );
	}
	
	public int lower( Component comp )
	{
		return lower( comp.getBounds( ) );
	}
	
	public int upper( Component comp )
	{
		return upper( comp.getBounds( ) );
	}
	
	public boolean contains( Component comp , int value )
	{
		return contains( comp.getBounds( ) , value );
	}
	
	public abstract int lower( Insets insets );
	
	public abstract void setLower( Insets insets , int lower );
	
	public abstract int upper( Insets insets );
	
	public abstract void setUpper( Insets insets , int upper );
	
	public int sizeReduction( Insets insets )
	{
		return lower( insets ) + upper( insets );
	}
	
	public int sizeReduction( Container parent )
	{
		return sizeReduction( parent.getInsets( ) );
	}
	
	public int insetSize( Container parent )
	{
		return size( parent ) - sizeReduction( parent );
	}
	
	public int lowerInset( Container parent )
	{
		return lower( parent.getInsets( ) );
	}
	
	public int upperInset( Container parent )
	{
		return upper( parent.getInsets( ) );
	}
}
