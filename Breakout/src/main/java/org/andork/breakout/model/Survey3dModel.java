package org.andork.breakout.model;

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_CLAMP_TO_EDGE;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import static javax.media.opengl.GL.GL_UNSIGNED_INT;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.spatial.Rectmath.nmax;
import static org.andork.spatial.Rectmath.nmin;
import static org.andork.spatial.Rectmath.rayIntersects;
import static org.andork.spatial.Rectmath.voidRectf;

import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2ES2;

import org.andork.breakout.PickResult;
import org.andork.breakout.awt.ParamGradientMapPaint;
import org.andork.collect.HashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.jogl.BasicJOGLObject.Uniform1fv;
import org.andork.jogl.BasicJOGLObject.Uniform3fv;
import org.andork.jogl.BasicJOGLObject.Uniform4fv;
import org.andork.jogl.BufferHelper;
import org.andork.jogl.SharedBuffer;
import org.andork.jogl.neu.JoglDrawContext;
import org.andork.jogl.neu.JoglDrawable;
import org.andork.jogl.neu.JoglResource;
import org.andork.jogl.util.JOGLUtils;
import org.andork.math3d.LinePlaneIntersection3f;
import org.andork.math3d.Vecmath;
import org.andork.spatial.RBranch;
import org.andork.spatial.RLeaf;
import org.andork.spatial.RNode;
import org.andork.spatial.RfStarTree;
import org.andork.spatial.RfStarTree.Branch;
import org.andork.spatial.RfStarTree.Leaf;
import org.andork.spatial.RfStarTree.Node;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.Task;

import com.andork.plot.LinearAxisConversion;
import com.jogamp.nativewindow.awt.DirectDataBufferInt;
import com.jogamp.nativewindow.awt.DirectDataBufferInt.BufferedImageInt;

public class Survey3dModel implements JoglDrawable , JoglResource
{
	
	private static final int				GEOM_BPV			= 24;
	
	private static final int				GEOM_VPS			= 8;
	
	private static final int				GEOM_BPS			= GEOM_BPV * GEOM_VPS;
	
	private static final int				STATION_ATTR_BPV	= 12;
	
	private static final int				STATION_ATTR_VPS	= GEOM_VPS;
	
	private static final int				STATION_ATTR_BPS	= STATION_ATTR_BPV * STATION_ATTR_VPS;
	
	private static final int				BPI					= 4;
	
	private static final int				FILL_IPS			= 24;
	
	private static final int				LINE_IPS			= 32;
	
	List<SurveyShot>						originalShots;
	List<Shot>								shots;
	
	RfStarTree<Shot>						tree;
	
	Set<Segment>							segments;
	
	MultiMap<SegmentDrawer, Segment>		drawers				= HashSetMultiMap.newInstance( );
	
	final Set<Shot>							selectedShots		= new HashSet<Shot>( );
	final Set<Shot>							hoveredShots		= new HashSet<Shot>( );
	final Map<Shot, Float>					hoverLocations		= new HashMap<Shot, Float>( );
	final Map<Shot, LinearAxisConversion>	highlightExtents	= new HashMap<Shot, LinearAxisConversion>( );
	
	LinearGradientPaint						paramPaint;
	int										paramTexture;
	BufferedImageInt						paramTextureImage;
	boolean									paramTextureNeedsUpdate;
	
	Uniform4fv								highlightColors;
	
	Uniform3fv								depthAxis;
	Uniform3fv								depthOrigin;
	
	Uniform1fv								ambient;
	
	Uniform1fv								nearDist;
	Uniform1fv								farDist;
	
	Uniform1fv								loParam;
	Uniform1fv								hiParam;
	
	Uniform4fv								glowColor;
	
	private Survey3dModel( List<SurveyShot> originalShots , List<Shot> shots , RfStarTree<Shot> tree , Set<Segment> segments , Subtask renderSubtask )
	{
		super( );
		this.originalShots = originalShots;
		this.shots = shots;
		this.tree = tree;
		this.segments = segments;
		
		highlightColors = new Uniform4fv( ).name( "u_highlightColors" );
		highlightColors.value(
				0f , 0f , 0f , 0f ,
				0f , 1f , 1f , 0.5f ,
				0f , 1f , 1f , 0.5f
				);
		highlightColors.count( 3 );
		
		depthAxis = new Uniform3fv( ).name( "u_axis" ).value( 0f , -1f , 0f );
		depthOrigin = new Uniform3fv( ).name( "u_origin" ).value( 0f , 0f , 0f );
		
		glowColor = new Uniform4fv( ).name( "u_glowColor" ).value( 0f , 1f , 1f , 1f );
		
		ambient = new Uniform1fv( ).name( "u_ambient" ).value( 0.5f );
		
		loParam = new Uniform1fv( ).name( "u_loParam" ).value( 0 );
		hiParam = new Uniform1fv( ).name( "u_hiParam" ).value( 1000 );
		nearDist = new Uniform1fv( ).name( "u_nearDist" ).value( 0 );
		farDist = new Uniform1fv( ).name( "u_farDist" ).value( 1000 );
		
		AxialLineSegmentDrawer lineSegmentDrawer = new AxialLineSegmentDrawer( );
		drawers.putAll( lineSegmentDrawer , segments );
		AxialFillSegmentDrawer fillSegmentDrawer = new AxialFillSegmentDrawer( );
		drawers.putAll( fillSegmentDrawer , segments );
	}
	
