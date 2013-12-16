
package com.andork.plot;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingConstants;

public class PlotUtils
{
	public static double evalLine( double x1 , double y1 , double x2 , double y2 , double x )
	{
		if( x == x1 )
		{
			return y1;
		}
		if( x == x2 )
		{
			return y2;
		}
		double f = ( x - x1 ) / ( x2 - x1 );
		return y1 * ( 1 - f ) + y2 * f;
	}
	
	public static double evalPolyline( double[ ] xx , double[ ] yy , double x )
	{
		int index = ArrayUtils.floorIndex( xx , x );
		
		if( index < 0 )
		{
			return Double.NaN;
		}
		
		if( index == xx.length - 1 )
		{
			return x == xx[ index ] ? yy[ index ] : Double.NaN;
		}
		
		return evalLine( xx[ index ] , yy[ index ] , xx[ index + 1 ] , yy[ index + 1 ] , x );
	}
	
	public static double evalPolyline( List<Double> xx , List<Double> yy , double x )
	{
		int index = ListUtils.floorIndex( xx , x );
		
		if( index < 0 )
		{
			return Double.NaN;
		}
		
		if( index == xx.size( ) - 1 )
		{
			return x == xx.get( index ) ? yy.get( index ) : Double.NaN;
		}
		
		return evalLine( xx.get( index ) , yy.get( index ) , xx.get( index + 1 ) , yy.get( index + 1 ) , x );
	}
	
	public static double evalMappedTrace( double[ ] domain , double[ ] values , List<Double> anchorDest , List<Double> anchorSrc , double x )
	{
		double srcDomain = evalPolyline( anchorDest , anchorSrc , x );
		return evalPolyline( domain , values , srcDomain );
	}
	
	public static double evalContinuous( double[ ] domain , double[ ] values , double x )
	{
		int fi = ArrayUtils.floorIndex( domain , x );
		
		if( fi < 0 || ( x > domain[ domain.length - 1 ] ) )
		{
			return Double.NaN;
		}
		if( domain[ fi ] == x )
		{
			return values[ fi ];
		}
		
		double ld = domain[ fi ];
		double lv = values[ fi ];
		double hd = domain[ fi + 1 ];
		double hv = values[ fi + 1 ];
		
		double f = ( x - ld ) / ( hd - ld );
		
		return lv * ( 1 - f ) + hv * f;
	}
	
	public static double evalStep( double[ ] domain , double[ ] values , double x )
	{
		int fi = ArrayUtils.floorIndex( domain , x );
		
		return fi < 0 || ( x > domain[ domain.length - 1 ] ) ? Double.NaN : values[ fi ];
	}
	
	public static double evalStepCentered( double[ ] domain , double[ ] values , double x )
	{
		int fi = ArrayUtils.floorIndex( domain , x );
		
		if( fi < 0 || ( x > domain[ domain.length - 1 ] ) )
		{
			return Double.NaN;
		}
		
		if( domain[ fi ] == x )
		{
			return values[ fi ];
		}
		
		double ld = domain[ fi ];
		double lv = values[ fi ];
		double hd = domain[ fi + 1 ];
		double hv = values[ fi + 1 ];
		
		if( Double.isNaN( lv ) || Double.isNaN( hv ) )
		{
			return Double.NaN;
		}
		
		return hd - x < x - ld ? hv : lv;
	}
	
	public static double convertDomain( double d , double srcStart , double srcEnd , double destStart , double destEnd )
	{
		return destStart + ( d - srcStart ) / ( srcEnd - srcStart ) * ( destEnd - destStart );
	}
	
	public static boolean lessOrEqual( double a , double b , boolean backward )
	{
		return backward ? b <= a : a <= b;
	}
	
	public static boolean greaterOrEqual( double a , double b , boolean backward )
	{
		return backward ? b >= a : a >= b;
	}
	
	public static boolean greater( double a , double b , boolean backward )
	{
		return backward ? b > a : a > b;
	}
	
	public static double max( double a , double b , boolean backward )
	{
		return backward ? Math.min( a , b ) : Math.max( a , b );
	}
	
	public static double min( double a , double b , boolean backward )
	{
		return backward ? Math.max( a , b ) : Math.min( a , b );
	}
	
	public static void drawVerticalGridLines( Graphics2D g2 , Rectangle bounds , double leftDomain , double rightDomain , double step )
	{
		double start;
		double end;
		
		if( leftDomain < rightDomain )
		{
			start = GridMath.modCeiling( leftDomain , step );
			end = GridMath.modFloor( rightDomain , step );
		}
		else
		{
			start = GridMath.modCeiling( rightDomain , step );
			end = GridMath.modFloor( leftDomain , step );
		}
		
		int count = ( int ) Math.round( ( end - start ) / step ) + 1;
		
		Line2D.Double line = new Line2D.Double( );
		
		for( int i = 0 ; i < count ; i++ )
		{
			double x = convertDomain( start + step * i , leftDomain , rightDomain , bounds.getMinX( ) , bounds.getMaxX( ) );
			line.setLine( x , bounds.getMinY( ) , x , bounds.getMaxY( ) );
			g2.draw( line );
		}
	}
	
