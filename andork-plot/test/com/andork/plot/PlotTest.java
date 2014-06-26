package com.andork.plot;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.andork.awt.event.MouseAdapterChain;

import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;

public class PlotTest
{
	public static void main( String[ ] args )
	{
		Plot plot = new Plot( );
		
		PlotAxis haxis = new PlotAxis( Orientation.HORIZONTAL , LabelPosition.TOP );
		PlotAxis vaxis = new PlotAxis( Orientation.VERTICAL , LabelPosition.LEFT );
		
		haxis.addPlot( plot );
		vaxis.addPlot( plot );
		
		plot.addLayer( new HorizontalGridLineLayer( vaxis ) );
		plot.addLayer( new VerticalGridLineLayer( haxis ) );
		
		PlotAxisController haxisController = new PlotAxisController( haxis );
		PlotAxisController vaxisController = new PlotAxisController( vaxis );
		
		PlotController plotController = new PlotController( plot , haxisController , vaxisController );
		MouseAdapterChain chain = new MouseAdapterChain( );
		chain.addMouseAdapter( plotController );
		chain.install( plot );
		
		JFrame frame = new JFrame( );
		frame.getContentPane( ).setLayout( new PlotPanelLayout( ) );
		frame.getContentPane( ).add( plot );
		frame.getContentPane( ).add( haxis );
		frame.getContentPane( ).add( vaxis );
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( new Dimension( 640 , 480 ) );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
}
