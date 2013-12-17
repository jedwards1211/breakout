package org.andork.frf;

import static org.andork.spatial.Rectmath.nmax;
import static org.andork.spatial.Rectmath.nmin;
import static org.andork.spatial.Rectmath.voidRectf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import org.andork.frf.model.SurveyShot;
import org.andork.generic.Ref;
import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.DistanceFragmentShader;
import org.andork.jogl.basic.BasicJOGLObject.Uniform1fv;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.jogl.basic.JOGLGroup;
import org.andork.jogl.basic.JOGLLineWidthModifier;
import org.andork.jogl.basic.JOGLPolygonModeModifier;
import org.andork.jogl.basic.SharedBuffer;
import org.andork.jogl.shader.DefaultNormalVertexShader;
import org.andork.jogl.shader.DefaultPositionVertexShader;
import org.andork.jogl.shader.GradientFragmentShader;
import org.andork.jogl.shader.MainCodeBlock;
import org.andork.jogl.shader.ShaderSegment;
import org.andork.jogl.shader.SimpleLightingFragmentShader;
import org.andork.jogl.shader.VariableDeclarations;
import org.andork.math3d.Vecmath;
import org.andork.spatial.RfStarTree;
import org.andork.spatial.RfStarTree.Branch;
import org.andork.spatial.RfStarTree.Leaf;
import org.andork.spatial.RfStarTree.Node;

public class Survey3dModel
{
	public static class Segment
	{
		final ArrayList<Shot>	shots	= new ArrayList<Shot>( );
		
		SharedBuffer			geomBuffer;
		SharedBuffer			stationAttrBuffer;
		SharedBuffer			fillIndexBuffer;
		SharedBuffer			lineIndexBuffer;
		
		JOGLGroup				group;
		
		Uniform1fv				fillNearDist;
		Uniform1fv				fillFarDist;
		
		Uniform1fv				lineNearDist;
		Uniform1fv				lineFarDist;
		
		void addShot( Shot shot )
		{
			shot.segment = this;
			shot.indexInSegment = shots.size( );
			shots.add( shot );
		}
		
