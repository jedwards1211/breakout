package org.andork.jogl.awt;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class BufferedImageByteFactory implements BufferedImageFactory
{
	int	imageType;
	
	private BufferedImageByteFactory( int imageType )
	{
		super( );
		this.imageType = imageType;
	}
	
	@Override
	public BufferedImage newImage( int width , int height )
	{
		return DirectDataBufferByte.createBufferedImage( width , height , imageType ,
				new Point( ) , new Hashtable<Object, Object>( ) );
	}
	
}
