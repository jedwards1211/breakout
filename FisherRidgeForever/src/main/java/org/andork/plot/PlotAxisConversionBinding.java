package org.andork.plot;

import org.andork.event.Binder.Binding;
import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.model.Model;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.PlotAxis;

public class PlotAxisConversionBinding extends Binding implements HierarchicalBasicPropertyChangeListener
{
	PlotAxis	axis;
	boolean		modelToViewFailed	= false;
	
	public PlotAxisConversionBinding( Object property , PlotAxis axis )
	{
		super( property );
		this.axis = axis;
	}
	
	@Override
	public void modelToView( )
	{
		Model model = getModel( );
		if( model == null )
		{
			return;
		}
		LinearAxisConversion conversion = ( LinearAxisConversion ) model.get( property );
		if( conversion != null )
		{
			axis.setAxisConversion( new LinearAxisConversion( conversion ) );
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
		model.set( property , new LinearAxisConversion( axis.getAxisConversion( ) ) );
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
	public void childrenChanged( Object source , ChangeType changeType , Object child )
	{
	}
}
