package org.andork.jogl.shadelet;

public class LinearColorBlendShadelet extends Shadelet
{
	public LinearColorBlendShadelet( )
	{
		setProperty( "temp" , "temp" );
		setProperty( "numColors" , "numColors" );
		setProperty( "slopes" , "slopes" );
		setProperty( "intercepts" , "intercepts" );
		setProperty( "colors" , "colors" );
		setProperty( "param" , "param" );
		setProperty( "out" , "gl_FragColor" );
		setProperty( "slopesDeclaration" , "/* fragment */ uniform float $slopes[$numColors];" );
		setProperty( "interceptsDeclaration" , "/* fragment */ uniform float $intercepts[$numColors];" );
		setProperty( "colorsDeclaration" , "/* fragment */ uniform vec4 $colors[$numColors];" );
		setProperty( "tempDeclaration" , "/* fragment */ float $temp;" );
		setProperty( "paramDeclaration" , "varying float $param;" );
	}
	
	public String slopes( )
	{
		return replaceProperties( "$slopes" );
	}
	
	public String intercepts( )
	{
		return replaceProperties( "$intercepts" );
	}
	
	public String colors( )
	{
		return replaceProperties( "$colors" );
	}
	
	public LinearColorBlendShadelet slopes( Object slopes )
	{
		setProperty( "slopes" , slopes );
		return this;
	}
	
	public LinearColorBlendShadelet intercepts( Object intercepts )
	{
		setProperty( "intercepts" , intercepts );
		return this;
	}
	
	public LinearColorBlendShadelet colors( Object colors )
	{
		setProperty( "colors" , colors );
		return this;
	}
	
	public LinearColorBlendShadelet param( Object param )
	{
		setProperty( "param" , param );
		return this;
	}
	
	public LinearColorBlendShadelet out( String out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public LinearColorBlendShadelet temp( String temp )
	{
		setProperty( "temp" , temp );
		return this;
	}
	
	public LinearColorBlendShadelet slopesDeclaration( String slopesDeclaration )
	{
		setProperty( "slopesDeclaration" , slopesDeclaration );
		return this;
	}
	
	public LinearColorBlendShadelet interceptsDeclaration( String interceptsDeclaration )
	{
		setProperty( "interceptsDeclaration" , interceptsDeclaration );
		return this;
	}
	
	public LinearColorBlendShadelet colorsDeclaration( Object colorsDeclaration )
	{
		setProperty( "colorsDeclaration" , colorsDeclaration );
		return this;
	}
	
	public LinearColorBlendShadelet paramDeclaration( Object paramDeclaration )
	{
		setProperty( "paramDeclaration" , paramDeclaration );
		return this;
	}
	
	public LinearColorBlendShadelet outDeclaration( String outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public LinearColorBlendShadelet tempDeclaration( String tempDeclaration )
	{
		setProperty( "tempDeclaration" , tempDeclaration );
		return this;
	}
	
	public String getFragmentShaderMainCode( )
	{
		return "  for (int $temp = 0; $temp < $numColors; $temp++) {" +
				"    $out += $colors[$temp] * clamp($slopes[$temp] * $param + $intercepts[$temp], 0.0, 1.0);" +
				"  }";
	}
}
