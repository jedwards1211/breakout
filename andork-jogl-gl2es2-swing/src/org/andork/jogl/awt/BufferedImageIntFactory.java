package org.andork.jogl.awt;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import com.jogamp.nativewindow.awt.DirectDataBufferInt;

public class BufferedImageIntFactory implements BufferedImageFactory
{
	int	imageType;
	
	public BufferedImageIntFactory( int imageType )
	{
		super( );
		this.imageType = imageType;
	}
	
	@Override
	public BufferedImage newImage( int width , int height )
	{
		return DirectDataBufferInt.createBufferedImage( width , height , imageType ,
				new Point( ) , new Hashtable<Object, Object>( ) );
	}
	
}
