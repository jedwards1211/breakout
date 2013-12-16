package org.andork.jogl.shader;

public class DefaultNormalVertexShader extends ShaderSegment
{
	public DefaultNormalVertexShader( )
	{
		setProperty( "v" , "v" );
		setProperty( "n" , "n" );
		setProperty( "norm" , "a_norm" );
		setProperty( "out" , "v_norm" );
	}
	
	public DefaultNormalVertexShader norm( String norm )
	{
		setProperty( "norm" , norm );
		return this;
	}
	
	public DefaultNormalVertexShader v( Object v )
	{
		setProperty( "v" , v );
		return this;
	}
	
	public DefaultNormalVertexShader n( Object n )
	{
		setProperty( "n" , n );
		return this;
	}
	
	public DefaultNormalVertexShader out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public VariableDeclarations defaultVariableDecls( )
	{
		return new VariableDeclarations(
				replaceProperties( "uniform mat3 $n;" ) ,
				replaceProperties( "uniform mat4 $v;" ) ,
				replaceProperties( "attribute vec3 $norm;" ) ,
				replaceProperties( "varying vec3 $out;" ) );
	}
	
	public String getMainCode( )
	{
		return "  $out = ($v * vec4(normalize($n * $norm), 0.0)).xyz;";
	}
}
