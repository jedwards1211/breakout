package org.andork.jogl.shader;

import java.util.Arrays;
import java.util.List;

public class VariableDeclarations extends ShaderSegment
{
	public List<String>	variableDeclarations;
	
	public VariableDeclarations( List<String> variableDeclarations )
	{
		super( );
		this.variableDeclarations = variableDeclarations;
	}
	
	public VariableDeclarations( String ... variableDeclarations )
	{
		this( Arrays.asList( variableDeclarations ) );
	}

	@Override
	public List<String> getVariableDecls( )
	{
		return variableDeclarations;
	}
}
