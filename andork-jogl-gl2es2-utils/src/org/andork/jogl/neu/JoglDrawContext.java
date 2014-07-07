package org.andork.jogl.neu;

public interface JoglDrawContext
{
	public int getWidth( );
	
	public int getHeight( );
	
	public float[ ] normalXform( );
	
	public float[ ] viewXform( );
	
	public float[ ] inverseViewXform( );
	
	public float[ ] projXform( );
	
	public float[ ] screenXform( );
	
	public float[ ] pixelScale( );
}
