package org.andork.awt.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

import org.andork.awt.event.UIBindings;
import org.andork.event.Binder;
import org.andork.event.Binder.Binding;
import org.andork.q.QObject;
import org.andork.swing.PaintablePanel;

@SuppressWarnings( "serial" )
public class Drawer extends PaintablePanel
{
	DrawerLayoutDelegate			delegate;
	JToggleButton					pinButton;
	TabLayoutDelegate				pinButtonDelegate;
	JToggleButton					maxButton;
	Component						mainResizeHandle;
	ResizeKnobHandler				mainResizeHandler;
	Binder<QObject<DrawerModel>>	binder;
	final List<Binding>				bindings	= new ArrayList<Binding>( );
	
	public Drawer( )
	{
		setOpaque( true );
		setLayout( new BorderLayout( ) );
		delegate = new DrawerLayoutDelegate( this , Side.TOP );
	}
	
	public Drawer( Component content )
	{
		this( );
		add( content , BorderLayout.CENTER );
	}
	
	public void setBinder( Binder<QObject<DrawerModel>> binder )
	{
		if( this.binder != binder )
		{
			if( this.binder != null && this.bindings != null )
			{
				for( Binding binding : bindings )
				{
					this.binder.unbind( binding );
				}
			}
			this.binder = binder;
			bindings.clear( );
			refreshBindings( );
		}
	}
	
	void refreshBindings( )
	{
		if( binder != null )
		{
			for( Binding binding : bindings )
			{
				binder.unbind( binding );
			}
			bindings.clear( );
			if( pinButton != null )
			{
				bindings.add( UIBindings.bind( binder , pinButton , DrawerModel.pinned ) );
			}
			if( maxButton != null )
			{
				bindings.add( UIBindings.bind( binder , maxButton , DrawerModel.maximized ) );
			}
		}
	}
	
	public DrawerLayoutDelegate delegate( )
	{
		return delegate;
	}
	
	public Drawer delegate( DrawerLayoutDelegate delegate )
	{
		this.delegate = delegate;
		return this;
	}
	
	public JToggleButton pinButton( )
	{
		if( pinButton == null )
		{
			pinButton = new JToggleButton( );
			pinButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
			pinButton.setIcon( new ImageIcon( Drawer.class.getResource( "unpinned.png" ) ) );
			pinButton.setSelectedIcon( new ImageIcon( Drawer.class.getResource( "pinned.png" ) ) );
			pinButton.addItemListener( new ItemListener( )
			{
				@Override
				public void itemStateChanged( ItemEvent e )
				{
					delegate.setPinned( pinButton.isSelected( ) );
				}
			} );
			refreshBindings( );
		}
		return pinButton;
	}
	
	public TabLayoutDelegate pinButtonDelegate( )
	{
		if( pinButtonDelegate == null )
		{
			pinButtonDelegate = new TabLayoutDelegate( this ,
					Corner.fromSides( delegate.dockingSide( ).opposite( ) ,
							delegate.dockingSide( ).axis( ).opposite( ).lowerSide( ) ) ,
					delegate.dockingSide( ).opposite( ) );
		}
		return pinButtonDelegate;
	}
	
	public JToggleButton maxButton( )
	{
		if( maxButton == null )
		{
			maxButton = new JToggleButton( );
			maxButton.setMargin( new Insets( 2 , 2 , 2 , 2 ) );
			maxButton.setIcon( new ImageIcon( Drawer.class.getResource( "maximize.png" ) ) );
			maxButton.setSelectedIcon( new ImageIcon( Drawer.class.getResource( "restore.png" ) ) );
			maxButton.addItemListener( new ItemListener( )
			{
				@Override
				public void itemStateChanged( ItemEvent e )
				{
					delegate.setMaximized( maxButton.isSelected( ) );
				}
			} );
			refreshBindings( );
		}
		return maxButton;
	}
	
	public Component mainResizeHandle( )
	{
		if( mainResizeHandle == null )
		{
			JButton handle = new JButton( );
			handle.setUI( new BasicButtonUI( ) );
			handle.setMargin( new Insets( 0 , 0 , 0 , 0 ) );
			handle.setPreferredSize( new Dimension( 5 , 5 ) );
			handle.setCursor( delegate.dockingSide( ).opposite( ).resizeCursor( ) );
			add( handle , delegate.dockingSide( ).opposite( ).borderLayoutAnchor( ) );
			
			mainResizeHandler = new ResizeKnobHandler( this , delegate.dockingSide( ).opposite( ) );
			handle.addMouseListener( mainResizeHandler );
			handle.addMouseMotionListener( mainResizeHandler );
			
			mainResizeHandle = handle;
		}
		return mainResizeHandle;
	}
	
	public void addTo( JLayeredPane layeredPane , int layer )
	{
		if( !( layeredPane.getLayout( ) instanceof DelegatingLayoutManager ) )
		{
			layeredPane.setLayout( new DelegatingLayoutManager( ) );
		}
		
		layeredPane.setLayer( this , layer );
		layeredPane.add( this , delegate );
		if( pinButtonDelegate != null )
		{
			layeredPane.setLayer( pinButton( ) , layer + 1 );
			layeredPane.add( pinButton( ) , pinButtonDelegate );
		}
	}
}
