package org.andork.swing.jump;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.andork.awt.layout.Axis;

/**
 * A companion to a scroll bar that draws colored rectangles indicating the position of specially marked elements in the scrolled content. When the user clicks
 * a rectangle it will scroll to the corresponding element (just like the component to the right of the scroll bar in an Eclipse editor pane). If the user
 * clicks a blank area it will scroll to that relative location in the content.<br />
 * <br />
 * {@code JumpBar} can be configured to work with any kind of content with discrete elements via a corresponding {@link ListModel} implementation.
 * 
 * @author andy.edwards
 */
@SuppressWarnings( "serial" )
public class JumpBar extends JComponent
{
	JScrollBar					scrollBar;
	
	ListModel					model;
	
	JumpSupport					jumpSupport;
	
	int							markSize			= 5;
	
	MouseHandler				mouseHandler		= new MouseHandler( );
	
	ChangeHandler				changeHandler		= new ChangeHandler( );
	
	Map<Object, Color>			colorMap			= null;
	
	public JumpBar( JScrollBar scrollBar )
	{
		this( scrollBar , null , null );
	}
	
	public JumpBar( JScrollBar scrollBar , ListModel model )
	{
		this( scrollBar , model , null );
	}
	
	public JumpBar( JScrollBar scrollBar , ListModel model , JumpSupport jumpSupport )
	{
		super( );
		this.scrollBar = scrollBar;
		this.model = model;
		this.jumpSupport = jumpSupport;
		setBorder( new EmptyBorder( 2 , 2 , 2 , 2 ) );
		addMouseListener( mouseHandler );
		addMouseMotionListener( mouseHandler );
	}
	
	public ListModel getModel( )
	{
		return model;
	}
	
	public void setModel( ListModel model )
	{
		if( this.model != model )
		{
			if( this.model != null )
			{
				this.model.removeListDataListener( changeHandler );
			}
			this.model = model;
			if( this.model != null )
			{
				this.model.addListDataListener( changeHandler );
			}
			repaint( );
		}
	}
	
	public JumpSupport getJumpSupport( )
	{
		return jumpSupport;
	}
	
	public void setJumpSupport( JumpSupport jumpSupport )
	{
		this.jumpSupport = jumpSupport;
	}
	
	public Map<Object, Color> getColorMap( )
	{
		return colorMap;
	}
	
	public void setColorMap( Map<?, Color> colorMap )
	{
		if( colorMap == null )
		{
			this.colorMap = null;
		}
		else
		{
			this.colorMap = Collections.unmodifiableMap( new HashMap<Object, Color>( colorMap ) );
		}
	}
	
	public Rectangle getScrollBarTrackBounds( )
	{
		Axis axis = getAxis( );
		
		AbstractButton decrButton = null, incrButton = null;
		
		for( Component comp : scrollBar.getComponents( ) )
		{
			if( comp instanceof AbstractButton )
			{
				if( decrButton == null )
				{
					decrButton = ( AbstractButton ) comp;
				}
				else if( incrButton == null )
				{
					incrButton = ( AbstractButton ) comp;
					break;
				}
			}
		}
		
		if( decrButton == null || incrButton == null )
		{
			return null;
		}
		
		if( axis.lower( decrButton ) > axis.lower( incrButton ) )
		{
			AbstractButton swap = decrButton;
			decrButton = incrButton;
			incrButton = swap;
		}
		
		Point p;
		p = axis.upperSide( ).center( decrButton.getBounds( ) );
		p = SwingUtilities.convertPoint( scrollBar , p , this );
		int start = axis.get( p ) + 1;
		
		p = axis.lowerSide( ).center( incrButton.getBounds( ) );
		p = SwingUtilities.convertPoint( scrollBar , p , this );
		int end = axis.get( p ) - 1;
		
		Rectangle r = SwingUtilities.calculateInnerArea( scrollBar , null );
		axis.setLower( r , start );
		axis.setUpper( r , end );
		
		return r;
	}
	
	protected Color getColor( int index )
	{
		Object o = model.getElementAt( index );
		if( o == null )
		{
			return null;
		}
		if( o instanceof Color )
		{
			return ( Color ) o;
		}
		if( colorMap != null )
		{
			return colorMap.get( o );
		}
		return null;
	}
	
	@Override
	protected void paintComponent( Graphics g )
	{
		if( model == null )
		{
			return;
		}
		
		Insets insets = getInsets( );
		
		Graphics2D g2 = ( Graphics2D ) g;
		
		Axis axis = getAxis( );
		
		Rectangle track = getScrollBarTrackBounds( );
		track = SwingUtilities.convertRectangle( scrollBar , track , this );
		
		int start = axis.lower( track );
		int span = axis.size( track );
		
		for( int index = 0 ; index < model.getSize( ) ; index++ )
		{
			Color markColor = getColor( index );
			if( markColor == null )
			{
				continue;
			}
			
			int markStart = start + index * span / model.getSize( );
			
			while( index + 1 < model.getSize( ) && markColor.equals( getColor( index + 1 ) ) )
			{
				index++ ;
			}
			
			int markEnd = start + ( index + 1 ) * span / model.getSize( );
			
			int remaining = markSize + markStart - markEnd;
			if( remaining > 0 )
			{
				markStart -= remaining / 2;
				markEnd = markStart + markSize;
			}
			
			g2.setColor( new Color( markColor.getRed( ) , markColor.getGreen( ) ,
					markColor.getBlue( ) , 64 ) );
			int x = axis == Axis.Y ? insets.left : markStart;
			int y = axis == Axis.Y ? markStart : insets.top;
			int width = axis == Axis.Y ? getWidth( ) - insets.right - insets.left : markEnd - markStart;
			int height = axis == Axis.Y ? markEnd - markStart : getHeight( ) - insets.top - insets.bottom;
			g2.fillRect( x , y , width , height );
			g2.setColor( getDarkerColor( markColor , 0.3f , 128 ) );
			g2.drawRect( x , y , width , height );
		}
	}
	
