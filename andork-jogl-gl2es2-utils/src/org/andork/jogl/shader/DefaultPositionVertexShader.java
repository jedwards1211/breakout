package org.andork.jogl.shader;


public class DefaultPositionVertexShader extends ShaderSegment
{
	public DefaultPositionVertexShader( )
	{
		setProperty( "p" , "p" );
		setProperty( "v" , "v" );
		setProperty( "m" , "m" );
		setProperty( "pos" , "a_pos" );
		setProperty( "out" , "gl_Position" );
	}
	
	public DefaultPositionVertexShader proj( String proj )
	{
		setProperty( "p" , proj );
		return this;
	}
	
	public DefaultPositionVertexShader view( String view )
	{
		setProperty( "p" , view );
		return this;
	}
	
	public DefaultPositionVertexShader model( String model )
	{
		setProperty( "m" , model );
		return this;
	}
	
	public DefaultPositionVertexShader pos( Object pos )
	{
		setProperty( "pos" , pos );
		return this;
	}
	
	public DefaultPositionVertexShader out( Object out )
	{
		setProperty( "out" , out );
		return this;
	}
	
	public VariableDeclarations defaultVariableDecls( )
	{
		return new VariableDeclarations(
				replaceProperties( "uniform mat4 $p;" ) ,
				replaceProperties( "uniform mat4 $v;" ) ,
				replaceProperties( "uniform mat4 $m;" ) ,
				replaceProperties( "attribute vec3 $pos;" ) );
	}
	
	public String getMainCode( )
	{
		return "  $out = $p * $v * $m * vec4($pos, 1.0);";
	}
}
