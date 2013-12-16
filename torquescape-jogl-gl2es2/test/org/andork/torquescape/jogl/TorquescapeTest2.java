package org.andork.torquescape.jogl;

import static org.andork.math3d.Vecmath.mpmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.setd;

import org.andork.torquescape.jogl.main.TorquescapeScene;
import org.andork.torquescape.jogl.main.TorquescapeSetup;
import org.andork.torquescape.jogl.render.ZoneRenderer;
import org.andork.torquescape.model.Zone;

@SuppressWarnings( "serial" )
public class TorquescapeTest2 extends TorquescapeSetup
{
	public static void main( String[ ] args )
	{
		TorquescapeTest2 test = new TorquescapeTest2( );
		test.glWindow.setVisible( true );
		test.waitUntilClosed( );
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
		
		setd( scene.player.modelForward , scene.player.basisForward );
		setd( scene.player.modelUp , scene.player.basisUp );
		
		return scene;
	}
}
