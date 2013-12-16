package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.andork.awt.layout.DelegatingLayoutManager.LayoutDelegate;

public class DrawerLayoutDelegate implements LayoutDelegate
{
	boolean			open		= true;
	boolean			maximized;
	boolean			animating;
	
	Corner			dockingCorner;
	Side			dockingSide;
	float			animFactor	= .2f;
	int				animSpeed	= 10;
	
	private long	lastAnimTime;
	private Timer	animTimer;
	
	Component		comp;
	
	boolean			fill		= false;
	
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
		if( dockingCorner != null && dockingSide != dockingCorner.xSide( ) && dockingSide != dockingCorner.ySide( ) )
		{
			throw new IllegalArgumentException( "dockingCorner must be on the same side as dockingSide" );
		}
		this.comp = comp;
		this.dockingCorner = dockingCorner;
		this.dockingSide = dockingSide;
		this.fill = fill;
	}
	
	public void close( )
	{
		if( open )
		{
			toggleOpen( );
		}
	}
	
	public void close( boolean animate )
	{
		if( open )
		{
			toggleOpen( animate );
		}
	}
	
	public void open( )
	{
		if( !open )
		{
			toggleOpen( );
		}
	}
	
	public void open( boolean animate )
	{
		if( !open )
		{
			toggleOpen( animate );
		}
	}
	
	public void toggleOpen( )
	{
		toggleOpen( true );
	}
	
	public void toggleOpen( boolean animate )
	{
		open = !open;
		animating = animate;
		if( comp.getParent( ) != null )
		{
			comp.getParent( ).invalidate( );
			comp.getParent( ).validate( );
		}
	}
	
	public void restore( )
	{
		if( maximized )
		{
			toggleMaximized( );
		}
	}
	
	public void maximize( )
	{
		if( !maximized )
		{
			toggleMaximized( );
		}
	}
	
	public void toggleMaximized( )
	{
		maximized = !maximized;
		animating = true;
		if( comp.getParent( ) != null )
		{
			comp.getParent( ).invalidate( );
			comp.getParent( ).validate( );
		}
	}
	
	private Rectangle getBounds( Container parent , Component target , LayoutSize layoutSize , boolean open , boolean maximized )
	{
		Rectangle bounds = new Rectangle( );
		bounds.setSize( layoutSize.get( target ) );
		
		if( dockingCorner != null )
		{
			Insets insets = parent.getInsets( );
			Side otherSide = dockingCorner.xSide( ) == dockingSide ? dockingCorner.ySide( ) : dockingCorner.xSide( );
			otherSide.setLocation( bounds , otherSide.insetLocalLocation( parent ) );
			
			if( maximized )
			{
				bounds.width = parent.getWidth( ) - insets.left - insets.right;
				bounds.height = parent.getHeight( ) - insets.top - insets.bottom;
			}
			else
			{
				bounds.width = Math.min( bounds.width , parent.getWidth( ) - insets.left - insets.right );
				bounds.height = Math.min( bounds.height , parent.getHeight( ) - insets.top - insets.bottom );
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
		else
		{
			Side invSide = dockingSide.inverse( );
			Axis axis = dockingSide.axis( );
			Axis invAxis = invSide.axis( );
			
			if( fill || maximized )
			{
				invAxis.setSize( bounds , invAxis.insetSize( parent ) );
				invAxis.setLower( bounds , invAxis.lowerInset( parent ) );
			}
			else
			{
				invAxis.setLower( bounds , invAxis.insetLocalCenter( parent ) - invAxis.size( bounds ) / 2 );
			}
			
			if( maximized )
			{
				axis.setSize( bounds , axis.insetSize( parent ) );
			}
			else
			{
				axis.setSize( bounds , Math.min( axis.size( bounds ) , axis.insetSize( parent ) ) );
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
		return getBounds( parent , target , layoutSize , true , maximized );
	}
	
	@Override
	public void layoutComponent( final Container parent , final Component target )
	{
		Rectangle targetBounds = getBounds( parent , target , LayoutSize.PREFERRED , open , maximized );
		Rectangle bounds = target.getBounds( );
		
		if( animating )
		{
			if( targetBounds.equals( bounds ) )
			{
				animating = false;
				if( animTimer != null )
				{
					animTimer.stop( );
					animTimer = null;
				}
			}
			else
			{
				long time = System.currentTimeMillis( );
				long elapsed = time - lastAnimTime;
				lastAnimTime = time;
				if( animTimer == null )
				{
					elapsed = animSpeed;
				}
				
				RectangleUtils.animate( bounds , targetBounds , elapsed , animFactor , 10 ,
						animSpeed , bounds );
				
				target.setBounds( bounds );
				target.invalidate( );
				target.validate( );
				
				if( animTimer == null )
				{
					animTimer = new Timer( animSpeed , new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							parent.invalidate( );
							parent.validate( );
						}
					} );
					animTimer.start( );
				}
			}
		}
		else
		{
			target.setBounds( targetBounds );
			target.invalidate( );
			target.validate( );
		}
	}
}
