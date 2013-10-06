package org.andork.torquescape.jogl;

import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.andork.jogl.basic.BasicGL3Object;
import org.andork.jogl.basic.BasicGL3Scene;
import org.andork.jogl.basic.GL3XformGroup;
import org.andork.torquescape.control.Vehicle;
import org.andork.torquescape.model.Zone;

import static org.andork.vecmath.FloatArrayVecmath.*;

public class TorquescapeScene extends BasicGL3Scene
{
	Vehicle			player;
	
	Set<Zone>		zones;
	
	float[ ]		right	= new float[ 3 ];
	
	GL3XformGroup	playerXformGroup;
	BasicGL3Object	playerRenderer;
	
	public TorquescapeScene( )
	{
		playerXformGroup = new GL3XformGroup( );
		playerXformGroup.objects.add( playerRenderer );
	}
	
	@Override
	public void display( GLAutoDrawable drawable )
	{
		cross( player.modelForward , player.modelUp , right );
		setColumn3( playerXformGroup.xform , 0 , right );
		setColumn3( playerXformGroup.xform , 1 , player.modelUp );
		setColumn3( playerXformGroup.xform , 2 , player.modelForward );
		setColumn3( playerXformGroup.xform , 3 , player.location );
		
		super.display( drawable );
	}
}
