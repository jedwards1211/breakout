package org.andork.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.andork.layout.DelegatingLayoutManager.LayoutDelegate;

public class DrawerLayoutDelegate implements LayoutDelegate
{
	public DrawerLayoutDelegate( Component comp , Side dockingSide )
	{
		this( comp , null , dockingSide , true );
	}
	
	public DrawerLayoutDelegate( Component comp , Side dockingSide , boolean fill )
	{
		this( comp , null , dockingSide , fill );
	}
	
	public DrawerLayoutDelegate( Component comp , Corner dockingCorner , Side dockingSide )
	{
		this( comp , dockingCorner , dockingSide , false );
	}
	
	private DrawerLayoutDelegate( Component comp , Corner dockingCorner , Side dockingSide , boolean fill )
	{
		super( );
		this.comp = comp;
		this.dockingCorner = dockingCorner;
		this.dockingSide = dockingSide;
		this.fill = fill;
	}
	
	public static enum State
	{
		CLOSED , CLOSING , OPENING , OPEN
	}
	
	Component		comp;
	
	Side			dockingSide;
	
	Corner			dockingCorner;
	
	boolean			fill		= false;
	
	float			openAmount	= 1;
	State			state		= State.OPEN;
	float			animFactor	= .2f;
	int				animSpeed	= 10;
	
	private long	lastAnimTime;
	private Timer	animTimer;
	
	private void animate( long time )
	{
		while( time > 0 )
		{
			if( state == State.CLOSING && openAmount > 0f )
			{
				openAmount = Math.max( 0f , openAmount - ( openAmount + .1f ) * animFactor );
				if( openAmount == 0f )
				{
					state = State.CLOSED;
					break;
				}
			}
			else if( state == State.OPENING && openAmount < 1f )
			{
				openAmount = Math.min( 1f , openAmount + ( 1.1f - openAmount ) * animFactor );
				if( openAmount == 1f )
				{
					state = State.OPEN;
					break;
				}
			}
			
			time -= animSpeed;
		}
	}
	
	public void close( )
	{
		if( state == State.OPENING || state == State.OPEN )
		{
			state = State.CLOSING;
			startAnimating( );
		}
	}
	
	public void open( )
	{
		if( state == State.CLOSED || state == State.CLOSING )
		{
			state = State.OPENING;
			startAnimating( );
		}
	}
	
	public void toggle( )
	{
		if( state == State.OPEN || state == State.OPENING )
		{
			close( );
		}
		else
		{
			open( );
		}
	}
	
	private void startAnimating( )
	{
		lastAnimTime = System.currentTimeMillis( );
		if( animTimer == null )
		{
			animTimer = new Timer( animSpeed , new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					long time = System.currentTimeMillis( );
					animate( time - lastAnimTime );
					if( state == State.OPEN || state == State.CLOSED )
					{
						animTimer.stop( );
					}
					lastAnimTime = time;
					if( comp.getParent( ) != null )
					{
						comp.getParent( ).invalidate( );
						comp.getParent( ).validate( );
					}
				}
			} );
		}
		animTimer.start( );
	}
	
	private Rectangle getBounds( Container parent , Component target , LayoutSize layoutSize , boolean open )
	{
		Rectangle bounds = new Rectangle( );
		bounds.setSize( layoutSize.get( target ) );
		
		if( dockingCorner != null )
		{
			Insets insets = parent.getInsets( );
			bounds.width = Math.min( bounds.width , parent.getWidth( ) - insets.left - insets.right );
			bounds.height = Math.min( bounds.height , parent.getHeight( ) - insets.top - insets.bottom );
			Side otherSide = dockingCorner.xSide( ) == dockingSide ? dockingCorner.ySide( ) : dockingCorner.xSide( );
			
			if( fill )
			{
				Axis otherAxis = otherSide.axis( );
				otherAxis.setSize( bounds , otherAxis.insetSize( parent ) );
			}
			
			otherSide.setLocation( bounds , otherSide.insetLocalLocation( parent ) );
			
			if( open )
			{
				dockingSide.setLocation( bounds , dockingSide.insetLocalLocation( parent ) );
			}
			else
			{
				dockingSide.opposite( ).setLocation( bounds , dockingSide.localLocation( parent ) );
			}
		}
		else
		{
			Side invSide = dockingSide.inverse( );
			Axis axis = dockingSide.axis( );
			Axis invAxis = invSide.axis( );
			axis.setSize( bounds , Math.min( axis.size( bounds ) , axis.insetSize( parent ) ) );
			
			if( fill )
			{
				invAxis.setSize( bounds , invAxis.insetSize( parent ) );
				invAxis.setLower( bounds , invAxis.lower( parent ) + invAxis.lowerInset( parent ) );
			}
			else
			{
				invAxis.setLower( bounds , invAxis.center( parent ) - invAxis.size( bounds ) / 2 );
			}
			
			if( open )
			{
				dockingSide.setLocation( bounds , dockingSide.insetLocalLocation( parent ) );
			}
			else
			{
				dockingSide.opposite( ).setLocation( bounds , dockingSide.localLocation( parent ) );
			}
		}
		
		return bounds;
	}
	
	@Override
	public Rectangle desiredBounds( Container parent , Component target , LayoutSize layoutSize )
	{
		return getBounds( parent , target , layoutSize , true );
	}
	
	@Override
	public void layoutComponent( Container parent , Component target )
	{
		Rectangle closedBounds = getBounds( parent , target , LayoutSize.PREFERRED , false );
		Rectangle openBounds = getBounds( parent , target , LayoutSize.PREFERRED , true );
		
		Rectangle bounds = new Rectangle( closedBounds );
		bounds.x = ( int ) ( openAmount * openBounds.x + ( 1 - openAmount ) * closedBounds.x );
		bounds.y = ( int ) ( openAmount * openBounds.y + ( 1 - openAmount ) * closedBounds.y );
		
		target.setBounds( bounds );
	}
}
