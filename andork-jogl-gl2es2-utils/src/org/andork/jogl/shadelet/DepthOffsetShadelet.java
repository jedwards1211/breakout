package org.andork.jogl.shadelet;

public class DepthOffsetShadelet extends Shadelet
{
	public DepthOffsetShadelet( )
	{
		setProperty( "offset" , "0.0" );
	}
	
	public DepthOffsetShadelet offset( double offset )
	{
		setProperty( "offset" , Double.toString( offset ) );
		return this;
	}
	
	public String getVertexShaderMainCode( )
	{
		return "  gl_Position.z += $offset;";
	}
}
