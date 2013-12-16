package org.andork.jogl.shader;

public class MainCodeBlock extends ShaderSegment
{
	String	mainCode;
	
	public MainCodeBlock( String mainCode )
	{
		super( );
		this.mainCode = mainCode;
	}
	
	public String getMainCode( )
	{
		return mainCode;
	}
}
