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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.andork.awt.layout.Corner;

import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;

@SuppressWarnings( "serial" )
public class ImagePlotPanel extends JPanel
{
	Plot				plot;
	ImagePlotLayer		imagePlotLayer;
	PlotAxis			xAxis;
	PlotAxis			yAxis;
	AxisLinkButton		axisLinkButton;
	
	PlotAxisController	xAxisController;
	PlotAxisController	yAxisController;
	PlotController		plotController;
	MouseLooper			mouseLooper;
	
	public static JFrame showImageViewer( Image image )
	{
		ImagePlotPanel panel = new ImagePlotPanel( );
		panel.setImage( image );
		Dimension screensize = Toolkit.getDefaultToolkit( ).getScreenSize( );
		panel.setPreferredSize( new Dimension( Math.min( image.getWidth( null ) , screensize.width * 3 / 4 ) ,
				Math.min( image.getHeight( null ) , screensize.height * 3 / 4 ) ) );
		
		JFrame frame = new JFrame( );
		frame.getContentPane( ).add( panel , BorderLayout.CENTER );
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
		return frame;
	}
	
	public ImagePlotPanel( )
	{
		plot = new Plot( );
		xAxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		yAxis = new PlotAxis( Orientation.VERTICAL , LabelPosition.LEFT );
		
		xAxis.addPlot( plot );
		yAxis.addPlot( plot );
		
		xAxisController = new PlotAxisController( xAxis );
		yAxisController = new PlotAxisController( yAxis );
		plotController = new PlotController( plot , xAxisController , yAxisController );
		
		axisLinkButton = new AxisLinkButton( xAxisController , yAxisController );
		
		imagePlotLayer = new ImagePlotLayer( xAxis.getAxisConversion( ) , yAxis.getAxisConversion( ) );
		plot.addLayer( new FillPlotLayer( new CheckerPaint( Color.LIGHT_GRAY , Color.GRAY , 9 ) ) );
		plot.addLayer( imagePlotLayer );
		
		setLayout( new PlotPanelLayout( ) );
		
		add( plot );
		add( xAxis );
		add( yAxis );
		add( axisLinkButton , Corner.TOP_LEFT );
		
		mouseLooper = new MouseLooper( );
		mouseLooper.addMouseAdapter( plotController );
		addMouseListener( mouseLooper );
		addMouseMotionListener( mouseLooper );
		addMouseWheelListener( mouseLooper );
	}
	
	public Plot getPlot( )
	{
		return plot;
	}
	
	public Image getImage( )
	{
		return imagePlotLayer.getImage( );
	}
	
	public void setImage( Image image )
	{
		imagePlotLayer.setImage( image );
		imagePlotLayer.setSrc( new Rectangle2D.Double( 0.0 , 0.0 , image.getWidth( null ) , image.getHeight( null ) ) );
		imagePlotLayer.setDest( imagePlotLayer.getSrc( ) );
		plot.repaint( );
	}
}