	public static void drawVerticalGridLineLabels( Graphics2D g2 , Rectangle bounds , double leftDomain , double rightDomain , double step , NumberFormat format )
	{
		double start;
		double end;
		
		if( leftDomain < rightDomain )
		{
			start = GridMath.modCeiling( leftDomain , step );
			end = GridMath.modFloor( rightDomain , step );
		}
		else
		{
			start = GridMath.modCeiling( rightDomain , step );
			end = GridMath.modFloor( leftDomain , step );
		}
		
		int count = ( int ) Math.round( ( end - start ) / step ) + 1;
		
		FontMetrics fm = g2.getFontMetrics( );
		
		Rectangle2D lastLabelBounds = null;
		
		for( int i = 0 ; i < count ; i++ )
		{
			double domain = start + step * i;
			double x = convertDomain( domain , leftDomain , rightDomain , bounds.getMinX( ) , bounds.getMaxX( ) );
			String label = format.format( domain );
			Rectangle2D labelBounds = fm.getStringBounds( label , g2 );
			double w = labelBounds.getWidth( );
			double h = labelBounds.getHeight( );
			
			labelBounds.setFrame( x - w / 2.0 , bounds.y + bounds.height - fm.getAscent( ) , w , h );
			
			if( lastLabelBounds == null || !lastLabelBounds.intersects( labelBounds ) )
			{
				g2.drawString( label , ( float ) labelBounds.getMinX( ) , ( float ) labelBounds.getMinY( ) + fm.getAscent( ) );
				lastLabelBounds = labelBounds;
			}
		}
	}
	
	public static void drawHorizontalGridLines( Graphics2D g2 , Rectangle bounds , double topDomain , double bottomDomain , double step )
	{
		double start;
		double end;
		
		if( topDomain < bottomDomain )
		{
			start = GridMath.modCeiling( topDomain , step );
			end = GridMath.modFloor( bottomDomain , step );
		}
		else
		{
			start = GridMath.modCeiling( bottomDomain , step );
			end = GridMath.modFloor( topDomain , step );
		}
		
		int count = ( int ) Math.round( ( end - start ) / step ) + 1;
		
		Line2D.Double line = new Line2D.Double( );
		
		for( int i = 0 ; i < count ; i++ )
		{
			double y = convertDomain( start + step * i , topDomain , bottomDomain , bounds.getMinY( ) , bounds.getMaxY( ) );
			line.setLine( bounds.getMinX( ) , y , bounds.getMaxX( ) , y );
			g2.draw( line );
		}
	}
	
	public static final int	LEFT	= SwingConstants.LEFT;
	public static final int	RIGHT	= SwingConstants.RIGHT;
	public static final int	CENTER	= SwingConstants.CENTER;
	
	public static void drawHorizontalGridLineLabels( Graphics2D g2 , Rectangle bounds , int justify , double topDomain , double bottomDomain , double step , NumberFormat format )
	{
		double start;
		double end;
		
		if( topDomain < bottomDomain )
		{
			start = GridMath.modCeiling( topDomain , step );
			end = GridMath.modFloor( bottomDomain , step );
		}
		else
		{
			start = GridMath.modCeiling( bottomDomain , step );
			end = GridMath.modFloor( topDomain , step );
		}
		
		int count = ( int ) Math.round( ( end - start ) / step ) + 1;
		
		FontMetrics fm = g2.getFontMetrics( );
		
		for( int i = 0 ; i < count ; i++ )
		{
			double domain = start + step * i;
			double y = convertDomain( domain , topDomain , bottomDomain , bounds.getMinY( ) , bounds.getMaxY( ) );
			String label = format.format( domain );
			Rectangle2D labelBounds = fm.getStringBounds( label , g2 );
			double w = labelBounds.getWidth( );
			
			double x = 0;
			switch ( justify )
			{
				case RIGHT :
					x = bounds.getMaxX( ) - w;
					break;
				case CENTER :
					x = bounds.getCenterX( ) - w / 2.0;
					break;
				default :
					x = bounds.getMinX( );
					break;
			}
			
			g2.drawString( label , ( int ) Math.round( x ) , ( int ) Math.round( y + fm.getAscent( ) / 2 ) );
		}
	}
	
