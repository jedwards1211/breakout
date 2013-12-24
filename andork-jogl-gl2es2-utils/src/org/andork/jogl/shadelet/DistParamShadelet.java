package org.andork.jogl.shadelet;

public class DistParamShadelet extends Shadelet
{
	public DistParamShadelet( )
	{
		setProperty( "v" , "v" );
		setProperty( "m" , "m" );
		setProperty( "pos" , "a_pos" );
		setProperty( "convPos" , "vec4($pos, 1.0)" );
		setProperty( "out" , "v_dist" );
		setProperty( "vDeclaration" , "/* vertex */ uniform mat4 $v;" );
		setProperty( "mDeclaration" , "/* vertex */ uniform mat4 $m;" );
		setProperty( "posDeclaration" , "attribute vec3 $pos;" );
		setProperty( "outDeclaration" , "varying float $out;" );
	}
	
	public DistParamShadelet v( Object v )
	{
		setProperty( "v" , v );
		return this;
	}
	
	public DistParamShadelet m( Object m )
	{
		setProperty( "m" , m );
		return this;
	}
	
	public DistParamShadelet pos( Object pos )
	{
		setProperty( "pos" , pos );
		return this;
	}
	
	public DistParamShadelet convPos( Object convPos )
	{
		setProperty( "convPos" , convPos );
		return this;
	}
	
	public DistParamShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public String out( )
	{
		return replaceProperties( "$out" );
	}
	
	public DistParamShadelet vDeclaration( Object vDeclaration )
	{
		setProperty( "vDeclaration" , vDeclaration );
		return this;
	}
	
	public DistParamShadelet mDeclaration( Object mDeclaration )
	{
		setProperty( "mDeclaration" , mDeclaration );
		return this;
	}
	
	public DistParamShadelet outDeclaration( Object outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public DistParamShadelet posDeclaration( Object posDeclaration )
	{
		setProperty( "posDeclaration" , posDeclaration );
		return this;
	}
	
	@Override
	public String getVertexShaderMainCode( )
	{
		return "  $out = -($v * $m * $convPos).z;";
	}
}
