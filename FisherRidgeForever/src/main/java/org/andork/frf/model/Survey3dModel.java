package org.andork.frf.model;

import static org.andork.math3d.Vecmath.setf;
import static org.andork.spatial.Rectmath.nmax;
import static org.andork.spatial.Rectmath.nmin;
import static org.andork.spatial.Rectmath.rayIntersects;
import static org.andork.spatial.Rectmath.voidRectf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2ES2;

import org.andork.frf.PickResult;
import org.andork.jogl.basic.BasicJOGLObject;
import org.andork.jogl.basic.BasicJOGLObject.BasicVertexShader;
import org.andork.jogl.basic.BasicJOGLObject.DistanceFragmentShader;
import org.andork.jogl.basic.BasicJOGLObject.Uniform1fv;
import org.andork.jogl.basic.BasicJOGLObject.Uniform3fv;
import org.andork.jogl.basic.BasicJOGLObject.Uniform4fv;
import org.andork.jogl.basic.BufferHelper;
import org.andork.jogl.basic.JOGLDepthModifier;
import org.andork.jogl.basic.JOGLGroup;
import org.andork.jogl.basic.JOGLLineWidthModifier;
import org.andork.jogl.basic.JOGLObject;
import org.andork.jogl.basic.SharedBuffer;
import org.andork.jogl.shadelet.AxisParamShadelet;
import org.andork.jogl.shadelet.CombinedShadelet;
import org.andork.jogl.shadelet.DepthOffsetShadelet;
import org.andork.jogl.shadelet.DistParamShadelet;
import org.andork.jogl.shadelet.GradientShadelet;
import org.andork.jogl.shadelet.IndexedHighlightShadelet;
import org.andork.jogl.shadelet.NormalVertexShadelet;
import org.andork.jogl.shadelet.PositionVertexShadelet;
import org.andork.jogl.shadelet.Shadelet;
import org.andork.jogl.shadelet.SimpleLightingShadelet;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.spatial.RBranch;
import org.andork.spatial.RLeaf;
import org.andork.spatial.RNode;
import org.andork.spatial.RfStarTree;
import org.andork.spatial.RfStarTree.Branch;
import org.andork.spatial.RfStarTree.Leaf;
import org.andork.spatial.RfStarTree.Node;

