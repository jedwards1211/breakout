package org.andork.torquescape.jogl;

import static org.andork.vecmath.Vecmath.invAffine;
import static org.andork.vecmath.Vecmath.lookAt;
import static org.andork.vecmath.Vecmath.mmul;
import static org.andork.vecmath.Vecmath.newMat4f;
import static org.andork.vecmath.Vecmath.normalize;
import static org.andork.vecmath.Vecmath.perspective;
import static org.andork.vecmath.Vecmath.transpose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.andork.torquescape.jogl.render.ISliceRenderer;
import org.andork.torquescape.jogl.render.ZoneRenderer;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DirectZoneGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.slice.RainbowSlice;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;
import org.andork.torquescape.model.vertex.IVertexAttrFn;
import org.andork.torquescape.model.vertex.IVertexVisitor;
import org.andork.torquescape.model.vertex.StandardVertexFn;

public class TorquescapeTestScene implements GLEventListener
{
	public final List<ZoneRenderer>	zones				= new ArrayList<ZoneRenderer>( );
	
	public float					tilt;
	public float					pan;
	
	float[ ]						cameraMatrix		= newMat4f( );
	
	float[ ]						modelMatrix			= newMat4f( );
	float[ ]						viewMatrix			= newMat4f( );
	float[ ]						projMatrix			= newMat4f( );
	float[ ]						panMatrix			= newMat4f( );
	float[ ]						tiltMatrix			= newMat4f( );
	float[ ]						modelViewMatrix		= newMat4f( );
	float[ ]						modelViewProjMatrix	= newMat4f( );
	float[ ]						identityMatrix		= newMat4f( );
	
	public void draw( GL2ES2 gl , float[ ] mvMatrix , float[ ] pMatrix )
	{
		// Redraw background color
		gl.glClear( GL2ES2.GL_COLOR_BUFFER_BIT | GL2ES2.GL_DEPTH_BUFFER_BIT );
		
		gl.glEnable( GL2ES2.GL_DEPTH_TEST );
		gl.glDepthFunc( GL2ES2.GL_LEQUAL );
		gl.glEnable( GL2ES2.GL_CULL_FACE );
		gl.glCullFace( GL2ES2.GL_BACK );
		
		invAffine( cameraMatrix , modelMatrix );
		mmul( viewMatrix , modelMatrix , modelViewMatrix );
		
		transpose( modelViewMatrix , modelViewMatrix );
		
		for( ZoneRenderer zone : zones )
		{
			for( ISliceRenderer<?> sliceRenderer : zone.sliceRenderers )
			{
				sliceRenderer.draw( gl , identityMatrix , mvMatrix , pMatrix );
			}
		}
	}
	
