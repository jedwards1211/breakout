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
import java.awt.PageAttributes.OriginType;
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
import org.andork.breakout.model.Survey3dModel.Segment;
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
import org.andork.math3d.InConeTester3f;
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
	
	private static final int			GEOM_BPV			= 24;
	
	private static final int			GEOM_VPS			= 8;
	
	private static final int			GEOM_BPS			= GEOM_BPV * GEOM_VPS;
	
	private static final int			STATION_ATTR_BPV	= 12;
	
	private static final int			STATION_ATTR_VPS	= GEOM_VPS;
	
	private static final int			STATION_ATTR_BPS	= STATION_ATTR_BPV * STATION_ATTR_VPS;
	
	private static final int			BPI					= 4;
	
	private static final int			FILL_IPS			= 24;
	
	private static final int			LINE_IPS			= 32;
	
	List<SurveyShot>					originalShots;
	List<Shot>							shots;
	
	RfStarTree<Shot>					tree;
	
	Set<Segment>						segments;
	
	MultiMap<SegmentDrawer, Segment>	drawers				= HashSetMultiMap.newInstance( );
	
	final Set<Shot>						selectedShots		= new HashSet<Shot>( );
	Shot								hoveredShot;
	Float								hoverLocation;
	LinearAxisConversion				highlightExtentConversion;
	
	LinearGradientPaint					paramPaint;
	int									paramTexture;
	BufferedImageInt					paramTextureImage;
	boolean								paramTextureNeedsUpdate;
	
	Uniform4fv							highlightColors;
	
	Uniform3fv							depthAxis;
	Uniform3fv							depthOrigin;
	
	Uniform1fv							ambient;
	
	Uniform1fv							nearDist;
	Uniform1fv							farDist;
	
	Uniform1fv							loParam;
	Uniform1fv							hiParam;
	
	Uniform4fv							glowColor;
	
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
		
		AxialSegmentDrawer axialSegmentDrawer = new AxialSegmentDrawer( );
		drawers.putAll( axialSegmentDrawer , segments );
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
				geomHelper.putAsFloats( shot.from.position[ i ] + leftAtFrom[ i ] * shot.fromXsection.dist[ 0 ] );
			}
			geomHelper.putAsFloats( leftAtFrom );
			for( int i = 0 ; i < 3 ; i++ )
			{
				geomHelper.putAsFloats( shot.from.position[ i ] - leftAtFrom[ i ] * shot.fromXsection.dist[ 1 ] );
			}
			geomHelper.putAsFloats( -leftAtFrom[ 0 ] , -leftAtFrom[ 1 ] , -leftAtFrom[ 2 ] );
			for( int i = 0 ; i < 3 ; i++ )
			{
				geomHelper.putAsFloats( shot.from.position[ i ] + ( i == 1 ? shot.fromXsection.dist[ 2 ] : 0.0 ) );
			}
			geomHelper.putAsFloats( 0 , 1 , 0 );
			for( int i = 0 ; i < 3 ; i++ )
			{
				geomHelper.putAsFloats( shot.from.position[ i ] - ( i == 1 ? shot.fromXsection.dist[ 3 ] : 0.0 ) );
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
						geomHelper.putAsFloats( shot.to.position[ i ] + leftAtTo[ i ] * bestShot.fromXsection.dist[ 0 ] );
					}
					geomHelper.putAsFloats( leftAtTo );
					for( int i = 0 ; i < 3 ; i++ )
					{
						geomHelper.putAsFloats( shot.to.position[ i ] - leftAtTo[ i ] * bestShot.fromXsection.dist[ 1 ] );
					}
					geomHelper.putAsFloats( -leftAtTo[ 0 ] , -leftAtTo[ 1 ] , -leftAtTo[ 2 ] );
					for( int i = 0 ; i < 3 ; i++ )
					{
						geomHelper.putAsFloats( shot.to.position[ i ] + ( i == 1 ? bestShot.fromXsection.dist[ 2 ] : 0.0 ) );
					}
					geomHelper.putAsFloats( 0 , 1 , 0 );
					for( int i = 0 ; i < 3 ; i++ )
					{
						geomHelper.putAsFloats( shot.to.position[ i ] - ( i == 1 ? bestShot.fromXsection.dist[ 3 ] : 0.0 ) );
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
	
	public void pickShots( float[ ] coneOrigin , float[ ] coneDirection , float coneAngle ,
			ShotPickContext spc , List<PickResult<Shot>> pickResults )
	{
		pickShots( tree.getRoot( ) , coneOrigin , coneDirection , coneAngle , spc , pickResults );
	}
	
	private void pickShots( RNode<float[ ], Shot> node , float[ ] coneOrigin , float[ ] coneDirection , float coneAngle ,
			ShotPickContext spc , List<PickResult<Shot>> pickResults )
	{
		if( spc.inConeTester.boxIntersectsCone( node.mbr( ) , coneOrigin , coneDirection , coneAngle ) )
		{
			if( node instanceof RBranch )
			{
				RBranch<float[ ], Shot> branch = ( RBranch<float[ ], Shot> ) node;
				for( int i = 0 ; i < branch.numChildren( ) ; i++ )
				{
					pickShots( branch.childAt( i ) , coneOrigin , coneDirection , coneAngle , spc , pickResults );
				}
			}
			else if( node instanceof RLeaf )
			{
				Shot shot = ( ( RLeaf<float[ ], Shot> ) node ).object( );
				shot.pick( coneOrigin , coneDirection , coneAngle , spc , pickResults );
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
		return hoveredShot == null ? Collections.<Shot>emptySet( ) : Collections.singleton( hoveredShot );
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
	
	private void updateHighlights( Collection<Shot> affectedShots , Shot prevHoveredShot , Float prevHoverLocation , LinearAxisConversion prevHighlightExtentConversion )
	{
		// find the segments that are affected by the affected shots
		// (not just the segments containing those shots but segments containing
		// shots within highlight distance from an affected shot)
		Set<Segment> affectedSegments = new HashSet<Segment>( );
		for( Shot shot : affectedShots )
		{
			affectedSegments.add( shot.segment );
		}
		
		if( prevHoveredShot != null )
		{
			findAffectedSegments( prevHoveredShot , affectedSegments , prevHoverLocation , ( float ) prevHighlightExtentConversion.invert( 0.0 ) );
		}
		
		if( hoveredShot != null )
		{
			findAffectedSegments( hoveredShot , affectedSegments , hoverLocation , ( float ) highlightExtentConversion.invert( 0.0 ) );
		}
		
		for( Segment segment : affectedSegments )
		{
			clearHighlights( segment );
		}
		
		applyHoverHighlights( );
		
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
	
	private void findAffectedSegments( Shot shot , Set<Segment> affectedSegments , float location , float distance )
	{
		Set<Shot> visitedShots = new HashSet<Shot>( );
		
		SurveyShot origShot = originalShots.get( shot.number );
		
		float forwardRemainingDistance = distance * 2 - ( float ) origShot.dist * ( 1f - location );
		float backwardRemainingDistance = distance * 2 - ( float ) origShot.dist * location;
		
		findAffectedSegments( origShot.to , Direction.FORWARD , visitedShots , forwardRemainingDistance , affectedSegments );
		findAffectedSegments( origShot.from , Direction.BACKWARD , visitedShots , backwardRemainingDistance , affectedSegments );
	}
	
	private void findAffectedSegments( SurveyStation station , Direction direction , Set<Shot> visitedShots , float remainingDistance , Set<Segment> affectedSegments )
	{
		for( SurveyShot next : station.frontsights )
		{
			findAffectedSegments( shots.get( next.number ) , Direction.FORWARD , visitedShots , remainingDistance ,
					affectedSegments );
		}
		for( SurveyShot next : station.backsights )
		{
			findAffectedSegments( shots.get( next.number ) , Direction.BACKWARD , visitedShots , remainingDistance ,
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
				SurveyShot origShot = originalShots.get( shot.number );
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
	
	private void applyHoverHighlights( )
	{
		if( hoveredShot == null )
		{
			return;
		}
		
		SurveyShot origShot = originalShots.get( hoveredShot.number );
		ByteBuffer buffer = hoveredShot.segment.stationAttrs.buffer( );
		
		float distToFrom = ( float ) ( origShot.dist * hoverLocation );
		float distToTo = ( float ) ( origShot.dist * ( 1f - hoverLocation ) );
		
		applyHoverHighlights( origShot.from , Direction.BACKWARD , distToFrom , highlightExtentConversion );
		applyHoverHighlights( origShot.to , Direction.FORWARD , distToTo , highlightExtentConversion );
		
		float fromHighlightA = ( float ) highlightExtentConversion.convert( distToTo - origShot.dist );
		float fromHighlightB = ( float ) highlightExtentConversion.convert( distToFrom );
		float toHighlightA = ( float ) highlightExtentConversion.convert( distToTo );
		float toHighlightB = ( float ) highlightExtentConversion.convert( distToFrom - origShot.dist );
		
		setFromHighlightA( buffer , hoveredShot.indexInSegment , fromHighlightA );
		setFromHighlightB( buffer , hoveredShot.indexInSegment , fromHighlightB );
		setToHighlightA( buffer , hoveredShot.indexInSegment , toHighlightA );
		setToHighlightB( buffer , hoveredShot.indexInSegment , toHighlightB );
	}
	
	private void applyHoverHighlights( SurveyStation station , Direction direction , float distance , LinearAxisConversion highlightConversion )
	{
		for( SurveyShot next : station.frontsights )
		{
			applyHoverHighlights( shots.get( next.number ) , Direction.FORWARD , distance , highlightConversion );
		}
		for( SurveyShot next : station.backsights )
		{
			applyHoverHighlights( shots.get( next.number ) , Direction.BACKWARD , distance , highlightConversion );
		}
	}
	
	private void applyHoverHighlights( Shot shot , Direction direction , float distance , LinearAxisConversion highlightConversion )
	{
		if( highlightConversion.convert( distance ) >= 0.0 )
		{
			ByteBuffer buffer = shot.segment.stationAttrs.buffer( );
			
			SurveyShot origShot = originalShots.get( shot.number );
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
	
	public final class SelectionEditor
	{
		private SelectionEditor( )
		{
			
		}
		
		final Set<Shot>			selected	= new HashSet<Shot>( );
		final Set<Shot>			deselected	= new HashSet<Shot>( );
		Shot					newHoveredShot;
		Float					newHoverLocation;
		LinearAxisConversion	newHighlightExtentConversion;
		
		boolean					committed	= false;
		
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
			newHoveredShot = shot;
			newHoverLocation = location;
			newHighlightExtentConversion = new LinearAxisConversion( highlightExtent );
			return this;
		}
		
		public SelectionEditor unhover( )
		{
			newHoveredShot = null;
			newHoverLocation = null;
			newHighlightExtentConversion = null;
			return this;
		}
		
		public void commit( )
		{
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
			Set<Shot> affectedShots = new HashSet<Shot>( );
			affectedShots.addAll( selected );
			affectedShots.addAll( deselected );
			if( hoveredShot != null )
			{
				affectedShots.add( hoveredShot );
			}
			if( newHoveredShot != null )
			{
				affectedShots.add( newHoveredShot );
			}
			
			Shot prevHoveredShot = hoveredShot;
			Float prevHoverLocation = hoverLocation;
			LinearAxisConversion prevHighlightExtentConversion = highlightExtentConversion;
			
			hoveredShot = newHoveredShot;
			hoverLocation = newHoverLocation;
			highlightExtentConversion = newHighlightExtentConversion;
			
			updateHighlights( affectedShots , prevHoveredShot , prevHoverLocation , prevHighlightExtentConversion );
		}
	}
	
	public static class Shot
	{
		int		number;
		
		Segment	segment;
		int		indexInSegment;
		
		Shot( int number )
		{
			super( );
			this.number = number;
		}
		
		public int getIndex( )
		{
			return number;
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
		
		public void pick( float[ ] coneOrigin , float[ ] coneDirection , float coneAngle , ShotPickContext c , List<PickResult<Shot>> pickResults )
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
					c.lpx.lineFromRay( coneOrigin , coneDirection );
					c.lpx.planeFromPoints( c.p0 , c.p1 , c.p2 );
					c.lpx.findIntersection( );
					if( c.lpx.isPointIntersection( ) && c.lpx.isOnRay( ) && c.lpx.isInTriangle( ) )
					{
						if( result == null || result.lateralDistance > 0 || c.lpx.t < result.distance )
						{
							if( result == null )
							{
								result = new ShotPickResult( );
							}
							result.picked = this;
							result.distance = c.lpx.t;
							result.locationAlongShot = i % 2 == 0 ? c.lpx.u : 1 - c.lpx.u;
							result.lateralDistance = 0;
							setf( result.location , c.lpx.result );
						}
					}
					else if( result == null || result.lateralDistance > 0 )
					{
						if( c.inConeTester.isLineSegmentInCone( c.p0 , c.p1 , coneOrigin , coneDirection , coneAngle ) )
						{
							if( result == null || c.inConeTester.lateralDistance * result.distance <
									result.lateralDistance * c.inConeTester.t )
							{
								if( result == null )
								{
									result = new ShotPickResult( );
								}
								result.picked = this;
								result.distance = c.inConeTester.t;
								result.lateralDistance = c.inConeTester.lateralDistance;
								result.locationAlongShot = i % 2 == 0 ? c.inConeTester.s : 1 - c.inConeTester.s;
								Vecmath.interp3( c.p0 , c.p1 , c.inConeTester.s , result.location );
							}
						}
						else if( c.inConeTester.isLineSegmentInCone( c.p1 , c.p2 , coneOrigin , coneDirection , coneAngle ) )
						{
							if( result == null || c.inConeTester.lateralDistance * result.distance <
									result.lateralDistance * c.inConeTester.t )
							{
								if( result == null )
								{
									result = new ShotPickResult( );
								}
								result.picked = this;
								result.distance = c.inConeTester.t;
								result.lateralDistance = c.inConeTester.lateralDistance;
								result.locationAlongShot = i % 2 == 0 ? 1 - c.inConeTester.s : c.inConeTester.s;
								Vecmath.interp3( c.p1 , c.p2 , c.inConeTester.s , result.location );
							}
						}
						else if( c.inConeTester.isLineSegmentInCone( c.p2 , c.p0 , coneOrigin , coneDirection , coneAngle ) )
						{
							if( result == null || c.inConeTester.lateralDistance * result.distance <
									result.lateralDistance * c.inConeTester.t )
							{
								if( result == null )
								{
									result = new ShotPickResult( );
								}
								result.picked = this;
								result.distance = c.inConeTester.t;
								result.lateralDistance = c.inConeTester.lateralDistance;
								result.locationAlongShot = i % 2;
								Vecmath.interp3( c.p2 , c.p0 , c.inConeTester.s , result.location );
							}
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
		final LinePlaneIntersection3f	lpx				= new LinePlaneIntersection3f( );
		final float[ ]					p0				= new float[ 3 ];
		final float[ ]					p1				= new float[ 3 ];
		final float[ ]					p2				= new float[ 3 ];
		final float[ ]					adjacent		= new float[ 3 ];
		final float[ ]					opposite		= new float[ 3 ];
		final InConeTester3f			inConeTester	= new InConeTester3f( );
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
		SharedBuffer			param0;
		boolean					param0NeedsRebuffering;
		SharedBuffer			fillIndices;
		SharedBuffer			lineIndices;
		
		void addShot( Shot shot )
		{
			shot.segment = this;
			shot.indexInSegment = shots.size( );
			shots.add( shot );
		}
		
		void calcParam0( Survey3dModel model )
		{
			param0 = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * GEOM_VPS * 4 ) );
			param0.buffer( ).position( 0 );
			for( Shot shot : shots )
			{
				SurveyShot origShot = model.originalShots.get( shot.number );
				float width = origShot.fromXsection.dist[ 0 ] + origShot.fromXsection.dist[ 1 ];
				float height = origShot.fromXsection.dist[ 2 ] + origShot.fromXsection.dist[ 3 ];
				float area = width * height;
				param0.buffer( ).putFloat( area );
				param0.buffer( ).putFloat( area );
				param0.buffer( ).putFloat( area );
				param0.buffer( ).putFloat( area );
				// height = origShot.toXsection.dist[ 2 ] + origShot.toXsection.dist[ 3 ];
				param0.buffer( ).putFloat( area );
				param0.buffer( ).putFloat( area );
				param0.buffer( ).putFloat( area );
				param0.buffer( ).putFloat( area );
			}
		}
		
		void populateData( ByteBuffer allGeomBuffer )
		{
			geometry = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * GEOM_BPS ) );
			stationAttrs = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * STATION_ATTR_BPS ) );
			fillIndices = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * FILL_IPS ) );
			lineIndices = new SharedBuffer( ).buffer( createBuffer( shots.size( ) * BPI * LINE_IPS ) );
			
			for( Shot shot : shots )
			{
				copyBytes( allGeomBuffer , geometry.buffer( ) , shot.number , GEOM_BPS );
			}
			
			createFillIndices( fillIndices.buffer( ) , shots.size( ) );
			createLineIndices( lineIndices.buffer( ) , shots.size( ) );
			
			geometry.buffer( ).position( 0 );
			stationAttrs.buffer( ).position( 0 );
			fillIndices.buffer( ).position( 0 );
			lineIndices.buffer( ).position( 0 );
		}
		
		public void dispose( GL2ES2 gl )
		{
			geometry.dispose( gl );
			stationAttrs.dispose( gl );
			if( param0 != null )
			{
				param0.dispose( gl );
			}
			fillIndices.dispose( gl );
			lineIndices.dispose( gl );
		}
	}
	
	private static interface SegmentDrawer extends JoglResource
	{
		public void draw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n );
	}
	
	private abstract class BaseSegmentDrawer implements SegmentDrawer
	{
		protected int	program	= 0;
		
		protected int	m_location;
		protected int	v_location;
		protected int	p_location;
		protected int	n_location;
		protected int	a_pos_location;
		protected int	a_norm_location;
		protected int	u_ambient_location;
		
		protected int	u_nearDist_location;
		protected int	u_farDist_location;
		
		protected int	a_glow_location;
		protected int	u_glowColor_location;
		
		protected int	a_highlightIndex_location;
		protected int	u_highlightColors_location;
		
		protected String createVertexShaderVariables( )
		{
			return "uniform mat4 m;" +
					"uniform mat4 v;" +
					"uniform mat4 p;" +
					"attribute vec3 a_pos;" +
					
					// lighting
					"attribute vec3 a_norm;" +
					"varying vec3 v_norm;" +
					"uniform mat3 n;" +
					
					// distance coloration
					"varying float v_dist;" +
					
					// glow
					"attribute vec2 a_glow;" +
					"varying vec2 v_glow;" +
					
					// highlights
					"attribute float a_highlightIndex;" +
					"varying float v_highlightIndex;";
		}
		
		protected String createVertexShaderCode( )
		{
			return "  gl_Position = p * v * m * vec4(a_pos, 1.0);" +
					"  v_norm = (v * vec4(normalize(n * a_norm), 0.0)).xyz;" +
					"  v_dist = -(v * m * vec4(a_pos, 1.0)).z;" +
					"  v_glow = a_glow;" +
					"  v_highlightIndex = a_highlightIndex;";
		}
		
		protected String createVertexShader( )
		{
			return createVertexShaderVariables( ) +
					"void main() {" +
					createVertexShaderCode( ) +
					"}";
		}
		
		protected String createFragmentShaderVariables( )
		{
			// lighting
			return "varying vec3 v_norm;" +
					"uniform float u_ambient;" +
					
					// distance coloration
					"varying float v_dist;" +
					"uniform float u_farDist;" +
					"uniform float u_nearDist;" +
					
					// glow
					"varying vec2 v_glow;" +
					"uniform vec4 u_glowColor;" +
					
					// highlights
					"uniform vec4 u_highlightColors[3];" +
					"varying float v_highlightIndex;";
			
		}
		
		protected String createFragmentShaderCode( )
		{
			return "  float temp;" +
					"  vec4 indexedHighlight;" +
					
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
					"  gl_FragColor = clamp(gl_FragColor + vec4(indexedHighlight.xyz * indexedHighlight.w, 0.0), 0.0, 1.0);";
		}
		
		protected String createFragmentShader( )
		{
			return createFragmentShaderVariables( ) +
					"void main() {" +
					createFragmentShaderCode( ) +
					"}";
		}
		
		public void init( GL2ES2 gl )
		{
			String vertShader, fragShader;
			
			if( program <= 0 )
			{
				vertShader = createVertexShader( );
				fragShader = createFragmentShader( );
				
				program = JOGLUtils.loadProgram( gl , vertShader , fragShader );
				
				m_location = gl.glGetUniformLocation( program , "m" );
				v_location = gl.glGetUniformLocation( program , "v" );
				p_location = gl.glGetUniformLocation( program , "p" );
				n_location = gl.glGetUniformLocation( program , "n" );
				
				a_pos_location = gl.glGetAttribLocation( program , "a_pos" );
				a_norm_location = gl.glGetAttribLocation( program , "a_norm" );
				
				a_glow_location = gl.glGetAttribLocation( program , "a_glow" );
				a_highlightIndex_location = gl.glGetAttribLocation( program , "a_highlightIndex" );
				
				u_ambient_location = gl.glGetUniformLocation( program , "u_ambient" );
				
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
			
			beforeDraw( segments , context , gl , m , n );
			
			for( Segment segment : segments )
			{
				draw( segment , context , gl , m , n );
			}
			
			afterDraw( segments , context , gl , m , n );
		}
		
		protected void beforeDraw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			gl.glUniformMatrix4fv( m_location , 1 , false , m , 0 );
			gl.glUniformMatrix3fv( n_location , 1 , false , n , 0 );
			gl.glUniformMatrix4fv( v_location , 1 , false , context.viewXform( ) , 0 );
			gl.glUniformMatrix4fv( p_location , 1 , false , context.projXform( ) , 0 );
			
			gl.glUniform1fv( u_ambient_location , 1 , ambient.value( ) , 0 );
			
			gl.glUniform1fv( u_nearDist_location , 1 , nearDist.value( ) , 0 );
			gl.glUniform1fv( u_farDist_location , 1 , farDist.value( ) , 0 );
			
			gl.glUniform4fv( u_glowColor_location , 1 , glowColor.value( ) , 0 );
			
			gl.glUniform4fv( u_highlightColors_location , highlightColors.count( ) , highlightColors.value( ) , 0 );
			
			gl.glEnableVertexAttribArray( a_pos_location );
			gl.glEnableVertexAttribArray( a_norm_location );
			gl.glEnableVertexAttribArray( a_glow_location );
			gl.glEnableVertexAttribArray( a_highlightIndex_location );
			
			gl.glEnable( GL_DEPTH_TEST );
		}
		
		protected void afterDraw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			gl.glDisable( GL_DEPTH_TEST );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , 0 );
		}
		
		public void draw( Segment segment , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			beforeDraw( segment , gl , m , n );
			
			doDraw( segment , gl );
		}
		
		protected void beforeDraw( Segment segment , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			segment.geometry.init( gl );
			segment.stationAttrs.init( gl );
			if( segment.stationAttrsNeedRebuffering )
			{
				segment.stationAttrs.rebuffer( gl );
				segment.stationAttrsNeedRebuffering = false;
			}
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.geometry.id( ) );
			gl.glVertexAttribPointer( a_pos_location , 3 , GL_FLOAT , false , GEOM_BPV , 0 );
			gl.glVertexAttribPointer( a_norm_location , 3 , GL_FLOAT , false , GEOM_BPV , 12 );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.stationAttrs.id( ) );
			gl.glVertexAttribPointer( a_glow_location , 2 , GL_FLOAT , false , STATION_ATTR_BPV , 0 );
			gl.glVertexAttribPointer( a_highlightIndex_location , 1 , GL_FLOAT , false , STATION_ATTR_BPV , 8 );
		}
		
		protected abstract void doDraw( Segment segment , GL2ES2 gl );
	}
	
	private abstract class OneParamSegmentDrawer extends BaseSegmentDrawer
	{
		private int	u_loParam_location;
		private int	u_hiParam_location;
		private int	u_paramSampler_location;
		
		@Override
		protected String createFragmentShaderVariables( )
		{
			return super.createFragmentShaderVariables( ) +
					"uniform float u_loParam;" +
					"uniform float u_hiParam;" +
					"uniform sampler2D u_paramSampler;" +
					"varying float v_param;";
		}
		
		@Override
		protected String createFragmentShaderCode( )
		{
			// param coloration
			return "  gl_FragColor = texture2D(u_paramSampler, vec2(0.5, clamp((v_param - u_loParam) / (u_hiParam - u_loParam), 0.0, 1.0)));" +
					super.createFragmentShaderCode( );
		}
		
		public void init( GL2ES2 gl )
		{
			if( program <= 0 )
			{
				super.init( gl );
				
				u_loParam_location = gl.glGetUniformLocation( program , "u_loParam" );
				u_hiParam_location = gl.glGetUniformLocation( program , "u_hiParam" );
				u_paramSampler_location = gl.glGetUniformLocation( program , "u_paramSampler" );
			}
		}
		
		@Override
		protected void beforeDraw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			super.beforeDraw( segments , context , gl , m , n );
			
			gl.glActiveTexture( GL_TEXTURE0 );
			gl.glBindTexture( GL_TEXTURE_2D , paramTexture );
			gl.glUniform1i( u_paramSampler_location , 0 );
			
			gl.glUniform1fv( u_loParam_location , 1 , loParam.value( ) , 0 );
			gl.glUniform1fv( u_hiParam_location , 1 , hiParam.value( ) , 0 );
		}
	}
	
	private class AxialSegmentDrawer extends OneParamSegmentDrawer
	{
		private int	u_axis_location;
		private int	u_origin_location;
		
		@Override
		protected String createVertexShaderVariables( )
		{
			return super.createVertexShaderVariables( ) +
					"uniform vec3 u_axis;" +
					"uniform vec3 u_origin;" +
					"varying float v_param;";
		}
		
		@Override
		protected String createVertexShaderCode( )
		{
			return super.createVertexShaderCode( ) +
					"  v_param = dot(a_pos - u_origin, u_axis);";
		}
		
		public void init( GL2ES2 gl )
		{
			if( program <= 0 )
			{
				super.init( gl );
				u_axis_location = gl.glGetUniformLocation( program , "u_axis" );
				u_origin_location = gl.glGetUniformLocation( program , "u_origin" );
			}
		}
		
		@Override
		protected void beforeDraw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			super.beforeDraw( segments , context , gl , m , n );
			gl.glUniform3fv( u_axis_location , 1 , depthAxis.value( ) , 0 );
			gl.glUniform3fv( u_origin_location , 1 , depthOrigin.value( ) , 0 );
		}
		
		@Override
		protected void beforeDraw( Segment segment , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			super.beforeDraw( segment , gl , m , n );
			segment.lineIndices.init( gl );
			segment.fillIndices.init( gl );
		}
		
		@Override
		protected void doDraw( Segment segment , GL2ES2 gl )
		{
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , segment.lineIndices.id( ) );
			gl.glDrawElements( GL_LINES , segment.lineIndices.buffer( ).capacity( ) / BPI , GL_UNSIGNED_INT , 0 );
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , segment.fillIndices.id( ) );
			gl.glDrawElements( GL_TRIANGLES , segment.fillIndices.buffer( ).capacity( ) / BPI , GL_UNSIGNED_INT , 0 );
		}
	}
	
	private class Param0SegmentDrawer extends OneParamSegmentDrawer
	{
		private int	a_param0_location;
		
		@Override
		protected String createVertexShaderVariables( )
		{
			return super.createVertexShaderVariables( ) +
					"attribute float a_param0;" +
					"varying float v_param;";
		}
		
		@Override
		protected String createVertexShaderCode( )
		{
			return super.createVertexShaderCode( ) +
					"  v_param = a_param0;";
		}
		
		public void init( GL2ES2 gl )
		{
			if( program <= 0 )
			{
				super.init( gl );
				a_param0_location = gl.glGetAttribLocation( program , "a_param0" );
			}
		}
		
		@Override
		protected void beforeDraw( Collection<Segment> segments , JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			super.beforeDraw( segments , context , gl , m , n );
			
			gl.glEnableVertexAttribArray( a_param0_location );
		}
		
		@Override
		protected void beforeDraw( Segment segment , GL2ES2 gl , float[ ] m , float[ ] n )
		{
			super.beforeDraw( segment , gl , m , n );
			segment.param0.init( gl );
			if( segment.param0NeedsRebuffering )
			{
				segment.param0.rebuffer( gl );
			}
			segment.lineIndices.init( gl );
			segment.fillIndices.init( gl );
			
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.param0.id( ) );
			gl.glVertexAttribPointer( a_param0_location , 1 , GL_FLOAT , false , 4 , 0 );
		}
		
		@Override
		protected void doDraw( Segment segment , GL2ES2 gl )
		{
			gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , segment.lineIndices.id( ) );
			gl.glDrawElements( GL_LINES , segment.lineIndices.buffer( ).capacity( ) / BPI , GL_UNSIGNED_INT , 0 );
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
		for( Segment segment : segments )
		{
			segment.dispose( gl );
		}
		
		for( SegmentDrawer drawer : drawers.keySet( ) )
		{
			drawer.dispose( gl );
		}
		
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