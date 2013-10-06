package org.andork.torquescape.jogl;

import org.andork.jogl.basic.BasicGL3Frame;
import org.andork.jogl.basic.BasicGL3Scene;

public class TorquescapeTest2 extends BasicGL3Frame
{
	public static void main( String[ ] args )
	{
		TorquescapeTest2 test = new TorquescapeTest2( );
		test.setVisible( true );
	}
	
	@Override
	protected BasicGL3Scene createScene( )
	{
		TorquescapeScene scene = new TorquescapeScene( );
		return scene;
	}
}
