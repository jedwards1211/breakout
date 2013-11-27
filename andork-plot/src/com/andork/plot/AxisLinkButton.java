package com.andork.plot;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

@SuppressWarnings( "serial" )
public class AxisLinkButton extends JToggleButton
{
	PlotAxisController[ ]	axisControllers;
	PlotAxis[ ]				axes;
	
	public AxisLinkButton( PlotAxisController ... axisControllers )
	{
		this.axisControllers = axisControllers;
		axes = new PlotAxis[ axisControllers.length ];
		for( int i = 0 ; i < axisControllers.length ; i++ )
		{
			axes[ i ] = axisControllers[ i ].getView( );
		}
		
		setIcon( new ImageIcon( getClass( ).getResource( "unlink.png" ) ) );
		setSelectedIcon( new ImageIcon( getClass( ).getResource( "link.png" ) ) );
		
		addItemListener( new ItemListener( )
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				if( isSelected( ) )
				{
					PlotAxis.equalizeScale( axes );
				}
				for( PlotAxisController axisController : AxisLinkButton.this.axisControllers )
				{
					axisController.setEnableZoom( !isSelected( ) );
				}
			}
		} );
	}
}
