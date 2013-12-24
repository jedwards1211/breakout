package org.andork.frf.model;

import org.andork.jogl.shadelet.Shadelet;

public class HighlightShadelet extends Shadelet
{
	public HighlightShadelet( )
	{
		setProperty( "vertParam" , "a_highlight" );
		setProperty( "fragParam" , "v_highlight" );
		setProperty( "color" , "color" );
		setProperty( "out" , "gl_FragColor" );
		setProperty( "vertParamDeclaration" , "attribute vec2 $vertParam;" );
		setProperty( "fragParamDeclaration" , "varying vec2 $fragParam;" );
		setProperty( "colorDeclaration" , "/* fragment */ uniform vec4 $color;" );
	}
	
	public String vertParam( )
	{
		return replaceProperties( "$vertParam" );
	}
	
	public HighlightShadelet vertParam( Object vertParam )
	{
		setProperty( "vertParam" , vertParam );
		return this;
	}
	
	public HighlightShadelet fragParam( Object fragParam )
	{
		setProperty( "fragParam" , fragParam );
		return this;
	}
	
	public HighlightShadelet color( Object color )
	{
		setProperty( "color" , color );
		return this;
	}
	
	public HighlightShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public HighlightShadelet vertParamDeclaration( Object vertParamDeclaration )
	{
		setProperty( "vertParamDeclaration" , vertParamDeclaration );
		return this;
	}
	
	public HighlightShadelet fragParamDeclaration( Object fragParamDeclaration )
	{
		setProperty( "fragParamDeclaration" , fragParamDeclaration );
		return this;
	}
	
	public HighlightShadelet colorDeclaration( Object colorDeclaration )
	{
		setProperty( "colorDeclaration" , colorDeclaration );
		return this;
	}
	
	public HighlightShadelet outDeclaration( Object outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public String getVertexShaderMainCode( )
	{
		return "$fragParam = $vertParam;";
	}
	
	public String getFragmentShaderMainCode( )
	{
		return "  $out = mix($out, $color, clamp(min($fragParam.x, $fragParam.y), 0.0, 1.0));";
	}
}
