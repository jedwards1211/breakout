package com.andork.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.andork.plot.Axis.Orientation;

public class HorizontalGridLineLayer implements IPlotLayer
{
	private LinearAxisConversion	axisConversion			= new LinearAxisConversion( );
	
	private int						minMinorGridLineSpacing	= 30;
	
	public HorizontalGridLineLayer( )
	{
		
	}
	
	public HorizontalGridLineLayer( Axis axis )
	{
		if( axis.getOrientation( ) != Orientation.VERTICAL )
		{
			throw new IllegalArgumentException( "axis must be VERTICAL" );
		}
		axisConversion = axis.getAxisConversion( );
	}
	
	public int getMinMinorGridLineSpacing( )
	{
		return minMinorGridLineSpacing;
	}
	
	public void setMinMinorGridLineSpacing( int minMinorGridLineSpacing )
	{
		this.minMinorGridLineSpacing = minMinorGridLineSpacing;
	}
	
	public LinearAxisConversion getAxisConversion( )
	{
		return axisConversion;
	}
	
	public void setAxisConversion( LinearAxisConversion axisConversion )
	{
		if( axisConversion == null )
		{
			throw new IllegalArgumentException( "axisConversion must be non-null" );
		}
		this.axisConversion = axisConversion;
	}
	
	public void render( Graphics2D g2 , Rectangle bounds )
	{
		Object prevAntialiasing = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
		Paint prevPaint = g2.getPaint( );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON );
		
		double axisStart = axisConversion.invert( 0 );
		double axisEnd = axisConversion.invert( bounds.height );
		
		double minorHorizontalGridLineSpacing = GridMath.niceCeiling( Math.abs( axisConversion.invert( minMinorGridLineSpacing ) - axisStart ) );
		double majorHorizontalGridLineSpacing = minorHorizontalGridLineSpacing * 2;
		
		g2.setColor( Color.LIGHT_GRAY );
		PlotUtils.drawHorizontalGridLines( g2 , bounds , axisStart , axisEnd , minorHorizontalGridLineSpacing );
		g2.setColor( Color.GRAY );
		PlotUtils.drawHorizontalGridLines( g2 , bounds , axisStart , axisEnd , majorHorizontalGridLineSpacing );
		
		g2.setPaint( prevPaint );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , prevAntialiasing );
	}
}
