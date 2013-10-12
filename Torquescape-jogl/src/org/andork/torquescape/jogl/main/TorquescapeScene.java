package org.andork.torquescape.jogl.main;

import static org.andork.vecmath.Vecmath.cross;
import static org.andork.vecmath.Vecmath.invAffine;
import static org.andork.vecmath.Vecmath.setColumn3;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;

import org.andork.jogl.basic.BasicGL3Object;
import org.andork.jogl.basic.BasicGL3Object.PerVertexDiffuseVertexShader;
import org.andork.jogl.basic.BasicGL3Object.VaryingColorFragmentShader;
import org.andork.jogl.basic.BasicGL3Scene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.GL3DepthModifier;
import org.andork.jogl.basic.GL3Object;
import org.andork.jogl.basic.GL3XformGroup;
import org.andork.math.prim.Primitives;
import org.andork.torquescape.control.CameraMover;
import org.andork.torquescape.control.ControlState;
import org.andork.torquescape.control.Vehicle;
import org.andork.torquescape.control.VehicleMover;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.normal.NormalGenerator;

public class TorquescapeScene extends BasicGL3Scene
{
	public Vehicle			player;
	VehicleMover			vehicleMover	= new VehicleMover( );
	CameraMover				cameraMover;
	
	public ControlState		controlState;
	
	public final Set<Zone>	zones			= new HashSet<Zone>( );
	
	double[ ]				forward			= new double[ 3 ];
	double[ ]				right			= new double[ 3 ];
	
	GL3XformGroup			playerXformGroup;
	GL3Object				playerRenderer;
	
	long					lastTimestep;
	
	public TorquescapeScene( )
	{
		controlState = new ControlState( );
		
		player = new Vehicle( );
		
		cameraMover = new CameraMover( player , .25f , 10 );
		
		playerRenderer = createDefaultPlayerRenderer( );
		
		playerXformGroup = new GL3XformGroup( );
		playerXformGroup.objects.add( playerRenderer );
		
		add( playerXformGroup );
	}
	
	public static GL3Object createDefaultPlayerRenderer( )
	{
		int latDivs = 36;
		int longDivs = 36;
		
		float height = 0.05f;
		float width = 0.2f;
		float length = 0.5f;
		
		BufferHelper bh = new BufferHelper( );
		for( float[ ] point : Primitives.ellipsoid(
				new float[ ] { 0 , height / 2 , 0 } ,
				new float[ ] { 0 , height , 0 } ,
				new float[ ] { width , 0 , 0 } ,
				new float[ ] { 0 , 0 , length } ,
				latDivs , longDivs ) )
		{
			bh.put( point );
			bh.put( 0f , 0f , 0f );
		}
		
		ByteBuffer vertexBuffer = bh.toByteBuffer( );
		
		bh = new BufferHelper( );
		for( int[ ] indices : Primitives.ellipsoidIndices( latDivs , longDivs ) )
		{
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
		obj.add( obj.new Uniform4fv( ).value( 1 , -1 , 1 , 0 ).name( "u_lightpos" ) );
		obj.add( obj.new Uniform4fv( ).value( 1 , 0 , 1 , 1 ).name( "u_lightcolor" ) );
		obj.add( obj.new Uniform1fv( ).value( 1 ).name( "u_constantAttenuation;" ) );
		obj.add( obj.new Uniform1fv( ).value( 0 ).name( "u_linearAttenuation;" ) );
		obj.add( obj.new Uniform1fv( ).value( 0 ).name( "u_quadraticAttenuation;" ) );
		obj.fragmentShaderCode( new VaryingColorFragmentShader( ).toString( ) );
		
		return obj;
	}
	
	@Override
	public void display( GLAutoDrawable drawable )
	{
		long time = System.nanoTime( );
		if( lastTimestep != 0 )
		{
			float timestep = ( time - lastTimestep ) / 1e9f;
			
			float forwardTime, reverseTime, leftTime, rightTime;
			
			synchronized( controlState )
			{
				controlState.update( time );
				forwardTime = controlState.acceleration;
				reverseTime = controlState.braking;
				leftTime = controlState.leftTurning;
				rightTime = controlState.rightTurning;
				controlState.clear( );
			}
			
			player.updateVelocity( timestep , forwardTime , reverseTime , leftTime , rightTime );
			
			vehicleMover.move( player , timestep );
		}
		
		cameraMover.updateXform( );
		
		invAffine( cameraMover.xform , v );
		
		lastTimestep = time;
		
		if( player != null )
		{
			cross( player.modelForward , player.modelUp , right );
			cross( player.modelUp , right , forward );
			setColumn3( playerXformGroup.xform , 0 , right );
			setColumn3( playerXformGroup.xform , 1 , player.modelUp );
			setColumn3( playerXformGroup.xform , 2 , forward );
			setColumn3( playerXformGroup.xform , 3 , player.location );
		}
		
		GL3 gl = ( GL3 ) drawable.getGL( );
		
		gl.glEnable( GL3.GL_DEPTH_TEST );
		gl.glDepthFunc( GL3.GL_LEQUAL );
		gl.glEnable( GL3.GL_CULL_FACE );
		gl.glCullFace( GL3.GL_BACK );
		
		super.display( drawable );
	}
}
