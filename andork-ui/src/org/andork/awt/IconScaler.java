package org.andork.awt;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconScaler
{
	public static ImageIcon rescale( Icon icon , int maxWidth , int maxHeight )
	{
		if( icon == null )
		{
			return null;
		}
		
		BufferedImage b = new BufferedImage( icon.getIconWidth( ) , icon.getIconHeight( ) , BufferedImage.TYPE_INT_ARGB );
		icon.paintIcon( null , b.getGraphics( ) , 0 , 0 );
		
		double aspect = ( double ) icon.getIconWidth( ) / icon.getIconHeight( );
		double newAspect = ( double ) maxWidth / maxHeight;
		
		int newWidth, newHeight;
		
		if( newAspect > aspect )
		{
			newHeight = maxHeight;
			newWidth = icon.getIconWidth( ) * maxHeight / icon.getIconHeight( );
		}
		else
		{
			newWidth = maxWidth;
			newHeight = icon.getIconHeight( ) * maxWidth / icon.getIconWidth( );
		}
		
		Image scaled = b.getScaledInstance( newWidth , newHeight , BufferedImage.SCALE_SMOOTH );
		return new ImageIcon( scaled );
	}
}