	public static Survey3dModel create( List<SurveyShot> originalShots , int M , int m , int p , Task task )
	{
		Subtask rootSubtask = null;
		int renderProportion = 5;
		
		if( task != null )
		{
			task.setTotal( 1000 );
			rootSubtask = new Subtask( task );
		}
		else
		{
			rootSubtask = Subtask.dummySubtask( );
		}
		rootSubtask.setStatus( "Updating view" );
		rootSubtask.setTotal( renderProportion + 5 );
		
		List<Shot> shots = new ArrayList<Shot>( );
		for( int i = 0 ; i < originalShots.size( ) ; i++ )
		{
			shots.add( new Shot( i ) );
		}
		if( rootSubtask.isCanceling( ) )
		{
			return null;
		}
		rootSubtask.setCompleted( rootSubtask.getCompleted( ) + 1 );
		
		ByteBuffer geomBuffer = createInitialGeometry( originalShots , rootSubtask.beginSubtask( 1 ) );
		if( rootSubtask.isCanceling( ) )
		{
			return null;
		}
		rootSubtask.setCompleted( rootSubtask.getCompleted( ) + 1 );
		
		RfStarTree<Shot> tree = createTree( shots , geomBuffer , M , m , p , rootSubtask.beginSubtask( 1 ) );
		if( rootSubtask.isCanceling( ) )
		{
			return null;
		}
		rootSubtask.setCompleted( rootSubtask.getCompleted( ) + 1 );
		
		int segmentLevel = Math.min( tree.getRoot( ).level( ) , 3 );
		
		Set<Segment> segments = createSegments( tree , segmentLevel , rootSubtask.beginSubtask( 1 ) );
		if( rootSubtask.isCanceling( ) )
		{
			return null;
		}
		rootSubtask.setCompleted( rootSubtask.getCompleted( ) + 1 );
		
		Subtask renderSubtask = rootSubtask.beginSubtask( renderProportion );
		renderSubtask.setStatus( "sending data to graphics card" );
		renderSubtask.setTotal( segments.size( ) * 2 );
		
		for( Segment segment : segments )
		{
			segment.populateData( geomBuffer );
			if( renderSubtask.isCanceling( ) )
			{
				return null;
			}
			renderSubtask.setCompleted( renderSubtask.getCompleted( ) + 1 );
		}
		Survey3dModel model = new Survey3dModel( originalShots , shots , tree , segments , renderSubtask );
		if( rootSubtask.isCanceling( ) )
		{
			return null;
		}
		renderSubtask.end( );
		rootSubtask.setCompleted( rootSubtask.getCompleted( ) + renderProportion );
		
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
	
	private static Set<Segment> createSegments( RfStarTree<Shot> tree , int segmentLevel , Subtask task )
	{
		task.setStatus( "creating render segments" );
		task.setIndeterminate( true );
		Set<Segment> result = new HashSet<Segment>( );
		
		createSegments( tree.getRoot( ) , segmentLevel , result );
		
		task.end( );
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
	
	private static RfStarTree<Shot> createTree( List<Shot> shots , ByteBuffer geomBuffer , int M , int m , int p , Subtask task )
	{
		RfStarTree<Shot> tree = new RfStarTree<Shot>( 3 , M , m , p );
		
		int numShots = geomBuffer.capacity( ) / GEOM_BPS;
		
		task.setStatus( "creating spatial index" );
		task.setTotal( numShots );
		
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
			
			if( ( s % 100 ) == 0 && task.isCanceling( ) )
			{
				return null;
			}
			task.setCompleted( s );
		}
		
		task.end( );
		return tree;
	}
	
	private static ByteBuffer createInitialGeometry( List<SurveyShot> originalShots , Subtask task )
	{
		task.setStatus( "creating geometry" );
		task.setTotal( originalShots.size( ) );
		
		final double[ ] fromLoc = new double[ 3 ];
		final double[ ] toLoc = new double[ 3 ];
		final double[ ] toToLoc = new double[ 3 ];
		final double[ ] leftAtTo = new double[ 3 ];
		final double[ ] leftAtTo2 = new double[ 3 ];
		final double[ ] leftAtFrom = new double[ 3 ];
		
		BufferHelper geomHelper = new BufferHelper( );
		
		int count = 0;
		for( SurveyShot shot : originalShots )
		{
			fromLoc[ 0 ] = shot.from.position[ 0 ];
			fromLoc[ 2 ] = shot.from.position[ 1 ];
			
			toLoc[ 0 ] = shot.to.position[ 0 ];
			toLoc[ 1 ] = shot.to.position[ 1 ];
			
			if( Vecmath.distance3( shot.from.position , shot.to.position ) > 200 )
			{
				System.err.println( shot.from.name + ": " + Arrays.toString( shot.from.position ) + " - " + shot.to.name + ": " + Arrays.toString( shot.to.position ) );
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
			
			boolean foundNext = false;
			
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
				
				if( bestShot != null )
				{
					foundNext = true;
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
			}
			if( !foundNext )
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
			
			if( ( count++ % 100 ) == 0 && task != null )
			{
				if( task.isCanceling( ) )
				{
					return null;
				}
				task.setCompleted( count );
			}
		}
		
		task.end( );
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
	
	public void setParamPaint( LinearGradientPaint paint )
	{
		if( paramPaint != paint )
		{
			this.paramPaint = paint;
			paramTextureNeedsUpdate = true;
		}
	}
	
	public RfStarTree<Shot> getTree( )
	{
		return tree;
	}
	
	public void setAmbientLight( float ambientLight )
	{
		this.ambient.value( ambientLight );
	}
	
	public void setNearDist( float nearDist )
	{
		this.nearDist.value( nearDist );
	}
	
	public void setFarDist( float farDist )
	{
		this.farDist.value( farDist );
	}
	
	public void setLoParam( float loParam )
	{
		this.loParam.value( loParam );
	}
	
	public void setHiParam( float hiParam )
	{
		this.hiParam.value( hiParam );
	}
	
	public void setDepthAxis( float[ ] axis )
	{
		depthAxis.value( axis );
	}
	
	public void setDepthOrigin( float[ ] origin )
	{
		depthOrigin.value( origin );
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
	
	public void getCenter( float[ ] center )
	{
		float[ ] mbr = tree.getRoot( ).mbr( );
		center[ 0 ] = ( mbr[ 0 ] + mbr[ 3 ] ) * 0.5f;
		center[ 1 ] = ( mbr[ 1 ] + mbr[ 4 ] ) * 0.5f;
		center[ 2 ] = ( mbr[ 2 ] + mbr[ 5 ] ) * 0.5f;
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
		
		public void getCoordinate( int i , float[ ] result )
		{
			ByteBuffer indexBuffer = segment.fillIndices.buffer( );
			ByteBuffer vertBuffer = segment.geometry.buffer( );
			indexBuffer.position( indexInSegment * FILL_IPS * BPI + i * BPI );
			vertBuffer.position( indexBuffer.getInt( ) * GEOM_BPV );
			result[ 0 ] = vertBuffer.getFloat( );
			result[ 1 ] = vertBuffer.getFloat( );
			result[ 2 ] = vertBuffer.getFloat( );
			vertBuffer.position( 0 );
			indexBuffer.position( 0 );
		}
		
		public void pick( float[ ] rayOrigin , float[ ] rayDirection , ShotPickContext c , List<PickResult<Shot>> pickResults )
		{
			ShotPickResult result = null;
			
			ByteBuffer indexBuffer = segment.fillIndices.buffer( );
			ByteBuffer vertBuffer = segment.geometry.buffer( );
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
	
	public static final class ShotPickContext
	{
		final LinePlaneIntersection3f	lpx	= new LinePlaneIntersection3f( );
		final float[ ]					p0	= new float[ 3 ];
		final float[ ]					p1	= new float[ 3 ];
		final float[ ]					p2	= new float[ 3 ];
	}
	
	public static class ShotPickResult extends PickResult<Shot>
	{
		public float	locationAlongShot;
	}
	
	public static class Segment
	{
		final ArrayList<Shot>	shots	= new ArrayList<Shot>( );
		
		SharedBuffer			geometry;
		SharedBuffer			stationAttrs;
		boolean					stationAttrsNeedRebuffering;
		SharedBuffer			fillIndices;
		SharedBuffer			lineIndices;
		
		void addShot( Shot shot )
		{
			shot.segment = this;
			shot.indexInSegment = shots.size( );
			shots.add( shot );
		}
		
		void populateData( ByteBuffer allGeomBuffer )
		{
			geometry = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * GEOM_BPS ) );
			stationAttrs = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * STATION_ATTR_BPS ) );
			fillIndices = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * FILL_IPS ) );
			lineIndices = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * LINE_IPS ) );
			
