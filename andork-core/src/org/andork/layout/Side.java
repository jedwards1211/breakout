package org.andork.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public enum Side
{
	TOP
	{
		
		@Override
		public Axis axis( )
		{
			return Axis.Y;
		}
		
		@Override
		public int direction( )
		{
			return -1;
		}
		
		@Override
		public Side opposite( )
		{
			return BOTTOM;
		}
		
		@Override
		public Side inverse( )
		{
			return LEFT;
		}
		
		@Override
		public Side nextClockwise( )
		{
			return RIGHT;
		}
		
		@Override
		public Side nextCounterClockwise( )
		{
			return LEFT;
		}
		
		@Override
		public int location( Rectangle bounds )
		{
			return bounds.y;
		}
		
		@Override
		public void setLocation( Rectangle bounds , int location )
		{
			bounds.y = location;
		}
		
		@Override
		public void stretch( Rectangle bounds , int location )
		{
			bounds.height += bounds.y - location;
			bounds.y = location;
		}
		
		@Override
		public int get( Insets insets )
		{
			return insets.top;
		}
		
		@Override
		public void set( Insets insets , int value )
		{
			insets.top = value;
		}
		
		@Override
		public int swingConstant( )
		{
			return SwingConstants.TOP;
		}
		
		@Override
		public int gbcAnchor( )
		{
			return GridBagConstraints.NORTH;
		}
		
	} ,
	BOTTOM
	{
		
		@Override
		public Axis axis( )
		{
			return Axis.Y;
		}
		
		@Override
		public int direction( )
		{
			return 1;
		}
		
		@Override
		public Side opposite( )
		{
			return TOP;
		}
		
		@Override
		public Side inverse( )
		{
			return RIGHT;
		}
		
		@Override
		public Side nextClockwise( )
		{
			return LEFT;
		}
		
		@Override
		public Side nextCounterClockwise( )
		{
			return RIGHT;
		}
		
		@Override
		public int location( Rectangle bounds )
		{
			return bounds.y + bounds.height;
		}
		
		@Override
		public void setLocation( Rectangle bounds , int location )
		{
			bounds.y = location - bounds.height;
		}
		
		@Override
		public void stretch( Rectangle bounds , int location )
		{
			bounds.height = location - bounds.y;
		}
		
		@Override
		public int get( Insets insets )
		{
			return insets.bottom;
		}
		
		@Override
		public void set( Insets insets , int value )
		{
			insets.bottom = value;
		}
		
		@Override
		public int swingConstant( )
		{
			return SwingConstants.BOTTOM;
		}
		
		@Override
		public int gbcAnchor( )
		{
			return GridBagConstraints.SOUTH;
		}
	} ,
	LEFT
	{
		
		@Override
		public Axis axis( )
		{
			return Axis.X;
		}
		
		@Override
		public int direction( )
		{
			return -1;
		}
		
		@Override
		public Side opposite( )
		{
			return RIGHT;
		}
		
		@Override
		public Side inverse( )
		{
			return TOP;
		}
		
		@Override
		public Side nextClockwise( )
		{
			return TOP;
		}
		
		@Override
		public Side nextCounterClockwise( )
		{
			return BOTTOM;
		}
		
		@Override
		public int location( Rectangle bounds )
		{
			return bounds.x;
		}
		
		@Override
		public void setLocation( Rectangle bounds , int location )
		{
			bounds.x = location;
		}
		
		@Override
		public void stretch( Rectangle bounds , int location )
		{
			bounds.width += bounds.x - location;
			bounds.x = location;
		}
		
		@Override
		public int get( Insets insets )
		{
			return insets.left;
		}
		
		@Override
		public void set( Insets insets , int value )
		{
			insets.left = value;
		}
		
		@Override
		public int swingConstant( )
		{
			return SwingConstants.LEFT;
		}
		
		@Override
		public int gbcAnchor( )
		{
			return GridBagConstraints.WEST;
		}
	} ,
	RIGHT
	{
		
		@Override
		public Axis axis( )
		{
			return Axis.X;
		}
		
		@Override
		public int direction( )
		{
			return 1;
		}
		
		@Override
		public Side opposite( )
		{
			return LEFT;
		}
		
		@Override
		public Side inverse( )
		{
			return BOTTOM;
		}
		
		@Override
		public Side nextClockwise( )
		{
			return BOTTOM;
		}
		
		@Override
		public Side nextCounterClockwise( )
		{
			return TOP;
		}
		
		@Override
		public int location( Rectangle bounds )
		{
			return bounds.x + bounds.width;
		}
		
		@Override
		public void setLocation( Rectangle bounds , int location )
		{
			bounds.x = location - bounds.width;
		}
		
		@Override
		public void stretch( Rectangle bounds , int location )
		{
			bounds.width = location - bounds.x;
		}
		
		@Override
		public int get( Insets insets )
		{
			return insets.right;
		}
		
		@Override
		public void set( Insets insets , int value )
		{
			insets.right = value;
		}
		
		@Override
		public int swingConstant( )
		{
			return SwingConstants.RIGHT;
		}
		
		@Override
		public int gbcAnchor( )
		{
			return GridBagConstraints.EAST;
		}
	};
	
	public static Side fromSwingConstant( int swingConstant )
	{
		switch( swingConstant )
		{
			case SwingConstants.TOP:
				return TOP;
			case SwingConstants.BOTTOM:
				return BOTTOM;
			case SwingConstants.LEFT:
				return LEFT;
			case SwingConstants.RIGHT:
				return RIGHT;
			default:
				throw new IllegalArgumentException( "swingConstant must be TOP, BOTTOM, LEFT, or RIGHT" );
		}
	}
	
	public abstract int swingConstant( );
	
	public static Side fromGbcAnchor( int gbcAnchor )
	{
		switch( gbcAnchor )
		{
			case GridBagConstraints.NORTH:
				return TOP;
			case GridBagConstraints.EAST:
				return RIGHT;
			case GridBagConstraints.SOUTH:
				return BOTTOM;
			case GridBagConstraints.WEST:
				return LEFT;
			default:
				throw new IllegalArgumentException( "gbcAnchor must be NORTH, EAST, SOUTH, or WEST" );
		}
	}
	
	public abstract int gbcAnchor( );
	
	/**
	 * @return the axis this side is positioned along (for example, even though the top side is a horizontal line, its position is on the y axis, so its axis is
	 *         {@link Axis#Y}).
	 */
	public abstract Axis axis( );
	
	public static Side fromAxis( Axis axis , int direction )
	{
		if( direction == 0 )
		{
			throw new IllegalArgumentException( "direction must be nonzero" );
		}
		switch( axis )
		{
			case X:
				return direction > 0 ? RIGHT : LEFT;
			case Y:
				return direction < 0 ? BOTTOM : TOP;
			default:
				throw new IllegalArgumentException( "axis must be non-null" );
		}
	}
	
	/**
	 * @return a unit increment along this side's axis from the center of the bounds toward the side (for example, the direction for {@link #TOP} is -1, for
	 *         {@link #RIGHT} is 1).
	 */
	public abstract int direction( );
	
	public boolean isLower( )
	{
		return direction( ) < 0;
	}
	
	public boolean isUpper( )
	{
		return direction( ) > 0;
	}
	
	public abstract Side opposite( );
	
	/**
	 * @return the side this becomes if the x and y axes are inverted.
	 */
	public abstract Side inverse( );
	
	public abstract Side nextClockwise( );
	
	public abstract Side nextCounterClockwise( );
	
	public abstract int location( Rectangle bounds );
	
	public abstract void setLocation( Rectangle bounds , int location );
	
	public abstract void stretch( Rectangle bounds , int location );
	
	public Point center( Rectangle bounds )
	{
		Point p = new Point( );
		axis( ).set( p , location( bounds ) );
		Axis opposite = axis( ).opposite( );
		opposite.set( p , opposite.center( bounds ) );
		return p;
	}
	
	public Point center( Component comp )
	{
		return center( comp.getBounds( ) );
	}
	
	public void grow( Rectangle bounds , int amount )
	{
		setLocation( bounds , location( bounds ) + amount * direction( ) );
	}
	
	public int location( Component comp )
	{
		return location( comp.getBounds( ) );
	}
	
	public int localLocation( Component comp )
	{
		return location( SwingUtilities.getLocalBounds( comp ) );
	}
	
	public abstract int get( Insets insets );
	
	public abstract void set( Insets insets , int value );
	
	public int inset( Container parent )
	{
		return get( parent.getInsets( ) );
	}
	
	public int insetLocalLocation( Container parent )
	{
		return localLocation( parent ) - direction( ) * inset( parent );
	}
}
