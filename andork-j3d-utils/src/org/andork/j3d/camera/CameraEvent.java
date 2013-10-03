/**
 * 
 */

package org.andork.j3d.camera;

import java.util.EventObject;

/**
 * @author brian.kamery
 * 
 */
@SuppressWarnings( "serial" )
public class CameraEvent extends EventObject
{
	public CameraEvent( Camera3D source )
	{
		super( source );
	}
	
	public Camera3D getCamera( )
	{
		return ( Camera3D ) super.getSource( );
	}
}
