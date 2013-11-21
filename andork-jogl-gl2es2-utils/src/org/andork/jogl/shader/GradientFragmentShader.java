package org.andork.jogl.shader;

public class GradientFragmentShader extends ShaderSegment
{
	public GradientFragmentShader( )
	{
		setProperty( "loValue" , "loValue" );
		setProperty( "hiValue" , "hiValue" );
		setProperty( "loColor" , "loColor" );
		setProperty( "hiColor" , "hiColor" );
		setProperty( "in" , "in" );
		setProperty( "temp" , "f" );
		setProperty( "out" , "gl_FragColor" );
	}
	
	public GradientFragmentShader loValue( String loValue )
	{
		setProperty( "loValue" , loValue );
		return this;
	}
	
	public GradientFragmentShader hiValue( String hiValue )
	{
		setProperty( "hiValue" , hiValue );
		return this;
	}
	
	public GradientFragmentShader loColor( Object loColor )
	{
		setProperty( "loColor" , loColor );
		return this;
	}
	
	public GradientFragmentShader hiColor( Object hiColor )
	{
		setProperty( "hiColor" , hiColor );
		return this;
	}
	
	public GradientFragmentShader in( Object in )
	{
		setProperty( "in" , in );
		return this;
	}
	
	public GradientFragmentShader temp( String temp )
	{
		setProperty( "temp" , temp );
		return this;
	}
	
	public GradientFragmentShader out( String out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public VariableDeclarations defaultVariableDecls( )
	{
		return new VariableDeclarations(
				replaceProperties( "uniform float $loValue;" ) ,
				replaceProperties( "uniform float $hiValue;" ) ,
				replaceProperties( "uniform vec4 $loColor;" ) ,
				replaceProperties( "uniform vec4 $hiColor;" ) );
	}
	
	public String getMainCode( )
	{
		return "  float $temp;" +
				"  if ($in > $hiValue) { $temp = 1.0; }" +
				"  else if ($in < $loValue) { $temp = 0.0; }" +
				"  else { $temp = ($in - $loValue) / ($hiValue - $loValue); }" +
				"  $out = mix($loColor, $hiColor, $temp);";
	}
}
