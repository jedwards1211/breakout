package org.andork.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


@SuppressWarnings( "serial" )
public class SplashFrame extends JFrame
{
	JButton				closeButton;
	JLabel				statusLabel;
	JProgressBar		progressBar;
	SimpleImagePanel	imagePanel;
	
	public SplashFrame( )
	{
		setUndecorated( true );
		closeButton = new JButton( );
		ModernStyleClearButton.createClearButton( closeButton );
		
		statusLabel = new JLabel( );
		progressBar = new JProgressBar( );
		
		imagePanel = new SimpleImagePanel( );
		imagePanel.add( closeButton );
		imagePanel.add( statusLabel );
		
		imagePanel.setLayout( new Layout( ) );
		
		getContentPane( ).add( imagePanel , BorderLayout.CENTER );
		getContentPane( ).add( progressBar , BorderLayout.SOUTH );
		
		setResizable( false );
		
		closeButton.addActionListener( new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				dispose( );
			}
		} );
	}
	
	public JButton getCloseButton( )
	{
		return closeButton;
	}
	
	public JLabel getStatusLabel( )
	{
		return statusLabel;
	}
	
	public JProgressBar getProgressBar( )
	{
		return progressBar;
	}
	
	public SimpleImagePanel getImagePanel( )
	{
		return imagePanel;
	}
	
	private final class Layout implements LayoutManager
	{
		
		@Override
		public void addLayoutComponent( String name , Component comp )
		{
		}
		
		@Override
		public void removeLayoutComponent( Component comp )
		{
		}
		
		@Override
		public Dimension preferredLayoutSize( Container parent )
		{
			return new Dimension( 600 , 400 );
		}
		
		@Override
		public Dimension minimumLayoutSize( Container parent )
		{
			return new Dimension( 600 , 400 );
		}
		
		@Override
		public void layoutContainer( Container parent )
		{
			Insets insets = imagePanel.getInsets( );
			closeButton.setSize( closeButton.getPreferredSize( ) );
			closeButton.setLocation( imagePanel.getWidth( ) - closeButton.getWidth( ) - insets.right - 2 , insets.top + 2 );
			statusLabel.setSize( statusLabel.getPreferredSize( ) );
			statusLabel.setLocation( insets.left + 5 , imagePanel.getHeight( ) - statusLabel.getHeight( ) - insets.bottom - 2 );
		}
	}
}
