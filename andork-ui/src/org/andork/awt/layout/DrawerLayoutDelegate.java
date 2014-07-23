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
package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;

import org.andork.awt.layout.DelegatingLayoutManager.LayoutDelegate;
import org.andork.event.BasicPropertyChangeSupport;

public class DrawerLayoutDelegate implements LayoutDelegate
{
	boolean								open			= false;
	boolean								pinned			= false;
	boolean								maximized		= false;
	boolean								animating		= false;
	
	Corner								dockingCorner;
	Side								dockingSide;
	float								animFactor		= .2f;
	int									animSpeed		= 10;
	
	private long						lastAnimTime;
	private Timer						animTimer;
	
	Component							drawer;
	
	boolean								fill			= false;
	
	public static final String			OPEN			= "open";
	public static final String			PINNED			= "pinned";
	public static final String			MAXIMIZED		= "maximized";

	private BasicPropertyChangeSupport	changeSupport	= new BasicPropertyChangeSupport( );
	
	public DrawerLayoutDelegate( Component drawer , Side dockingSide )
	{
		this( drawer , null , dockingSide , true );
	}
	
	public DrawerLayoutDelegate( Component drawer , Side dockingSide , boolean fill )
	{
		this( drawer , null , dockingSide , fill );
	}
	
	public DrawerLayoutDelegate( Component drawer , Corner dockingCorner , Side dockingSide )
	{
		this( drawer , dockingCorner , dockingSide , false );
	}
	
	private DrawerLayoutDelegate( Component drawer , Corner dockingCorner , Side dockingSide , boolean fill )
	{
		super( );
		if( dockingCorner != null && dockingSide != dockingCorner.xSide( ) && dockingSide != dockingCorner.ySide( ) )
		{
			throw new IllegalArgumentException( "dockingCorner must be on the same side as dockingSide" );
		}
		this.drawer = drawer;
		this.dockingCorner = dockingCorner;
		this.dockingSide = dockingSide;
		this.fill = fill;
	}
	
	public DrawerLayoutDelegate dockingSide( Side dockingSide )
	{
		this.dockingSide = dockingSide;
		return this;
	}
	
	public Side dockingSide( )
	{
		return dockingSide;
	}
	
	public DrawerLayoutDelegate dockingCorner( Corner dockingCorner )
	{
		this.dockingCorner = dockingCorner;
		return this;
	}
	
	public Corner dockingCorner( )
	{
		return dockingCorner;
	}
	
	public DrawerLayoutDelegate fill( boolean fill )
	{
		this.fill = fill;
		return this;
	}
	
	public boolean fill( )
	{
		return fill;
	}
	
	public Component getDrawer( )
	{
		return drawer;
	}
	
	public boolean isOpen( )
	{
		return open;
	}
	
	public void close( )
	{
		setOpen( false , true );
	}
	
	public void close( boolean animate )
	{
		setOpen( false , animate );
	}
	
	public void open( )
	{
		setOpen( true , true );
	}
	
	public void open( boolean animate )
	{
		setOpen( true , animate );
	}
	
	public void toggleOpen( )
	{
		toggleOpen( true );
	}
	
	public void toggleOpen( boolean animate )
	{
		if( !open || !pinned )
		{
			open = !open;
			animating = animate;
			if( drawer.getParent( ) != null )
			{
				drawer.getParent( ).invalidate( );
				drawer.getParent( ).validate( );
			}
			changeSupport.firePropertyChange( this , OPEN , !open , open );
		}
	}
	
	public void setOpen( boolean open )
	{
		setOpen( open , true );
	}
	
	public void setOpen( boolean open , boolean animate )
	{
		if( this.open != open )
		{
			toggleOpen( animate );
		}
	}
	
	public boolean isPinned( )
	{
		return pinned;
	}
	
	public void setPinned( boolean pinned )
	{
		setPinned( pinned , true );
	}
	
	public void setPinned( boolean pinned , boolean animate )
	{
		if( this.pinned != pinned )
		{
			this.pinned = pinned;
			
			setOpen( pinned , animate );
			changeSupport.firePropertyChange( this , PINNED , !pinned , pinned );
		}
	}
	
	public boolean isMaximized( )
	{
		return maximized;
	}
	
	public void restore( )
	{
		setMaximized( false , true );
	}
	
	public void restore( boolean animate )
	{
		setMaximized( false , animate );
	}
	
	public void maximize( )
	{
		setMaximized( true , true );
	}
	
	public void maximize( boolean animate )
	{
		setMaximized( true , animate );
	}
	
	public void toggleMaximized( )
	{
		toggleMaximized( true );
	}
	
	public void toggleMaximized( boolean animate )
	{
		maximized = !maximized;
		animating = animate;
		if( drawer.getParent( ) != null )
		{
			drawer.getParent( ).invalidate( );
			drawer.getParent( ).validate( );
		}
		changeSupport.firePropertyChange( this , MAXIMIZED , !maximized , maximized );
	}
	
	public void setMaximized( boolean maximized )
	{
		setMaximized( maximized , true );
	}
	
	public void setMaximized( boolean maximized , boolean animate )
	{
		if( this.maximized != maximized )
		{
			toggleMaximized( animate );
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
	
	boolean	bypass	= false;
	
	@Override
	public void layoutComponent( final Container parent , final Component target )
	{
		if( bypass )
		{
			return;
		}
		
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
				
				bypass = true;
				try
				{
					onLayoutAnimated( parent , target );
				}
				finally
				{
					bypass = false;
				}
				
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
		}
	}
	
	protected void onLayoutAnimated( Container parent , Component target )
	{
		if( parent != null && parent.getLayout( ) instanceof DelegatingLayoutManager )
		{
			( ( DelegatingLayoutManager ) parent.getLayout( ) ).onLayoutChanged( parent );
		}
		else
		{
			parent.invalidate( );
			parent.validate( );
		}
	}
	
	@Override
	public List<Component> getDependencies( )
	{
		return Collections.emptyList( );
	}
	
	public BasicPropertyChangeSupport.External changeSupport( )
	{
		return changeSupport.external( );
	}
}
