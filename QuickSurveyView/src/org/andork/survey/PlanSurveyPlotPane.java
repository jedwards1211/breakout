package org.andork.survey;

import javax.swing.JPanel;

import com.andork.plot.Axis;
import com.andork.plot.Axis.LabelPosition;
import com.andork.plot.Axis.Orientation;
import com.andork.plot.AxisController;
import com.andork.plot.HorizontalGridLineLayer;
import com.andork.plot.MouseAdapterChain;
import com.andork.plot.Plot;
import com.andork.plot.PlotController;
import com.andork.plot.PlotPanelLayout;
import com.andork.plot.VerticalGridLineLayer;

public class PlanSurveyPlotPane extends JPanel
{
	Plot			plot;
	Axis			xaxis;
	Axis			yaxis;
	
	PlanSurveyLayer	planSurveyLayer;
	
	public PlanSurveyPlotPane( )
	{
		plot = new Plot( );
		xaxis = new Axis( Orientation.HORIZONTAL , LabelPosition.TOP );
		yaxis = new Axis( Orientation.VERTICAL , LabelPosition.LEFT );
		yaxis.getAxisConversion( ).set( 1 , 0 , -1 , 10 );
		
		xaxis.addPlot( plot );
		yaxis.addPlot( plot );
		
		planSurveyLayer = new PlanSurveyLayer( xaxis , yaxis );
		plot.addLayer( planSurveyLayer );
		
		HorizontalGridLineLayer hglayer = new HorizontalGridLineLayer( yaxis );
		plot.addLayer( hglayer );
		
		VerticalGridLineLayer vglayer = new VerticalGridLineLayer( xaxis );
		plot.addLayer( vglayer );
		
		new AxisController( xaxis );
		new AxisController( yaxis );
		
		MouseAdapterChain chain = new MouseAdapterChain( );
		chain.addMouseAdapter( new PlotController( plot , xaxis , yaxis ) );
		chain.install( plot );
		
		setLayout( new PlotPanelLayout( ) );
		add( plot );
		add( xaxis );
		add( yaxis );
	}
}
