/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl.basic.test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.BasicJOGLObject;
import org.andork.jogl.BasicJOGLScene;
import org.andork.jogl.BasicJOGLSetup;
import org.andork.jogl.BufferHelper;
import org.andork.jogl.JOGLDepthModifier;
import org.andork.jogl.BasicJOGLObject.PerVertexDiffuseVertexShader;
import org.andork.jogl.BasicJOGLObject.Uniform1fv;
import org.andork.jogl.BasicJOGLObject.Uniform1iv;
import org.andork.jogl.BasicJOGLObject.Uniform4fv;
import org.andork.jogl.BasicJOGLObject.VaryingColorFragmentShader;
import org.andork.jogl.util.NormalGenerator;
import org.andork.math3d.Primitives;

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
		obj.transpose( false );
		obj.add( new JOGLDepthModifier( ) );
		
		obj.vertexShaderCode( new PerVertexDiffuseVertexShader( ).toString( ) );
		obj.normalMatrixName( "n" );
		obj.add( obj.new Attribute3fv( ).name( "a_pos" ) );
		obj.add( obj.new Attribute3fv( ).name( "a_norm" ) );
		obj.add( new Uniform4fv( ).value( 1 , 0 , 0 , 1 ).name( "u_color" ) );
		obj.add( new Uniform1iv( ).value( 1 ).name( "u_nlights" ) );
		obj.add( new Uniform4fv( ).value( 1 , 1 , 1 , 0 ).name( "u_lightpos" ) );
		obj.add( new Uniform4fv( ).value( 1 , 0 , 1 , 1 ).name( "u_lightcolor" ) );
		obj.add( new Uniform1fv( ).value( 1 ).name( "u_constantAttenuation;" ) );
		obj.add( new Uniform1fv( ).value( 0 ).name( "u_linearAttenuation;" ) );
		obj.add( new Uniform1fv( ).value( 0 ).name( "u_quadraticAttenuation;" ) );
		obj.fragmentShaderCode( new VaryingColorFragmentShader( ).toString( ) );
		
		scene.add( obj );
		
		return scene;
	}
}
