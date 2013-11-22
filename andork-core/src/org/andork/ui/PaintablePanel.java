package org.andork.ui;

import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.border.Border;

@SuppressWarnings( "serial" )
public class PaintablePanel extends JPanel
{
	private LayeredBorder	underpaintBorder;
	
	public void addUnderpaintBorder( Border b )
	{
		underpaintBorder.borders.add( b );
	}
	
	public void removeUnderpaintBorder( Border b )
	{
		underpaintBorder.borders.remove( b );
	}
	
	protected void paintComponent( Graphics2D g )
	{
		if( underpaintBorder != null )
		{
			underpaintBorder.paintBorder( this , g , 0 , 0 , getWidth( ) , getHeight( ) );
		}
	}
}
