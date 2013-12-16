package com.andork.plot;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public interface ITraceRenderer
{
	void drawPoint( Point2D point );
	
	void drawPoint( Point2D point , Color color );
	
	void drawLine( Path2D line );
	
	void drawFill( Path2D fill );
}
