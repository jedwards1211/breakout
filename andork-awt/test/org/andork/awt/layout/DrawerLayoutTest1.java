package org.andork.awt.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.andork.awt.GradientFillBorder;
import org.andork.awt.LayeredBorder;
import org.andork.awt.PaintablePanel;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.DelegatingLayoutManager;
import org.andork.awt.layout.DrawerLayoutDelegate;
import org.andork.awt.layout.Side;
import org.andork.awt.layout.TabLayoutDelegate;

public class DrawerLayoutTest1
{
	public static void main( String[ ] args )
	{
		JFrame frame = new JFrame( );
		JPanel content = new JPanel( );
		LayeredBorder.addBorder( new GradientFillBorder( Corner.TOP_LEFT , content.getBackground( ) , Side.BOTTOM , Color.LIGHT_GRAY ) , content );
		// content.setBorder( new EmptyBorder( 10 , 10 , 10 , 10 ) );
		content.setPreferredSize( new Dimension( 640 , 480 ) );
		content.setLayout( new DelegatingLayoutManager( ) );
		
		PaintablePanel drawer = new PaintablePanel( );
		drawer.setLayout( new DelegatingLayoutManager( ) );
		drawer.setPreferredSize( new Dimension( 200 , 100 ) );
		
		drawer.addUnderpaintBorder( new GradientFillBorder( Side.TOP , Color.LIGHT_GRAY , Side.BOTTOM , Color.GRAY ) );
		
		final DrawerLayoutDelegate delegate = new DrawerLayoutDelegate( drawer , Side.LEFT );
		
		JButton toggleButton = new JButton( "T" );
		toggleButton.setMargin( new Insets( 5 , 10 , 5 , 10 ) );
		
		JButton maxButton = new JButton( "M" );
		maxButton.setMargin( new Insets( 5 , 10 , 5 , 10 ) );
		
		toggleButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				delegate.toggleOpen( );
			}
		} );
		
		maxButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				delegate.toggleMaximized( );
			}
		} );
		
		TabLayoutDelegate tabDelegate = new TabLayoutDelegate( drawer ,
				Corner.fromSides( delegate.dockingSide.opposite( ) , delegate.dockingSide.inverse( ).axis( ).lowerSide( ) ) ,
				// delegate.dockingCorner.adjacent( delegate.dockingSide.opposite( ) ) ,
				delegate.dockingSide.opposite( ) );
		tabDelegate.setInsets( new Insets( 10 , -10 , -10 , 10 ) );
		
		content.add( drawer , delegate );
		content.add( toggleButton , tabDelegate );
		
		drawer.add( maxButton , new DrawerLayoutDelegate( maxButton , Corner.BOTTOM_RIGHT , Side.RIGHT ) );
		
		frame.getContentPane( ).add( content , BorderLayout.CENTER );
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}