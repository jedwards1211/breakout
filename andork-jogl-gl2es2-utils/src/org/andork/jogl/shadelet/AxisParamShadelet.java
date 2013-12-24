package org.andork.jogl.shadelet;

public class AxisParamShadelet extends Shadelet
{
	public AxisParamShadelet( )
	{
		setProperty( "pos" , "a_pos" );
		setProperty( "origin" , "u_origin" );
		setProperty( "axis" , "u_axis" );
		setProperty( "out" , "v_axisParam" );
		setProperty( "posDeclaration" , "attribute vec3 $pos;" );
		setProperty( "originDeclaration" , "/* vertex */ uniform vec3 u_origin;" );
		setProperty( "axisDeclaration" , "/* vertex */ uniform vec3 u_axis;" );
		setProperty( "outDeclaration" , "varying float $out;" );
	}
	
	public String out( )
	{
		return replaceProperties( "$out" );
	}
	
	public String origin( )
	{
		return replaceProperties( "$origin" );
	}
	
	public String axis( )
	{
		return replaceProperties( "$axis" );
	}
	
	public AxisParamShadelet pos( Object pos )
	{
		setProperty( "pos" , pos );
		return this;
	}
	
	public AxisParamShadelet origin( Object origin )
	{
		setProperty( "origin" , origin );
		return this;
	}
	
	public AxisParamShadelet axis( Object axis )
	{
		setProperty( "axis" , axis );
		return this;
	}
	
	public AxisParamShadelet convPos( Object convPos )
	{
		setProperty( "convPos" , convPos );
		return this;
	}
	
	public AxisParamShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public AxisParamShadelet outDeclaration( Object outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public AxisParamShadelet posDeclaration( Object posDeclaration )
	{
		setProperty( "posDeclaration" , posDeclaration );
		return this;
	}
	
	public AxisParamShadelet originDeclaration( Object originDeclaration )
	{
		setProperty( "originDeclaration" , originDeclaration );
		return this;
	}
	
	public AxisParamShadelet axisDeclaration( Object axisDeclaration )
	{
		setProperty( "axisDeclaration" , axisDeclaration );
		return this;
	}
	
	@Override
	public String getVertexShaderMainCode( )
	{
		return "  $out = dot($pos - $origin, $axis);";
	}
}