import com.andork.plot.LinearAxisConversion;

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
		
		Uniform1fv				fillLoParam;
		Uniform1fv				fillHiParam;
		
		Uniform1fv				lineLoParam;
		Uniform1fv				lineHiParam;
		
		boolean					stationAttrsNeedRebuffering;
		
		private BasicJOGLObject	fillObj;
		
		private BasicJOGLObject	lineObj;
		
		void addShot( Shot shot )
		{
			shot.segment = this;
			shot.indexInSegment = shots.size( );
			shots.add( shot );
		}
		
		void populateData( ByteBuffer allGeomBuffer )
		{
			geomBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * GEOM_BPS ) );
			stationAttrBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * STATION_ATTR_BPS ) );
			fillIndexBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * FILL_IPS ) );
			lineIndexBuffer = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * LINE_IPS ) );
			
			for( Shot shot : shots )
			{
				copyBytes( allGeomBuffer , geomBuffer.buffer( ) , shot.index , GEOM_BPS );
			}
			
			createFillIndices( fillIndexBuffer.buffer( ) , shots.size( ) );
			createLineIndices( lineIndexBuffer.buffer( ) , shots.size( ) );
			
			geomBuffer.buffer( ).position( 0 );
			stationAttrBuffer.buffer( ).position( 0 );
			fillIndexBuffer.buffer( ).position( 0 );
			lineIndexBuffer.buffer( ).position( 0 );
		}
		
		void renderData( )
		{
			fillObj = new BasicJOGLObject( );
			
			fillObj = new BasicJOGLObject( );
			fillObj.addVertexBuffer( geomBuffer ).vertexCount( geomBuffer.buffer( ).capacity( ) / GEOM_BPV );
			fillObj.addVertexBuffer( stationAttrBuffer ).vertexCount( stationAttrBuffer.buffer( ).capacity( ) / STATION_ATTR_BPV );
			fillObj.drawMode( GL2ES2.GL_TRIANGLES );
			fillObj.indexBuffer( fillIndexBuffer ).indexCount( fillIndexBuffer.buffer( ).capacity( ) / BPI );
			fillObj.indexType( GL2ES2.GL_UNSIGNED_INT );
			fillObj.transpose( false );
			fillObj.add( new JOGLDepthModifier( ) );
			fillObj.normalMatrixName( "n" );
			
			PositionVertexShadelet posShadelet = new PositionVertexShadelet( );
			NormalVertexShadelet normShadelet = new NormalVertexShadelet( );
			AxisParamShadelet axisShadelet = new AxisParamShadelet( );
			GradientShadelet axisGradShadelet = new GradientShadelet( );
			DistParamShadelet distShadelet = new DistParamShadelet( );
			GradientShadelet distGradShadelet = new GradientShadelet( );
			GlowShadelet glowShadelet = new GlowShadelet( );
			SimpleLightingShadelet lightShadelet = new SimpleLightingShadelet( );
			IndexedHighlightShadelet highlightShadelet = new IndexedHighlightShadelet( );
			
			axisGradShadelet.param( axisShadelet.out( ) );
			
			distGradShadelet.loColor( "gl_FragColor" ).hiColor( "$loColor * 0.3" )
					.param( distShadelet.out( ) ).loColorDeclaration( null ).hiColorDeclaration( null )
					.loValue( "nearDist" ).hiValue( "farDist" );
			
			glowShadelet.color( "vec4(1.0, 1.0, 0.0, 1.0)" );
			glowShadelet.colorDeclaration( null );
			
			lightShadelet.color( "gl_FragColor" ).colorDeclaration( null ).ambientAmt( "0.3" );
			
			highlightShadelet.colorCount( 3 );
			
			CombinedShadelet combShadelet = new CombinedShadelet( posShadelet , normShadelet ,
					axisShadelet , axisGradShadelet , distShadelet , distGradShadelet , glowShadelet , lightShadelet , highlightShadelet );
			
			fillObj.vertexShaderCode( combShadelet.createVertexShaderCode( ) );
			fillObj.fragmentShaderCode( combShadelet.createFragmentShaderCode( ) );
			
			fillObj.add( fillObj.new Attribute3fv( ).name( posShadelet.pos( ) ) );
			fillObj.add( fillObj.new Attribute3fv( ).name( normShadelet.norm( ) ) );
			fillObj.add( fillObj.new Attribute2fv( ).name( glowShadelet.vertParam( ) ).bufferIndex( 1 ) );
			fillObj.add( fillObj.new Attribute1fv( ).name( highlightShadelet.vertIndexParam( ) ).bufferIndex( 1 ) );
			fillObj.add( new Uniform4fv( ).name( axisGradShadelet.loColor( ) ).value( 1 , 0 , 0 , 1 ) );
			fillObj.add( new Uniform4fv( ).name( axisGradShadelet.hiColor( ) ).value( 0 , 0 , 1 , 1 ) );
			fillObj.add( new Uniform3fv( ).name( axisShadelet.origin( ) ).value( 0 , 0 , 0 ) );
			fillObj.add( new Uniform3fv( ).name( axisShadelet.axis( ) ).value( 0 , -1 , 0 ) );
			fillObj.add( fillLoParam = new Uniform1fv( ).name( axisGradShadelet.loValue( ) ).value( 0 ) );
			fillObj.add( fillHiParam = new Uniform1fv( ).name( axisGradShadelet.hiValue( ) ).value( 1000 ) );
			fillObj.add( fillNearDist = new Uniform1fv( ).name( distGradShadelet.loValue( ) ).value( 0 ) );
			fillObj.add( fillFarDist = new Uniform1fv( ).name( distGradShadelet.hiValue( ) ).value( 10000 ) );
			
			System.out.println( Shadelet.prettyPrint( combShadelet.createVertexShaderCode( ) ) );
			System.out.println( Shadelet.prettyPrint( combShadelet.createFragmentShaderCode( ) ) );
			
			lineObj = new BasicJOGLObject( );
			lineObj.addVertexBuffer( geomBuffer ).vertexCount( geomBuffer.buffer( ).capacity( ) / GEOM_BPV );
			lineObj.addVertexBuffer( stationAttrBuffer ).vertexCount( stationAttrBuffer.buffer( ).capacity( ) / STATION_ATTR_BPV );
			lineObj.drawMode( GL2ES2.GL_LINES );
			lineObj.indexBuffer( lineIndexBuffer ).indexCount( lineIndexBuffer.buffer( ).capacity( ) / BPI );
			lineObj.indexType( GL2ES2.GL_UNSIGNED_INT );
			lineObj.transpose( false );
			lineObj.vertexShaderCode( new BasicVertexShader( ).passPosToFragmentShader( true ).toString( ) );
			lineObj.fragmentShaderCode( new DistanceFragmentShader( ).toString( ) );
			lineObj.add( new JOGLLineWidthModifier( 1f ) );
			lineObj.add( new JOGLDepthModifier( ) );
			lineObj.normalMatrixName( "n" );
			
			DepthOffsetShadelet depthOffsShadelet = new DepthOffsetShadelet( ).offset( 0.1f );
			
			combShadelet = new CombinedShadelet( posShadelet , depthOffsShadelet , normShadelet ,
					axisShadelet , axisGradShadelet , distShadelet , distGradShadelet , glowShadelet , lightShadelet , highlightShadelet );
			
			lineObj.vertexShaderCode( combShadelet.createVertexShaderCode( ) );
			lineObj.fragmentShaderCode( combShadelet.createFragmentShaderCode( ) );
			
			lineObj.add( lineObj.new Attribute3fv( ).name( posShadelet.pos( ) ) );
			lineObj.add( lineObj.new Attribute3fv( ).name( normShadelet.norm( ) ) );
			lineObj.add( lineObj.new Attribute2fv( ).name( glowShadelet.vertParam( ) ).bufferIndex( 1 ) );
			lineObj.add( lineObj.new Attribute1fv( ).name( highlightShadelet.vertIndexParam( ) ).bufferIndex( 1 ) );
			lineObj.add( new Uniform4fv( ).name( axisGradShadelet.loColor( ) ).value( 1 , 0 , 0 , 1 ) );
			lineObj.add( new Uniform4fv( ).name( axisGradShadelet.hiColor( ) ).value( 0 , 0 , 1 , 1 ) );
			lineObj.add( new Uniform3fv( ).name( axisShadelet.origin( ) ).value( 0 , 0 , 0 ) );
			lineObj.add( new Uniform3fv( ).name( axisShadelet.axis( ) ).value( 0 , -1 , 0 ) );
			lineObj.add( lineLoParam = new Uniform1fv( ).name( axisGradShadelet.loValue( ) ).value( 0 ) );
			lineObj.add( lineHiParam = new Uniform1fv( ).name( axisGradShadelet.hiValue( ) ).value( 1000 ) );
			lineObj.add( lineNearDist = new Uniform1fv( ).name( distGradShadelet.loValue( ) ).value( 0 ) );
			lineObj.add( lineFarDist = new Uniform1fv( ).name( distGradShadelet.hiValue( ) ).value( 10000 ) );
			
			group = new JOGLGroup( this );
			group.objects.add( new Rebufferer( ) );
			group.objects.add( fillObj );
			group.objects.add( lineObj );
		}
		
		private class Rebufferer implements JOGLObject
		{
			@Override
			public void init( GL2ES2 gl )
			{
			}
			
			@Override
			public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
			{
				if( stationAttrsNeedRebuffering )
				{
					stationAttrsNeedRebuffering = false;
					stationAttrBuffer.rebuffer( gl );
				}
			}
			
			@Override
			public void destroy( GL2ES2 gl )
			{
			}
			
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
		
		public int getIndex( )
		{
			return index;
		}
		
		public void pick( float[ ] rayOrigin , float[ ] rayDirection , ShotPickContext c , List<PickResult<Shot>> pickResults )
		{
			ShotPickResult result = null;
			
			ByteBuffer indexBuffer = segment.fillIndexBuffer.buffer( );
			ByteBuffer vertBuffer = segment.geomBuffer.buffer( );
			indexBuffer.position( indexInSegment * FILL_IPS * BPI );
			for( int i = 0 ; i < 8 ; i++ )
			{
				int i0 = indexBuffer.getInt( );
				int i1 = indexBuffer.getInt( );
				int i2 = indexBuffer.getInt( );
				
				vertBuffer.position( i0 * GEOM_BPV );
				c.p0[ 0 ] = vertBuffer.getFloat( );
				c.p0[ 1 ] = vertBuffer.getFloat( );
				c.p0[ 2 ] = vertBuffer.getFloat( );
				
				vertBuffer.position( i1 * GEOM_BPV );
				c.p1[ 0 ] = vertBuffer.getFloat( );
				c.p1[ 1 ] = vertBuffer.getFloat( );
				c.p1[ 2 ] = vertBuffer.getFloat( );
				
				vertBuffer.position( i2 * GEOM_BPV );
				c.p2[ 0 ] = vertBuffer.getFloat( );
				c.p2[ 1 ] = vertBuffer.getFloat( );
				c.p2[ 2 ] = vertBuffer.getFloat( );
				
				try
				{
					c.lpx.lineFromRay( rayOrigin , rayDirection );
					c.lpx.planeFromPoints( c.p0 , c.p1 , c.p2 );
					c.lpx.findIntersection( );
					if( c.lpx.isPointIntersection( ) && c.lpx.isOnRay( ) && c.lpx.isInTriangle( ) )
					{
						if( result == null || c.lpx.t < result.distance )
						{
							result = new ShotPickResult( );
							result.picked = this;
							result.distance = c.lpx.t;
							result.locationAlongShot = i % 2 == 0 ? c.lpx.u : 1 - c.lpx.u;
							setf( result.location , c.lpx.result );
						}
					}
				}
				catch( Exception ex )
				{
					
				}
			}
			
			if( result != null )
			{
				pickResults.add( result );
			}
			
			vertBuffer.position( 0 );
			indexBuffer.position( 0 );
		}
	}
	
	public static class ShotPickResult extends PickResult<Shot>
	{
		public float	locationAlongShot;
	}
	
	Uniform4fv								highlightColors;
	
	List<SurveyShot>						originalShots;
	List<Shot>								shots;
	
	RfStarTree<Shot>						tree;
	
	Set<Segment>							segments;
	
	final Set<Shot>							selectedShots		= new HashSet<Shot>( );
	final Set<Shot>							hoveredShots		= new HashSet<Shot>( );
	final Map<Shot, Float>					hoverLocations		= new HashMap<Shot, Float>( );
	final Map<Shot, LinearAxisConversion>	highlightExtents	= new HashMap<Shot, LinearAxisConversion>( );
	
	JOGLGroup								group;
	
	public static final class ShotPickContext
	{
		final LinePlaneIntersection3f	lpx	= new LinePlaneIntersection3f( );
		final float[ ]					p0	= new float[ 3 ];
		final float[ ]					p1	= new float[ 3 ];
		final float[ ]					p2	= new float[ 3 ];
	}
	
	private static final int	GEOM_BPV			= 24;
	private static final int	GEOM_VPS			= 8;
	private static final int	GEOM_BPS			= GEOM_BPV * GEOM_VPS;
	private static final int	STATION_ATTR_BPV	= 12;
	private static final int	STATION_ATTR_VPS	= GEOM_VPS;
	private static final int	STATION_ATTR_BPS	= STATION_ATTR_BPV * STATION_ATTR_VPS;
	private static final int	BPI					= 4;
	private static final int	FILL_IPS			= 24;
	private static final int	LINE_IPS			= 32;
	
	private Survey3dModel( List<SurveyShot> originalShots , List<Shot> shots , RfStarTree<Shot> tree , Set<Segment> segments )
	{
		super( );
		this.originalShots = originalShots;
		this.shots = shots;
		this.tree = tree;
		this.segments = segments;
		
		highlightColors = new Uniform4fv( ).name( "highlightColors" );
		highlightColors.value(
				0f , 0f , 0f , 0f ,
				0f , 1f , 1f , 0.5f ,
				1f , 1f , 0f , 0.5f
				);
		highlightColors.count( 3 );
		
		group = new JOGLGroup( this );
		for( Segment segment : segments )
		{
			group.objects.add( segment.group );
			segment.fillObj.add( highlightColors );
			segment.lineObj.add( highlightColors );
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
	
	public void setNearDist( float nearDist )
	{
		for( Segment segment : segments )
		{
			segment.fillNearDist.value( nearDist );
			segment.lineNearDist.value( nearDist );
		}
	}
	
	public void setFarDist( float farDist )
	{
		for( Segment segment : segments )
		{
			segment.fillFarDist.value( farDist );
			segment.lineFarDist.value( farDist );
		}
	}
	
	public void setLoParam( float loParam )
	{
		for( Segment segment : segments )
		{
			segment.fillLoParam.value( loParam );
			segment.lineLoParam.value( loParam );
		}
	}
	
	public void setHiParam( float hiParam )
	{
		for( Segment segment : segments )
		{
			segment.fillHiParam.value( hiParam );
			segment.lineHiParam.value( hiParam );
		}
	}
	
	public void pickShots( float[ ] rayOrigin , float[ ] rayDirection ,
			ShotPickContext spc , List<PickResult<Shot>> pickResults )
	{
		pickShots( tree.getRoot( ) , rayOrigin , rayDirection , spc , pickResults );
	}
	
	private void pickShots( RNode<float[ ], Shot> node , float[ ] rayOrigin , float[ ] rayDirection ,
			ShotPickContext spc , List<PickResult<Shot>> pickResults )
	{
		if( rayIntersects( rayOrigin , rayDirection , node.mbr( ) ) )
		{
			if( node instanceof RBranch )
			{
				RBranch<float[ ], Shot> branch = ( RBranch<float[ ], Shot> ) node;
				for( int i = 0 ; i < branch.numChildren( ) ; i++ )
				{
					pickShots( branch.childAt( i ) , rayOrigin , rayDirection , spc , pickResults );
				}
			}
			else if( node instanceof RLeaf )
			{
				Shot shot = ( ( RLeaf<float[ ], Shot> ) node ).object( );
				shot.pick( rayOrigin , rayDirection , spc , pickResults );
			}
		}
	}
	
	public List<SurveyShot> getOriginalShots( )
	{
		return Collections.unmodifiableList( originalShots );
	}
	
	public List<Shot> getShots( )
	{
		return Collections.unmodifiableList( shots );
	}
	
	public Set<Shot> getHoveredShots( )
	{
		return Collections.unmodifiableSet( hoveredShots );
	}
	
	public Set<Shot> getSelectedShots( )
	{
		return Collections.unmodifiableSet( selectedShots );
	}
	
	public SelectionEditor editSelection( )
	{
		return new SelectionEditor( );
	}
	
	public final class SelectionEditor
	{
		private SelectionEditor( )
		{
			
		}
		
		final Set<Shot>							selected			= new HashSet<Shot>( );
		final Set<Shot>							deselected			= new HashSet<Shot>( );
		final Set<Shot>							hovered				= new HashSet<Shot>( );
		final Map<Shot, Float>					hoverLocations		= new HashMap<Shot, Float>( );
		final Map<Shot, LinearAxisConversion>	highlightExtents	= new HashMap<Shot, LinearAxisConversion>( );
		final Set<Shot>							unhovered			= new HashSet<Shot>( );
		
		boolean									committed			= false;
		
		public SelectionEditor select( Shot shot )
		{
			selected.add( shot );
			deselected.remove( shot );
			return this;
		}
		
		public SelectionEditor deselect( Shot shot )
		{
			selected.remove( shot );
			deselected.add( shot );
			return this;
		}
		
		public SelectionEditor hover( Shot shot , float location , LinearAxisConversion highlightExtent )
		{
			hovered.add( shot );
			unhovered.remove( shot );
			hoverLocations.put( shot , location );
			highlightExtents.put( shot , highlightExtent );
			return this;
		}
		
		public SelectionEditor unhover( Shot shot )
		{
			hovered.remove( shot );
			unhovered.add( shot );
			hoverLocations.remove( shot );
			highlightExtents.remove( shot );
			return this;
		}
		
		public void commit( )
		{
			Map<Shot, LinearAxisConversion> prevHighlightExtents = new HashMap<Shot, LinearAxisConversion>( Survey3dModel.this.highlightExtents );
			
			if( committed )
			{
				throw new IllegalStateException( "already committed" );
			}
			committed = true;
			
			for( Shot shot : selected )
			{
				selectedShots.add( shot );
			}
			for( Shot shot : deselected )
			{
				selectedShots.remove( shot );
			}
			for( Shot shot : hovered )
			{
				hoveredShots.add( shot );
				Survey3dModel.this.hoverLocations.put( shot , hoverLocations.get( shot ) );
				Survey3dModel.this.highlightExtents.put( shot , highlightExtents.get( shot ) );
			}
			for( Shot shot : unhovered )
			{
				hoveredShots.remove( shot );
				Survey3dModel.this.hoverLocations.remove( shot );
				Survey3dModel.this.highlightExtents.remove( shot );
			}
			
			Set<Shot> affectedShots = new HashSet<Shot>( );
			affectedShots.addAll( selected );
			affectedShots.addAll( deselected );
			affectedShots.addAll( hovered );
			affectedShots.addAll( unhovered );
			
			updateHighlights( affectedShots , prevHighlightExtents );
		}
	}
	
	public static Survey3dModel create( List<SurveyShot> originalShots , int M , int m , int p , int segmentLevel )
	{
		List<Shot> shots = new ArrayList<Shot>( );
		for( int i = 0 ; i < originalShots.size( ) ; i++ )
		{
			shots.add( new Shot( i ) );
		}
		
		ByteBuffer geomBuffer = createInitialGeometry( originalShots );
		
		RfStarTree<Shot> tree = createTree( shots , geomBuffer , M , m , p );
		
		Set<Segment> segments = createSegments( tree , segmentLevel );
		
		for( Segment segment : segments )
		{
			segment.populateData( geomBuffer );
			segment.renderData( );
		}
		
		Survey3dModel model = new Survey3dModel( originalShots , shots , tree , segments );
		
		return model;
	}
	
	private static void copyBytes( ByteBuffer src , ByteBuffer dest , int shotIndex , int bytesPerShot )
	{
		src.clear( );
		src.position( shotIndex * bytesPerShot );
		src.limit( src.position( ) + bytesPerShot );
		dest.put( src );
	}
	
	private static void createFillIndices( ByteBuffer dest , int shotCount )
	{
		for( int i = 0 ; i < shotCount ; i++ )
		{
			for( int index : offset( i * GEOM_VPS ,
					0 , 4 , 2 , 6 , 2 , 4 ,
					2 , 6 , 1 , 5 , 1 , 6 ,
					1 , 5 , 3 , 7 , 3 , 5 ,
					3 , 7 , 0 , 4 , 0 , 7 ) )
			{
				dest.putInt( index );
			}
		}
	}
	
	private static void createLineIndices( ByteBuffer dest , int shotCount )
	{
		for( int i = 0 ; i < shotCount ; i++ )
		{
			for( int index : offset( i * GEOM_VPS ,
					0 , 4 , 0 , 2 , 4 , 2 , 4 , 6 ,
					2 , 6 , 2 , 1 , 6 , 1 , 6 , 5 ,
					1 , 5 , 1 , 3 , 5 , 3 , 5 , 7 ,
					3 , 7 , 3 , 0 , 7 , 0 , 7 , 4 ) )
			{
				dest.putInt( index );
			}
		}
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
	
	private static RfStarTree<Shot> createTree( List<Shot> shots , ByteBuffer geomBuffer , int M , int m , int p )
	{
		RfStarTree<Shot> tree = new RfStarTree<Shot>( 3 , M , m , p );
		
		int numShots = geomBuffer.capacity( ) / GEOM_BPS;
		
		for( int s = 0 ; s < numShots ; s++ )
		{
			float[ ] mbr = voidRectf( 3 );
			
			int shotStart = s * GEOM_BPS;
			
			for( int v = 0 ; v < GEOM_VPS ; v++ )
			{
				geomBuffer.position( shotStart + v * GEOM_BPV );
				float x = geomBuffer.getFloat( );
				float y = geomBuffer.getFloat( );
				float z = geomBuffer.getFloat( );
				
				mbr[ 0 ] = nmin( mbr[ 0 ] , x );
				mbr[ 1 ] = nmin( mbr[ 1 ] , y );
				mbr[ 2 ] = nmin( mbr[ 2 ] , z );
				mbr[ 3 ] = nmax( mbr[ 3 ] , x );
				mbr[ 4 ] = nmax( mbr[ 4 ] , y );
				mbr[ 5 ] = nmax( mbr[ 5 ] , z );
			}
			
			RfStarTree.Leaf<Shot> leaf = tree.createLeaf( mbr , shots.get( s ) );
			
			tree.insert( leaf );
		}
		
		return tree;
	}
	
	private static ByteBuffer createInitialGeometry( List<SurveyShot> originalShots )
	{
		final double[ ] fromLoc = new double[ 3 ];
		final double[ ] toLoc = new double[ 3 ];
		final double[ ] toToLoc = new double[ 3 ];
		final double[ ] leftAtTo = new double[ 3 ];
		final double[ ] leftAtTo2 = new double[ 3 ];
		final double[ ] leftAtFrom = new double[ 3 ];
		
		BufferHelper geomHelper = new BufferHelper( );
		
		for( SurveyShot shot : originalShots )
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
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( leftAtFrom );
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( -leftAtFrom[ 0 ] , -leftAtFrom[ 1 ] , -leftAtFrom[ 2 ] );
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( 0 , 1 , 0 );
				geomHelper.putAsFloats( shot.to.position );
				geomHelper.putAsFloats( 0 , -1 , 0 );
			}
		}
		
		return geomHelper.toByteBuffer( );
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
	
	private void updateHighlights( Collection<Shot> affectedShots , Map<Shot, LinearAxisConversion> prevHighlightExtents )
	{
		// find the segments that are affected by the affected shots
		// (not just the segments containing those shots but segments containing
		// shots within highlight distance from an affected shot)
		Set<Segment> affectedSegments = new HashSet<Segment>( );
		for( Shot shot : affectedShots )
		{
			findAffectedSegments( shot , affectedSegments , prevHighlightExtents );
		}
		
		for( Segment segment : affectedSegments )
		{
			clearHighlights( segment );
		}
		
		for( Shot shot : hoveredShots )
		{
			if( affectedSegments.contains( shot.segment ) )
			{
				applyHoverHighlights( shot );
			}
		}
		for( Shot shot : selectedShots )
		{
			if( affectedSegments.contains( shot.segment ) )
			{
				applySelectionHighlights( shot );
			}
		}
		
		for( Segment segment : affectedSegments )
		{
			segment.stationAttrsNeedRebuffering = true;
		}
	}
	
	private static enum Direction
	{
		FORWARD , BACKWARD;
	}
	
	private void findAffectedSegments( Shot shot , Set<Segment> affectedSegments , Map<Shot, LinearAxisConversion> prevHighlightExtents )
	{
		Set<Shot> visitedShots = new HashSet<Shot>( );
		LinearAxisConversion newConversion = highlightExtents.get( shot );
		Float newRemainingDistance;
		if( newConversion != null )
		{
			newRemainingDistance = ( float ) newConversion.invert( 0.0 );
		}
		else
		{
			newRemainingDistance = 0f;
		}
		LinearAxisConversion prevConversion = prevHighlightExtents.get( shot );
		Float prevRemainingDistance;
		if( prevConversion != null )
		{
			prevRemainingDistance = ( float ) prevConversion.invert( 0.0 );
		}
		else
		{
			prevRemainingDistance = 0f;
		}
		float remainingDistance = Math.max( prevRemainingDistance , newRemainingDistance );
		
		SurveyShot origShot = originalShots.get( shot.index );
		
		findAffectedSegments( origShot.to , Direction.FORWARD , visitedShots , remainingDistance , affectedSegments );
		findAffectedSegments( origShot.from , Direction.BACKWARD , visitedShots , remainingDistance , affectedSegments );
	}
	
	private void findAffectedSegments( SurveyStation station , Direction direction , Set<Shot> visitedShots , float remainingDistance , Set<Segment> affectedSegments )
	{
		for( SurveyShot next : station.frontsights )
		{
			findAffectedSegments( shots.get( next.index ) , Direction.FORWARD , visitedShots , remainingDistance ,
					affectedSegments );
		}
		for( SurveyShot next : station.backsights )
		{
			findAffectedSegments( shots.get( next.index ) , Direction.BACKWARD , visitedShots , remainingDistance ,
					affectedSegments );
		}
	}
	
	private void findAffectedSegments( Shot shot , Direction direction , Set<Shot> visitedShots , float remainingDistance , Set<Segment> affectedSegments )
	{
		if( visitedShots.add( shot ) )
		{
			affectedSegments.add( shot.segment );
			if( remainingDistance > 0 )
			{
				SurveyShot origShot = originalShots.get( shot.index );
				SurveyStation nextStation = direction == Direction.FORWARD ? origShot.to : origShot.from;
				float nextRemainingDistance = ( float ) ( remainingDistance - origShot.dist );
				
				findAffectedSegments( nextStation , direction , visitedShots , nextRemainingDistance , affectedSegments );
			}
		}
	}
	
	private void clearHighlights( Segment segment )
	{
		ByteBuffer buffer = segment.stationAttrBuffer.buffer( );
		buffer.position( 0 );
		for( int i = 0 ; i < buffer.capacity( ) ; i += STATION_ATTR_BPV )
		{
			buffer.putFloat( i , -Float.MAX_VALUE );
			buffer.putFloat( i + 4 , -Float.MAX_VALUE );
			buffer.putFloat( i + 8 , 0f );
		}
	}
	
	private float getFromHighlightA( ByteBuffer buffer , int shotIndex )
	{
		return buffer.getFloat( shotIndex * STATION_ATTR_BPS );
	}
	
	private float getFromHighlightB( ByteBuffer buffer , int shotIndex )
	{
		return buffer.getFloat( shotIndex * STATION_ATTR_BPS + 4 );
	}
	
	private float getToHighlightA( ByteBuffer buffer , int shotIndex )
	{
		return buffer.getFloat( shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2 );
	}
	
	private float getToHighlightB( ByteBuffer buffer , int shotIndex )
	{
		return buffer.getFloat( shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2 + 4 );
	}
	
	private void setFromHighlightA( ByteBuffer buffer , int shotIndex , float value )
	{
		int index = shotIndex * STATION_ATTR_BPS;
		for( int i = 0 ; i < STATION_ATTR_VPS / 2 ; i++ )
		{
			buffer.putFloat( index + i * STATION_ATTR_BPV , value );
		}
	}
	
	private void setFromHighlightB( ByteBuffer buffer , int shotIndex , float value )
	{
		int index = shotIndex * STATION_ATTR_BPS + 4;
		for( int i = 0 ; i < STATION_ATTR_VPS / 2 ; i++ )
		{
			buffer.putFloat( index + i * STATION_ATTR_BPV , value );
		}
	}
	
	private void setToHighlightA( ByteBuffer buffer , int shotIndex , float value )
	{
		int index = shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2;
		for( int i = 0 ; i < STATION_ATTR_VPS / 2 ; i++ )
		{
			buffer.putFloat( index + i * STATION_ATTR_BPV , value );
		}
	}
	
	private void setToHighlightB( ByteBuffer buffer , int shotIndex , float value )
	{
		int index = shotIndex * STATION_ATTR_BPS + STATION_ATTR_BPV * STATION_ATTR_VPS / 2 + 4;
		for( int i = 0 ; i < STATION_ATTR_VPS / 2 ; i++ )
		{
			buffer.putFloat( index + i * STATION_ATTR_BPV , value );
		}
	}
	
	private void applyHoverHighlights( Shot shot )
	{
		SurveyShot origShot = originalShots.get( shot.index );
		ByteBuffer buffer = shot.segment.stationAttrBuffer.buffer( );
		
		LinearAxisConversion highlightConversion = highlightExtents.get( shot );
		
		Float hoverLocation = hoverLocations.get( shot );
		if( hoverLocation == null )
		{
			hoverLocation = 0.5f;
		}
		
		float distToFrom = ( float ) ( origShot.dist * hoverLocation );
		float distToTo = ( float ) ( origShot.dist * ( 1f - hoverLocation ) );
		
		applyHoverHighlights( origShot.from , Direction.BACKWARD , distToFrom , highlightConversion );
		applyHoverHighlights( origShot.to , Direction.FORWARD , distToTo , highlightConversion );
		
		float fromHighlightA = ( float ) highlightConversion.convert( distToTo - origShot.dist );
		float fromHighlightB = ( float ) highlightConversion.convert( distToFrom );
		float toHighlightA = ( float ) highlightConversion.convert( distToTo );
		float toHighlightB = ( float ) highlightConversion.convert( distToFrom - origShot.dist );
		
		setFromHighlightA( buffer , shot.indexInSegment , fromHighlightA );
		setFromHighlightB( buffer , shot.indexInSegment , fromHighlightB );
		setToHighlightA( buffer , shot.indexInSegment , toHighlightA );
		setToHighlightB( buffer , shot.indexInSegment , toHighlightB );
	}
	
	private void applyHoverHighlights( SurveyStation station , Direction direction , float distance , LinearAxisConversion highlightConversion )
	{
		for( SurveyShot next : station.frontsights )
		{
			applyHoverHighlights( shots.get( next.index ) , Direction.FORWARD , distance , highlightConversion );
		}
		for( SurveyShot next : station.backsights )
		{
			applyHoverHighlights( shots.get( next.index ) , Direction.BACKWARD , distance , highlightConversion );
		}
	}
	
	private void applyHoverHighlights( Shot shot , Direction direction , float distance , LinearAxisConversion highlightConversion )
	{
		if( distance < highlightConversion.invert( 0 ) )
		{
			ByteBuffer buffer = shot.segment.stationAttrBuffer.buffer( );
			
			SurveyShot origShot = originalShots.get( shot.index );
			float nextDistance = ( float ) ( distance + origShot.dist );
			
			float fromHighlight;
			float toHighlight;
			
			if( direction == Direction.FORWARD )
			{
				fromHighlight = ( float ) highlightConversion.convert( distance );
				toHighlight = ( float ) highlightConversion.convert( nextDistance );
			}
			else
			{
				fromHighlight = ( float ) highlightConversion.convert( nextDistance );
				toHighlight = ( float ) highlightConversion.convert( distance );
			}
			
			float currentFromHighlight = Math.min( getFromHighlightA( buffer , shot.indexInSegment ) , getFromHighlightB( buffer , shot.indexInSegment ) );
			float currentToHighlight = Math.min( getToHighlightA( buffer , shot.indexInSegment ) , getToHighlightB( buffer , shot.indexInSegment ) );
			
			boolean keepGoing = false;
			
			if( fromHighlight > currentFromHighlight )
			{
				keepGoing = true;
				setFromHighlightA( buffer , shot.indexInSegment , fromHighlight );
				setFromHighlightB( buffer , shot.indexInSegment , fromHighlight );
			}
			if( toHighlight > currentToHighlight )
			{
				keepGoing = true;
				setToHighlightA( buffer , shot.indexInSegment , toHighlight );
				setToHighlightB( buffer , shot.indexInSegment , toHighlight );
			}
			
			if( keepGoing )
			{
				SurveyStation nextStation = direction == Direction.FORWARD ? origShot.to : origShot.from;
				applyHoverHighlights( nextStation , direction , nextDistance , highlightConversion );
			}
		}
	}
	
	private void applySelectionHighlights( Shot shot )
	{
		ByteBuffer buffer = shot.segment.stationAttrBuffer.buffer( );
		for( int i = 0 ; i < STATION_ATTR_VPS ; i++ )
		{
			buffer.putFloat( shot.indexInSegment * STATION_ATTR_BPS + 8 + i * STATION_ATTR_BPV , 2f );
		}
	}
}