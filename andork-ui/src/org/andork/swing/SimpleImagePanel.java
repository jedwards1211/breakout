package org.andork.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class SimpleImagePanel extends JPanel
{
	private Image	image;
	
	public SimpleImagePanel( )
	{
		
	}
	
	public SimpleImagePanel( Image image )
	{
		super( );
		this.image = image;
	}
	
	public Image getImage( )
	{
		return image;
	}
	
	public void setImage( Image image )
	{
		if( this.image != image )
		{
			this.image = image;
			repaint( );
		}
	}
	
	public Dimension getMinimumSize( )
	{
		if( !isMinimumSizeSet( ) && image != null )
		{
			return new Dimension( image.getWidth( null ) , image.getHeight( null ) );
		}
		return super.getMinimumSize( );
	}
	
	public Dimension getPreferredSize( )
	{
		if( !isPreferredSizeSet( ) && image != null )
		{
			return new Dimension( image.getWidth( null ) , image.getHeight( null ) );
		}
		return super.getPreferredSize( );
	}
	
	public Dimension getMaximumSize( )
	{
		if( !isMaximumSizeSet( ) && image != null )
		{
			return new Dimension( image.getWidth( null ) , image.getHeight( null ) );
		}
		return super.getMaximumSize( );
	}
	
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		if( image != null )
		{
			g.drawImage( image , 0 , 0 , null );
		}
	}
}