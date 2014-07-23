/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package com.andork.plot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.andork.event.BasicPropertyChangeSupport.External;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.model.HasChangeSupport;
import org.andork.model.Model;

@SuppressWarnings( "serial" )
public class PlotAxis extends JComponent implements Model
{
	public static enum Orientation
	{
		HORIZONTAL , VERTICAL;
	}
	
	public static enum LabelPosition
	{
		TOP , BOTTOM , LEFT , RIGHT;
	}
	
	public static enum Property
	{
		AXIS_CONVERSION;
	}
	
	private LinearAxisConversion								axisConversion			= new LinearAxisConversion( );
	
	private static final HierarchicalBasicPropertyChangeSupport	changeSupport			= new HierarchicalBasicPropertyChangeSupport( );
	
	private final Set<Component>								plots					= new HashSet<Component>( );
	
	private final Orientation									orientation;
	private LabelPosition										labelPosition;
	private int													majorTickSize			= 10;
	private int													minorTickSize			= 5;
	private Color												majorTickColor			= Color.GRAY;
	private Color												minorTickColor			= Color.LIGHT_GRAY;
	
	private int													minMinorGridLineSpacing	= 30;
	
	private int													labelPadding			= 3;
	
	private Double												minValueForCalcSizes;
	private Double												maxValueForCalcSizes;
	private Dimension											calcMinSize;
	private Dimension											calcPrefSize;
	
	private NumberFormat										format;
	
	public PlotAxis( Orientation orientation , LabelPosition labelPosition )
	{
		this.orientation = orientation;
		setLabelPosition( labelPosition );
		
		format = DecimalFormat.getInstance( );
		format.setGroupingUsed( false );
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
		changeSupport.firePropertyChange( this , Property.AXIS_CONVERSION , null , axisConversion );
	}
	
	public LabelPosition getLabelPosition( )
	{
		return labelPosition;
	}
	
	public void setLabelPosition( LabelPosition labelPosition )
	{
		switch( labelPosition )
		{
			case TOP:
			case BOTTOM:
				if( orientation != Orientation.HORIZONTAL )
					throw new IllegalArgumentException( );
				break;
			case LEFT:
			case RIGHT:
				if( orientation != Orientation.VERTICAL )
					throw new IllegalArgumentException( );
				break;
		}
		this.labelPosition = labelPosition;
	}
	
	public void addPlot( Plot plot )
	{
		plots.add( plot );
	}
	
	public void removePlot( Plot plot )
	{
		plots.remove( plot );
	}
	
	public Set<Component> getPlots( )
	{
		return Collections.unmodifiableSet( plots );
	}
	
	public Color getMajorTickColor( )
	{
		return majorTickColor;
	}
	
	public void setMajorTickColor( Color majorTickColor )
	{
		if( majorTickColor == null )
		{
			throw new IllegalArgumentException( "majorTickColor must be non-null" );
		}
		this.majorTickColor = majorTickColor;
	}
	
	public Color getMinorTickColor( )
	{
		return minorTickColor;
	}
	
	public void setMinorTickColor( Color minorTickColor )
	{
		if( minorTickColor == null )
		{
			throw new IllegalArgumentException( "minorTickColor must be non-null" );
		}
		this.minorTickColor = minorTickColor;
	}
	
	public int getMajorTickSize( )
	{
		return majorTickSize;
	}
	
	public void setMajorTickSize( int majorTickSize )
	{
		this.majorTickSize = majorTickSize;
	}
	
	public int getMinorTickSize( )
	{
		return minorTickSize;
	}
	
	public void setMinorTickSize( int minorTickSize )
	{
		this.minorTickSize = minorTickSize;
	}
	
	public int getMinMinorGridLineSpacing( )
	{
		return minMinorGridLineSpacing;
	}
	
	public void setMinMinorGridLineSpacing( int minMinorGridLineSpacing )
	{
		this.minMinorGridLineSpacing = minMinorGridLineSpacing;
	}
	
	public int getTextPadding( )
	{
		return labelPadding;
	}
	
	public void setTextPadding( int textPadding )
	{
		this.labelPadding = textPadding;
	}
	
	public Orientation getOrientation( )
	{
		return orientation;
	}
	
	public Dimension getMinimumSize( )
	{
		if( calcMinSize != null && !isMinimumSizeSet( ) )
		{
			return new Dimension( calcMinSize );
		}
		return super.getMinimumSize( );
	}
	
