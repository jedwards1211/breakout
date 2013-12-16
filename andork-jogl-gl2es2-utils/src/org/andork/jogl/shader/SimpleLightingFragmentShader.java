package org.andork.jogl.shader;

public class SimpleLightingFragmentShader extends ShaderSegment
{
	public SimpleLightingFragmentShader( )
	{
		setProperty( "norm" , "v_norm" );
		setProperty( "color" , "color" );
		setProperty( "temp" , "slfs_temp" );
		setProperty( "ambientAmt" , "0.0" );
		setProperty( "out" , "gl_FragColor" );
	}
	
	public SimpleLightingFragmentShader norm( Object norm )
	{
		setProperty( "norm" , norm );
		return this;
	}
	
	public SimpleLightingFragmentShader ambientAmt( Object ambientAmt )
	{
		setProperty( "ambientAmt" , ambientAmt );
		return this;
	}
	
	public SimpleLightingFragmentShader temp( String temp )
	{
		setProperty( "temp" , temp );
		return this;
	}
	
	public SimpleLightingFragmentShader color( Object color )
	{
		setProperty( "color" , color );
		return this;
	}
	
	public SimpleLightingFragmentShader out( String out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public VariableDeclarations defaultVariableDecls( )
	{
		return new VariableDeclarations(
				replaceProperties( "varying vec3 $norm;" ) ,
				replaceProperties( "uniform vec4 $color;" ) );
	}
	
	public String getMainCode( )
	{
		return "  float $temp = dot($norm, vec3(0.0, 0.0, 1.0));" +
				"  $temp = $ambientAmt + $temp * (1.0 - $ambientAmt);" +
				"  $out = $temp * $color;";
	}
}
