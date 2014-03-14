package org.andork.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

public class SpinnerButtonUI extends BasicButtonUI
{
	AbstractButton		button;
	static Icon[ ]		icons;
	static Icon			offIcon;
	javax.swing.Timer	timer;
	
	boolean				spinning	= false;
	
	static
	{
		icons = new Icon[ 30 ];
		for( int i = 0 ; i < 30 ; i++ )
		{
			icons[ i ] = new ImageIcon( SpinnerButtonUI.class.getResource( "spinner-" + i + ".png" ) );
		}
		
		offIcon = new ImageIcon( SpinnerButtonUI.class.getResource( "spinner-off.png" ) );
	}
	
	public SpinnerButtonUI( )
	{
		timer = new Timer( 30 , new ActionListener( )
		{
			int	frame	= 0;
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( button != null && spinning )
				{
					if( !button.isShowing( ) )
					{
						timer.stop( );
					}
					frame++ ;
					if( frame >= icons.length )
					{
						frame %= icons.length;
					}
					button.setIcon( icons[ frame ] );
					button.setSelectedIcon( icons[ frame ] );
					button.repaint( );
				}
			}
		} );
	}
	
	public boolean isSpinning( )
	{
		return spinning;
	}
	
	public void setSpinning( boolean spinning )
	{
		this.spinning = spinning;
		if( !spinning )
		{
			timer.stop( );
			button.setIcon( offIcon );
			button.setSelectedIcon( offIcon );
			button.repaint( );
		}
		else
		{
			timer.start( );
		}
	}
	
	@Override
	public void installUI( JComponent c )
	{
		super.installUI( c );
		button = ( AbstractButton ) c;
		button.setMargin( new Insets( 0 , 0 , 0 , 0 ) );
		button.setFocusPainted( false );
		button.setMinimumSize( new Dimension( icons[ 0 ].getIconWidth( ) , icons[ 0 ].getIconHeight( ) ) );
		button.setPreferredSize( new Dimension( icons[ 0 ].getIconWidth( ) , icons[ 0 ].getIconHeight( ) ) );
		button.setIcon( offIcon );
		button.setSelectedIcon( offIcon );
	}
	
	@Override
	public void update( Graphics g , JComponent c )
	{
		super.update( g , c );
		timer.start( );
	}
}
