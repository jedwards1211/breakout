package org.andork.jogl.neu;


public interface JoglResourceManager
{
	void initLater( JoglResource resource );
	
	void disposeLater( JoglResource resource );
}
