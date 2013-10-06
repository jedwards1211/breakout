package org.andork.jogl.basic.test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.media.opengl.GL3;

import org.andork.jogl.basic.BasicGL3Frame;
import org.andork.jogl.basic.BasicGL3Object;
import org.andork.jogl.basic.BasicGL3Object.FlatFragmentShader;
import org.andork.jogl.basic.BasicGL3Scene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.GL3DepthModifier;
import org.andork.jogl.basic.GL3XformGroup;
import org.andork.jogl.prim.Primitives;

import static org.andork.jogl.basic.BasicGL3Object.*;

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
			bh.put( 0f , 0f , 0f );
		}
		
		ByteBuffer vertexBuffer = bh.toByteBuffer( );
		
		bh = new BufferHelper( );
		for( int[ ] indices : Primitives.ellipsoidIndices( latDivs , longDivs ) )
		{
			System.out.println( Arrays.toString( indices ) );
			bh.put( indices );
		}
		ByteBuffer indexBuffer = bh.toByteBuffer( );
		
		NormalGenerator.generateNormals3fi( vertexBuffer , 12 , 24 , indexBuffer , 0 , indexBuffer.capacity( ) / 4 );
		
		BasicGL3Object obj = new BasicGL3Object( );
		obj.addVertexBuffer( vertexBuffer ).vertexCount( vertexBuffer.capacity( ) / 4 );
		obj.indexBuffer( indexBuffer ).indexCount( indexBuffer.capacity( ) / 4 );
		obj.indexType( GL3.GL_UNSIGNED_INT );
		obj.drawMode( GL3.GL_TRIANGLES );
		obj.transpose( true );
		obj.add( new GL3DepthModifier( ) );
		obj.debug( true );
		
		obj.vertexShaderCode( new PerVertexDiffuseVertexShader( ).toString( ) );
		obj.normalMatrixName( "n" );
		obj.add( obj.new Attribute3fv( ).name( "a_pos" ) );
		obj.add( obj.new Attribute3fv( ).name( "a_norm" ) );
		obj.add( obj.new Uniform4fv( ).value( 1 , 0 , 0 , 1 ).name( "u_color" ) );
		obj.add( obj.new Uniform1iv( ).value( 1 ).name( "u_nlights" ) );
		obj.add( obj.new Uniform4fv( ).value( 1 , 1 , 1 , 0 ).name( "u_lightpos" ) );
		obj.add( obj.new Uniform4fv( ).value( 1 , 0 , 1 , 1 ).name( "u_lightcolor" ) );
		obj.add( obj.new Uniform1fv( ).value( 1 ).name( "u_constantAttenuation;" ) );
		obj.add( obj.new Uniform1fv( ).value( 0 ).name( "u_linearAttenuation;" ) );
		obj.add( obj.new Uniform1fv( ).value( 0 ).name( "u_quadraticAttenuation;" ) );
		obj.fragmentShaderCode( new VaryingColorFragmentShader( ).toString( ) );
		
		scene.add( obj );
		
		return scene;
	}
}
