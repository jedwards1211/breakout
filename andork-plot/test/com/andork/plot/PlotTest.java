package com.andork.plot;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.wonderly.awt.Packer;

import com.andork.plot.Axis.LabelPosition;
import com.andork.plot.Axis.Orientation;

public class PlotTest
{
	public static void main( String[ ] args )
	{
		Plot plot = new Plot( );
		
		Axis haxis = new Axis( Orientation.HORIZONTAL , LabelPosition.TOP );
		Axis vaxis = new Axis( Orientation.VERTICAL , LabelPosition.LEFT );
		
		haxis.addPlot( plot );
		vaxis.addPlot( plot );
		
		plot.addLayer( new HorizontalGridLineLayer( vaxis ) );
		plot.addLayer( new VerticalGridLineLayer( haxis ) );
		
		AxisController haxisController = new AxisController( haxis );
		AxisController vaxisController = new AxisController( vaxis );
		
		PlotController plotController = new PlotController( plot , haxis , vaxis );
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
