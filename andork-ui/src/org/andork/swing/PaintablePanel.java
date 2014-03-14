package org.andork.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Paint;

import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

@SuppressWarnings( "serial" )
public class PaintablePanel extends JPanel
{
	private Border	underpaintBorder	= null;
	
	public PaintablePanel( )
	{
		setOpaque( false );
	}
	
	public void setUnderpaintBorder( Border b )
	{
		if( underpaintBorder != b )
		{
			underpaintBorder = b;
			repaint( );
		}
	}
	
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		if( underpaintBorder != null )
		{
			underpaintBorder.paintBorder( this , g , 0 , 0 , getWidth( ) , getHeight( ) );
		}
	}
	
	public static PaintablePanel wrap( Component c )
	{
		if( c instanceof JComponent )
		{
			( ( JComponent ) c ).setOpaque( false );
		}
		PaintablePanel panel = new PaintablePanel( );
		panel.setLayout( new BorderLayout( ) );
		panel.add( c , BorderLayout.CENTER );
		return panel;
	}
}
