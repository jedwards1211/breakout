package org.andork.awt;

import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.Border;

@SuppressWarnings( "serial" )
public class PaintablePanel extends JPanel
{
	private LayeredBorder	underpaintBorder	= new LayeredBorder( );
	
	public void addUnderpaintBorder( Border b )
	{
		underpaintBorder.borders.add( b );
	}
	
	public void removeUnderpaintBorder( Border b )
	{
		underpaintBorder.borders.remove( b );
	}
	
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		if( underpaintBorder != null )
		{
			underpaintBorder.paintBorder( this , g , 0 , 0 , getWidth( ) , getHeight( ) );
		}
	}
}
