package org.andork.awt.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class Drawer extends JPanel
{
	Component				content;
	DrawerLayoutDelegate	drawerDelegate;
	JToggleButton			pinButton;
	JToggleButton			maxButton;
	Component				resizeHandle;
	
	public Drawer( )
	{
		setLayout( new BorderLayout( ) );
		drawerDelegate = new DrawerLayoutDelegate( this , Side.TOP );
	}
	
	public Drawer( Component content )
	{
		this( );
		
		this.content = content;
		add( content , BorderLayout.CENTER );
	}
	
	public DrawerLayoutDelegate drawerDelegate( )
	{
		return drawerDelegate;
	}
	
	public JToggleButton maxButton( )
	{
		if( maxButton == null )
		{
			maxButton = new JToggleButton( );
			maxButton.setIcon( new ImageIcon( getClass( ).getResource( "maximize.png" ) ) );
			maxButton.setSelectedIcon( new ImageIcon( getClass( ).getResource( "restore.png" ) ) );
			maxButton.addActionListener( new ActionListener( )
			{
				@Override
				public void actionPerformed( ActionEvent e )
				{
					drawerDelegate.setMaximized( maxButton.isSelected( ) );
				}
			} );
		}
		return maxButton;
	}
}
