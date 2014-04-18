package org.andork.jogl.awt;

import java.math.BigDecimal;

import org.andork.jogl.awt.ScreenCaptureDialogModel.ResolutionUnit;
import org.andork.snakeyaml.YamlSpec;

public class ScreenCaptureDialogModel extends YamlSpec<ScreenCaptureDialogModel>
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
	
	public static final Attribute<String>									outputDirectory	= stringAttribute( "outputDirectory" );
	public static final Attribute<String>									fileNamePrefix	= stringAttribute( "fileNamePrefix" );
	public static final Attribute<Integer>									fileNumber		= integerAttribute( "fileNumber" );
	public static final Attribute<Integer>									pixelWidth		= integerAttribute( "pixelWidth" );
	public static final Attribute<Integer>									pixelHeight		= integerAttribute( "pixelHeight" );
	public static final Attribute<BigDecimal>								resolution		= bigDecimalAttribute( "resolution" );
	public static final Attribute<ScreenCaptureDialogModel.ResolutionUnit>	resolutionUnit	= enumAttribute( "resolutionUnit" , ScreenCaptureDialogModel.ResolutionUnit.class );
	
	private ScreenCaptureDialogModel( )
	{
		
	}
	
	public static final ScreenCaptureDialogModel	instance	= new ScreenCaptureDialogModel( );
}
