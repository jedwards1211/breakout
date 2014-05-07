package com.andork.plot;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public class CheckerPaint implements Paint
{
	Color	c1 , c2;
	int		size;
	
	public CheckerPaint( Color c1 , Color c2 , int size )
	{
		super( );
		this.c1 = c1;
		this.c2 = c2;
		this.size = size;
	}
	
	@Override
	public int getTransparency( )
	{
		return 0;
	}
	
	@Override
	public PaintContext createContext( ColorModel cm , Rectangle deviceBounds , Rectangle2D userBounds , AffineTransform xform , RenderingHints hints )
	{
		return new CheckerPaintContext( cm , c1 , c2 , size );
	}
	
}
