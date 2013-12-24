package org.andork.jogl.shadelet;

public class GradientShadelet extends Shadelet
{
	public GradientShadelet( )
	{
		setProperty( "loValue" , "loValue" );
		setProperty( "hiValue" , "hiValue" );
		setProperty( "loColor" , "loColor" );
		setProperty( "hiColor" , "hiColor" );
		setProperty( "param" , "param" );
		setProperty( "temp" , "temp" );
		setProperty( "out" , "gl_FragColor" );
		setProperty( "loValueDeclaration" , "/* fragment */ uniform float $loValue;" );
		setProperty( "hiValueDeclaration" , "/* fragment */ uniform float $hiValue;" );
		setProperty( "loColorDeclaration" , "/* fragment */ uniform vec4 $loColor;" );
		setProperty( "hiColorDeclaration" , "/* fragment */ uniform vec4 $hiColor;" );
		setProperty( "paramDeclaration" , "varying float $param;" );
		setProperty( "tempDeclaration" , "/* fragment */ float $temp;" );
	}
	
	public String loValue( )
	{
		return replaceProperties( "$loValue" );
	}
	
	public String hiValue( )
	{
		return replaceProperties( "$hiValue" );
	}
	
	public String loColor( )
	{
		return replaceProperties( "$loColor" );
	}
	
	public String hiColor( )
	{
		return replaceProperties( "$hiColor" );
	}
	
	public GradientShadelet loValue( String loValue )
	{
		setProperty( "loValue" , loValue );
		return this;
	}
	
	public GradientShadelet hiValue( String hiValue )
	{
		setProperty( "hiValue" , hiValue );
		return this;
	}
	
	public GradientShadelet loColor( Object loColor )
	{
		setProperty( "loColor" , loColor );
		return this;
	}
	
	public GradientShadelet hiColor( Object hiColor )
	{
		setProperty( "hiColor" , hiColor );
		return this;
	}
	
	public GradientShadelet param( Object param )
	{
		setProperty( "param" , param );
		return this;
	}
	
	public GradientShadelet temp( String temp )
	{
		setProperty( "temp" , temp );
		return this;
	}
	
	public GradientShadelet out( String out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public GradientShadelet loValueDeclaration( String loValueDeclaration )
	{
		setProperty( "loValueDeclaration" , loValueDeclaration );
		return this;
	}
	
	public GradientShadelet hiValueDeclaration( String hiValueDeclaration )
	{
		setProperty( "hiValueDeclaration" , hiValueDeclaration );
		return this;
	}
	
	public GradientShadelet loColorDeclaration( Object loColorDeclaration )
	{
		setProperty( "loColorDeclaration" , loColorDeclaration );
		return this;
	}
	
	public GradientShadelet hiColorDeclaration( Object hiColorDeclaration )
	{
		setProperty( "hiColorDeclaration" , hiColorDeclaration );
		return this;
	}
	
	public GradientShadelet paramDeclaration( Object paramDeclaration )
	{
		setProperty( "paramDeclaration" , paramDeclaration );
		return this;
	}
	
	public GradientShadelet tempDeclaration( String tempDeclaration )
	{
		setProperty( "tempDeclaration" , tempDeclaration );
		return this;
	}
	
	public GradientShadelet outDeclaration( String outDeclaration )
	{
		setProperty( "outDeclaration" , outDeclaration );
		return this;
	}
	
	public String getFragmentShaderMainCode( )
	{
		return "  if ($param > $hiValue) { $temp = 1.0; }" +
				"  else if ($param < $loValue) { $temp = 0.0; }" +
				"  else { $temp = ($param - $loValue) / ($hiValue - $loValue); }" +
				"  $out = mix($loColor, $hiColor, $temp);";
	}
}
