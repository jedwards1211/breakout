package org.andork.breakout.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.andork.breakout.awt.ParamGradientMapPaint;
import org.andork.swing.QuickTestFrame;

import com.andork.plot.ImagePlotPanel;
import com.jogamp.nativewindow.awt.DirectDataBufferInt;
import com.jogamp.nativewindow.awt.DirectDataBufferInt.BufferedImageInt;

public class ParamGradientMapPaintTest
{
	public static void main( String[ ] args )
	{
		int width = 256;
		int height = 256;
		
		ParamGradientMapPaint paint = new ParamGradientMapPaint(
				new float[ ] { 0 , 0 } , new float[ ] { 0 , height } , new float[ ] { width , 0 } ,
				0 , 100 ,
				new float[ ] { 0 , 25 , 26 , 100 } ,
				new Color[ ] { Color.WHITE , Color.RED , Color.GREEN , Color.BLUE } );
		
		BufferedImage image = new BufferedImage( width , height , BufferedImage.TYPE_INT_ARGB );
		
		Graphics2D g2 = ( Graphics2D ) image.createGraphics( );
		g2.setPaint( paint );
		g2.fillRect( 0 , 0 , width , height );
		
		ImagePlotPanel imagePanel = new ImagePlotPanel( );
		imagePanel.setImage( image );
		imagePanel.getPlot( ).setPreferredSize( new Dimension( width , height ) );
		
		QuickTestFrame.frame( imagePanel ).setVisible( true );
	}
}
