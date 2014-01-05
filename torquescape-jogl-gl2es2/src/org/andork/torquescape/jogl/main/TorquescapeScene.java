package org.andork.torquescape.jogl.main;

import static org.andork.math3d.Vecmath.cross;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.setColumn3;
import static org.andork.math3d.Vecmath.setf;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;

import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.PerVertexDiffuseVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.Uniform1fv;
import org.andork.jogl.basic.BasicJOGLObject.Uniform1iv;
import org.andork.jogl.basic.BasicJOGLObject.Uniform4fv;
import org.andork.jogl.basic.BasicJOGLObject.VaryingColorFragmentShader;
import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.jogl.basic.JOGLObject;
import org.andork.jogl.basic.JOGLXformGroup;
import org.andork.math3d.Primitives;
import org.andork.math3d.Vecmath;
import org.andork.torquescape.control.CameraMover;
import org.andork.torquescape.control.ControlState;
import org.andork.torquescape.control.Vehicle;
import org.andork.torquescape.control.VehicleMover;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.normal.NormalGenerator;

public class TorquescapeScene extends BasicJOGLScene
{
	public Vehicle			player;
	VehicleMover			vehicleMover	= new VehicleMover( );
	CameraMover				cameraMover;
	
	public ControlState		controlState;
	
	public final Set<Zone>	zones			= new HashSet<Zone>( );
	
	double[ ]				forward			= new double[ 3 ];
	double[ ]				right			= new double[ 3 ];
	
	JOGLXformGroup			playerXformGroup;
	JOGLObject				playerRenderer;
	
	long					lastTimestep;
	
	public TorquescapeScene( )
	{
		controlState = new ControlState( );
		
		player = new Vehicle( );
		
		cameraMover = new CameraMover( player , .25f , 10 );
		
		playerRenderer = createDefaultPlayerRenderer( );
		
		playerXformGroup = new JOGLXformGroup( );
		playerXformGroup.objects.add( playerRenderer );
		
		add( playerXformGroup );
	}
	
	public static JOGLObject createDefaultPlayerRenderer( )
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
		obj.add( new Uniform4fv( ).value( 1 , -1 , 1 , 0 ).name( "u_lightpos" ) );
		obj.add( new Uniform4fv( ).value( 1 , 0 , 1 , 1 ).name( "u_lightcolor" ) );
		obj.add( new Uniform1fv( ).value( 1 ).name( "u_constantAttenuation;" ) );
		obj.add( new Uniform1fv( ).value( 0 ).name( "u_linearAttenuation;" ) );
		obj.add( new Uniform1fv( ).value( 0 ).name( "u_quadraticAttenuation;" ) );
		obj.fragmentShaderCode( new VaryingColorFragmentShader( ).toString( ) );
		
		return obj;
	}
	
	long lastPrintTime = 0;
	
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
		
		if (time - lastPrintTime > 1000000000) {
			lastPrintTime = time;
			float[] pvm = new float[16];
			Vecmath.mmul( v , m , pvm );
			Vecmath.mmul( p , pvm , pvm );
			float[] f = new float[3];
			
			System.out.println();
			setf(f, -10, 0, -10);
			Vecmath.mpmul( pvm , f );
			System.out.println(Arrays.toString( f ));
			setf(f, -10, 0, 10);
			Vecmath.mpmul( pvm , f );
			System.out.println(Arrays.toString( f ));
			setf(f, 10, 0, 10);
			Vecmath.mpmul( pvm , f );
			System.out.println(Arrays.toString( f ));
			setf(f, 10, 0, -10);
			Vecmath.mpmul( pvm , f );
			System.out.println(Arrays.toString( f ));
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
		
		GL2ES2 gl = ( GL2ES2 ) drawable.getGL( );
		
		gl.glEnable( GL2ES2.GL_DEPTH_TEST );
		gl.glDepthFunc( GL2ES2.GL_LEQUAL );
		gl.glEnable( GL2ES2.GL_CULL_FACE );
		gl.glCullFace( GL2ES2.GL_BACK );
		
		super.display( drawable );
	}
}
