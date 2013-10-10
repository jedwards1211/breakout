package org.andork.torquescape.jogl.render;

import static org.andork.vecmath.Vecmath.mpmulAffine;
import static org.andork.vecmath.Vecmath.mvmulAffine;
import static org.andork.vecmath.Vecmath.normalize3;
import static org.andork.vecmath.Vecmath.set;

import org.andork.torquescape.jogl.RainbowSliceRendererTest;
import org.andork.torquescape.jogl.TorquescapeScene;
import org.andork.torquescape.jogl.main.TorquescapeFrame;
import org.andork.torquescape.model.Zone;

@SuppressWarnings( "serial" )
public class TorquescapeTest2 extends TorquescapeFrame
{
	public static void main( String[ ] args )
	{
		TorquescapeTest2 test = new TorquescapeTest2( );
		test.setVisible( true );
	}
	
	@Override
	protected TorquescapeScene createScene( )
	{
		TorquescapeScene scene = super.createScene( );
		ZoneRenderer rainbowZoneRenderer = RainbowSliceRendererTest.createTestRainbowZone( );
		scene.add( rainbowZoneRenderer );
		
		Zone rainbowZone = rainbowZoneRenderer.zone;
		int i0 = rainbowZone.getIndexBuffer( ).get( 0 ) * rainbowZone.getBytesPerVertex( );
		int i1 = rainbowZone.getIndexBuffer( ).get( 1 ) * rainbowZone.getBytesPerVertex( );
		int i2 = rainbowZone.getIndexBuffer( ).get( 2 ) * rainbowZone.getBytesPerVertex( );
		
		scene.player.currentZone = rainbowZone;
		scene.player.indexInZone = 0;
		scene.player.basis.set( rainbowZone.getVertBuffer( ) , i0 , i1 , i2 );
		
		mpmulAffine( scene.player.basis.getUVNToXYZDirect( ) , 0.25 , 0.25 , 0 , scene.player.location );
		mvmulAffine( scene.player.basis.getEFGToXYZDirect( ) , 1 , 0 , 0 , scene.player.basisForward );
		mvmulAffine( scene.player.basis.getEFGToXYZDirect( ) , 0 , 0 , 1 , scene.player.basisUp );
		
		normalize3( scene.player.basisForward );
		normalize3( scene.player.modelForward );
		
		set( scene.player.modelForward , scene.player.basisForward );
		set( scene.player.modelUp , scene.player.basisUp );
		
		return scene;
	}
}
