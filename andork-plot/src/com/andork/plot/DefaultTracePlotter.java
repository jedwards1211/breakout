
package com.andork.plot;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Decides what to actually draw to a trace renderer. When the viewport is zoomed out so far that there are multiple data points for
 * each column of pixels, special logic is required to draw the trace efficiently without losing peaks and valleys.
 * Here's the trick in a nutshell:
 * For each one pixel line on the screen, find all of the data points that map to within that line. If they're monotonically
 * increasing or decreasing, just draw the last point. This preserves peaks and valleys well. Otherwise,
 * draw a fill from the last point (or last fill points) to the min and max values within the one pixel line.
 */
public class DefaultTracePlotter implements ITracePlotter
{
	public DefaultTracePlotter( )
	{
		reset( );
	}
	
	private final ArrayList<Point2D>	linePoints		= new ArrayList<Point2D>( );
	private final ArrayList<Point2D>	fillMinPoints	= new ArrayList<Point2D>( );
	private final ArrayList<Point2D>	fillMaxPoints	= new ArrayList<Point2D>( );
	
	private boolean						columnIsMonotonic;
	private double						columnStartDomain;
	private double						columnStartValue;
	private double						columnMinDomain;
	private double						columnMinValue;
	private double						columnMaxDomain;
	private double						columnMaxValue;
	private double						columnEndDomain;
	private double						columnEndValue;
	
	private double						lastColumnEndDomain;
	private double						lastColumnEndValue;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ITracePlotter#reset()
	 */
	@Override
	public void reset( )
	{
		linePoints.clear( );
		fillMinPoints.clear( );
		fillMaxPoints.clear( );
		
		resetColumn( );
		
		lastColumnEndDomain = Double.NaN;
		lastColumnEndValue = Double.NaN;
	}
	