	public Dimension getPreferredSize( )
	{
		if( calcPrefSize != null && !isPreferredSizeSet( ) )
		{
			return new Dimension( calcPrefSize );
		}
		return super.getPreferredSize( );
	}
	
	public boolean updateSizes( Graphics2D g2 )
	{
		Insets insets = getInsets( );
		
		Dimension newMinSize;
		Dimension newPrefSize;
		
		if( orientation == Orientation.VERTICAL )
		{
			double minorSpacing = GridMath.niceCeiling( Math.abs( axisConversion.invert( minMinorGridLineSpacing ) - axisConversion.invert( 0 ) ) );
			double majorSpacing = minorSpacing * 2;
			
			double topValue = axisConversion.invert( 0 );
			double bottomValue = axisConversion.invert( getHeight( ) );
			
			int fractionDigits = GridMath.niceCeilingFractionDigits( majorSpacing );
			format.setMinimumFractionDigits( fractionDigits );
			format.setMaximumFractionDigits( fractionDigits );
			
			int labelWidth = ( int ) Math.ceil( PlotUtils.calcHorizontalGridLineLabelsWidth( g2 , topValue , bottomValue , majorSpacing , format ) );
			
			int width = labelWidth + majorTickSize + labelPadding + insets.left + insets.right;
			
			newMinSize = new Dimension( width , 0 );
			newPrefSize = new Dimension( width , 100 );
		}
		else
		{
			int height = g2.getFontMetrics( ).getAscent( ) + majorTickSize + labelPadding + insets.top + insets.bottom;
			
			newMinSize = new Dimension( 0 , height );
			newPrefSize = new Dimension( 100 , height );
		}
		
		boolean changed = !getMinimumSize( ).equals( newPrefSize ) || !getPreferredSize( ).equals( newPrefSize );
		
		calcMinSize = newMinSize;
		calcPrefSize = newPrefSize;
		
		return changed;
	}
	
	@Override
	protected void paintComponent( Graphics g )
	{
		if( updateSizes( ( Graphics2D ) g ) )
		{
			revalidate( );
		}
		
		super.paintComponent( g );
		Rectangle bounds = getBounds( );
		bounds.x = 0;
		bounds.y = 0;
		
		Insets insets = getInsets( );
		
		Graphics2D g2 = ( Graphics2D ) g;
		Object origAntialiasing = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
		Paint origPaint = g2.getPaint( );
		Stroke origStroke = g2.getStroke( );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON );
		
		double minorSpacing = GridMath.niceCeiling( Math.abs( axisConversion.invert( minMinorGridLineSpacing ) - axisConversion.invert( 0 ) ) );
		double majorSpacing = minorSpacing * 2;
		
