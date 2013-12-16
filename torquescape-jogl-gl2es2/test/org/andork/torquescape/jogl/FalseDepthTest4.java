package org.andork.torquescape.jogl;

import static org.andork.math3d.Vecmath.mpmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.setd;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.JOGLBlendModifier;
import org.andork.jogl.basic.JOGLPolygonModeModifier;
import org.andork.torquescape.jogl.main.TorquescapeScene;
import org.andork.torquescape.jogl.main.TorquescapeSetup;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.gen.DirectZoneGenerator;
import org.andork.torquescape.model.normal.NormalGenerator;
import org.andork.torquescape.model.track.Track;
import org.andork.torquescape.model.track.Track1;
import org.andork.torquescape.model.vertex.IVertexAttrFn;
import org.andork.torquescape.model.vertex.IVertexVisitor;
import org.andork.torquescape.model.vertex.StandardVertexFn;

public class FalseDepthTest4 extends TorquescapeSetup
{
	public static void main( String[ ] args )
	{
		FalseDepthTest4 test = new FalseDepthTest4( );
		test.glWindow.setVisible( true );
		test.waitUntilClosed( );
	}
	
	@Override
	protected TorquescapeScene createScene( )
	{
		TorquescapeScene scene = super.createScene( );
		
		// BufferHelper bh = new BufferHelper( );
		// bh.put( -10f , 0f , -10f ).put( 0f , 1f , 0f ).put( -10f , -10f ).put( 1f , 0f , 0f ).put( 0f , 0f , 1f );
		// bh.put( 10f , 0f , -10f ).put( 0f , 1f , 0f ).put( 10f , -10f ).put( 1f , 0f , 0f ).put( 0f , 0f , 1f );
		// bh.put( 10f , 0f , 10f ).put( 0f , 1f , 0f ).put( 10f , 10f ).put( 1f , 0f , 0f ).put( 0f , 0f , 1f );
		// bh.put( -10f , 0f , 10f ).put( 0f , 1f , 0f ).put( -10f , 10f ).put( 1f , 0f , 0f ).put( 0f , 0f , 1f );
		//
		// ByteBuffer verts = bh.toByteBuffer( );
		//
		// bh = new BufferHelper( );
		// bh.put( ( char ) 0 , ( char ) 3 , ( char ) 1 ).put( ( char ) 2 , ( char ) 1 , ( char ) 3 );
		// ByteBuffer indices = bh.toByteBuffer( );
		//
		// Zone zone = new Zone( );
		// zone.init( verts , verts.capacity( ) / 4 , indices );
		// zone.rebuildMaps( );
		
		Zone zone = createZone( new Track1( ) , sequence( 0 , ( float ) Math.PI * 4 , ( float ) Math.PI / 360f ) );
		
		BasicJOGLObject obj = new BasicJOGLObject( );
		obj.addVertexBuffer( zone.getVertBuffer( ) );
		obj.indexBuffer( zone.getIndexByteBuffer( ) );
		obj.vertexShaderCode( new MatrixVertexShader( ).toString( ) );
		obj.fragmentShaderCode( new MatrixFragmentShader2( ).numLayers( 3 ).toString( ) );
		obj.normalMatrixName( "n" );
		obj.add( obj.new Attribute3fv( ).name( "a_pos" ) );
		obj.add( obj.new Attribute3fv( ).name( "a_norm" ) );
		obj.add( obj.new Attribute1fv( ).name( "a_u_value" ) );
		obj.add( obj.new Attribute1fv( ).name( "a_v_value" ) );
		obj.add( obj.new Attribute3fv( ).name( "a_u" ) );
		obj.add( obj.new Attribute3fv( ).name( "a_v" ) );
		// obj.add( obj.new Uniform1fv( ).name( "u_layer_depths" ).count( 3 ).value( 0 , 3 , 5 ) );
		// obj.add( obj.new Uniform4fv( ).name( "u_layer_colors" ).count( 3 ).value( 1 , 0 , 0 , 1 , 0.7f , 0 , 0.3f , 1 , 0.3f , 0 , 0.7f , 1 ) );
		// obj.add( obj.new Uniform1fv( ).name( "u_layer_depths" ).count( 2 ).value( 0 , 1f ) );
		// obj.add( obj.new Uniform4fv( ).name( "u_layer_colors" ).count( 2 ).value( 1 , 0 , 0 , 1 , 0 , 0 , 1f , 1 ) );
		obj.add( obj.new Uniform1fv( ).name( "u_layer_depths" ).count( 5 ).value( 0 , 1f , 2f ) );
		obj.add( obj.new Uniform4fv( ).name( "u_layer_colors" ).count( 5 ).value(
				// 1 , 0 , 0 , 1 ,
				// 0 , 1 , 0 , 1 ,
				// 0 , 0 , 1 , 1 ) );
				1 , 0 , 0 , 1 ,
				0 , 1 , 0 , 1 ,
				0 , 0 , 1 , 1 ) );
		obj.add( obj.new Uniform1fv( ).name( "u_u_period" ).value( 1f ) );
		obj.add( obj.new Uniform1fv( ).name( "u_v_period" ).value( 1f ) );
		obj.transpose( false );
		obj.ignoreMissingLocations( true );
		obj.indexType( GL2ES2.GL_UNSIGNED_SHORT );
		obj.vertexCount( zone.getVertBuffer( ).capacity( ) / zone.getBytesPerVertex( ) );
		obj.indexCount( zone.getIndexBuffer( ).capacity( ) );
		obj.drawMode( GL2ES2.GL_TRIANGLES );
		obj.add( new JOGLBlendModifier( ) );
		obj.add( new JOGLPolygonModeModifier( GL.GL_NONE ) );
		scene.add( obj );
		
		int i0 = zone.getIndexBuffer( ).get( 0 ) * zone.getBytesPerVertex( );
		int i1 = zone.getIndexBuffer( ).get( 1 ) * zone.getBytesPerVertex( );
		int i2 = zone.getIndexBuffer( ).get( 2 ) * zone.getBytesPerVertex( );
		
		scene.player.currentZone = zone;
		scene.player.indexInZone = 0;
		scene.player.basis.set( zone.getVertBuffer( ) , i0 , i1 , i2 );
		
		mpmulAffine( scene.player.basis.getUVNToXYZDirect( ) , 0.25 , 0.25 , 0 , scene.player.location );
		mvmulAffine( scene.player.basis.getEFGToXYZDirect( ) , 1 , 0 , 0 , scene.player.basisForward );
		mvmulAffine( scene.player.basis.getEFGToXYZDirect( ) , 0 , 0 , 1 , scene.player.basisUp );
		
		normalize3( scene.player.basisForward );
		normalize3( scene.player.modelForward );
		
		setd( scene.player.modelForward , scene.player.basisForward );
		setd( scene.player.modelUp , scene.player.basisUp );
		
		return scene;
	}
	