	private void resetColumn( )
	{
		columnIsMonotonic = true;
		columnStartDomain = Double.NaN;
		columnStartValue = Double.NaN;
		columnMinDomain = Double.NaN;
		columnMinValue = Double.NaN;
		columnMaxDomain = Double.NaN;
		columnMaxValue = Double.NaN;
		columnEndDomain = Double.NaN;
		columnEndValue = Double.NaN;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ITracePlotter#addPoint(ITraceRenderer, ViewportParams, double, double)
	 */
	@Override
	public void addPoint( double domain , double value , ITraceRenderer traceRenderer , IAxisConversion domainConversion , IAxisConversion valueConversion )
	{
		double columnX = domainConversion.convert( columnStartDomain );
		double viewX = domainConversion.convert( domain );
		
		if( !Double.isNaN( columnStartDomain ) && Math.round( viewX ) > Math.round( columnX ) )
		{
			advanceColumn( traceRenderer , domainConversion , valueConversion , false );
		}
		
		if( Double.isNaN( columnStartDomain ) )
		{
			columnStartDomain = domain;
			columnStartValue = value;
		}
		
		columnEndDomain = domain;
		columnEndValue = value;
		
		if( !Double.isNaN( value ) )
		{
			if( columnMinDomain < columnMaxDomain && value < columnMaxValue )
			{
				columnIsMonotonic = false;
			}
			if( columnMinDomain > columnMaxDomain && value > columnMinValue )
			{
				columnIsMonotonic = false;
			}
			
			if( Double.isNaN( columnMinValue ) || value < columnMinValue )
			{
				columnMinDomain = domain;
				columnMinValue = value;
			}
			if( Double.isNaN( columnMaxValue ) || value > columnMaxValue )
			{
				columnMaxDomain = domain;
				columnMaxValue = value;
			}
		}
	}
	
	private double roundNaN( double a )
	{
		return Double.isNaN( a ) ? Double.NaN : Math.round( a );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ITracePlotter#flush(ITraceRenderer, ViewportParams)
	 */
	@Override
	public void flush( ITraceRenderer traceRenderer , IAxisConversion domainConversion , IAxisConversion valueConversion )
	{
		advanceColumn( traceRenderer , domainConversion , valueConversion , true );
	}
	
	private void advanceColumn( ITraceRenderer traceRenderer , IAxisConversion domainConversion , IAxisConversion valueConversion , boolean forceFlush )
	{
		double lastX = domainConversion.convert( lastColumnEndDomain );
		double lastY = valueConversion.convert( lastColumnEndValue );
		
		double startX = domainConversion.convert( columnStartDomain );
		double startY = valueConversion.convert( columnStartValue );
		double minX = domainConversion.convert( columnMinDomain );
		double minY = valueConversion.convert( columnMinValue );
		double maxX = domainConversion.convert( columnMaxDomain );
		double maxY = valueConversion.convert( columnMaxValue );
		
		double monoStartX = Double.NaN;
		double monoStartY = Double.NaN;
		double monoEndX = Double.NaN;
		double monoEndY = Double.NaN;
		
		if( columnIsMonotonic )
		{
			if( minX < maxX )
			{
				monoStartX = minX;
				monoStartY = minY;
				monoEndX = maxX;
				monoEndY = maxY;
			}
			else
			{
				monoStartX = maxX;
				monoStartY = maxY;
				monoEndX = minX;
				monoEndY = minY;
			}
		}
		
		boolean adjacent = ( int ) roundNaN( startX ) == ( int ) roundNaN( lastX ) + 1;
		boolean startEqualsLast = roundNaN( startX ) == roundNaN( lastX ) && roundNaN( startY ) == roundNaN( lastY );
		boolean monoStartEqualsLast = roundNaN( monoStartX ) == roundNaN( lastX ) && roundNaN( monoStartY ) == roundNaN( lastY );
		boolean drawStart = !Double.isNaN( startY ) && !startEqualsLast;
		
		if( !Double.isNaN( lastY ) )
		{
			if( !fillMinPoints.isEmpty( ) )
			{
				if( !columnIsMonotonic && adjacent )
				{
					fillMinPoints.add( new Point2D.Double( minX , minY ) );
					fillMaxPoints.add( new Point2D.Double( maxX , maxY ) );
				}
				else
				{
					Point2D p = new Point2D.Double( lastX , lastY );
					fillMinPoints.add( p );
					fillMaxPoints.add( p );
					flushFill( traceRenderer );
					
					linePoints.add( p );
				}
			}
			
			if( fillMinPoints.isEmpty( ) )
			{
				if( drawStart )
				{
					linePoints.add( new Point2D.Double( startX , startY ) );
				}
				
				if( columnIsMonotonic )
				{
					if( !drawStart && !monoStartEqualsLast )
					{
						linePoints.add( new Point2D.Double( monoStartX , monoStartY ) );
					}
					linePoints.add( new Point2D.Double( monoEndX , monoEndY ) );
				}
				else
				{
					flushLine( traceRenderer );
					
					if( drawStart )
					{
						Point2D p = new Point2D.Double( startX , startY );
						fillMinPoints.add( p );
						fillMaxPoints.add( p );
					}
					
					fillMinPoints.add( new Point2D.Double( minX , minY ) );
					fillMaxPoints.add( new Point2D.Double( maxX , maxY ) );
				}
			}
		}
		else
		{
			flushLine( traceRenderer );
			flushFill( traceRenderer );
			
			if( columnIsMonotonic )
			{
				if( !monoStartEqualsLast )
				{
					linePoints.add( new Point2D.Double( monoStartX , monoStartY ) );
				}
				linePoints.add( new Point2D.Double( monoEndX , monoEndY ) );
			}
			else
			{
				if( drawStart )
				{
					Point2D p = new Point2D.Double( startX , startY );
					fillMinPoints.add( p );
					fillMaxPoints.add( p );
				}
				fillMinPoints.add( new Point2D.Double( minX , minY ) );
				fillMaxPoints.add( new Point2D.Double( maxX , maxY ) );
			}
		}
		
		if( forceFlush )
		{
			flushLine( traceRenderer );
			flushFill( traceRenderer );
		}
		
		lastColumnEndDomain = columnEndDomain;
		lastColumnEndValue = columnEndValue;
		
		resetColumn( );
	}
	
	public void flushLine( ITraceRenderer traceRenderer )
	{
		if( linePoints.size( ) > 1 )
		{
			Path2D linePointsPath = new Path2D.Double( );
			Point2D p = linePoints.get( 0 );
			
			linePointsPath.moveTo( p.getX( ) , p.getY( ) );
			
			for( int i = 1 ; i < linePoints.size( ) ; i++ )
			{
				p = linePoints.get( i );
				linePointsPath.lineTo( p.getX( ) , p.getY( ) );
			}
			
			traceRenderer.drawLine( linePointsPath );
		}
		linePoints.clear( );
	}
	
	private void flushFill( ITraceRenderer traceRenderer )
	{
		if( fillMinPoints.size( ) > 1 && fillMaxPoints.size( ) > 1 )
		{
			Path2D.Double minLinePath = new Path2D.Double( );
			Path2D.Double maxLinePath = new Path2D.Double( );
			Path2D.Double fillPath = new Path2D.Double( );
			
			Point2D p = fillMinPoints.get( 0 );
			minLinePath.moveTo( p.getX( ) , p.getY( ) );
			fillPath.moveTo( p.getX( ) , p.getY( ) );
			
			for( int i = 1 ; i < fillMinPoints.size( ) ; i++ )
			{
				p = fillMinPoints.get( i );
				minLinePath.lineTo( p.getX( ) , p.getY( ) );
				fillPath.lineTo( p.getX( ) , p.getY( ) );
			}
			
			p = fillMaxPoints.get( fillMaxPoints.size( ) - 1 );
			maxLinePath.moveTo( p.getX( ) , p.getY( ) );
			fillPath.lineTo( p.getX( ) , p.getY( ) );
			
			for( int i = fillMaxPoints.size( ) - 2 ; i >= 0 ; i-- )
			{
				p = fillMaxPoints.get( i );
				maxLinePath.lineTo( p.getX( ) , p.getY( ) );
				fillPath.lineTo( p.getX( ) , p.getY( ) );
			}
			fillPath.closePath( );
			
			try
			{
				traceRenderer.drawFill( fillPath );
				traceRenderer.drawLine( maxLinePath );
				traceRenderer.drawLine( minLinePath );
			}
			catch( Exception e )
			{
				e.printStackTrace( );
			}
		}
		
		fillMinPoints.clear( );
		fillMaxPoints.clear( );
	}
}
