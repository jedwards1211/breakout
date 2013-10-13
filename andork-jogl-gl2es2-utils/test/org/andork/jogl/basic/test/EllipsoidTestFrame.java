package org.andork.jogl.basic.test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL2ES2;

import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.PerVertexDiffuseVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.VaryingColorFragmentShader;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.BasicJOGLSetup;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.jogl.util.NormalGenerator;
import org.andork.math.prim.Primitives;

public class EllipsoidTestFrame extends BasicJOGLSetup
{
	public static void main( String[ ] args )
	{
		EllipsoidTestFrame frame = new EllipsoidTestFrame( );
		frame.glWindow.setVisible( true );
		frame.waitUntilClosed( );
	}
	
	@Override
	protected BasicJOGLScene createScene( )
	{
		BasicJOGLScene scene = new BasicJOGLScene( );
		
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
		
		BasicJOGLObject obj = new BasicJOGLObject( );
		obj.addVertexBuffer( vertexBuffer ).vertexCount( vertexBuffer.capacity( ) / 4 );
		obj.indexBuffer( indexBuffer ).indexCount( indexBuffer.capacity( ) / 4 );
		obj.indexType( GL2ES2.GL_UNSIGNED_INT );
		obj.drawMode( GL2ES2.GL_TRIANGLES );
		obj.transpose( true );
		obj.add( new JOGLDepthModifier( ) );
		
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