	private Color getDarkerColor( Color c , float darkness , int alpha )
	{
		float[ ] hsb = new float[ 4 ];
		Color.RGBtoHSB( c.getRed( ) , c.getGreen( ) , c.getBlue( ) , hsb );
		hsb[ 2 ] = Math.max( 0 , hsb[ 2 ] - darkness );
		Color result = Color.getHSBColor( hsb[ 0 ] , hsb[ 1 ] , hsb[ 2 ] );
		return new Color( result.getRed( ) , result.getGreen( ) , result.getBlue( ) , alpha );
	}
	
	@Override
	public Dimension getPreferredSize( )
	{
		if( !isPreferredSizeSet( ) )
		{
			return scrollBar.getPreferredSize( );
		}
		return super.getPreferredSize( );
	}
	
	@Override
	public Dimension getMinimumSize( )
	{
		if( !isMinimumSizeSet( ) )
		{
			return scrollBar.getMinimumSize( );
		}
		return super.getMinimumSize( );
	}
	
	private Axis getAxis( )
	{
		Axis axis = scrollBar.getOrientation( ) == JScrollBar.VERTICAL ? Axis.Y : Axis.X;
		return axis;
	}
	
	/**
	 * Provides {@link JumpBar} the ability to scroll any element of the content to visible.
	 * 
	 * @author andy.edwards
	 */
	public static interface JumpSupport
	{
		/**
		 * {@link JumpBar} will call this method to scroll a given element of the content to visible.
		 * 
		 * @param index
		 *            the index of the element to scroll to visible.
		 */
		public void scrollElementToVisible( int index );
	}
	
	private class MouseHandler extends MouseAdapter
	{
		@Override
		public void mousePressed( MouseEvent e )
		{
			Axis axis = getAxis( );
			
			Rectangle track = getScrollBarTrackBounds( );
			track = SwingUtilities.convertRectangle( scrollBar , track , JumpBar.this );
			
			if( jumpSupport != null && model != null )
			{
				Rectangle insetRect = SwingUtilities.calculateInnerArea( JumpBar.this , null );
				
				if( insetRect.contains( e.getPoint( ) ) )
				{
					int mid = axis.get( e.getPoint( ) );
					int start = mid - markSize / 2;
					int end = start + markSize;
					
					start = Math.max( axis.lower( track ) , start );
					end = Math.min( axis.upper( track ) , end );
					
					int startIndex = ( start - axis.lower( track ) ) * model.getSize( ) / axis.size( track );
					int midIndex = ( mid - axis.lower( track ) ) * model.getSize( ) / axis.size( track );
					int endIndex = ( end - axis.lower( track ) ) * model.getSize( ) / axis.size( track );
					endIndex = Math.min( endIndex , model.getSize( ) - 1 );
					
					for( int i = 0 ; i <= Math.max( endIndex - midIndex , midIndex - startIndex ) ; i = i < 0 ? -i : -i - 1 )
					{
						int index = midIndex + i;
						if( index >= 0 && index < model.getSize( ) )
						{
							if( getColor( index ) != null )
							{
								jumpSupport.scrollElementToVisible( index );
								return;
							}
						}
					}
				}
			}
			
			scrollBar.setValue( Math.max( scrollBar.getMinimum( ) , Math.min( scrollBar.getMaximum( ) ,
					( axis.get( e.getPoint( ) ) - axis.lower( track ) ) * ( scrollBar.getMaximum( ) - scrollBar.getMinimum( ) )
							/ axis.size( track ) - scrollBar.getVisibleAmount( ) / 2 ) ) );
		}
		
		@Override
		public void mouseMoved( MouseEvent e )
		{
			Rectangle insetRect = SwingUtilities.calculateInnerArea( JumpBar.this , null );
			
			Axis axis = getAxis( );
			
			Rectangle track = getScrollBarTrackBounds( );
			track = SwingUtilities.convertRectangle( scrollBar , track , JumpBar.this );
			
			int start = axis.get( e.getPoint( ) ) - markSize / 2;
			int end = start + markSize;
			
			Cursor cursor = null;
			
			if( insetRect.contains( e.getPoint( ) ) && model != null )
			{
				start = Math.max( axis.lower( track ) , start );
				end = Math.min( axis.upper( track ) , end );
				
				int startIndex = ( start - axis.lower( track ) ) * model.getSize( ) / axis.size( track );
				int endIndex = ( end - axis.lower( track ) ) * model.getSize( ) / axis.size( track );
				endIndex = Math.min( endIndex , model.getSize( ) - 1 );
				
				for( int index = startIndex ; index <= endIndex ; index++ )
				{
					if( getColor( index ) != null )
					{
						cursor = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
						break;
					}
				}
			}
			
			setCursor( cursor );
		}
	}
	
	private class ChangeHandler implements ListDataListener
	{
		@Override
		public void intervalAdded( ListDataEvent e )
		{
			repaint( );
		}
		
		@Override
		public void intervalRemoved( ListDataEvent e )
		{
			repaint( );
		}
		
		@Override
		public void contentsChanged( ListDataEvent e )
		{
			repaint( );
		}
	}
}
