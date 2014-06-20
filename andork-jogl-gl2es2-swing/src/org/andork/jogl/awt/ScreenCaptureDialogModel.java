package org.andork.jogl.awt;

import java.math.BigDecimal;
import java.text.Bidi;

import org.andork.func.BigDecimalBimapper;
import org.andork.func.Bimapper;
import org.andork.func.EnumBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;

public class ScreenCaptureDialogModel extends QSpec<ScreenCaptureDialogModel>
{
	public static enum PrintSizeUnit
	{
		INCHES , CENTIMETERS;
		
		String	displayName;
		
		public String toString( )
		{
			return displayName;
		}
	}
	
	public static enum ResolutionUnit
	{
		PIXELS_PER_IN , PIXELS_PER_CM;
		
		String	displayName;
		
		public String toString( )
		{
			return displayName;
		}
	}
	
	public static final Attribute<String>									outputDirectory	= newAttribute( String.class , "outputDirectory" );
	public static final Attribute<String>									fileNamePrefix	= newAttribute( String.class , "fileNamePrefix" );
	public static final Attribute<Integer>									fileNumber		= newAttribute( Integer.class , "fileNumber" );
	public static final Attribute<Integer>									pixelWidth		= newAttribute( Integer.class , "pixelWidth" );
	public static final Attribute<Integer>									pixelHeight		= newAttribute( Integer.class , "pixelHeight" );
	public static final Attribute<BigDecimal>								resolution		= newAttribute( BigDecimal.class , "resolution" );
	public static final Attribute<ScreenCaptureDialogModel.ResolutionUnit>	resolutionUnit	= newAttribute( ScreenCaptureDialogModel.ResolutionUnit.class , "resolutionUnit" );
	public static final Attribute<Integer>									numSamples		= newAttribute( Integer.class , "numSamples" );
	
	private ScreenCaptureDialogModel( )
	{
		
	}
	
	public static final ScreenCaptureDialogModel							instance	= new ScreenCaptureDialogModel( );
	
	public static final Bimapper<QObject<ScreenCaptureDialogModel>, Object>	defaultMapper;
	
	static
	{
		defaultMapper = new QObjectMapBimapper<ScreenCaptureDialogModel>( instance );
	}
}
