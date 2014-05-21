package org.andork.jogl.neu;

public interface JoglDrawContext
{
	public float[ ] normalXform( );
	
	public float[ ] viewXform( );
	
	public float[ ] projXform( );
	
	public float[ ] screenXform( );
	
	public float[ ] pixelScale( );
}
