package org.andork.awt.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JFrame;

import org.andork.swing.PaintablePanel;
import org.andork.swing.border.GradientFillBorder;
import org.andork.swing.border.LayeredBorder;

public class DrawerLayoutTest2
{
	public static void main( String[ ] args )
	{
		JFrame frame = new JFrame( );
		// Container content = frame.getContentPane( );
		// LayeredBorder.addOnTop( GradientFillBorder.from( Corner.TOP_LEFT ).to( Side.BOTTOM ).colors( content.getBackground( ) , Color.LIGHT_GRAY ) , content
		// );
		// content.setPreferredSize( new Dimension( 640 , 480 ) );
		frame.getContentPane( ).setPreferredSize( new Dimension( 640 , 480 ) );
		// content.setLayout( new DelegatingLayoutManager( ) );
		
		PaintablePanel panel = new PaintablePanel( );
		panel.setLayout( new DelegatingLayoutManager( ) );
		panel.setPreferredSize( new Dimension( 200 , 100 ) );
		
		panel.setUnderpaintBorder( GradientFillBorder.from( Side.TOP ).to( Side.BOTTOM ).colors( Color.LIGHT_GRAY , Color.GRAY ) );
		
		Drawer drawer = new Drawer( panel );
		drawer.delegate( ).dockingSide( Side.BOTTOM );
		drawer.mainResizeHandle( );
		drawer.pinButtonDelegate( ).insets( new Insets( 10 , 10 , -10 , -10 ) );
		
		drawer.addTo( frame.getLayeredPane( ) , 2 );
		
		DrawerAutoshowController autoshowController = new DrawerAutoshowController( );
		frame.getContentPane( ).addMouseMotionListener( autoshowController );
		
		panel.add( drawer.maxButton( ) , new DrawerLayoutDelegate( drawer.maxButton( ) , Corner.TOP_RIGHT , Side.RIGHT ) );
		
		PaintablePanel panel2 = new PaintablePanel( );
		panel2.setPreferredSize( new Dimension( 100 , 200 ) );
		panel2.setUnderpaintBorder( GradientFillBorder.from( Side.TOP ).to( Side.BOTTOM ).colors( Color.LIGHT_GRAY , Color.GRAY ) );
		
		Drawer drawer2 = new Drawer( panel2 );
		drawer2.delegate( ).dockingSide( Side.LEFT );
		drawer2.mainResizeHandle( );
		drawer2.pinButtonDelegate( ).insets( new Insets( 10 , -10 , -10 , 10 ) );
		
		drawer2.addTo( frame.getLayeredPane( ) , 0 );
		
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
}
