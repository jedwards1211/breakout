package com.andork.plot;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import com.andork.plot.PlotAxis.Orientation;

public class PlotPanelLayout implements LayoutManager
{
	@Override
	public void addLayoutComponent( String name , Component comp )
	{
		
	}
	
	@Override
	public void removeLayoutComponent( Component comp )
	{
		
	}
	
	private static enum SizeType
	{
		MINIMUM , PREFERRED , MAXIMUM;
	}
	
	private Dimension getSize( Component comp , SizeType sizeType )
	{
		switch( sizeType )
		{
			case MAXIMUM:
				return comp.getMaximumSize( );
			case PREFERRED:
				return comp.getPreferredSize( );
			case MINIMUM:
				return comp.getMinimumSize( );
			default:
				throw new IllegalArgumentException( "invalid sizeType: " + sizeType );
		}
	}
	
	private Dimension layoutSize( Container parent , SizeType sizeType )
	{
		Dimension plotSize = new Dimension( );
		Dimension axisSize = new Dimension( );
		
		for( Component comp : parent.getComponents( ) )
		{
			if( !comp.isVisible( ) )
			{
				continue;
			}
			if( comp instanceof Plot )
			{
				Dimension size = getSize( comp , sizeType );
				plotSize.width = Math.max( plotSize.width , size.width );
				plotSize.height = Math.max( plotSize.height , size.height );
			}
			else if( comp instanceof PlotAxis )
			{
				PlotAxis axis = ( PlotAxis ) comp;
				Dimension size = getSize( comp , sizeType );
				if( axis.getOrientation( ) == Orientation.HORIZONTAL )
				{
					axisSize.height += size.height;
					axisSize.width = Math.max( axisSize.width , size.width );
				}
				else
				{
					axisSize.width += size.width;
					axisSize.height = Math.max( axisSize.height , size.height );
				}
			}
		}
		
		Insets insets = parent.getInsets( );
		
		return new Dimension( plotSize.width + axisSize.width + insets.left + insets.right , plotSize.height + axisSize.height + insets.top + insets.bottom );
	}
	
	@Override
	public Dimension preferredLayoutSize( Container parent )
	{
		return layoutSize( parent , SizeType.PREFERRED );
	}
	
	@Override
	public Dimension minimumLayoutSize( Container parent )
	{
		return layoutSize( parent , SizeType.MINIMUM );
	}
	
	@Override
	public void layoutContainer( Container parent )
	{
		Insets insets = parent.getInsets( );
		Insets plotInsets = new Insets( 0 , 0 , 0 , 0 );
		
		Dimension prefSize = preferredLayoutSize( parent );
		Dimension actualSize = parent.getSize( );
		
		SizeType hSizeType = actualSize.width >= prefSize.width ? SizeType.PREFERRED : SizeType.MINIMUM;
		SizeType vSizeType = actualSize.height >= prefSize.height ? SizeType.PREFERRED : SizeType.MINIMUM;
		
		for( Component comp : parent.getComponents( ) )
		{
			if( !comp.isVisible( ) )
			{
				continue;
			}
			if( comp instanceof PlotAxis )
			{
				PlotAxis axis = ( PlotAxis ) comp;
				switch( axis.getLabelPosition( ) )
				{
					case TOP:
						plotInsets.top += getSize( axis , vSizeType ).height;
						break;
					case LEFT:
						plotInsets.left += getSize( axis , hSizeType ).width;
						break;
					case BOTTOM:
						plotInsets.bottom += getSize( axis , vSizeType ).height;
						break;
					case RIGHT:
						plotInsets.right += getSize( axis , hSizeType ).width;
						break;
				}
			}
		}
		
		Insets axisInsets = ( Insets ) insets.clone( );
		
		int plotLeft = insets.left + plotInsets.left;
		int plotTop = insets.top + plotInsets.top;
		int plotWidth = parent.getWidth( ) - insets.left - plotInsets.left - insets.right - plotInsets.right;
		int plotHeight = parent.getHeight( ) - insets.top - plotInsets.top - insets.bottom - plotInsets.bottom;
		
		for( Component comp : parent.getComponents( ) )
		{
			if( comp instanceof Plot )
			{
				comp.setBounds( plotLeft , plotTop , plotWidth , plotHeight );
			}
			else if( comp instanceof PlotAxis )
			{
				PlotAxis axis = ( PlotAxis ) comp;
				Dimension size;
				switch( axis.getLabelPosition( ) )
				{
					case TOP:
						size = getSize( axis , vSizeType );
						axis.setBounds( plotLeft , axisInsets.top , plotWidth , size.height );
						axisInsets.top += size.height;
						break;
					case LEFT:
						size = getSize( axis , hSizeType );
						axis.setBounds( axisInsets.left , plotTop , size.width , plotHeight );
						axisInsets.bottom += size.height;
						break;
					case BOTTOM:
						size = getSize( axis , vSizeType );
						axis.setBounds( plotLeft , parent.getHeight( ) - axisInsets.bottom - size.height , plotWidth , size.height );
						axisInsets.bottom += size.height;
						break;
					case RIGHT:
						size = getSize( axis , hSizeType );
						axis.setBounds( parent.getWidth( ) - axisInsets.right - size.width , plotTop , size.width , plotHeight );
						axisInsets.right += size.width;
						break;
				}
			}
		}
	}
}