	public static List<Float> sequence( float start , float end , float step )
	{
		List<Float> result = new ArrayList<Float>( );
		for( float f = start ; f < end ; f += step )
		{
			result.add( f );
		}
		return result;
	}
	
	public static Zone createZone( final Track track , List<Float> params )
	{
		final float step = ( float ) Math.PI / 180;
		
		IVertexAttrFn attrFn1 = new IVertexAttrFn( )
		{
			@Override
			public int getBytesPerVertex( )
			{
				return 8;
			}
			
			@Override
			public void eval( float param , int index , int vertexCount , float x , float y , float z , IVertexVisitor visitor )
			{
				visitor.visit( param / step );
				visitor.visit( index / 2 );
			}
		};
		
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
				
				// normalize( next , 0 , 3 );
				
				visitor.visit( next[ 0 ] );
				visitor.visit( next[ 1 ] );
				visitor.visit( next[ 2 ] );
				
				if( ( index % 2 ) == 0 )
				{
					track.getCoordFn( ).eval( param , ( index + vertexCount - 1 ) % vertexCount , next );
					next[ 0 ] = prev[ 0 ] - next[ 0 ];
					next[ 1 ] = prev[ 1 ] - next[ 1 ];
					next[ 2 ] = prev[ 2 ] - next[ 2 ];
				}
				else
				{
					track.getCoordFn( ).eval( param , ( index + 1 ) % vertexCount , next );
					next[ 0 ] -= prev[ 0 ];
					next[ 1 ] -= prev[ 1 ];
					next[ 2 ] -= prev[ 2 ];
				}
				
				// normalize( next , 0 , 3 );
				
				visitor.visit( next[ 0 ] );
				visitor.visit( next[ 1 ] );
				visitor.visit( next[ 2 ] );
			}
		};
		
		StandardVertexFn vertexFn = new StandardVertexFn( track.getCoordFn( ) , attrFn1 , attrFn2 );
		
		Zone zone = new Zone( );
		
		int vertexCount = params.size( ) * vertexFn.getVertexCount( 0 );
		
		int indexCount = track.getIndexFn( ).getIndexCount( 0 ) * params.size( );
		
		zone.init( vertexCount , vertexFn.getBytesPerVertex( ) , indexCount );
		
		DirectZoneGenerator zoneGen = DirectZoneGenerator.newInstance( );
		zoneGen.setZone( zone );
		zoneGen.generate( vertexFn , track.getIndexFn( ) , params );
		
		NormalGenerator.generateNormals( zone.getVertBuffer( ) , 12 , vertexFn.getBytesPerVertex( ) , zone.getIndexBuffer( ) , 0 , indexCount );
		
		zone.rebuildMaps( );
		return zone;
	}
	
	public static class MatrixVertexShader
	{
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform mat4 m, v, p;" );
			sb.append( "uniform mat3 n;" );
			sb.append( "attribute vec3 a_pos;" );
			sb.append( "attribute vec3 a_norm;" );
			sb.append( "attribute vec3 a_u;" );
			sb.append( "attribute vec3 a_v;" );
			sb.append( "attribute float a_u_value;" );
			sb.append( "attribute float a_v_value;" );
			sb.append( "varying vec3 v_vmpos;" );
			sb.append( "varying vec3 v_norm;" );
			sb.append( "varying vec3 v_u;" );
			sb.append( "varying vec3 v_v;" );
			sb.append( "varying float v_u_value;" );
			sb.append( "varying float v_v_value;" );
			sb.append( "void main(void)" );
			sb.append( "{" );
			sb.append( "  v_vmpos = (v * m * vec4(a_pos, 1.0)).xyz;" );
			sb.append( "  v_norm = (v * vec4(n * a_norm, 0.0)).xyz;" );
			sb.append( "  v_u = (v * vec4(n * a_u, 0.0)).xyz;" );
			sb.append( "  v_v = (v * vec4(n * a_v, 0.0)).xyz;" );
			// sb.append( "  v_vmpos = a_pos - (v * m * vec4(0.0, 0.0, 0.0, 1.0)).xyz;");
			// sb.append( "  v_norm = a_norm;");
			// sb.append( "  v_u = a_u;");
			// sb.append( "  v_v = a_v;");
			sb.append( "  v_u_value = a_u_value;" );
			sb.append( "  v_v_value = a_v_value;" );
			sb.append( "  gl_Position = p * v * m * vec4(a_pos, 1.0);" );
			sb.append( "}" );
			
			return sb.toString( );
		}
	}
	
	public static class MatrixFragmentShader
	{
		int	numLayers	= 1;
		
		public MatrixFragmentShader numLayers( int numLayers )
		{
			this.numLayers = numLayers;
			return this;
		}
		
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform float u_layer_depths[" + numLayers + "];" );
			sb.append( "uniform vec4 u_layer_colors[" + numLayers + "];" );
			sb.append( "uniform float u_u_period;" );
			sb.append( "uniform float u_v_period;" );
			sb.append( "varying vec3 v_vmpos;" );
			sb.append( "varying vec3 v_norm;" );
			sb.append( "varying vec3 v_u;" );
			sb.append( "varying vec3 v_v;" );
			sb.append( "varying float v_u_value;" );
			sb.append( "varying float v_v_value;" );
			sb.append( "void main() {" );
			sb.append( "  vec3 topos = normalize(v_vmpos);" );
			sb.append( "  float norm = dot(v_norm, topos);" );
			sb.append( "  float u = dot(v_u, topos);" );
			sb.append( "  float v = dot(v_v, topos);" );
			sb.append( "  for (int i = 0; i < " + numLayers + "; i++) {" );
			sb.append( "    float cu = cos((v_u_value + u * u_layer_depths[i]) * u_u_period);" );
			sb.append( "    float cv = cos((v_v_value + v * u_layer_depths[i]) * u_v_period);" );
			// sb.append( "    gl_FragColor += max(cu * cu , cv * cv) * u_layer_colors[i];" );
			sb.append( "    gl_FragColor += cu * cu * u_layer_colors[i];" );
			sb.append( "  }" );
			// sb.append( "  if (gl_FragColor.z > 1.0) {");
			// sb.append( "    gl_FragColor.x = gl_FragColor.z - 1.0;");
			// sb.append( "    gl_FragColor.y = gl_FragColor.z - 1.0;");
			// sb.append( "  }" );
			sb.append( "}" );
			return sb.toString( );
		}
	}
	
	public static class MatrixFragmentShader2
	{
		int	numLayers	= 1;
		
		public MatrixFragmentShader2 numLayers( int numLayers )
		{
			this.numLayers = numLayers;
			return this;
		}
		
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform float u_layer_depths[" + numLayers + "];" );
			sb.append( "uniform vec4 u_layer_colors[" + numLayers + "];" );
			sb.append( "uniform float u_u_period;" );
			sb.append( "uniform float u_v_period;" );
			sb.append( "varying vec3 v_vmpos;" );
			sb.append( "varying vec3 v_norm;" );
			sb.append( "varying vec3 v_u;" );
			sb.append( "varying vec3 v_v;" );
			sb.append( "varying float v_u_value;" );
			sb.append( "varying float v_v_value;" );
			sb.append( "void main() {" );
			sb.append( "  for (int i = 0; i < " + numLayers + "; i++) {" );
			sb.append( "    vec3 virtpos = v_vmpos * u_layer_depths[i] / dot(v_vmpos, -v_norm);" );
			sb.append( "    float virt_u_value = v_u_value + dot(v_u, virtpos);" );
			sb.append( "    float virt_v_value = v_v_value + dot(v_v, virtpos);" );
			sb.append( "    float cu = cos(virt_u_value * u_u_period);" );
			sb.append( "    float cv = cos(virt_v_value * u_v_period);" );
			sb.append( "    gl_FragColor += min(cu * cu , cv * cv) * u_layer_colors[i];" );
			sb.append( "  }" );
			sb.append( "}" );
			return sb.toString( );
		}
	}
}
