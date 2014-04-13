package org.andork.plot;

import org.andork.breakout.model.FloatRange;
import org.andork.event.Binder.Binding;
import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.model.Model;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.PlotAxis;

public class PlotAxisFloatRangeBinding extends Binding implements HierarchicalBasicPropertyChangeListener
{
	PlotAxis	axis;
	boolean		modelToViewFailed	= false;
	
	public PlotAxisFloatRangeBinding( Object property , PlotAxis axis )
	{
		super( property );
		this.axis = axis;
	}
	
	@Override
	public void modelToView( )
	{
		Model model = getModel( );
		if( axis.getViewSpan( ) == 0 )
		{
			modelToViewFailed = true;
			return;
		}
		modelToViewFailed = false;
		if( model == null )
		{
			return;
		}
		FloatRange range = ( FloatRange ) model.get( property );
		if( range != null )
		{
			LinearAxisConversion axisConversion = axis.getAxisConversion( );
			axisConversion.set( range.getLo( ) , 0 , range.getHi( ) , axis.getViewSpan( ) );
		}
	}
	
	@Override
	public void viewToModel( )
	{
		Model model = getModel( );
		if( model == null || axis.getViewSpan( ) == 0 )
		{
			return;
		}
		if( modelToViewFailed )
		{
			modelToView( );
		}
		LinearAxisConversion axisConversion = axis.getAxisConversion( );
		FloatRange range = new FloatRange( ( float ) axisConversion.invert( 0 ) , ( float ) axisConversion.invert( axis.getViewSpan( ) ) );
		model.set( property , range );
	}
	
	@Override
	public void registerWithView( )
	{
		axis.changeSupport( ).addPropertyChangeListener( this );
	}
	
	@Override
	public void unregisterFromView( )
	{
		axis.changeSupport( ).removePropertyChangeListener( this );
	}
	
	@Override
	public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		if( source == axis && property == PlotAxis.Property.AXIS_CONVERSION )
		{
			viewToModel( );
		}
	}
	
	@Override
	public void childrenChanged( Object source , ChangeType changeType , Object... children )
	{
	}
}
