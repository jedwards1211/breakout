package org.andork.jogl.awt;

import java.math.BigDecimal;

import org.andork.jogl.awt.ScreenCaptureDialog.ResolutionUnit;
import org.andork.snakeyaml.YamlSpec;

public class ScreenCaptureDialogModel extends YamlSpec<ScreenCaptureDialogModel>
{
	public static final Attribute<String>			outputDirectory	= stringAttribute( "outputDirectory" );
	public static final Attribute<String>			fileNamePrefix	= stringAttribute( "fileNamePrefix" );
	public static final Attribute<Integer>			fileNumber		= integerAttribute( "fileNumber" );
	public static final Attribute<Integer>			pixelWidth		= integerAttribute( "pixelWidth" );
	public static final Attribute<Integer>			pixelHeight		= integerAttribute( "pixelHeight" );
	public static final Attribute<BigDecimal>		resolution		= bigDecimalAttribute( "resolution" );
	public static final Attribute<ResolutionUnit>	resolutionUnit	= enumAttribute( "resolutionUnit" , ResolutionUnit.class );
	
	private ScreenCaptureDialogModel( )
	{
		
	}
	
	public static final ScreenCaptureDialogModel	instance	= new ScreenCaptureDialogModel( );
}
