package org.andork.jogl.util;

import javax.media.opengl.GL;

import org.andork.jogl.BasicJOGLObject;
import org.andork.jogl.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.BasicJOGLObject.FlatFragmentShader;
import org.andork.jogl.BufferHelper;
import org.andork.jogl.JOGLGroup;
import org.andork.math3d.PlanarHull3f;

public class DebugRenderers
{
	public static JOGLGroup render( PlanarHull3f hull , float[ ] outlineColor , float[ ] normalColor )
	{
		BasicJOGLObject bounds = new BasicJOGLObject( );
		BufferHelper bufferHelper = new BufferHelper( );
		for( float[ ] vertex : hull.vertices )
		{
			bufferHelper.put( vertex );
		}
		bounds.addVertexBuffer( bufferHelper.toByteBuffer( ) );
		BufferHelper indexBufferHelper = new BufferHelper( );
		indexBufferHelper.put( 0 , 1 , 0 , 2 , 1 , 3 , 2 , 3 , 4 , 5 , 4 , 6 , 5 , 7 , 6 , 7 , 0 , 4 , 1 , 5 , 2 , 6 , 3 , 7 );
		bounds.indexBuffer( indexBufferHelper.toByteBuffer( ) );
		bounds.vertexCount( 8 );
		bounds.indexCount( 24 );
		bounds.indexType( GL.GL_UNSIGNED_INT );
		bounds.drawMode( GL.GL_LINES );
		bounds.vertexShaderCode( new BasicVertexShader( ).toString( ) ).add( bounds.new Attribute3fv( ).name( "a_pos" ) );
		bounds.fragmentShaderCode( new FlatFragmentShader( ).color(
				outlineColor[ 0 ] , outlineColor[ 1 ] , outlineColor[ 2 ] , outlineColor[ 3 ] ).toString( ) );
		
		BasicJOGLObject normals = new BasicJOGLObject( );
		bufferHelper = new BufferHelper( );
		for( int side = 0 ; side < hull.origins.length ; side++ )
		{
			bufferHelper.put( hull.origins[ side ] );
			bufferHelper.put( hull.origins[ side ][ 0 ] + hull.normals[ side ][ 0 ] * 50 );
			bufferHelper.put( hull.origins[ side ][ 1 ] + hull.normals[ side ][ 1 ] * 50 );
			bufferHelper.put( hull.origins[ side ][ 2 ] + hull.normals[ side ][ 2 ] * 50 );
		}
		normals.addVertexBuffer( bufferHelper.toByteBuffer( ) );
		normals.vertexCount( 12 );
		normals.drawMode( GL.GL_LINES );
		normals.vertexShaderCode( new BasicVertexShader( ).toString( ) ).add( normals.new Attribute3fv( ).name( "a_pos" ) );
		normals.fragmentShaderCode( new FlatFragmentShader( ).color(
				normalColor[ 0 ] , normalColor[ 1 ] , normalColor[ 2 ] , normalColor[ 3 ] ).toString( ) );
		
		JOGLGroup result = new JOGLGroup( );
		result.objects.add( bounds );
		result.objects.add( normals );
		return result;
	}
}