	public static double calcHorizontalGridLineLabelsWidth( Graphics2D g2 , double topDomain , double bottomDomain , double step , NumberFormat format )
	{
		double start;
		double end;
		
		if( topDomain < bottomDomain )
		{
			start = GridMath.modCeiling( topDomain , step );
			end = GridMath.modFloor( bottomDomain , step );
		}
		else
		{
			start = GridMath.modCeiling( bottomDomain , step );
			end = GridMath.modFloor( topDomain , step );
		}
		
		int count = ( int ) Math.round( ( end - start ) / step ) + 1;
		
		FontMetrics fm = g2.getFontMetrics( );
		
		double width = 0;
		
		for( int i = 0 ; i < count ; i++ )
		{
			double domain = start + step * i;
			String label = format.format( domain );
			Rectangle2D labelBounds = fm.getStringBounds( label , g2 );
			width = Math.max( width , labelBounds.getWidth( ) );
		}
		
		return width;
	}
	
	public static void drawVerticalLines( Graphics2D g2 , Rectangle bounds , double leftDomain , double rightDomain , double[ ] lines )
	{
		int startIndex;
		int endIndex;
		
		if( leftDomain < rightDomain )
		{
			startIndex = ArrayUtils.ceilingIndex( lines , leftDomain );
			endIndex = ArrayUtils.floorIndex( lines , rightDomain );
		}
		else
		{
			startIndex = ArrayUtils.ceilingIndex( lines , rightDomain );
			endIndex = ArrayUtils.floorIndex( lines , leftDomain );
		}
		
		if( startIndex < 0 || endIndex < 0 )
		{
			return;
		}
		
		Line2D.Double line = new Line2D.Double( );
		
		double lastX = 0;
		
		for( int i = startIndex ; i <= endIndex ; i++ )
		{
			double x = convertDomain( lines[ i ] , leftDomain , rightDomain , bounds.getMinX( ) , bounds.getMaxX( ) );
			if( i == startIndex || Math.round( x ) != Math.round( lastX ) )
			{
				line.setLine( x , bounds.getMinY( ) , x , bounds.getMaxY( ) );
				g2.draw( line );
			}
			lastX = x;
		}
	}
	
	public static Color lighterColor( Color lineColor , int adj )
	{
		int r = Math.max( 0 , Math.min( 255 , lineColor.getRed( ) + adj ) );
		int g = Math.max( 0 , Math.min( 255 , lineColor.getGreen( ) + adj ) );
		int b = Math.max( 0 , Math.min( 255 , lineColor.getBlue( ) + adj ) );
		
		return new Color( r , g , b , lineColor.getAlpha( ) );
	}
	
	public static double[ ] toArray( Collection<Double> collection )
	{
		double[ ] result = new double[ collection.size( ) ];
		int k = 0;
		for( Double d : collection )
		{
			result[ k++ ] = d;
		}
		return result;
	}
	
	public static ArrayList<Double> toList( double[ ] array )
	{
		ArrayList<Double> result = new ArrayList<Double>( );
		for( double d : array )
		{
			result.add( d );
		}
		return result;
	}
	
	public static double[ ] remove( double[ ] array , int index )
	{
		double[ ] newArray = new double[ array.length - 1 ];
		
		System.arraycopy( array , 0 , newArray , 0 , index );
		if( index < array.length - 1 )
		{
			System.arraycopy( array , index + 1 , newArray , index , array.length - index - 1 );
		}
		return newArray;
	}
	
	public static double[ ] insert( double[ ] array , double key , int index )
	{
		double[ ] newArray = Arrays.copyOf( array , array.length + 1 );
		System.arraycopy( array , 0 , newArray , 0 , index );
		newArray[ index ] = key;
		System.arraycopy( array , index , newArray , index + 1 , array.length - index );
		return newArray;
	}
	
	public static double[ ] insert( double[ ] sortedSet , double key )
	{
		int insertIndex = ArrayUtils.ceilingIndex( sortedSet , key );
		
		if( insertIndex < 0 )
		{
			insertIndex = sortedSet.length;
		}
		else if( sortedSet[ insertIndex ] == key )
		{
			return sortedSet;
		}
		
		double[ ] newSet = Arrays.copyOf( sortedSet , sortedSet.length + 1 );
		System.arraycopy( sortedSet , 0 , newSet , 0 , insertIndex );
		sortedSet[ insertIndex ] = key;
		System.arraycopy( sortedSet , insertIndex , newSet , insertIndex + 1 , sortedSet.length - insertIndex );
		
		return newSet;
	}
	
	public static boolean equals( Object o1 , Object o2 )
	{
		return ( o1 == null && o2 == null ) || ( o1 != null && o1.equals( o2 ) ) || ( o2 != null && o2.equals( o1 ) );
	}
	
	public static double minValue( double[ ] values )
	{
		double min = Double.NaN;
		for( double d : values )
		{
			if( Double.isNaN( min ) || d < min )
			{
				min = d;
			}
		}
		return min;
	}
	
	public static double maxValue( double[ ] values )
	{
		double max = Double.NaN;
		for( double d : values )
		{
			if( Double.isNaN( max ) || d > max )
			{
				max = d;
			}
		}
		return max;
	}
}
