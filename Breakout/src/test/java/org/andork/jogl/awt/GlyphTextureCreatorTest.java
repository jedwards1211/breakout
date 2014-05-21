package org.andork.jogl.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import com.andork.plot.ImagePlotPanel;
import com.jogamp.nativewindow.awt.DirectDataBufferInt;

public class GlyphTextureCreatorTest
{
	public static void main( String[ ] args )
	{
		// BufferedImage image = new BufferedImage( 512 , 512 , BufferedImage.TYPE_BYTE_GRAY );
		// BufferedImage image = DirectDataBufferByte.createBufferedImage( 512 , 512 , BufferedImage.TYPE_BYTE_GRAY , new Point( ) , new Hashtable<Object,
		// Object>( ) );
		BufferedImage image = DirectDataBufferInt.createBufferedImage( 512 , 512 , BufferedImage.TYPE_INT_ARGB , new Point( ) , new Hashtable<Object, Object>( ) );
		Graphics2D g2 = image.createGraphics( );
		FontMetrics fm = g2.getFontMetrics( new Font( "Arial" , Font.PLAIN , 24 ) );
		GlyphPage page = new GlyphPage( null , fm , image , new OutlinedGlyphPagePainter(
				new BasicStroke( 2f , BasicStroke.CAP_ROUND , BasicStroke.JOIN_ROUND ) ,
				Color.RED , Color.WHITE ) , ( char ) 0 );
		
		ImagePlotPanel.showImageViewer( image );
	}
}
