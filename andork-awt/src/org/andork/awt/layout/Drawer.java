package org.andork.awt.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

import org.andork.swing.PaintablePanel;

@SuppressWarnings( "serial" )
public class Drawer extends PaintablePanel
{
	Component				content;
	DrawerLayoutDelegate	delegate;
	JToggleButton			pinButton;
	TabLayoutDelegate		pinButtonDelegate;
	JToggleButton			maxButton;
	Component				mainResizeHandle;
	ResizeKnobHandler		mainResizeHandler;
	
	public Drawer( )
	{
		setLayout( new BorderLayout( ) );
		delegate = new DrawerLayoutDelegate( this , Side.TOP );
	}
	
	public Drawer( Component content )
	{
		this( );
		
		this.content = content;
		add( content , BorderLayout.CENTER );
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
			pinButton.setMargin( new Insets( 0 , 0 , 0 , 0 ) );
			pinButton.setIcon( new ImageIcon( getClass( ).getResource( "unpinned.png" ) ) );
			pinButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "pinned.png" ) ) );
			pinButton.addActionListener( new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					delegate.setPinned( pinButton.isSelected( ) );
				}
			} );
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
			maxButton.setMargin( new Insets( 0 , 0 , 0 , 0 ) );
			maxButton.setIcon( new ImageIcon( getClass( ).getResource( "maximize.png" ) ) );
			maxButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "restore.png" ) ) );
			maxButton.addActionListener( new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					delegate.setMaximized( maxButton.isSelected( ) );
				}
			} );
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
