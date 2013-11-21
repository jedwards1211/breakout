package org.andork.jogl.shader;


public class VariableSetter extends ShaderSegment
{
	public VariableSetter dest( String variableName )
	{
		setProperty( "dest" , variableName );
		return this;
	}
	
	public VariableSetter value(Object value) {
		setProperty( "value" , value );
		return this;
	}
	
	@Override
	public String getMainCode( )
	{
		return "$dest = $src;";
	}
}