			for( Shot shot : shots )
			{
				copyBytes( allGeomBuffer , geometry.buffer( ) , shot.index , GEOM_BPS );
			}
			
			createFillIndices( fillIndices.buffer( ) , shots.size( ) );
			createLineIndices( lineIndices.buffer( ) , shots.size( ) );
			
			geometry.buffer( ).position( 0 );
			stationAttrs.buffer( ).position( 0 );
			fillIndices.buffer( ).position( 0 );
			lineIndices.buffer( ).position( 0 );
		}
	}
	
	private void updateParamTexture( GL2ES2 gl )
	{
		if( paramTextureImage == null )
		{
			paramTextureImage = DirectDataBufferInt.createBufferedImage( 256 , 256 , BufferedImage.TYPE_INT_BGR ,
					new Point( ) , new Hashtable<Object, Object>( ) );
		}
		
		Graphics2D g2 = paramTextureImage.createGraphics( );
		
		g2.clearRect( 0 , 0 , paramTextureImage.getWidth( ) , paramTextureImage.getHeight( ) );
		
		if( paramPaint != null )
		{
			g2.setPaint( new ParamGradientMapPaint(
					new float[ ] { 0 , 0 } ,
					new float[ ] { 0 , paramTextureImage.getHeight( ) } ,
					new float[ ] { paramTextureImage.getWidth( ) , 0 } ,
					0 , 1 ,
					paramPaint.getFractions( ) ,
					paramPaint.getColors( ) ) );
			g2.fillRect( 0 , 0 , paramTextureImage.getWidth( ) , paramTextureImage.getHeight( ) );
		}
		
		g2.dispose( );
		
		IntBuffer paramTextureBuffer = ( ( DirectDataBufferInt ) paramTextureImage.getRaster( ).getDataBuffer( ) ).getData( );
		
		if( paramTexture == 0 )
		{
			int textures[] = new int[ 1 ];
			gl.glGenTextures( 1 , textures , 0 );
			paramTexture = textures[ 0 ];
		}
		gl.glBindTexture( GL_TEXTURE_2D , paramTexture );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_WRAP_S , GL_CLAMP_TO_EDGE );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_WRAP_T , GL_CLAMP_TO_EDGE );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_MAG_FILTER , GL_LINEAR );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_MIN_FILTER , GL_LINEAR );
		gl.glTexImage2D( GL_TEXTURE_2D , 0 , GL_RGBA , paramTextureImage.getWidth( ) , paramTextureImage.getHeight( ) ,
				0 , GL_RGBA , GL_UNSIGNED_BYTE , paramTextureBuffer );
		gl.glBindTexture( GL_TEXTURE_2D , 0 );
	}
	
	private void disposeParamTexture( GL2ES2 gl )
	{
		if( paramTexture > 0 )
		{
			gl.glDeleteTextures( 1 , new int[ ] { paramTexture } , 0 );
			paramTexture = 0;
		}
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
		ByteBuffer buffer = segment.stationAttrs.buffer( );
		buffer.position( 0 );
		for( int i = 0 ; i < buffer.capacity( ) ; i += STATION_ATTR_BPV )
		{
			buffer.putFloat( i , -Float.MAX_VALUE );
			buffer.putFloat( i + 4 , -Float.MAX_VALUE );
			buffer.putFloat( i + 8 , 0f );
		}
	}
	
	private void applyHoverHighlights( Shot shot )
	{
		SurveyShot origShot = originalShots.get( shot.index );
		ByteBuffer buffer = shot.segment.stationAttrs.buffer( );
		
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
			ByteBuffer buffer = shot.segment.stationAttrs.buffer( );
			
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
	
	private void applySelectionHighlights( Shot shot )
	{
		ByteBuffer buffer = shot.segment.stationAttrs.buffer( );
		for( int i = 0 ; i < STATION_ATTR_VPS ; i++ )
		{
			buffer.putFloat( shot.indexInSegment * STATION_ATTR_BPS + 8 + i * STATION_ATTR_BPV , 2f );
		}
	}
	
	private static interface SegmentDrawer extends JoglResource
	{
		public void draw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n );
	}
	
	private class AxialLineSegmentDrawer implements SegmentDrawer
	{
		private int	program	= 0;
		
		private int	m_location;
		private int	v_location;
		private int	p_location;
		private int	n_location;
		
		private int	a_pos_location;
		private int	a_norm_location;
		private int	u_ambient_location;
		
		private int	u_axis_location;
		private int	u_origin_location;
		
		private int	u_loParam_location;
		private int	u_hiParam_location;
		private int	u_paramSampler_location;
		
		private int	u_nearDist_location;
		private int	u_farDist_location;
		
		private int	a_glow_location;
		private int	u_glowColor_location;
		
		private int	a_highlightIndex_location;
		private int	u_highlightColors_location;
		
		public void init( GL2ES2 gl )
		{
			String vertShader, fragShader;
			
			if( program <= 0 )
			{
				vertShader = "uniform mat4 m;" +
						"uniform mat4 p;" +
						"uniform mat4 v;" +
						
						"attribute vec3 a_pos;" +
						
						// lighting
						"attribute vec3 a_norm;" +
						"varying vec3 v_norm;" +
						"uniform mat3 n;" +
						
						// depth coloration
						"varying float v_param;" +
						"uniform vec3 u_axis;" +
						"uniform vec3 u_origin;" +
						
						// distance coloration
						"varying float v_dist;" +
						
						// glow
						"varying vec2 v_glow;" +
						"attribute vec2 a_glow;" +
						
						// highlights
						"attribute float a_highlightIndex;" +
						"varying float v_highlightIndex;" +
						
						"void main() " +
						"{" +
						"  gl_Position = p * v * m * vec4(a_pos, 1.0);" +
						"  gl_Position.z += 0.1;" +
						"  v_norm = (v * vec4(normalize(n * a_norm), 0.0)).xyz;" +
						"  v_param = dot(a_pos - u_origin, u_axis);" +
						"  v_dist = -(v * m * vec4(a_pos, 1.0)).z;" +
						"  v_glow = a_glow;" +
						"  v_highlightIndex = a_highlightIndex;" +
						"}";
				
				fragShader = "varying vec3 v_norm;" +
						"uniform float u_ambient;" +
						
						// param coloration
						"varying float v_param;" +
						"uniform float u_loParam;" +
						"uniform float u_hiParam;" +
						"uniform sampler2D u_paramSampler;" +
						
						// distance coloration
						"varying float v_dist;" +
						"uniform float u_farDist;" +
						"uniform float u_nearDist;" +
						
						// glow
						"varying vec2 v_glow;" +
						"uniform vec4 u_glowColor;" +
						
						// highlights
						"uniform vec4 u_highlightColors[3];" +
						"varying float v_highlightIndex;" +
						
						"void main() " +
						"{" +
						"  float temp;" +
						"  vec4 indexedHighlight;" +
						
						// param coloration
						"  gl_FragColor = texture2D(u_paramSampler, vec2(0.5, clamp((v_param - u_loParam) / (u_hiParam - u_loParam), 0.0, 1.0)));" +
						
						// distance coloration
						"  gl_FragColor = mix(gl_FragColor, gl_FragColor * u_ambient, clamp((v_dist - u_nearDist) / (u_farDist - u_nearDist), 0.0, 1.0));" +
						
						// glow
						"  gl_FragColor = mix(gl_FragColor, u_glowColor, clamp(min(v_glow.x, v_glow.y), 0.0, 1.0));" +
						
						// lighting
						"  temp = dot(v_norm, vec3(0.0, 0.0, 1.0));" +
						"  temp = u_ambient + temp * (1.0 - u_ambient);" +
						"  gl_FragColor = temp * gl_FragColor;" +
						
						// highlights
						"  indexedHighlight = u_highlightColors[int(floor(v_highlightIndex + 0.5))];" +
						"  gl_FragColor = clamp(gl_FragColor + vec4(indexedHighlight.xyz * indexedHighlight.w, 0.0), 0.0, 1.0);" +
						"}";
				
				program = JOGLUtils.loadProgram( gl , vertShader , fragShader );
				
				m_location = gl.glGetUniformLocation( program , "m" );
				v_location = gl.glGetUniformLocation( program , "v" );
				p_location = gl.glGetUniformLocation( program , "p" );
				n_location = gl.glGetUniformLocation( program , "n" );
				
				a_pos_location = gl.glGetAttribLocation( program , "a_pos" );
				a_norm_location = gl.glGetAttribLocation( program , "a_norm" );
				
				u_axis_location = gl.glGetUniformLocation( program , "u_axis" );
				u_origin_location = gl.glGetUniformLocation( program , "u_origin" );
				
				a_glow_location = gl.glGetAttribLocation( program , "a_glow" );
				a_highlightIndex_location = gl.glGetAttribLocation( program , "a_highlightIndex" );
				
				u_ambient_location = gl.glGetUniformLocation( program , "u_ambient" );
				u_loParam_location = gl.glGetUniformLocation( program , "u_loParam" );
				u_hiParam_location = gl.glGetUniformLocation( program , "u_hiParam" );
				u_paramSampler_location = gl.glGetUniformLocation( program , "u_paramSampler" );
				
				u_nearDist_location = gl.glGetUniformLocation( program , "u_nearDist" );
				u_farDist_location = gl.glGetUniformLocation( program , "u_farDist" );
				
				u_glowColor_location = gl.glGetUniformLocation( program , "u_glowColor" );
				
				u_highlightColors_location = gl.glGetUniformLocation( program , "u_highlightColors" );
			}
		}
		
		public void dispose( GL2ES2 gl )
		{
			if( program > 0 )
			{
				gl.glDeleteProgram( program );
				program = 0;
			}
		}
		
		@Override
		public void draw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			if( program <= 0 )
			{
				init( gl );
			}
			
			gl.glUseProgram( program );
			
			gl.glActiveTexture( GL_TEXTURE0 );
			gl.glBindTexture( GL_TEXTURE_2D , paramTexture );
			gl.glUniform1i( u_paramSampler_location , 0 );
			
			gl.glUniformMatrix4fv( m_location , 1 , false , m , 0 );
			gl.glUniformMatrix3fv( n_location , 1 , false , n , 0 );
			gl.glUniformMatrix4fv( v_location , 1 , false , context.viewXform( ) , 0 );
			gl.glUniformMatrix4fv( p_location , 1 , false , context.projXform( ) , 0 );
			
			gl.glUniform3fv( u_axis_location , 1 , depthAxis.value( ) , 0 );
			gl.glUniform3fv( u_origin_location , 1 , depthOrigin.value( ) , 0 );
			
			gl.glUniform1fv( u_ambient_location , 1 , ambient.value( ) , 0 );
			
			gl.glUniform1fv( u_loParam_location , 1 , loParam.value( ) , 0 );
			gl.glUniform1fv( u_hiParam_location , 1 , hiParam.value( ) , 0 );
			
			gl.glUniform1fv( u_nearDist_location , 1 , nearDist.value( ) , 0 );
			gl.glUniform1fv( u_farDist_location , 1 , farDist.value( ) , 0 );
			
			gl.glUniform4fv( u_glowColor_location , 1 , glowColor.value( ) , 0 );
			
			gl.glUniform4fv( u_highlightColors_location , highlightColors.count( ) , highlightColors.value( ) , 0 );
			
			gl.glEnableVertexAttribArray( a_pos_location );
			gl.glEnableVertexAttribArray( a_norm_location );
			gl.glEnableVertexAttribArray( a_glow_location );
			gl.glEnableVertexAttribArray( a_highlightIndex_location );
			
			gl.glEnable( GL_DEPTH_TEST );
			
			for( Segment segment : segments )
			{
				draw( segment , context , gl , m , n );
			}
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , 0 );
			
			gl.glDisable( GL_DEPTH_TEST );
		}
		
		public void draw( Segment segment , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			segment.geometry.init( gl );
			segment.stationAttrs.init( gl );
			if( segment.stationAttrsNeedRebuffering )
			{
				segment.stationAttrs.rebuffer( gl );
				segment.stationAttrsNeedRebuffering = false;
			}
			segment.lineIndices.init( gl );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.geometry.id( ) );
			gl.glVertexAttribPointer( a_pos_location , 3 , GL_FLOAT , false , GEOM_BPV , 0 );
			gl.glVertexAttribPointer( a_norm_location , 3 , GL_FLOAT , false , GEOM_BPV , 12 );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.stationAttrs.id( ) );
			gl.glVertexAttribPointer( a_glow_location , 2 , GL_FLOAT , false , STATION_ATTR_BPV , 0 );
			gl.glVertexAttribPointer( a_highlightIndex_location , 1 , GL_FLOAT , false , STATION_ATTR_BPV , 8 );
			
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , segment.lineIndices.id( ) );
			
			gl.glDrawElements( GL_LINES , segment.lineIndices.buffer( ).capacity( ) / BPI , GL_UNSIGNED_INT , 0 );
		}
	}
	
	private class AxialFillSegmentDrawer implements SegmentDrawer
	{
		private int	program	= 0;
		
		private int	m_location;
		private int	v_location;
		private int	p_location;
		private int	n_location;
		private int	a_pos_location;
		private int	a_norm_location;
		private int	u_ambient_location;
		
		private int	u_axis_location;
		private int	u_origin_location;
		
		private int	u_loParam_location;
		private int	u_hiParam_location;
		private int	u_paramSampler_location;
		
		private int	u_nearDist_location;
		private int	u_farDist_location;
		
		private int	a_glow_location;
		private int	u_glowColor_location;
		
		private int	a_highlightIndex_location;
		private int	u_highlightColors_location;
		
		public void init( GL2ES2 gl )
		{
			String vertShader, fragShader;
			
			if( program <= 0 )
			{
				vertShader = "uniform mat4 m;" +
						"uniform mat4 v;" +
						"uniform mat4 p;" +
						"attribute vec3 a_pos;" +
						
						// lighting
						"attribute vec3 a_norm;" +
						"varying vec3 v_norm;" +
						"uniform mat3 n;" +
						
						// depth coloration
						"uniform vec3 u_axis;" +
						"uniform vec3 u_origin;" +
						"varying float v_param;" +
						
						// distance coloration
						"varying float v_dist;" +
						
						// glow
						"attribute vec2 a_glow;" +
						"varying vec2 v_glow;" +
						
						// highlights
						"attribute float a_highlightIndex;" +
						"varying float v_highlightIndex;" +
						
						"void main() " +
						"{" +
						"  gl_Position = p * v * m * vec4(a_pos, 1.0);" +
						"  v_norm = (v * vec4(normalize(n * a_norm), 0.0)).xyz;" +
						"  v_param = dot(a_pos - u_origin, u_axis);" +
						"  v_dist = -(v * m * vec4(a_pos, 1.0)).z;" +
						"  v_glow = a_glow;" +
						"  v_highlightIndex = a_highlightIndex;" +
						"}";
				
				fragShader = "varying vec3 v_norm;" +
						"uniform float u_ambient;" +
						
						// param coloration
						"varying float v_param;" +
						"uniform float u_loParam;" +
						"uniform float u_hiParam;" +
						"uniform sampler2D u_paramSampler;" +
						
						// distance coloration
						"varying float v_dist;" +
						"uniform float u_farDist;" +
						"uniform float u_nearDist;" +
						
						// glow
						"varying vec2 v_glow;" +
						"uniform vec4 u_glowColor;" +
						
						// highlights
						"uniform vec4 u_highlightColors[3];" +
						"varying float v_highlightIndex;" +
						
						"void main() " +
						"{" +
						"  float temp;" +
						"  vec4 indexedHighlight;" +
						
						// param coloration
						"  gl_FragColor = texture2D(u_paramSampler, vec2(0.5, clamp((v_param - u_loParam) / (u_hiParam - u_loParam), 0.0, 1.0)));" +
						
						// distance coloration
						"  gl_FragColor = mix(gl_FragColor, gl_FragColor * u_ambient, clamp((v_dist - u_nearDist) / (u_farDist - u_nearDist), 0.0, 1.0));" +
						
						// glow
						"  gl_FragColor = mix(gl_FragColor, u_glowColor, clamp(min(v_glow.x, v_glow.y), 0.0, 1.0));" +
						
						// lighting
						"  temp = dot(v_norm, vec3(0.0, 0.0, 1.0));" +
						"  temp = u_ambient + temp * (1.0 - u_ambient);" +
						"  gl_FragColor.xyz = temp * gl_FragColor.xyz;" +
						
						// highlights
						"  indexedHighlight = u_highlightColors[int(floor(v_highlightIndex + 0.5))];" +
						"  gl_FragColor = clamp(gl_FragColor + vec4(indexedHighlight.xyz * indexedHighlight.w, 0.0), 0.0, 1.0);" +
						"}";
				
				program = JOGLUtils.loadProgram( gl , vertShader , fragShader );
				
				m_location = gl.glGetUniformLocation( program , "m" );
				v_location = gl.glGetUniformLocation( program , "v" );
				p_location = gl.glGetUniformLocation( program , "p" );
				n_location = gl.glGetUniformLocation( program , "n" );
				
				a_pos_location = gl.glGetAttribLocation( program , "a_pos" );
				a_norm_location = gl.glGetAttribLocation( program , "a_norm" );
				
				u_axis_location = gl.glGetUniformLocation( program , "u_axis" );
				u_origin_location = gl.glGetUniformLocation( program , "u_origin" );
				
				a_glow_location = gl.glGetAttribLocation( program , "a_glow" );
				a_highlightIndex_location = gl.glGetAttribLocation( program , "a_highlightIndex" );
				
				u_ambient_location = gl.glGetUniformLocation( program , "u_ambient" );
				u_loParam_location = gl.glGetUniformLocation( program , "u_loParam" );
				u_hiParam_location = gl.glGetUniformLocation( program , "u_hiParam" );
				u_paramSampler_location = gl.glGetUniformLocation( program , "u_paramSampler" );
				
				u_nearDist_location = gl.glGetUniformLocation( program , "u_nearDist" );
				u_farDist_location = gl.glGetUniformLocation( program , "u_farDist" );
				
				u_glowColor_location = gl.glGetUniformLocation( program , "u_glowColor" );
				
				u_highlightColors_location = gl.glGetUniformLocation( program , "u_highlightColors" );
			}
		}
		
		public void dispose( GL2ES2 gl )
		{
			if( program > 0 )
			{
				gl.glDeleteProgram( program );
				program = 0;
			}
		}
		
		@Override
		public void draw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			if( program <= 0 )
			{
				init( gl );
			}
			
			gl.glUseProgram( program );
			
			gl.glActiveTexture( GL_TEXTURE0 );
			gl.glBindTexture( GL_TEXTURE_2D , paramTexture );
			gl.glUniform1i( u_paramSampler_location , 0 );
			
			gl.glUniformMatrix4fv( m_location , 1 , false , m , 0 );
			gl.glUniformMatrix3fv( n_location , 1 , false , n , 0 );
			gl.glUniformMatrix4fv( v_location , 1 , false , context.viewXform( ) , 0 );
			gl.glUniformMatrix4fv( p_location , 1 , false , context.projXform( ) , 0 );
			
			gl.glUniform3fv( u_axis_location , 1 , depthAxis.value( ) , 0 );
			gl.glUniform3fv( u_origin_location , 1 , depthOrigin.value( ) , 0 );
			
			gl.glUniform1fv( u_ambient_location , 1 , ambient.value( ) , 0 );
			
			gl.glUniform1fv( u_loParam_location , 1 , loParam.value( ) , 0 );
			gl.glUniform1fv( u_hiParam_location , 1 , hiParam.value( ) , 0 );
			
			gl.glUniform1fv( u_nearDist_location , 1 , nearDist.value( ) , 0 );
			gl.glUniform1fv( u_farDist_location , 1 , farDist.value( ) , 0 );
			
			gl.glUniform4fv( u_glowColor_location , 1 , glowColor.value( ) , 0 );
			
			gl.glUniform4fv( u_highlightColors_location , highlightColors.count( ) , highlightColors.value( ) , 0 );
			
			gl.glEnableVertexAttribArray( a_pos_location );
			gl.glEnableVertexAttribArray( a_norm_location );
			gl.glEnableVertexAttribArray( a_glow_location );
			gl.glEnableVertexAttribArray( a_highlightIndex_location );
			
			gl.glEnable( GL_DEPTH_TEST );

			for( Segment segment : segments )
			{
				draw( segment , context , gl , m , n );
			}

			gl.glDisable( GL_DEPTH_TEST );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , 0 );
		}
		
		public void draw( Segment segment , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			segment.geometry.init( gl );
			segment.stationAttrs.init( gl );
			if( segment.stationAttrsNeedRebuffering )
			{
				segment.stationAttrs.rebuffer( gl );
				segment.stationAttrsNeedRebuffering = false;
			}
			segment.fillIndices.init( gl );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.geometry.id( ) );
			gl.glVertexAttribPointer( a_pos_location , 3 , GL_FLOAT , false , GEOM_BPV , 0 );
			gl.glVertexAttribPointer( a_norm_location , 3 , GL_FLOAT , false , GEOM_BPV , 12 );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.stationAttrs.id( ) );
			gl.glVertexAttribPointer( a_glow_location , 2 , GL_FLOAT , false , STATION_ATTR_BPV , 0 );
			gl.glVertexAttribPointer( a_highlightIndex_location , 1 , GL_FLOAT , false , STATION_ATTR_BPV , 8 );
			
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , segment.fillIndices.id( ) );
			
			gl.glDrawElements( GL_TRIANGLES , segment.fillIndices.buffer( ).capacity( ) / BPI , GL_UNSIGNED_INT , 0 );
		}
	}
	
	@Override
	public void init( GL2ES2 gl )
	{
	}
	
	@Override
	public void dispose( GL2ES2 gl )
	{
		disposeParamTexture( gl );
	}
	
	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
	{
		if( paramTextureNeedsUpdate )
		{
			updateParamTexture( gl );
			paramTextureNeedsUpdate = false;
		}
		for( SegmentDrawer drawer : drawers.keySet( ) )
		{
			drawer.draw( drawers.get( drawer ) , context , gl , m , n );
		}
	}
}
