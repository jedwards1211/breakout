package org.andork.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.andork.ui.GradientFillBorder;
import org.andork.ui.LayeredBorder;

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
		
		JPanel drawer = new JPanel( );
		drawer.setBackground( Color.BLUE );
		drawer.setPreferredSize( new Dimension( 200 , 100 ) );
		
		LayeredBorder.addBorder( new GradientFillBorder( Side.TOP , Color.LIGHT_GRAY , Side.BOTTOM , Color.GRAY ) , drawer );
		
		final DrawerLayoutDelegate delegate = new DrawerLayoutDelegate( );
		// delegate.dockingCorner = Corner.TOP_LEFT;
		delegate.dockingSide = Side.LEFT;
		delegate.parent = content;
		
		JButton toggleButton = new JButton( "T" );
		toggleButton.setMargin( new Insets( 5 , 10 , 5 , 10 ) );
		
		toggleButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				delegate.toggle( );
			}
		} );
		
		content.add( drawer , delegate );
		content.add( toggleButton , new TabLayoutDelegate( drawer ,
				Corner.fromSides( delegate.dockingSide.opposite( ) , delegate.dockingSide.inverse( ).axis( ).lowerSide( ) ) ,
				// delegate.dockingCorner.adjacent( delegate.dockingSide.opposite( ) ) ,
				delegate.dockingSide.opposite( ) ) );
		
		frame.getContentPane( ).add( content , BorderLayout.CENTER );
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