		if( orientation == Orientation.VERTICAL )
		{
			double topValue = axisConversion.invert( 0 );
			double bottomValue = axisConversion.invert( getHeight( ) );
			
			int fractionDigits = GridMath.niceCeilingFractionDigits( majorSpacing );
			format.setMinimumFractionDigits( fractionDigits );
			format.setMaximumFractionDigits( fractionDigits );
			
			Rectangle minorBounds = new Rectangle( bounds );
			minorBounds.width = minorTickSize;
			Rectangle majorBounds = new Rectangle( bounds );
			majorBounds.width = majorTickSize;
			Rectangle textBounds = new Rectangle( bounds );
			textBounds.width = ( int ) Math.ceil( PlotUtils.calcHorizontalGridLineLabelsWidth( g2 , topValue , bottomValue , majorSpacing , format ) );
			
			int alignment;
			
			if( labelPosition == LabelPosition.LEFT )
			{
				minorBounds.x = getWidth( ) - insets.right - minorBounds.width;
				majorBounds.x = getWidth( ) - insets.right - majorBounds.width;
				textBounds.x = majorBounds.x - textBounds.width - labelPadding;
				alignment = PlotUtils.RIGHT;
			}
			else
			{
				minorBounds.x = insets.left;
				majorBounds.x = insets.left;
				textBounds.x = majorBounds.x + majorBounds.width + labelPadding;
				alignment = PlotUtils.LEFT;
			}
			
			g2.setColor( minorTickColor );
			PlotUtils.drawHorizontalGridLines( g2 , minorBounds , topValue , bottomValue , minorSpacing );
			
			g2.setColor( majorTickColor );
			PlotUtils.drawHorizontalGridLines( g2 , majorBounds , topValue , bottomValue , majorSpacing );
			
			g2.setColor( getForeground( ) );
			
			PlotUtils.drawHorizontalGridLineLabels( g2 , textBounds , alignment , topValue , bottomValue , majorSpacing , format );
		}
		else
		{
			double leftDomain = axisConversion.invert( 0 );
			double rightDomain = axisConversion.invert( getWidth( ) );
			
			Rectangle minorBounds = new Rectangle( bounds );
			minorBounds.height = minorTickSize;
			Rectangle majorBounds = new Rectangle( bounds );
			majorBounds.height = majorTickSize;
			Rectangle textBounds = new Rectangle( bounds );
			textBounds.height = g2.getFontMetrics( ).getAscent( );
			
			if( labelPosition == LabelPosition.TOP )
			{
				minorBounds.y = getHeight( ) - insets.bottom - minorBounds.height;
				majorBounds.y = getHeight( ) - insets.bottom - majorBounds.height;
				textBounds.y = majorBounds.y - textBounds.height - labelPadding;
			}
			else
			{
				minorBounds.y = insets.top;
				majorBounds.y = insets.top;
				textBounds.y = majorBounds.y + majorBounds.height + labelPadding;
			}
			
			g2.setColor( minorTickColor );
			PlotUtils.drawVerticalGridLines( g2 , minorBounds , leftDomain , rightDomain , minorSpacing );
			
			g2.setColor( majorTickColor );
			PlotUtils.drawVerticalGridLines( g2 , majorBounds , leftDomain , rightDomain , majorSpacing );
			
			NumberFormat format = DecimalFormat.getInstance( );
			int fractionDigits = GridMath.niceCeilingFractionDigits( majorSpacing );
			format.setMinimumFractionDigits( fractionDigits );
			format.setMaximumFractionDigits( fractionDigits );
			format.setGroupingUsed( false );
			
			g2.setColor( getForeground( ) );
			
			PlotUtils.drawVerticalGridLineLabels( g2 , textBounds , leftDomain , rightDomain , majorSpacing , format );
		}
		
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , origAntialiasing );
		g2.setPaint( origPaint );
		g2.setStroke( origStroke );
	}
	
	public int getViewSpan( )
	{
		int result = orientation == Orientation.HORIZONTAL ? getWidth( ) : getHeight( );
		if( result != 0 )
		{
			return result;
		}
		return orientation == Orientation.HORIZONTAL ? getPreferredSize( ).width : getPreferredSize( ).height;
	}
	
	public static void equalizeScale( PlotAxis ... axes )
	{
		double scale = Double.MAX_VALUE;
		for( PlotAxis axis : axes )
		{
			scale = Math.min( scale , Math.abs( axis.getAxisConversion( ).getScale( ) ) );
		}
		
		for( PlotAxis axis : axes )
		{
			LinearAxisConversion conv = axis.getAxisConversion( );
			if( axis.getViewSpan( ) == 0 )
			{
				conv.setScale( scale * Math.signum( conv.getScale( ) ) );
			}
			else
			{
				double start = conv.invert( 0 );
				double end = conv.invert( axis.getViewSpan( ) );
				double mid = ( start + end ) * 0.5;
				double newSpan = axis.getViewSpan( ) / scale;
				
				if( start < end )
				{
					conv.set( mid - newSpan / 2 , 0 , mid + newSpan / 2 , axis.getViewSpan( ) );
				}
				else
				{
					conv.set( mid + newSpan / 2 , 0 , mid - newSpan / 2 , axis.getViewSpan( ) );
				}
			}
		}
		
		for( PlotAxis axis : axes )
		{
			axis.repaint( );
			for( Component plot : axis.plots )
			{
				plot.repaint( );
			}
		}
	}
	
	@Override
	public External changeSupport( )
	{
		return changeSupport.external( );
	}
	
	@Override
	public Object get( Object key )
	{
		if( key == Property.AXIS_CONVERSION )
		{
			return getAxisConversion( );
		}
		throw new IllegalArgumentException( "Invalid key: " + key );
	}
	
	@Override
	public void set( Object key , Object newValue )
	{
		if( key == Property.AXIS_CONVERSION )
		{
			setAxisConversion( ( LinearAxisConversion ) newValue );
		}
		throw new IllegalArgumentException( "Invalid key: " + key );
	}
}