		void populateData( Ref<ByteBuffer> geomBufferRef , Ref<ByteBuffer> stationAttrBufferRef , Ref<ByteBuffer> fillIndexBufferRef , Ref<ByteBuffer> lineIndexBufferRef )
		{
			geomBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * GEOM_BPS ) );
			stationAttrBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * STATION_ATTR_BPS ) );
			fillIndexBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * FILL_IPS ) );
			lineIndexBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * LINE_IPS ) );
			
			for( Shot shot : shots )
			{
				copyBytes( geomBufferRef.value , geomBuffer.buffer( ) , shot.index , GEOM_BPS );
				copyBytes( stationAttrBufferRef.value , stationAttrBuffer.buffer( ) , shot.index , STATION_ATTR_BPS );
				copyBytes( fillIndexBufferRef.value , fillIndexBuffer.buffer( ) , shot.index , BPI * FILL_IPS );
				copyBytes( lineIndexBufferRef.value , lineIndexBuffer.buffer( ) , shot.index , BPI * LINE_IPS );
			}
			
			geomBuffer.buffer( ).position( 0 );
			stationAttrBuffer.buffer( ).position( 0 );
			fillIndexBuffer.buffer( ).position( 0 );
			lineIndexBuffer.buffer( ).position( 0 );
		}
		
		void renderData( )
		{
			BasicJOGLObject fillObj = new BasicJOGLObject( );
			
			fillObj = new BasicJOGLObject( );
			fillObj.addVertexBuffer( geomBuffer ).vertexCount( geomBuffer.buffer( ).capacity( ) / GEOM_BPV );
			fillObj.drawMode( GL2ES2.GL_TRIANGLES );
			fillObj.indexBuffer( fillIndexBuffer ).indexCount( fillIndexBuffer.buffer( ).capacity( ) / BPI );
			fillObj.indexType( GL2ES2.GL_UNSIGNED_INT );
			fillObj.transpose( false );
			fillObj.add( fillObj.new Attribute3fv( ).name( "a_pos" ) );
			fillObj.add( fillObj.new Attribute3fv( ).name( "a_norm" ) );
			fillObj.add( new JOGLDepthModifier( ) );
			fillObj.add( new JOGLPolygonModeModifier( GL.GL_BACK ) );
			// fillObj.add( new JOGLDepthRangeModifier( 0.0f , 0.9f ) );
			fillObj.add( fillObj.new Uniform4fv( ).name( "nearColor" ).value( 1 , 0 , 0 , 1 ) );
			fillObj.add( fillObj.new Uniform4fv( ).name( "farColor" ).value( 0 , 0 , 1 , 1 ) );
			fillObj.add( fillNearDist = fillObj.new Uniform1fv( ).name( "nearDist" ).value( 0 ) );
			fillObj.add( fillFarDist = fillObj.new Uniform1fv( ).name( "farDist" ).value( 1000 ) );
			fillObj.normalMatrixName( "n" );
			// fillObj.vertexShaderCode( new BasicVertexShader( ).passPosToFragmentShader( true ).toString( ) );
			// fillObj.fragmentShaderCode( new DistanceFragmentShader( ).toString( ) );
			DefaultPositionVertexShader fillObjVertShader = new DefaultPositionVertexShader( );
			DefaultNormalVertexShader fillObjNormShader = new DefaultNormalVertexShader( );
			GradientFragmentShader fillObjFragShader = new GradientFragmentShader( );
			fillObjFragShader.in( "v_z" ).loValue( "nearDist" ).hiValue( "farDist" ).loColor( "nearColor" ).hiColor( "farColor" );
			SimpleLightingFragmentShader lightingFragShader = new SimpleLightingFragmentShader( );
			lightingFragShader.color( "gl_FragColor" ).ambientAmt( "0.3" );
			
			fillObj.vertexShaderCode( "precision highp float;" + ShaderSegment.combine(
					fillObjVertShader.defaultVariableDecls( ) ,
					fillObjNormShader.defaultVariableDecls( ) ,
					fillObjVertShader ,
					fillObjNormShader ,
					new VariableDeclarations( "varying float v_z;" ) ,
					new MainCodeBlock( "  v_z = -(v * m * vec4(a_pos, 1.0)).z;" )
					) );
			fillObj.fragmentShaderCode( ShaderSegment.combine(
					new VariableDeclarations( "varying float v_z;" ) ,
					fillObjFragShader.defaultVariableDecls( ) ,
					new VariableDeclarations( "varying vec3 v_norm;" ) ,
					fillObjFragShader ,
					lightingFragShader ) );
			
			BasicJOGLObject lineObj = new BasicJOGLObject( );
			lineObj.addVertexBuffer( geomBuffer ).vertexCount( geomBuffer.buffer( ).capacity( ) / GEOM_BPV );
			lineObj.drawMode( GL2ES2.GL_LINES );
			lineObj.indexBuffer( lineIndexBuffer ).indexCount( lineIndexBuffer.buffer( ).capacity( ) / BPI );
			lineObj.indexType( GL2ES2.GL_UNSIGNED_INT );
			lineObj.transpose( false );
			lineObj.vertexShaderCode( new BasicVertexShader( ).passPosToFragmentShader( true ).toString( ) );
			lineObj.fragmentShaderCode( new DistanceFragmentShader( ).toString( ) );
			lineObj.add( lineObj.new Attribute3fv( ).name( "a_pos" ) );
			lineObj.add( lineObj.new PlaceholderAttribute( 12 ) );
			// fillObj.add( new JOGLDepthRangeModifier( 0.1f , 1f ) );
			lineObj.add( new JOGLLineWidthModifier( 1.5f ) );
			lineObj.add( new JOGLDepthModifier( ) );
			lineObj.add( lineObj.new Uniform4fv( ).name( "nearColor" ).value( 0.7f , 0f , 0f , 1f ) );
			lineObj.add( lineObj.new Uniform4fv( ).name( "farColor" ).value( 0.0f , 0f , 0.7f , 1 ) );
			lineObj.add( lineNearDist = lineObj.new Uniform1fv( ).name( "nearDist" ).value( 0 ) );
			lineObj.add( lineFarDist = lineObj.new Uniform1fv( ).name( "farDist" ).value( 1000 ) );
			
			group = new JOGLGroup( this );
			group.objects.add( fillObj );
			group.objects.add( lineObj );
		}
	}
	
	public static class Shot
	{
		int		index;
		
		Segment	segment;
		int		indexInSegment;
		
		Shot( int index )
		{
			super( );
			this.index = index;
		}
	}
	
	RfStarTree<Shot>			tree;
	
	Set<Segment>				segments;
	
	JOGLGroup					group;
	
	private static final int	GEOM_BPV			= 24;
	private static final int	GEOM_VPS			= 8;
	private static final int	GEOM_BPS			= GEOM_BPV * GEOM_VPS;
	private static final int	STATION_ATTR_BPV	= 8;
	private static final int	STATION_ATTR_VPS	= 2;
	private static final int	STATION_ATTR_BPS	= STATION_ATTR_BPV * STATION_ATTR_VPS;
	private static final int	BPI					= 4;
	private static final int	FILL_IPS			= 24;
	private static final int	LINE_IPS			= 32;
	
	private Survey3dModel( RfStarTree<Shot> tree , Set<Segment> segments )
	{
		super( );
		this.tree = tree;
		this.segments = segments;
		
		group = new JOGLGroup( this );
		for( Segment segment : segments )
		{
			group.objects.add( segment.group );
		}
	}
	
	public JOGLGroup getRootGroup( )
	{
		return group;
	}
	
	public RfStarTree<Shot> getTree( )
	{
		return tree;
	}
	
	public static Survey3dModel create( List<SurveyShot> shots , int M , int m , int p , int segmentLevel )
	{
		Ref<ByteBuffer> geomBufferRef = new Ref<ByteBuffer>( );
		Ref<ByteBuffer> stationAttrBufferRef = new Ref<ByteBuffer>( );
		Ref<ByteBuffer> fillIndexBufferRef = new Ref<ByteBuffer>( );
		Ref<ByteBuffer> lineIndexBufferRef = new Ref<ByteBuffer>( );
		
		createInitialGeometry( shots , geomBufferRef , stationAttrBufferRef , fillIndexBufferRef , lineIndexBufferRef );
		
		RfStarTree<Shot> tree = createTree( geomBufferRef , M , m , p );
		
		Set<Segment> segments = createSegments( tree , segmentLevel );
		
		for( Segment segment : segments )
		{
			segment.populateData( geomBufferRef , stationAttrBufferRef , fillIndexBufferRef , lineIndexBufferRef );
			segment.renderData( );
		}
		
		return new Survey3dModel( tree , segments );
	}
	
	private static void copyBytes( ByteBuffer src , ByteBuffer dest , int shotIndex , int bytesPerShot )
	{
		src.position( shotIndex * bytesPerShot );
		src.limit( src.position( ) + bytesPerShot );
		dest.put( src );
	}
	
	private static ByteBuffer createBuffer( int capacity )
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect( capacity );
		buffer.order( ByteOrder.nativeOrder( ) );
		return buffer;
	}
	
	private static Set<Segment> createSegments( RfStarTree<Shot> tree , int segmentLevel )
	{
		Set<Segment> result = new HashSet<Segment>( );
		
		createSegments( tree.getRoot( ) , segmentLevel , result );
		
		return result;
	}
	
	private static void createSegments( RfStarTree.Node<Shot> node , int segmentLevel , Set<Segment> result )
	{
		if( node.level( ) == segmentLevel )
		{
			result.add( createSegment( node ) );
		}
		else if( node instanceof RfStarTree.Branch )
		{
			RfStarTree.Branch<Shot> branch = ( RfStarTree.Branch<Shot> ) node;
			for( int i = 0 ; i < branch.numChildren( ) ; i++ )
			{
				createSegments( branch.childAt( i ) , segmentLevel , result );
			}
		}
	}
	
	private static Segment createSegment( Node<Shot> node )
	{
		Segment segment = new Segment( );
		
		addShots( node , segment );
		
		segment.shots.trimToSize( );
		
		return segment;
	}
	
	private static void addShots( Node<Shot> node , Segment segment )
	{
		if( node instanceof Leaf )
		{
			segment.addShot( ( ( Leaf<Shot> ) node ).object( ) );
		}
		else if( node instanceof Branch )
		{
			Branch<Shot> branch = ( Branch<Shot> ) node;
			for( int i = 0 ; i < branch.numChildren( ) ; i++ )
			{
				addShots( branch.childAt( i ) , segment );
			}
		}
	}
	
	private static RfStarTree<Shot> createTree( Ref<ByteBuffer> geomBufferRef , int M , int m , int p )
	{
		RfStarTree<Shot> tree = new RfStarTree<Shot>( 3 , M , m , p );
		
		for( int s = 0 ; s < geomBufferRef.value.capacity( ) ; s += GEOM_VPS )
		{
			float[ ] mbr = voidRectf( 3 );
			
			for( int v = 0 ; v < GEOM_VPS ; v++ )
			{
				geomBufferRef.value.position( s + v * GEOM_BPV );
				float x = geomBufferRef.value.getFloat( );
				float y = geomBufferRef.value.getFloat( );
				float z = geomBufferRef.value.getFloat( );
				
				mbr[ 0 ] = nmin( mbr[ 0 ] , x );
				mbr[ 1 ] = nmin( mbr[ 1 ] , y );
				mbr[ 2 ] = nmin( mbr[ 2 ] , z );
				mbr[ 3 ] = nmax( mbr[ 3 ] , x );
				mbr[ 4 ] = nmax( mbr[ 4 ] , y );
				mbr[ 5 ] = nmax( mbr[ 5 ] , z );
			}
			
			Shot shot = new Shot( s );
			
			RfStarTree.Leaf<Shot> leaf = tree.createLeaf( mbr , shot );
			
			tree.insert( leaf );
		}
		
		return tree;
	}
	
	private static void createInitialGeometry( List<SurveyShot> shots , Ref<ByteBuffer> geomBufferRef , Ref<ByteBuffer> stationAttrBufferRef , Ref<ByteBuffer> fillIndexBufferRef , Ref<ByteBuffer> lineIndexBufferRef )
	{
		final double[ ] fromLoc = new double[ 3 ];
		final double[ ] toLoc = new double[ 3 ];
		final double[ ] toToLoc = new double[ 3 ];
		final double[ ] leftAtTo = new double[ 3 ];
		final double[ ] leftAtTo2 = new double[ 3 ];
		final double[ ] leftAtFrom = new double[ 3 ];
		
		BufferHelper geomHelper = new BufferHelper( );
		BufferHelper stationAttrHelper = new BufferHelper( );
		BufferHelper fillIndexHelper = new BufferHelper( );
		BufferHelper lineIndexHelper = new BufferHelper( );
		
		int vertCount = 0;
		
		for( SurveyShot shot : shots )
		{
			fromLoc[ 0 ] = shot.from.position[ 0 ];
			fromLoc[ 2 ] = shot.from.position[ 1 ];
			
			toLoc[ 0 ] = shot.to.position[ 0 ];
			toLoc[ 1 ] = shot.to.position[ 1 ];
			
			if( Vecmath.distance3( shot.from.position , shot.to.position ) > 200 )
			{
				System.out.println( shot.from.name + ": " + Arrays.toString( shot.from.position ) + " - " + shot.to.name + ": " + Arrays.toString( shot.to.position ) );
			}
			
			leftAtFrom[ 0 ] = shot.from.position[ 2 ] - shot.to.position[ 2 ];
			leftAtFrom[ 1 ] = 0;
			leftAtFrom[ 2 ] = shot.to.position[ 0 ] - shot.from.position[ 0 ];
			
			if( leftAtFrom[ 0 ] != 0 || leftAtFrom[ 2 ] != 0 )
			{
				Vecmath.normalize3( leftAtFrom );
			}
			
			stationAttrHelper.put( 0f , 0f );
			for( int i = 0 ; i < 3 ; i++ )
			{
				geomHelper.putAsFloats( shot.from.position[ i ] + leftAtFrom[ i ] * shot.left );
			}
			geomHelper.putAsFloats( leftAtFrom );
			for( int i = 0 ; i < 3 ; i++ )
			{
				geomHelper.putAsFloats( shot.from.position[ i ] - leftAtFrom[ i ] * shot.right );
			}
			geomHelper.putAsFloats( -leftAtFrom[ 0 ] , -leftAtFrom[ 1 ] , -leftAtFrom[ 2 ] );
			for( int i = 0 ; i < 3 ; i++ )
			{
				geomHelper.putAsFloats( shot.from.position[ i ] + ( i == 1 ? shot.up : 0.0 ) );
			}
			geomHelper.putAsFloats( 0 , 1 , 0 );
			for( int i = 0 ; i < 3 ; i++ )
			{
				geomHelper.putAsFloats( shot.from.position[ i ] - ( i == 1 ? shot.down : 0.0 ) );
			}
			geomHelper.putAsFloats( 0 , -1 , 0 );
			
			SurveyShot nextNonVertical = nextNonVerticalShot( shot );
			
			if( nextNonVertical != null )
			{
				double bestWidth = -1.0;
				SurveyShot bestShot = null;
				
				for( SurveyShot nextShot : nextNonVertical.from.frontsights )
				{
					toToLoc[ 0 ] = nextShot.to.position[ 0 ];
					toToLoc[ 2 ] = nextShot.to.position[ 2 ];
					
					leftAtTo2[ 0 ] = shot.to.position[ 2 ] - nextShot.to.position[ 2 ];
					leftAtTo2[ 1 ] = 0;
					leftAtTo2[ 2 ] = nextShot.to.position[ 0 ] - shot.to.position[ 0 ];
					
					if( leftAtTo2[ 0 ] != 0 || leftAtTo2[ 2 ] != 0 )
					{
						Vecmath.normalize3( leftAtTo2 );
					}
					
					double dot = Vecmath.dot3( leftAtFrom , leftAtTo2 );
					double width = Math.abs( dot );
					if( width > bestWidth )
					{
						bestShot = nextShot;
						bestWidth = width;
						leftAtTo[ 0 ] = leftAtTo2[ 0 ];
						leftAtTo[ 2 ] = leftAtTo2[ 2 ];
					}
				}
				
				if( bestShot == null )
				{
					System.err.println( shot.from.name );
				}
				
				stationAttrHelper.put( 0f , 0f );
				for( int i = 0 ; i < 3 ; i++ )
				{
					geomHelper.putAsFloats( shot.to.position[ i ] + leftAtTo[ i ] * bestShot.left );
				}
				geomHelper.putAsFloats( leftAtTo );
				for( int i = 0 ; i < 3 ; i++ )
				{
					geomHelper.putAsFloats( shot.to.position[ i ] - leftAtTo[ i ] * bestShot.right );
				}
				geomHelper.putAsFloats( -leftAtTo[ 0 ] , -leftAtTo[ 1 ] , -leftAtTo[ 2 ] );
				for( int i = 0 ; i < 3 ; i++ )
				{
					geomHelper.putAsFloats( shot.to.position[ i ] + ( i == 1 ? bestShot.up : 0.0 ) );
				}
				geomHelper.putAsFloats( 0 , 1 , 0 );
				for( int i = 0 ; i < 3 ; i++ )
				{
					geomHelper.putAsFloats( shot.to.position[ i ] - ( i == 1 ? bestShot.down : 0.0 ) );
				}
				geomHelper.putAsFloats( 0 , -1 , 0 );
			}
			else
			{
				stationAttrHelper.put( 0f , 0f );
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( leftAtFrom );
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( -leftAtFrom[ 0 ] , -leftAtFrom[ 1 ] , -leftAtFrom[ 2 ] );
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( 0 , 1 , 0 );
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( 0 , -1 , 0 );
			}
			
			fillIndexHelper.put( offset( vertCount ,
					0 , 4 , 2 , 6 , 2 , 4 ,
					2 , 6 , 1 , 5 , 1 , 6 ,
					1 , 5 , 3 , 7 , 3 , 5 ,
					3 , 7 , 0 , 4 , 0 , 7 ) );
			
			lineIndexHelper.put( offset( vertCount ,
					0 , 4 , 0 , 2 , 4 , 2 , 4 , 6 ,
					2 , 6 , 2 , 1 , 6 , 1 , 6 , 5 ,
					1 , 5 , 1 , 3 , 5 , 3 , 5 , 7 ,
					3 , 7 , 3 , 0 , 7 , 0 , 7 , 4 ) );
			
			vertCount += GEOM_VPS;
		}
		
		geomBufferRef.value = geomHelper.toByteBuffer( );
		stationAttrBufferRef.value = stationAttrHelper.toByteBuffer( );
		fillIndexBufferRef.value = fillIndexHelper.toByteBuffer( );
		lineIndexBufferRef.value = lineIndexHelper.toByteBuffer( );
	}
	
	private static SurveyShot nextNonVerticalShot( SurveyShot shot )
	{
		if( shot.to.frontsights.isEmpty( ) )
		{
			return null;
		}
		for( SurveyShot next : shot.to.frontsights )
		{
			if( !isVertical( next ) )
			{
				return next;
			}
		}
		for( SurveyShot next : shot.to.frontsights )
		{
			SurveyShot nextNext = nextNonVerticalShot( next );
			if( nextNext != null )
			{
				return nextNext;
			}
		}
		return null;
	}
	
	private static boolean isVertical( SurveyShot shot )
	{
		double inc = ( shot.fsInc + shot.bsInc ) * 0.5;
		inc = Math.floor( inc * 1000.0 ) / 1000.0;
		inc %= 360.0;
		return inc == 90.0 || inc == -90.0;
	}
	
	private static int[ ] offset( int offset , int ... in )
	{
		for( int i = 0 ; i < in.length ; i++ )
		{
			in[ i ] += offset;
		}
		return in;
	}
}