	@Override
	public void init( GLAutoDrawable drawable )
	{
		// Set the camera position (View matrix)
		lookAt( viewMatrix , 0 , 0 , 5 , 0f , 0f , 1f , 0f , 1.0f , 0.0f );
		
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		
		final Track track = new Track1( );
		
		IVertexAttrFn attrFn1 = new IVertexAttrFn( )
		{
			@Override
			public int getBytesPerVertex( )
			{
				return 4;
			}
			
			@Override
			public void eval( float param , int index , int vertexCount , float x , float y , float z , IVertexVisitor visitor )
			{
				visitor.visit( param );
			}
		};
		
		final float step = ( float ) Math.PI / 180;
		
		IVertexAttrFn attrFn2 = new IVertexAttrFn( )
		{
			float[ ]	prev	= new float[ 3 ];
			float[ ]	next	= new float[ 3 ];
			
			@Override
			public int getBytesPerVertex( )
			{
				return 24;
			}
			
			@Override
			public void eval( float param , int index , int vertexCount , float x , float y , float z , IVertexVisitor visitor )
			{
				track.getCoordFn( ).eval( param , index , prev );
				track.getCoordFn( ).eval( param + step , index , next );
				
				next[ 0 ] -= prev[ 0 ];
				next[ 1 ] -= prev[ 1 ];
				next[ 2 ] -= prev[ 2 ];
				
				normalize( next , 0 , 3 );
				
				visitor.visit( next[ 0 ] );
				visitor.visit( next[ 1 ] );
				visitor.visit( next[ 2 ] );
				
				if( ( index % 2 ) == 0 )
				{
					track.getCoordFn( ).eval( param , ( index + vertexCount - 1 ) % vertexCount , next );
				}
				else
				{
					track.getCoordFn( ).eval( param , ( index + 1 ) % vertexCount , next );
				}
				
				next[ 0 ] -= prev[ 0 ];
				next[ 1 ] -= prev[ 1 ];
				next[ 2 ] -= prev[ 2 ];
				
				normalize( next , 0 , 3 );
				
				visitor.visit( next[ 0 ] );
				visitor.visit( next[ 1 ] );
				visitor.visit( next[ 2 ] );
			}
		};
		
		StandardVertexFn vertexFn = new StandardVertexFn( track.getCoordFn( ) , attrFn1 , attrFn2 );
		
		Zone zone = new Zone( );
		
		int paramCount = ( int ) ( Math.PI * 4 / step );
		
		int vertexCount = paramCount * vertexFn.getVertexCount( 0 );
		
		int indexCount = track.getIndexFn( ).getIndexCount( 0 ) * paramCount;
		
		zone.init( vertexCount , vertexFn.getBytesPerVertex( ) , indexCount );
		
		Float[ ] params = new Float[ paramCount ];
		for( int i = 0 ; i < paramCount ; i++ )
		{
			params[ i ] = i * step;
		}
		
		DirectZoneGenerator zoneGen = DirectZoneGenerator.newInstance( );
		zoneGen.setZone( zone );
		zoneGen.generate( vertexFn , track.getIndexFn( ) , Arrays.asList( params ) );
		
		NormalGenerator.generateNormals( zone.getVertBuffer( ) , 12 , vertexFn.getBytesPerVertex( ) , zone.getIndexBuffer( ) , 0 , indexCount );
		
		zone.rebuildMaps( );
		
		// ColorWaveSlice slice = new ColorWaveSlice( );
		// slice.wavelength = 2f;
		// slice.velocity = 5f;
		// slice.setIndexBuffer( zone.getIndexCharBuffer( ) );
		// set( slice.ambientColor , 0.1f , 0 , 0 , 1 );
		// set( slice.ambientColor , 4 , 0.05f , 0 , 0 , 1 );
		// set( slice.diffuseColor , 1 , 0 , 0 , 1 );
		// set( slice.diffuseColor , 4 , 0.5f , 0 , 0 , 1 );
		// zone.addSlice( slice );
		
		RainbowSlice rainbowSlice = new RainbowSlice( );
		rainbowSlice.setIndexBuffer( zone.getIndexBuffer( ) );
		zone.addSlice( rainbowSlice );
		
		ZoneRenderer rend1 = new ZoneRenderer( zone );
		rend1.init( gl );
		
		zones.add( rend1 );
	}
	
	private void set( float[ ] array , float a , float b , float c , float d )
	{
		array[ 0 ] = a;
		array[ 1 ] = b;
		array[ 2 ] = c;
		array[ 3 ] = d;
	}
	
	private void set( float[ ] array , int index , float a , float b , float c , float d )
	{
		array[ index++ ] = a;
		array[ index++ ] = b;
		array[ index++ ] = c;
		array[ index++ ] = d;
	}
	
	@Override
	public void dispose( GLAutoDrawable drawable )
	{
		
	}
	
	@Override
	public void display( GLAutoDrawable drawable )
	{
		draw( ( GL2ES2 ) drawable.getGL( ) , modelViewMatrix , projMatrix );
	}
	
	@Override
	public void reshape( GLAutoDrawable drawable , int x , int y , int width , int height )
	{
		drawable.getGL( ).glViewport( 0 , 0 , width , height );
		
		float ratio = ( float ) width / height;
		
		perspective( projMatrix , ( float ) Math.PI / 2 , ratio , 0.01f , 10000 );
		projMatrix[ 8 ] = -projMatrix[ 8 ];
		projMatrix[ 9 ] = -projMatrix[ 9 ];
		projMatrix[ 10 ] = -projMatrix[ 10 ];
		projMatrix[ 11 ] = -projMatrix[ 11 ];
		transpose( projMatrix , projMatrix );
	}
}
