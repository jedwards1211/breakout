package org.andork.jogl.basic.test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.media.opengl.GL3;

import org.andork.jogl.basic.BasicGL3Frame;
import org.andork.jogl.basic.BasicGL3Object;
import org.andork.jogl.basic.BasicGL3Object.BasicVertexShader;
import org.andork.jogl.basic.BasicGL3Object.FlatFragmentShader;
import org.andork.jogl.basic.BasicGL3Scene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.GL3DepthModifier;
import org.andork.jogl.prim.Primitives;

public class EllipsoidTestFrame extends BasicGL3Frame
{
	
	public static void main( String[ ] args )
	{
		EllipsoidTestFrame frame = new EllipsoidTestFrame( );
		frame.setVisible( true );
	}
	
	@Override
	protected BasicGL3Scene createScene( )
	{
		BasicGL3Scene scene = new BasicGL3Scene( );
		
		int latDivs = 36;
		int longDivs = 36;
		
		BufferHelper bh = new BufferHelper( );
		for( float[ ] point : Primitives.ellipsoid(
				new float[ ] { 0 , 0 , 0 } ,
				new float[ ] { 0 , 1 , 0 } ,
				new float[ ] { 2 , 0 , 0 } ,
				new float[ ] { 0 , 0 , 3 } ,
				latDivs , longDivs ) )
		{
			System.out.println( Arrays.toString( point ) );
			bh.put( point );
		}
		
		BasicGL3Object obj = new BasicGL3Object( );
		ByteBuffer vertexBuffer = bh.toByteBuffer( );
		obj.addVertexBuffer( vertexBuffer ).vertexCount( vertexBuffer.capacity( ) / 4 );
		
		bh = new BufferHelper( );
		for( int[ ] indices : Primitives.ellipsoidIndices( latDivs , longDivs ) )
		{
			System.out.println( Arrays.toString( indices ) );
			bh.put( indices );
		}
		ByteBuffer indexBuffer = bh.toByteBuffer( );
		obj.indexBuffer( indexBuffer ).indexCount( indexBuffer.capacity( ) / 4 );
		obj.indexType( GL3.GL_UNSIGNED_INT );
		obj.drawMode( GL3.GL_LINES );
		
		obj.vertexShaderCode( new BasicVertexShader( ).toString( ) );
		obj.add( obj.new AttributeVec3fv( ).name( "a_pos" ) );
		obj.fragmentShaderCode( new FlatFragmentShader( ).toString( ) );
		obj.transpose( true );
		obj.add( new GL3DepthModifier( ) );
		obj.debug( true );
		
		scene.add( obj );
		
		return scene;
	}
}
