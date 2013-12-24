package org.andork.jogl.shadelet;

public class NormalVertexShadelet extends Shadelet
{
	public NormalVertexShadelet( )
	{
		setProperty( "v" , "v" );
		setProperty( "n" , "n" );
		setProperty( "norm" , "a_norm" );
		setProperty( "out" , "v_norm" );
		setProperty( "nDeclaration" , "/* vertex */ uniform mat3 $n;" );
		setProperty( "vDeclaration" , "/* vertex */ uniform mat4 $v;" );
		setProperty( "normDeclaration" , "attribute vec3 $norm;" );
		setProperty( "outDeclaration" , "varying vec3 $out;" );
	}
	
	public String norm( )
	{
		return replaceProperties( "$norm" );
	}
	
	public NormalVertexShadelet norm( String norm )
	{
		setProperty( "norm" , norm );
		return this;
	}
	
	public NormalVertexShadelet v( Object v )
	{
		setProperty( "v" , v );
		return this;
	}
	
	public NormalVertexShadelet n( Object n )
	{
		setProperty( "n" , n );
		return this;
	}
	
	public NormalVertexShadelet out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public NormalVertexShadelet vDeclaration( String vDeclaration )
	{
		setProperty( "vDeclaration" , vDeclaration );
		return this;
	}
	
	public NormalVertexShadelet nDeclaration( String nDeclaration )
	{
		setProperty( "nDeclaration" , nDeclaration );
		return this;
	}
	
	public NormalVertexShadelet normDeclaration( String normDeclaration )
	{
		setProperty( "normDeclaration" , normDeclaration );
		return this;
	}
	
	public NormalVertexShadelet outDeclaration( String outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public String getVertexShaderMainCode( )
	{
		return "  $out = ($v * vec4(normalize($n * $norm), 0.0)).xyz;";
	}
}
