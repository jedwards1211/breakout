
package org.andork.j3d;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryStripArray;
import javax.media.j3d.Group;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedGeometryStripArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.IndexedPointArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PointArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.SceneGraphObject;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.swing.JOptionPane;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.andork.generic.Predicate;
import org.andork.generic.Visitor;
import org.andork.util.ArrayUtils;

public class J3DUtils
{
	public static float max( float a , float b , float c )
	{
		return a > b ? ( a > c ? a : c ) : ( b > c ? b : c );
	}
	
	public static float min( float a , float b , float c )
	{
		return a < b ? ( a < c ? a : c ) : ( b < c ? b : c );
	}
	
	public static double min( double ... args )
	{
		double result = args[ 0 ];
		for( int i = 1 ; i < args.length ; i++ )
		{
			result = Math.min( result , args[ i ] );
		}
		return result;
	}
	
	public static double max( double ... args )
	{
		double result = args[ 0 ];
		for( int i = 1 ; i < args.length ; i++ )
		{
			result = Math.max( result , args[ i ] );
		}
		return result;
	}
	
	public static void printSceneGraph( Group g , int tablevel )
	{
		for( int i = 0 ; i < tablevel ; i++ )
		{
			System.out.print( '\t' );
		}
		System.out.println( g );
		final Enumeration children = g.getAllChildren( );
		while( children.hasMoreElements( ) )
		{
			final SceneGraphObject o = ( SceneGraphObject ) children.nextElement( );
			if( o instanceof Group )
			{
				printSceneGraph( ( Group ) o , tablevel + 1 );
			}
			if( o instanceof Node )
			{
				printSceneGraph( ( Node ) o , tablevel + 1 );
			}
		}
	}
	
	public static void printSceneGraph( Node n , int tablevel )
	{
		for( int i = 0 ; i < tablevel ; i++ )
		{
			System.out.print( '\t' );
		}
		System.out.println( n );
	}
	
	public static int getFirstPtAfter( ArrayList<Point2f> data , float depth )
	{
		if( data.size( ) == 0 )
		{
			return -2;
		}
		if( depth > data.get( data.size( ) - 1 ).x )
		{
			return -1;
		}
		
		int lb = 0 , ub = data.size( );
		while( ub > lb + 1 )
		{
			final int mid = ( lb + ub ) >> 1;
			if( data.get( mid ).getX( ) < depth )
			{
				lb = mid;
			}
			else
			{
				ub = mid;
			}
		}
		if( data.get( lb ).getX( ) >= depth )
		{
			return lb;
		}
		else
		{
			return ub;
		}
	}
	
	public static int getFirstPtBefore( ArrayList<Point2f> data , float depth )
	{
		if( data.size( ) == 0 )
		{
			return -2;
		}
		if( depth < data.get( 0 ).x )
		{
			return -1;
		}
		
		int lb = 0 , ub = data.size( ) - 1;
		while( ub > lb + 1 )
		{
			final int mid = ( lb + ub ) >> 1;
			if( data.get( mid ).getX( ) < depth )
			{
				lb = mid;
			}
			else
			{
				ub = mid;
			}
		}
		if( data.get( ub ).getX( ) <= depth )
		{
			return ub;
		}
		else
		{
			return lb;
		}
	}
	
	public static class DataPointComparator implements Comparator<Point2f>
	{
		private static final DataPointComparator	instance	= new DataPointComparator( );
		
		public int compare( Point2f o1 , Point2f o2 )
		{
			return ( o1.x > o2.x ? 1 : ( o1.x < o2.x ? -1 : 0 ) );
		}
		
		public static DataPointComparator getIntance( )
		{
			return instance;
		}
	}
	
	
	
	// TODO all of these transform methods could be greatly optimized
	
	/**
	 * Creates a {@link Shape3D} backed by a {@link QuadArray}.
	 * 
	 * @param coords
	 *            a <code>List</code> of 4*N entries, where N >= 1.
	 * @param texcoords
	 *            the texture coordinates for each coordinate (can be <code>null</code>).
	 * @param colors
	 *            the colors for each coordinate (can be <code>null</code>).
	 * @param normals
	 *            the normals for each coordinate (can be <code>null</code>).
	 * @param appearance
	 *            the appearance (can be <code>null</code>).
	 */
	public static Shape3D createQuadArray( List<Point3f> coords , List<TexCoord2f> texcoords , List<Color4f> colors , List<Vector3f> normals , Appearance appearance )
	{
		
		final boolean useTexcoords = texcoords != null && !texcoords.isEmpty( );
		final boolean useColors = colors != null && !colors.isEmpty( );
		final boolean useNormals = normals != null && !normals.isEmpty( );
		
		if( coords.size( ) < 4 || ( coords.size( ) % 4 ) != 0 )
		{
			throw new IllegalArgumentException( "coords must have 4*N entries, where N >= 1" );
		}
		
		if( useTexcoords && texcoords.size( ) != coords.size( ) )
		{
			throw new IllegalArgumentException( "if texcoords is not null or empty it must be the same size as coords" + " (texcoords.size() = " + texcoords.size( ) + ", coords.size() = " + coords.size( ) );
		}
		
		if( useColors && colors.size( ) != coords.size( ) )
		{
			throw new IllegalArgumentException( "if colors is not null or empty it must be the same size as coords" + " (colors.size() = " + colors.size( ) + ", coords.size() = " + coords.size( ) );
		}
		
		if( useNormals && normals.size( ) != coords.size( ) )
		{
			throw new IllegalArgumentException( "if normals is not null or empty it must be the same size as coords" + " (normals.size() = " + normals.size( ) + ", coords.size() = " + coords.size( ) );
		}
		
		int vertexFormat = GeometryArray.COORDINATES;
		if( useTexcoords )
		{
			vertexFormat |= GeometryArray.TEXTURE_COORDINATE_2;
		}
		if( useColors )
		{
			vertexFormat |= GeometryArray.COLOR_4;
		}
		if( useNormals )
		{
			vertexFormat |= GeometryArray.NORMALS;
		}
		
		final QuadArray geom = new QuadArray( coords.size( ) , vertexFormat );
		
		int i;
		for( i = 0 ; i < coords.size( ) ; i++ )
		{
			geom.setCoordinate( i , coords.get( i ) );
			if( useTexcoords )
			{
				geom.setTextureCoordinate( 0 , i , texcoords.get( i ) );
			}
			if( useColors )
			{
				geom.setColor( i , colors.get( i ) );
			}
			if( useNormals )
			{
				geom.setNormal( i , normals.get( i ) );
			}
		}
		
		final Shape3D shape = new Shape3D( geom );
		if( appearance != null )
		{
			shape.setAppearance( appearance );
		}
		return shape;
	}
	
	/**
	 * Creates a {@link Shape3D} backed by an {@link IndexedQuadArray}.
	 * 
	 * @param coordRef
	 *            the reference coordinates
	 * @param coordIdx
	 *            the coordinate indices (must have 4*N entries, where N >= 1)
	 * @param texCoordRef
	 *            the reference texture coordinates (can be null)
	 * @param texCoordIdx
	 *            the texture coordinate indices (can be null)
	 * @param colorRef
	 *            the reference vertex colors (can be null)
	 * @param colorIdx
	 *            the vertex color indices (can be null)
	 * @param normalRef
	 *            the reference normals (can be null)
	 * @param normalIdx
	 *            the normal indices (can be null)
	 * @param appearance
	 *            the appearance (can be null)
	 */
	public static Shape3D createIndexedQuadArray( List<Point3f> coordRef , List<Integer> coordIdx , List<TexCoord2f> texCoordRef , List<Integer> texCoordIdx , List<Color4f> colorRef , List<Integer> colorIdx , List<Vector3f> normalRef , List<Integer> normalIdx , Appearance appearance )
	{
		
		final boolean useTexcoords = texCoordIdx != null && !texCoordIdx.isEmpty( );
		final boolean useColors = colorIdx != null && !colorIdx.isEmpty( );
		final boolean useNormals = normalIdx != null && !normalIdx.isEmpty( );
		
		if( useTexcoords && texCoordIdx.size( ) != coordIdx.size( ) )
		{
			throw new IllegalArgumentException( "if texCoordIdx is not null or empty it must be the same size as coordIdx" + " (texCoordIdx.size() = " + texCoordIdx.size( ) + ", coordIdx.size() = " + coordIdx.size( ) );
		}
		
		if( useColors && colorIdx.size( ) != coordIdx.size( ) )
		{
			throw new IllegalArgumentException( "if colorIdx is not null or empty it must be the same size as coordIdx" + " (colorIdx.size() = " + colorIdx.size( ) + ", coordIdx.size() = " + coordIdx.size( ) );
		}
		
		if( useNormals && normalIdx.size( ) != coordIdx.size( ) )
		{
			throw new IllegalArgumentException( "if normalIdx is not null or empty it must be the same size as coordIdx" + " (normalIdx.size() = " + normalIdx.size( ) + ", coordIdx.size() = " + coordIdx.size( ) );
		}
		
		int vertexFormat = GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE;
		if( useTexcoords )
		{
			vertexFormat |= GeometryArray.TEXTURE_COORDINATE_2;
		}
		if( useColors )
		{
			vertexFormat |= GeometryArray.COLOR_4;
		}
		if( useNormals )
		{
			vertexFormat |= GeometryArray.NORMALS;
		}
		
		int refCount = coordRef.size( );
		if( useTexcoords )
		{
			refCount = Math.max( refCount , texCoordRef.size( ) );
		}
		if( useColors )
		{
			refCount = Math.max( refCount , colorRef.size( ) );
		}
		if( useNormals )
		{
			refCount = Math.max( refCount , normalRef.size( ) );
		}
		
		final IndexedQuadArray geom = new IndexedQuadArray( refCount , vertexFormat , coordIdx.size( ) );
		
		float[ ] coordArray = org.andork.vecmath.VecmathUtils.toFloatArray3( coordRef , null );
		
		// if there are more texcoords, colors, or normals than coordinates we
		// need to resize the coordinates
		// array so that there isn't an IndexOutOfBoundsException
		if( coordArray.length / 3 < refCount )
		{
			final float[ ] newArray = new float[ refCount * 3 ];
			System.arraycopy( coordArray , 0 , newArray , 0 , coordArray.length );
			
			// duplicate the last coordinate so that the bounding box doesn't
			// get screwed up
			final float x = coordArray[ coordArray.length - 3 ];
			final float y = coordArray[ coordArray.length - 2 ];
			final float z = coordArray[ coordArray.length - 1 ];
			for( int i = coordArray.length ; i < refCount * 3 ; i += 3 )
			{
				newArray[ i ] = x;
				newArray[ i + 1 ] = y;
				newArray[ i + 2 ] = z;
			}
			
			coordArray = newArray;
		}
		
		geom.setCoordRefFloat( coordArray );
		
		if( useTexcoords )
		{
			final float[ ] texCoordArray = org.andork.vecmath.VecmathUtils.toFloatArray2( texCoordRef , null );
			geom.setTexCoordRefFloat( 0 , texCoordArray );
		}
		if( useColors )
		{
			final float[ ] colorArray = org.andork.vecmath.VecmathUtils.toFloatArray4( colorRef , null );
			geom.setColorRefFloat( colorArray );
		}
		if( useNormals )
		{
			final float[ ] normalArray = org.andork.vecmath.VecmathUtils.toFloatArray3( normalRef , null );
			geom.setNormalRefFloat( normalArray );
		}
		
		int i;
		for( i = 0 ; i < coordIdx.size( ) ; i++ )
		{
			geom.setCoordinateIndex( i , coordIdx.get( i ) );
			if( useTexcoords )
			{
				geom.setTextureCoordinateIndex( 0 , i , texCoordIdx.get( i ) );
			}
			if( useColors )
			{
				geom.setColorIndex( i , colorIdx.get( i ) );
			}
			if( useNormals )
			{
				geom.setNormalIndex( i , normalIdx.get( i ) );
			}
		}
		
		final Shape3D shape = new Shape3D( geom );
		if( appearance != null )
		{
			shape.setAppearance( appearance );
		}
		return shape;
	}
	
	/**
	 * Creates a {@link Shape3D} backed by an {@link IndexedQuadArray}.
	 * 
	 * @param coordRef
	 *            the reference coordinates
	 * @param coordIdx
	 *            the coordinate indices (must have 4*N entries, where N >= 1)
	 * @param texCoordRef
	 *            the reference 2d texture coordinates (can be null)
	 * @param texCoordIdx
	 *            the texture coordinate indices (can be null)
	 * @param colorRef
	 *            the reference vertex colors (can be null)
	 * @param colorIdx
	 *            the vertex color 4f indices (can be null)
	 * @param normalRef
	 *            the reference normals (can be null)
	 * @param normalIdx
	 *            the normal indices (can be null)
	 * @param appearance
	 *            the appearance (can be null)
	 */
	public static Shape3D createIndexedQuadArray( float[ ] coordRef , int[ ] coordIdx , float[ ] texCoordRef , int[ ] texCoordIdx , float[ ] colorRef , int[ ] colorIdx , float[ ] normalRef , int[ ] normalIdx , Appearance appearance )
	{
		
		final boolean useTexcoords = texCoordIdx != null && texCoordIdx.length != 0;
		final boolean useColors = colorIdx != null && colorIdx.length != 0;
		final boolean useNormals = normalIdx != null && normalIdx.length != 0;
		
		if( useTexcoords && texCoordIdx.length != coordIdx.length )
		{
			throw new IllegalArgumentException( "if texCoordIdx is not null or empty it must be the same length as coordIdx" + " (texCoordIdx.length = " + texCoordIdx.length + ", coordIdx.length = " + coordIdx.length );
		}
		
		if( useColors && colorIdx.length != coordIdx.length )
		{
			throw new IllegalArgumentException( "if colorIdx is not null or empty it must be the same length as coordIdx" + " (colorIdx.length = " + colorIdx.length + ", coordIdx.length = " + coordIdx.length );
		}
		
		if( useNormals && normalIdx.length != coordIdx.length )
		{
			throw new IllegalArgumentException( "if normalIdx is not null or empty it must be the same length as coordIdx" + " (normalIdx.length = " + normalIdx.length + ", coordIdx.length = " + coordIdx.length );
		}
		
		int vertexFormat = GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE;
		if( useTexcoords )
		{
			vertexFormat |= GeometryArray.TEXTURE_COORDINATE_2;
		}
		if( useColors )
		{
			vertexFormat |= GeometryArray.COLOR_4;
		}
		if( useNormals )
		{
			vertexFormat |= GeometryArray.NORMALS;
		}
		
		int refCount = coordRef.length / 3;
		if( useTexcoords )
		{
			refCount = Math.max( refCount , texCoordRef.length / 2 );
		}
		if( useColors )
		{
			refCount = Math.max( refCount , colorRef.length / 4 );
		}
		if( useNormals )
		{
			refCount = Math.max( refCount , normalRef.length / 3 );
		}
		
		final IndexedQuadArray geom = new IndexedQuadArray( refCount , vertexFormat , coordIdx.length );
		
		// if there are more texcoords, colors, or normals than coordinates we
		// need to resize the coordinates
		// array so that there isn't an IndexOutOfBoundsException
		if( coordRef.length / 3 < refCount )
		{
			final float[ ] newArray = new float[ refCount * 3 ];
			System.arraycopy( coordRef , 0 , newArray , 0 , coordRef.length );
			
			// duplicate the last coordinate so that the bounding box doesn't
			// get screwed up
			final float x = coordRef[ coordRef.length - 3 ];
			final float y = coordRef[ coordRef.length - 2 ];
			final float z = coordRef[ coordRef.length - 1 ];
			for( int i = coordRef.length ; i < refCount * 3 ; i += 3 )
			{
				newArray[ i ] = x;
				newArray[ i + 1 ] = y;
				newArray[ i + 2 ] = z;
			}
			
			coordRef = newArray;
		}
		
		geom.setCoordRefFloat( coordRef );
		if( useTexcoords )
		{
			geom.setTexCoordRefFloat( 0 , texCoordRef );
		}
		if( useColors )
		{
			geom.setColorRefFloat( colorRef );
		}
		if( useNormals )
		{
			geom.setNormalRefFloat( normalRef );
		}
		
		geom.setCoordinateIndices( 0 , coordIdx );
		if( useTexcoords )
		{
			geom.setTextureCoordinateIndices( 0 , 0 , texCoordIdx );
		}
		if( useColors )
		{
			geom.setColorIndices( 0 , colorIdx );
		}
		if( useNormals )
		{
			geom.setNormalIndices( 0 , normalIdx );
		}
		
		final Shape3D shape = new Shape3D( geom );
		if( appearance != null )
		{
			shape.setAppearance( appearance );
		}
		return shape;
	}
	
	/**
	 * Creates a {@link Shape3D} backed by an {@link IndexedLineArray}.
	 * 
	 * @param coordRef
	 *            the reference coordinates
	 * @param coordIdx
	 *            the coordinate indices
	 * @param texCoordRef
	 *            the reference 2d texture coordinates (can be null)
	 * @param texCoordIdx
	 *            the texture coordinate indices (can be null)
	 * @param colorRef
	 *            the reference vertex colors (can be null)
	 * @param colorIdx
	 *            the vertex color 4f indices (can be null)
	 * @param normalRef
	 *            the reference normals (can be null)
	 * @param normalIdx
	 *            the normal indices (can be null)
	 * @param appearance
	 *            the appearance (can be null)
	 */
	public static Shape3D createIndexedLineArray( float[ ] coordRef , int[ ] coordIdx , float[ ] texCoordRef , int[ ] texCoordIdx , float[ ] colorRef , int[ ] colorIdx , float[ ] normalRef , int[ ] normalIdx , Appearance appearance )
	{
		
		final boolean useTexcoords = texCoordIdx != null && texCoordIdx.length != 0;
		final boolean useColors = colorIdx != null && colorIdx.length != 0;
		final boolean useNormals = normalIdx != null && normalIdx.length != 0;
		
		if( useTexcoords && texCoordIdx.length != coordIdx.length )
		{
			throw new IllegalArgumentException( "if texCoordIdx is not null or empty it must be the same length as coordIdx" + " (texCoordIdx.length = " + texCoordIdx.length + ", coordIdx.length = " + coordIdx.length );
		}
		
		if( useColors && colorIdx.length != coordIdx.length )
		{
			throw new IllegalArgumentException( "if colorIdx is not null or empty it must be the same length as coordIdx" + " (colorIdx.length = " + colorIdx.length + ", coordIdx.length = " + coordIdx.length );
		}
		
		if( useNormals && normalIdx.length != coordIdx.length )
		{
			throw new IllegalArgumentException( "if normalIdx is not null or empty it must be the same length as coordIdx" + " (normalIdx.length = " + normalIdx.length + ", coordIdx.length = " + coordIdx.length );
		}
		
		int vertexFormat = GeometryArray.COORDINATES | GeometryArray.BY_REFERENCE;
		if( useTexcoords )
		{
			vertexFormat |= GeometryArray.TEXTURE_COORDINATE_2;
		}
		if( useColors )
		{
			vertexFormat |= GeometryArray.COLOR_4;
		}
		if( useNormals )
		{
			vertexFormat |= GeometryArray.NORMALS;
		}
		
		int refCount = coordRef.length / 3;
		if( useTexcoords )
		{
			refCount = Math.max( refCount , texCoordRef.length / 2 );
		}
		if( useColors )
		{
			refCount = Math.max( refCount , colorRef.length / 4 );
		}
		if( useNormals )
		{
			refCount = Math.max( refCount , normalRef.length / 3 );
		}
		
		final IndexedLineArray geom = new IndexedLineArray( refCount , vertexFormat , coordIdx.length );
		
		// if there are more texcoords, colors, or normals than coordinates we
		// need to resize the coordinates
		// array so that there isn't an IndexOutOfBoundsException
		if( coordRef.length / 3 < refCount )
		{
			final float[ ] newArray = new float[ refCount * 3 ];
			System.arraycopy( coordRef , 0 , newArray , 0 , coordRef.length );
			
			// duplicate the last coordinate so that the bounding box doesn't
			// get screwed up
			final float x = coordRef[ coordRef.length - 3 ];
			final float y = coordRef[ coordRef.length - 2 ];
			final float z = coordRef[ coordRef.length - 1 ];
			for( int i = coordRef.length ; i < refCount * 3 ; i += 3 )
			{
				newArray[ i ] = x;
				newArray[ i + 1 ] = y;
				newArray[ i + 2 ] = z;
			}
			
			coordRef = newArray;
		}
		
		geom.setCoordRefFloat( coordRef );
		if( useTexcoords )
		{
			geom.setTexCoordRefFloat( 0 , texCoordRef );
		}
		if( useColors )
		{
			geom.setColorRefFloat( colorRef );
		}
		if( useNormals )
		{
			geom.setNormalRefFloat( normalRef );
		}
		
		geom.setCoordinateIndices( 0 , coordIdx );
		if( useTexcoords )
		{
			geom.setTextureCoordinateIndices( 0 , 0 , texCoordIdx );
		}
		if( useColors )
		{
			geom.setColorIndices( 0 , colorIdx );
		}
		if( useNormals )
		{
			geom.setNormalIndices( 0 , normalIdx );
		}
		
		final Shape3D shape = new Shape3D( geom );
		if( appearance != null )
		{
			shape.setAppearance( appearance );
		}
		return shape;
	}
	
	public static GeometryArray changeVertexFormat( GeometryArray array , int newVertexFormat )
	{
		int unionFormatMask = newVertexFormat;
		int diffFormatMask = array.getVertexFormat( ) & ~newVertexFormat;
		
		return changeVertexFormat( array , unionFormatMask , diffFormatMask );
	}
	
	/**
	 * Creates a copy of a GeometryArray with a different vertex format, and copies all the data it can into the new array. GeometryStripArrays,
	 * IndexedGeometryStripArrays, and INTERLEAVED and USE_NIO_BUFFER GeometryArrays are not supported, nor are GeometryArrays using referenced TupleXX data.
	 * 
	 * @param array
	 *            the array to copy from.
	 * @param unionFormatMask
	 *            bits that will be added to the original vertex format
	 * @param diffFormatMask
	 *            bits that will be removed from the original vertex format (trumps unionFormatMask)
	 * @return a <code>GeometryArray</code> of the same type as <code>array</code>. If the vertex format is not changed, the original array is returned.
	 */
	public static GeometryArray changeVertexFormat( GeometryArray array , int unionFormatMask , int diffFormatMask )
	{
		if( array instanceof GeometryStripArray || array instanceof IndexedGeometryStripArray )
		{
			throw new IllegalArgumentException( "GeometryStripArrays are not currently supported" );
		}
		
		IndexedGeometryArray indexedArray = null;
		final boolean indexed = array instanceof IndexedGeometryArray;
		if( indexed )
		{
			indexedArray = ( IndexedGeometryArray ) array;
		}
		
		final int vertexCount = array.getVertexCount( );
		final int vertexFormat = array.getVertexFormat( );
		final int texCoordSetCount = array.getTexCoordSetCount( );
		final int newVertexFormat = ( vertexFormat | unionFormatMask ) & ~diffFormatMask;
		
		if( ( vertexFormat & GeometryArray.INTERLEAVED ) != 0 || ( newVertexFormat & GeometryArray.INTERLEAVED ) != 0 )
		{
			throw new IllegalArgumentException( "Interleaved GeometryArrays are not currently supported" );
		}
		if( ( vertexFormat & GeometryArray.USE_NIO_BUFFER ) != 0 || ( newVertexFormat & GeometryArray.USE_NIO_BUFFER ) != 0 )
		{
			throw new IllegalArgumentException( "NIO Buffer GeometryArrays are not currently supported" );
		}
		
		if( vertexFormat == newVertexFormat )
		{
			return array;
		}
		
		int indexCount = -1;
		if( indexed )
		{
			indexCount = indexedArray.getIndexCount( );
		}
		
		GeometryArray newArray = null;
		IndexedGeometryArray newIdxArray = null;
		
		// Allocate the new array
		
		if( array instanceof LineArray )
		{
			newArray = new LineArray( vertexCount , newVertexFormat );
		}
		else if( array instanceof PointArray )
		{
			newArray = new PointArray( vertexCount , newVertexFormat );
		}
		else if( array instanceof QuadArray )
		{
			newArray = new QuadArray( vertexCount , newVertexFormat );
		}
		else if( array instanceof TriangleArray )
		{
			newArray = new TriangleArray( vertexCount , newVertexFormat );
		}
		else if( array instanceof IndexedLineArray )
		{
			newArray = newIdxArray = new IndexedLineArray( vertexCount , newVertexFormat , indexCount );
		}
		else if( array instanceof IndexedPointArray )
		{
			newArray = newIdxArray = new IndexedPointArray( vertexCount , newVertexFormat , indexCount );
		}
		else if( array instanceof IndexedQuadArray )
		{
			newArray = newIdxArray = new IndexedQuadArray( vertexCount , newVertexFormat , indexCount );
		}
		else if( array instanceof IndexedTriangleArray )
		{
			newArray = newIdxArray = new IndexedTriangleArray( vertexCount , newVertexFormat , indexCount );
		}
		
		// handy variables
		
		final boolean from_by_reference = ( vertexFormat & GeometryArray.BY_REFERENCE ) != 0;
		final boolean from_by_reference_indices = ( vertexFormat & GeometryArray.BY_REFERENCE_INDICES ) != 0;
		final boolean from_use_coord_index_only = ( vertexFormat & GeometryArray.USE_COORD_INDEX_ONLY ) != 0;
		
		final boolean from_coordinates = ( vertexFormat & GeometryArray.COORDINATES ) != 0;
		final boolean from_color_3 = ( vertexFormat & GeometryArray.COLOR_3 ) != 0;
		final boolean from_color_4 = ( vertexFormat & GeometryArray.COLOR_4 ) != 0;
		final boolean from_texture_coordinate_2 = ( vertexFormat & GeometryArray.TEXTURE_COORDINATE_2 ) != 0;
		final boolean from_texture_coordinate_3 = ( vertexFormat & GeometryArray.TEXTURE_COORDINATE_3 ) != 0;
		final boolean from_texture_coordinate_4 = ( vertexFormat & GeometryArray.TEXTURE_COORDINATE_4 ) != 0;
		final boolean from_normals = ( vertexFormat & GeometryArray.NORMALS ) != 0;
		
		final boolean to_by_reference = ( newVertexFormat & GeometryArray.BY_REFERENCE ) != 0;
		final boolean to_by_reference_indices = ( newVertexFormat & GeometryArray.BY_REFERENCE_INDICES ) != 0;
		final boolean to_use_coord_index_only = ( newVertexFormat & GeometryArray.USE_COORD_INDEX_ONLY ) != 0;
		
		final boolean to_coordinates = ( newVertexFormat & GeometryArray.COORDINATES ) != 0;
		final boolean to_color_3 = ( newVertexFormat & GeometryArray.COLOR_3 ) != 0;
		final boolean to_color_4 = ( newVertexFormat & GeometryArray.COLOR_4 ) != 0;
		final boolean to_texture_coordinate_2 = ( newVertexFormat & GeometryArray.TEXTURE_COORDINATE_2 ) != 0;
		final boolean to_texture_coordinate_3 = ( newVertexFormat & GeometryArray.TEXTURE_COORDINATE_3 ) != 0;
		final boolean to_texture_coordinate_4 = ( newVertexFormat & GeometryArray.TEXTURE_COORDINATE_4 ) != 0;
		final boolean to_normals = ( newVertexFormat & GeometryArray.NORMALS ) != 0;
		
		// copy coordinates
		
		if( from_coordinates && to_coordinates )
		{
			float[ ] coordsFloat = null;
			double[ ] coordsDouble = null;
			
			if( from_by_reference )
			{
				coordsFloat = array.getCoordRefFloat( );
				coordsDouble = array.getCoordRefDouble( );
			}
			else
			{
				coordsDouble = new double[ vertexCount * 3 ];
				array.getCoordinates( 0 , coordsDouble );
			}
			
			if( to_by_reference )
			{
				if( coordsFloat != null )
				{
					newArray.setCoordRefFloat( coordsFloat );
				}
				else if( coordsDouble != null )
				{
					newArray.setCoordRefDouble( coordsDouble );
				}
			}
			else
			{
				if( coordsFloat != null )
				{
					newArray.setCoordinates( 0 , coordsFloat );
				}
				else if( coordsDouble != null )
				{
					newArray.setCoordinates( 0 , coordsDouble );
				}
			}
			
			if( indexed )
			{
				int[ ] coordIdx = null;
				if( from_by_reference_indices )
				{
					coordIdx = indexedArray.getCoordIndicesRef( );
				}
				else
				{
					coordIdx = new int[ indexCount ];
					indexedArray.getCoordinateIndices( 0 , coordIdx );
				}
				if( to_by_reference_indices )
				{
					newIdxArray.setCoordIndicesRef( coordIdx );
				}
				else
				{
					newIdxArray.setCoordinateIndices( 0 , coordIdx );
				}
			}
		}
		
		// copy colors
		
		if( ( from_color_3 || from_color_4 ) && ( to_color_3 || to_color_4 ) )
		{
			final int fromSize = from_color_3 ? 3 : 4;
			final int toSize = to_color_3 ? 3 : 4;
			
			float[ ] colorsFloat = null;
			byte[ ] colorsByte = null;
			
			if( from_by_reference )
			{
				colorsFloat = array.getColorRefFloat( );
				colorsByte = array.getColorRefByte( );
			}
			else
			{
				colorsFloat = new float[ vertexCount * fromSize ];
				array.getColors( 0 , colorsFloat );
			}
			
			if( fromSize != toSize )
			{
				if( colorsFloat != null )
				{
					colorsFloat = ArrayUtils.changeBlockSize( colorsFloat , fromSize , toSize );
				}
				else if( colorsByte != null )
				{
					colorsByte = ArrayUtils.changeBlockSize( colorsByte , fromSize , toSize );
				}
			}
			
			if( to_by_reference )
			{
				if( colorsFloat != null )
				{
					newArray.setColorRefFloat( colorsFloat );
				}
				else if( colorsByte != null )
				{
					newArray.setColorRefByte( colorsByte );
				}
			}
			else
			{
				if( colorsFloat != null )
				{
					newArray.setColors( 0 , colorsFloat );
				}
				else if( colorsByte != null )
				{
					newArray.setColors( 0 , colorsByte );
				}
			}
			
			if( indexed && !to_use_coord_index_only )
			{
				int[ ] colorIdx = null;
				if( from_by_reference_indices && from_use_coord_index_only )
				{
					colorIdx = indexedArray.getCoordIndicesRef( );
				}
				else
				{
					colorIdx = new int[ indexCount ];
					indexedArray.getColorIndices( 0 , colorIdx );
				}
				newIdxArray.setColorIndices( 0 , colorIdx );
			}
		}
		
		// copy texture coordinates
		
		if( ( from_texture_coordinate_2 || from_texture_coordinate_3 || from_texture_coordinate_4 ) && ( to_texture_coordinate_2 || to_texture_coordinate_3 || to_texture_coordinate_4 ) )
		{
			final int fromSize = from_texture_coordinate_2 ? 2 : from_texture_coordinate_3 ? 3 : 4;
			final int toSize = to_texture_coordinate_2 ? 2 : to_texture_coordinate_3 ? 3 : 4;
			
			for( int i = 0 ; i < texCoordSetCount ; i++ )
			{
				float[ ] texturesFloat = null;
				
				if( from_by_reference )
				{
					texturesFloat = array.getTexCoordRefFloat( i );
				}
				else
				{
					texturesFloat = new float[ vertexCount * fromSize ];
					array.getTextureCoordinates( i , 0 , texturesFloat );
				}
				
				if( fromSize != toSize )
				{
					texturesFloat = ArrayUtils.changeBlockSize( texturesFloat , fromSize , toSize );
				}
				
				if( to_by_reference )
				{
					newArray.setTexCoordRefFloat( i , texturesFloat );
				}
				else
				{
					newArray.setTextureCoordinates( i , 0 , texturesFloat );
				}
				
				if( indexed && !to_use_coord_index_only )
				{
					int[ ] textureIdx = null;
					if( from_by_reference_indices && from_use_coord_index_only )
					{
						textureIdx = indexedArray.getCoordIndicesRef( );
					}
					else
					{
						textureIdx = new int[ indexCount ];
						indexedArray.getTextureCoordinateIndices( i , 0 , textureIdx );
					}
					newIdxArray.setTextureCoordinateIndices( i , 0 , textureIdx );
				}
			}
		}
		
		// copy normals
		
		if( from_normals && to_normals )
		{
			float[ ] normalsFloat = null;
			
			if( from_by_reference )
			{
				normalsFloat = array.getNormalRefFloat( );
			}
			else
			{
				normalsFloat = new float[ vertexCount * 3 ];
				array.getNormals( 0 , normalsFloat );
			}
			
			if( to_by_reference )
			{
				newArray.setNormalRefFloat( normalsFloat );
			}
			else
			{
				newArray.setNormals( 0 , normalsFloat );
			}
			
			if( indexed && !to_use_coord_index_only )
			{
				int[ ] normalIdx = null;
				if( from_by_reference_indices && from_use_coord_index_only )
				{
					normalIdx = indexedArray.getCoordIndicesRef( );
				}
				else
				{
					normalIdx = new int[ indexCount ];
					indexedArray.getNormalIndices( 0 , normalIdx );
				}
				newIdxArray.setNormalIndices( 0 , normalIdx );
			}
		}
		
		// finally, we're done!
		
		return newArray;
	}
	
	public static Color4f toColor4f( Color color , Color4f result )
	{
		result.x = Math.max( Math.min( color.getRed( ) / 255f , 1f ) , 0f );
		result.y = Math.max( Math.min( color.getGreen( ) / 255f , 1f ) , 0f );
		result.z = Math.max( Math.min( color.getBlue( ) / 255f , 1f ) , 0f );
		result.w = Math.max( Math.min( color.getAlpha( ) / 255f , 1f ) , 0f );
		return result;
	}
	
	/**
	 * Finds the first node in a depth-first traversal of the scene graph whose name (all or part) matches a regular expression.
	 * 
	 * @param start
	 *            the starting node for the scene graph traversal.
	 * @param nameRegexp
	 *            a regular expression
	 * @return a matching {@link Node}, or <code>null</code> if none is found.
	 */
	public static Node findNodeByName( Node start , String nameRegexp )
	{
		final Pattern p = Pattern.compile( nameRegexp );
		for( final Node node : SceneGraphIterator.unboundedIterable( start ) )
		{
			if( node.getName( ) == null )
			{
				continue;
			}
			final Matcher m = p.matcher( node.getName( ) );
			if( m.find( ) )
			{
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first node in a depth-first traversal of the scene graph whose {@link Node#toString()} representation (all or part) matches a regular
	 * expression.
	 * 
	 * @param start
	 *            the starting node for the scene graph traversal.
	 * @param regexp
	 *            a regular expression
	 * @return a matching {@link Node}, or <code>null</code> if none is found.
	 */
	public static Node findNodeByString( Node start , String regexp )
	{
		final Pattern p = Pattern.compile( regexp );
		for( final Node node : SceneGraphIterator.unboundedIterable( start ) )
		{
			final Matcher m = p.matcher( node.toString( ) );
			if( m.find( ) )
			{
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first node in a depth-first traversal of the scene graph of a given type.
	 * 
	 * @param start
	 *            the starting node for the scene graph traversal.
	 * @param clazz
	 *            the type to find
	 * @return a matching {@link Node}, or <code>null</code> if none is found.
	 */
	public static Node findNodeByClass( Node start , Class<?> clazz )
	{
		for( final Node node : SceneGraphIterator.unboundedIterable( start ) )
		{
			if( node.getClass( ).equals( clazz ) )
			{
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first node in a depth-first traversal of the scene graph with the given user data.
	 * 
	 * @param start
	 *            the starting node for the scene graph traversal.
	 * @param userData
	 *            the userData to find
	 * @return a {@link Node} whose user data <code>== userData</code>, or <code>null</code> if none is found.
	 */
	public static Node findNodeByUserData( Node start , Object userData )
	{
		for( final Node node : SceneGraphIterator.unboundedIterable( start ) )
		{
			if( node.getUserData( ) == userData )
			{
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first node in a depth-first traversal of the scene graph with the given user data.
	 * 
	 * @param start
	 *            the starting node for the scene graph traversal.
	 * @param userData
	 *            the userData to find
	 * @return a {@link Node} whose user data <code>.equals(userData)</code>, or <code>null</code> if none is found.
	 */
	public static Node findNodeByEqualUserData( Node start , Object userData )
	{
		for( final Node node : SceneGraphIterator.unboundedIterable( start ) )
		{
			final Object nodeUserData = node.getUserData( );
			if( nodeUserData == userData || ( nodeUserData != null && userData != null && nodeUserData.equals( userData ) ) )
			{
				return node;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first node in a depth-first traversal of the scene graph matching an arbitrary predicate.
	 * 
	 * @param start
	 *            the starting node for the scene graph traversal.
	 * @param p
	 *            the predicate
	 * @return a {@link Node} for which the predicate is <code>true</code>, or <code>null</code> if none is found.
	 */
	public static Node findNodeByPredicate( Node start , Predicate<Node, Boolean> p )
	{
		for( final Node node : SceneGraphIterator.unboundedIterable( start ) )
		{
			if( p.eval( node ) )
			{
				return node;
			}
		}
		return null;
	}
	
	public static BranchGroup createDefaultBranchGroup( )
	{
		final BranchGroup result = new BranchGroup( );
		result.setCapability( BranchGroup.ALLOW_DETACH );
		result.setCapability( Group.ALLOW_CHILDREN_READ );
		result.setCapability( Group.ALLOW_CHILDREN_WRITE );
		result.setCapability( Group.ALLOW_CHILDREN_EXTEND );
		return result;
	}
	
	/**
	 * Tries to remove a {@link Node} from the scene graph. If the necessary capabilities are not set, the node's top-level parent will be removed from its
	 * {@link Locale}, allowing the node to be detached, and then the top-level parent will be added back to the locale.
	 * 
	 * @param n
	 *            the node to detach
	 * @return <code>true</code> if the node was successfully detached.
	 */
	public static boolean forceDetach( Node n )
	{
		if( !n.isLive( ) )
		{
			return true;
		}
		
		final Node parent = n.getParent( );
		try
		{
			if( parent != null && parent instanceof Group )
			{
				( ( Group ) parent ).removeChild( n );
				return true;
			}
		}
		catch( final Throwable t )
		{
		}
		
		final Locale l = n.getLocale( );
		if( l == null )
		{
			return false;
		}
		
		Node topParent = n;
		while( topParent.getParent( ) != null )
		{
			topParent = topParent.getParent( );
		}
		
		if( topParent instanceof BranchGroup )
		{
			try
			{
				l.removeBranchGraph( ( BranchGroup ) topParent );
				if( topParent != n )
				{
					( ( Group ) parent ).removeChild( n );
				}
				return true;
			}
			finally
			{
				if( topParent != n )
				{
					l.addBranchGraph( ( BranchGroup ) topParent );
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Tries to add a {@link Node} to the scene graph. If the necessary capabilities are not set, the node's top-level parent will be removed from its
	 * {@link Locale}, allowing the node to be attached, and then the top-level parent will be added back to the locale.
	 * 
	 * @param child
	 *            the node to attach
	 * @param newParent
	 *            the parent to attach <code>child</code> to
	 * @return <code>true</code> if the node was successfully attached.
	 */
	public static boolean forceAttach( Node child , Group newParent )
	{
		try
		{
			newParent.addChild( child );
			return true;
		}
		catch( final Throwable t )
		{
		}
		
		final Locale l = newParent.getLocale( );
		if( l == null )
		{
			return false;
		}
		
		Node topParent = newParent;
		while( topParent.getParent( ) != null )
		{
			topParent = topParent.getParent( );
		}
		
		if( topParent instanceof BranchGroup )
		{
			try
			{
				l.removeBranchGraph( ( BranchGroup ) topParent );
				newParent.addChild( child );
				return true;
			}
			finally
			{
				l.addBranchGraph( ( BranchGroup ) topParent );
			}
		}
		
		return false;
	}
	
	/**
	 * Forcefully detaches a node from the scene graph, modifies it, and then forcefully reattaches the node.
	 * 
	 * @param node
	 *            the node to modify
	 * @param visitor
	 *            the modifying procedure
	 * @see #forceDetach(Node)
	 * @see #forceAttach(Node, Group)
	 */
	public static void forceModify( Node node , Visitor<Node, Boolean> visitor )
	{
		Node parent = node.getParent( );
		if( parent != null && !( parent instanceof Group ) )
		{
			parent = null;
		}
		try
		{
			if( parent != null && !J3DUtils.forceDetach( node ) )
			{
				return;
			}
			visitor.visit( node );
		}
		catch( final Throwable t )
		{
			t.printStackTrace( );
		}
		finally
		{
			if( parent != null )
			{
				try
				{
					J3DUtils.forceAttach( node , ( Group ) parent );
				}
				catch( final Throwable t )
				{
					t.printStackTrace( );
				}
			}
		}
	}
	
	/**
	 * Forcefully modifies a list of nodes.
	 * 
	 * @param node
	 *            the node to modify
	 * @param visitor
	 *            the modifying procedure. If <code>visitor.visit()</code> returns <code>false</code> for a node, the rest of the list is skipped.
	 * @see #forceModify(Node, Visitor)
	 */
	public static void forceModify( List<Node> nodes , Visitor<Node, Boolean> visitor )
	{
		for( final Node node : nodes )
		{
			Node parent = node.getParent( );
			Locale locale = node.getLocale( );
			boolean wasLive = node.isLive( );
			if( parent != null && !( parent instanceof Group ) )
			{
				parent = null;
			}
			try
			{
				if( !J3DUtils.forceDetach( node ) )
				{
					continue;
				}
				if( !visitor.visit( node ) )
				{
					break;
				}
			}
			catch( final Throwable t )
			{
				t.printStackTrace( );
			}
			finally
			{
				if( parent != null )
				{
					try
					{
						J3DUtils.forceAttach( node , ( Group ) parent );
					}
					catch( final Throwable t )
					{
						t.printStackTrace( );
					}
				}
				else if( node instanceof BranchGroup && wasLive )
				{
					try
					{
						locale.addBranchGraph( ( BranchGroup ) node );
					}
					catch( final Throwable t )
					{
						t.printStackTrace( );
					}
				}
			}
		}
	}
	
	static Appearance getAppearance( Shape3D s3D )
	{
		Appearance app = s3D.getAppearance( );
		if( app == null )
		{
			s3D.setAppearance( app = new Appearance( ) );
		}
		return app;
	}
	
	public static Appearance maskAppearance( )
	{
		final Appearance app = new Appearance( );
		
		final TransparencyAttributes ta = new TransparencyAttributes( );
		ta.setTransparencyMode( TransparencyAttributes.BLENDED );
		ta.setSrcBlendFunction( TransparencyAttributes.BLEND_ZERO );
		ta.setDstBlendFunction( TransparencyAttributes.BLEND_ONE );
		app.setTransparencyAttributes( ta );
		
		final ColoringAttributes ca = new ColoringAttributes( );
		ca.setColor( new Color3f( 0 , 0 , 0 ) );
		app.setColoringAttributes( ca );
		
		final RenderingAttributes ra = new RenderingAttributes( );
		ra.setDepthBufferWriteEnable( true );
		app.setRenderingAttributes( ra );
		
		return app;
	}
	
	public static Appearance flatAppearance( Color4f color )
	{
		final Appearance result = new Appearance( );
		setColor( result , color , false );
		return result;
	}
	
	public static Appearance shinyAppearance( Color4f color , float shininess )
	{
		final Appearance result = new Appearance( );
		
		final Material mat = new Material( );
		mat.setLightingEnable( true );
		mat.setShininess( shininess );
		result.setMaterial( mat );
		
		setColor( result , color , true );
		
		return result;
	}
	
	public static void makeDoubleSided( Appearance app )
	{
		PolygonAttributes pa = app.getPolygonAttributes( );
		if( pa == null )
		{
			pa = new PolygonAttributes( );
			app.setPolygonAttributes( pa );
		}
		pa.setCullFace( PolygonAttributes.CULL_NONE );
		pa.setBackFaceNormalFlip( true );
	}
	
	public static void setDepthTestFunction( Appearance app , int function )
	{
		RenderingAttributes ra = app.getRenderingAttributes( );
		if( ra == null )
		{
			ra = new RenderingAttributes( );
			app.setRenderingAttributes( ra );
		}
		ra.setDepthTestFunction( function );
	}
	
	public static Appearance debugAppearance( Color4f color )
	{
		final Appearance result = new Appearance( );
		setColor( result , color , false );
		setDepthTestFunction( result , RenderingAttributes.ALWAYS );
		return result;
	}
	
	public static BranchGroup visualizeBounds( Bounds b , Transform3D xform , Appearance app )
	{
		final BranchGroup bg = new BranchGroup( );
		bg.setCapability( BranchGroup.ALLOW_DETACH );
		
		final TransformGroup tg = new TransformGroup( );
		final Transform3D xlate = new Transform3D( );
		
		if( b instanceof BoundingBox )
		{
			final BoundingBox bbox = ( BoundingBox ) b;
			final Point3d lower = new Point3d( ) , upper = new Point3d( );
			bbox.getLower( lower );
			bbox.getUpper( upper );
			
			xlate.set( new double[ ] { ( upper.x - lower.x ) * 0.5 , 0 , 0 , ( upper.x + lower.x ) * 0.5 , 0 , ( upper.y - lower.y ) * 0.5 , 0 , ( upper.y + lower.y ) * 0.5 , 0 , 0 , ( upper.z - lower.z ) * 0.5 , ( upper.z + lower.z ) * 0.5 , 0 , 0 , 0 , 1 } );
			if( xform != null )
			{
				xlate.mul( xform , xlate );
			}
			
			tg.setTransform( xlate );
			bg.addChild( tg );
			
			final com.sun.j3d.utils.geometry.Box vbox = new com.sun.j3d.utils.geometry.Box( );
			vbox.setAppearance( app );
			tg.addChild( vbox );
		}
		else if( b instanceof BoundingSphere )
		{
			final BoundingSphere bsphere = ( BoundingSphere ) b;
			final Point3d center = new Point3d( );
			bsphere.getCenter( center );
			final double radius = bsphere.getRadius( );
			
			xlate.setTranslation( new Vector3d( center ) );
			if( xform != null )
			{
				xlate.mul( xform , xlate );
			}
			
			tg.setTransform( xlate );
			bg.addChild( tg );
			
			final com.sun.j3d.utils.geometry.Sphere vsphere = new com.sun.j3d.utils.geometry.Sphere( ( float ) radius );
			vsphere.setAppearance( app );
			tg.addChild( vsphere );
		}
		
		return bg;
	}
	
	/**
	 * Copies the coordinates from a GeometryArray into a Point3f array, even if they are stored in a different form in the GeometryArray.
	 * 
	 * @param array
	 * @return
	 */
	public static Point3f[ ] copyCoordinates3f( GeometryArray array )
	{
		Point3f[ ] coordinates = null;
		
		final int format = array.getVertexFormat( );
		if( ( format & GeometryArray.BY_REFERENCE ) > 0 )
		{
			final Point3f[ ] ref3f = array.getCoordRef3f( );
			final Point3d[ ] ref3d = array.getCoordRef3d( );
			final double[ ] refDouble = array.getCoordRefDouble( );
			final float[ ] refFloat = array.getCoordRefFloat( );
			
			if( ref3f != null || ref3d != null || refDouble != null || refFloat != null )
			{
				coordinates = new Point3f[ array.getVertexCount( ) ];
				if( ref3f != null )
				{
					for( int i = 0 ; i < ref3d.length ; i++ )
					{
						coordinates[ i ] = new Point3f( ref3f[ i ] );
					}
				}
				if( ref3d != null )
				{
					for( int i = 0 ; i < ref3d.length ; i++ )
					{
						coordinates[ i ] = new Point3f( ref3d[ i ] );
					}
				}
				else if( refDouble != null )
				{
					int k = 0;
					for( int i = 0 ; i < refDouble.length ; i += 3 )
					{
						coordinates[ k++ ] = new Point3f( ( float ) refDouble[ i ] , ( float ) refDouble[ i + 1 ] , ( float ) refDouble[ i + 2 ] );
					}
				}
				else if( refFloat != null )
				{
					int k = 0;
					for( int i = 0 ; i < refFloat.length ; i += 3 )
					{
						coordinates[ k++ ] = new Point3f( refFloat[ i ] , refFloat[ i + 1 ] , refFloat[ i + 2 ] );
					}
				}
			}
		}
		else
		{
			coordinates = org.andork.vecmath.VecmathUtils.allocPoint3fArray( array.getVertexCount( ) );
			array.getCoordinates( 0 , coordinates );
		}
		
		return coordinates;
	}
	
	/**
	 * Copies the normals from a GeometryArray into a Vector3f array, even if they're stored in a different form in the GeometryArray.
	 * 
	 * @param array
	 * @return
	 */
	public static Vector3f[ ] copyNormals3f( GeometryArray array )
	{
		Vector3f[ ] normals = null;
		
		final int format = array.getVertexFormat( );
		if( ( format & GeometryArray.BY_REFERENCE ) > 0 )
		{
			final Vector3f[ ] ref3f = array.getNormalRef3f( );
			final float[ ] refFloat = array.getNormalRefFloat( );
			
			if( ref3f != null || refFloat != null )
			{
				normals = new Vector3f[ array.getVertexCount( ) ];
				if( ref3f != null )
				{
					for( int i = 0 ; i < refFloat.length ; i += 3 )
					{
						normals[ i ] = new Vector3f( ref3f[ i ] );
					}
				}
				else if( refFloat != null )
				{
					int k = 0;
					for( int i = 0 ; i < refFloat.length ; i += 3 )
					{
						normals[ k++ ] = new Vector3f( refFloat[ i ] , refFloat[ i + 1 ] , refFloat[ i + 2 ] );
					}
				}
			}
		}
		else
		{
			normals = org.andork.vecmath.VecmathUtils.allocVector3fArray( array.getVertexCount( ) );
			array.getNormals( 0 , normals );
		}
		
		return normals;
	}
	
	/**
	 * Copies the coordinate indices out of an IndexedGeometryArray, whether they're stored by reference or not.
	 * 
	 * @param array
	 * @return
	 */
	public static int[ ] copyCoordinateIndices( IndexedGeometryArray array )
	{
		int[ ] coordinateIndices = null;
		
		final int format = array.getVertexFormat( );
		if( ( format & GeometryArray.BY_REFERENCE_INDICES ) > 0 )
		{
			coordinateIndices = array.getCoordIndicesRef( ).clone( );
		}
		else
		{
			coordinateIndices = new int[ array.getIndexCount( ) ];
			array.getCoordinateIndices( 0 , coordinateIndices );
		}
		
		return coordinateIndices;
	}
	
	/**
	 * Copies the normal indices out of an IndexedGeometryArray, whether they're stored by reference or not.
	 * 
	 * @param array
	 * @return
	 */
	public static int[ ] copyNormalIndices( IndexedGeometryArray array )
	{
		int[ ] normalIndices = null;
		
		final int format = array.getVertexFormat( );
		if( ( format & GeometryArray.BY_REFERENCE_INDICES ) > 0 && ( format & GeometryArray.USE_COORD_INDEX_ONLY ) > 0 )
		{
			normalIndices = array.getCoordIndicesRef( ).clone( );
		}
		else
		{
			normalIndices = new int[ array.getIndexCount( ) ];
			array.getNormalIndices( 0 , normalIndices );
		}
		
		return normalIndices;
	}
	
	public static LineArray renderNormals( IndexedGeometryArray array , float scale )
	{
		final Point3f[ ] coordinates = copyCoordinates3f( array );
		final Vector3f[ ] normals = copyNormals3f( array );
		final int[ ] coordinateIndices = copyCoordinateIndices( array );
		final int[ ] normalIndices = copyNormalIndices( array );
		
		if( coordinates == null || normals == null || coordinateIndices == null || normalIndices == null )
		{
			throw new IllegalArgumentException( "Unable to get necessary information from the geometry" );
		}
		
		final int vertexCount = coordinateIndices.length * 2;
		
		final Point3f temp = new Point3f( );
		
		final LineArray result = new LineArray( vertexCount , GeometryArray.COORDINATES );
		int k = 0;
		for( int i = 0 ; i < coordinateIndices.length ; i++ )
		{
			final Point3f coordinate = coordinates[ coordinateIndices[ i ] ];
			final Vector3f normal = normals[ normalIndices[ i ] ];
			result.setCoordinate( k++ , coordinate );
			temp.scaleAdd( scale , normal , coordinate );
			result.setCoordinate( k++ , temp );
		}
		
		return result;
	}
	
	public static LineArray renderNormals( GeometryArray array , float scale )
	{
		final Point3f[ ] coordinates = copyCoordinates3f( array );
		final Vector3f[ ] normals = copyNormals3f( array );
		
		if( coordinates == null || normals == null )
		{
			throw new IllegalArgumentException( "Unable to get necessary information from the geometry" );
		}
		
		final int vertexCount = coordinates.length * 2;
		
		final Point3f temp = new Point3f( );
		
		final LineArray result = new LineArray( vertexCount , GeometryArray.COORDINATES );
		int k = 0;
		for( int i = 0 ; i < coordinates.length ; i++ )
		{
			final Point3f coordinate = coordinates[ i ];
			final Vector3f normal = normals[ i ];
			result.setCoordinate( k++ , coordinate );
			temp.scaleAdd( scale , normal , coordinate );
			result.setCoordinate( k++ , temp );
		}
		
		return result;
	}
	
	/**
	 * Transforms the coordinates in a geometry array. DOES NOT TRANSFORM THE NORMALS
	 * 
	 * @param geomArray
	 * @param xform
	 */
	public static void transformCoordinates( GeometryArray geomArray , Transform3D xform )
	{
		final Point3f temp = new Point3f( );
		for( int i = 0 ; i < geomArray.getVertexCount( ) ; i++ )
		{
			geomArray.getCoordinate( i , temp );
			xform.transform( temp );
			geomArray.setCoordinate( i , temp );
		}
	}
	
	public static Shape3D visualizeNormals( Shape3D shape , float scale , boolean local , Appearance app )
	{
		final GeometryArray geomArray = ( GeometryArray ) shape.getGeometry( );
		
		LineArray rendered;
		
		if( geomArray instanceof IndexedGeometryArray )
		{
			final IndexedGeometryArray indexedGeomArray = ( IndexedGeometryArray ) geomArray;
			rendered = renderNormals( indexedGeomArray , scale );
		}
		else
		{
			rendered = renderNormals( geomArray , scale );
		}
		
		if( !local )
		{
			final Transform3D localToVworld = new Transform3D( );
			shape.getLocalToVworld( localToVworld );
			transformCoordinates( rendered , localToVworld );
		}
		
		return new Shape3D( rendered , app );
	}
	
	public static Shape3D visualizeNormals( Shape3D shape , float scale , Appearance app )
	{
		return visualizeNormals( shape , scale , true , app );
	}
	
	public static Shape3D visualizeFronts( Shape3D shape , float scale , boolean local , Appearance app )
	{
		LineArray rendered = renderFronts( shape , scale );
		
		if( rendered == null )
		{
			return null;
		}
		if( local )
		{
			final Transform3D localToVworld = new Transform3D( );
			shape.getLocalToVworld( localToVworld );
			transformCoordinates( rendered , localToVworld );
		}
		return new Shape3D( rendered , app );
	}
	
	public static LineArray renderFronts( Shape3D shape , float scale )
	{
		Geometry geom = shape.getGeometry( );
		
		if( geom instanceof IndexedGeometryArray )
		{
			return renderFronts( ( IndexedGeometryArray ) geom , scale );
		}
		else if( geom instanceof GeometryArray )
		{
			return renderFronts( ( GeometryArray ) geom , scale );
		}
		return null;
	}
	
	public static LineArray renderFronts( IndexedGeometryArray geom , float scale )
	{
		List<Point3f> points = new ArrayList<Point3f>( );
		
		Point3f[ ] coordinates = copyCoordinates3f( geom );
		int[ ] indices = copyCoordinateIndices( geom );
		
		Point3f t = new Point3f( );
		Vector3f v1 = new Vector3f( );
		Vector3f v2 = new Vector3f( );
		Vector3f n = new Vector3f( );
		
		int k;
		for( k = 0 ; k < indices.length ; k += 3 )
		{
			Point3f a = coordinates[ indices[ k ] ];
			Point3f b = coordinates[ indices[ k + 1 ] ];
			Point3f c = coordinates[ indices[ k + 2 ] ];
			
			v1.sub( b , a );
			v2.sub( c , b );
			
			n.cross( v2 , v1 );
			n.normalize( );
			
			t.add( a , b );
			t.add( c );
			t.scale( 1 / 3f );
			
			points.add( new Point3f( t ) );
			t.add( n );
			points.add( new Point3f( t ) );
		}
		
		LineArray result = new LineArray( points.size( ) , GeometryArray.COORDINATES );
		k = 0;
		for( Point3f p : points )
		{
			result.setCoordinate( k++ , p );
		}
		
		return result;
	}
	
	public static LineArray renderFronts( GeometryArray geom , float scale )
	{
		if( geom instanceof IndexedGeometryArray )
		{
			return renderFronts( ( IndexedGeometryArray ) geom , scale );
		}
		
		List<Point3f> points = new ArrayList<Point3f>( );
		
		Point3f[ ] coordinates = copyCoordinates3f( geom );
		
		Point3f t = new Point3f( );
		Vector3f v1 = new Vector3f( );
		Vector3f v2 = new Vector3f( );
		Vector3f n = new Vector3f( );
		
		int k;
		for( k = 0 ; k < coordinates.length ; k += 3 )
		{
			Point3f a = coordinates[ k ];
			Point3f b = coordinates[ k + 1 ];
			Point3f c = coordinates[ k + 2 ];
			
			v1.sub( b , a );
			v2.sub( c , b );
			
			n.cross( v2 , v1 );
			n.normalize( );
			
			t.add( a , b );
			t.add( c );
			t.scale( 1 / 3f );
			
			points.add( new Point3f( t ) );
			t.add( n );
			points.add( new Point3f( t ) );
		}
		
		LineArray result = new LineArray( points.size( ) , GeometryArray.COORDINATES );
		k = 0;
		for( Point3f p : points )
		{
			result.setCoordinate( k++ , p );
		}
		
		return result;
	}
	
	public static void hardTransform( GeometryArray geom , Transform3D xform )
	{
		Point3f coord = new Point3f( );
		for( int i = 0 ; i < geom.getVertexCount( ) ; i++ )
		{
			geom.getCoordinate( i , coord );
			xform.transform( coord );
			geom.setCoordinate( i , coord );
		}
	}
	
	public static void showErrorDialog( Component parent , Throwable t )
	{
		final StringBuffer message = new StringBuffer( );
		message.append( t );
		for( final StackTraceElement elem : t.getStackTrace( ) )
		{
			message.append( "\n" );
			message.append( elem );
		}
		
		JOptionPane.showMessageDialog( parent , message , "Exception" , JOptionPane.ERROR_MESSAGE );
	}
	
	public static void setVisible( Appearance app , boolean visible )
	{
		RenderingAttributes ra = app.getRenderingAttributes( );
		if( ra == null )
		{
			ra = new RenderingAttributes( );
			app.setRenderingAttributes( ra );
		}
		ra.setVisible( visible );
	}
	
	public static void setVisible( Shape3D s3D , boolean visible )
	{
		setVisible( getAppearance( s3D ) , visible );
	}
	
	public static void setVisibleRecursive( Node n , boolean visible )
	{
		for( final Node node : SceneGraphIterator.boundedIterable( n ) )
		{
			if( node instanceof Shape3D )
			{
				setVisible( getAppearance( ( Shape3D ) node ) , visible );
			}
		}
	}
	
	public static boolean isVisible( Appearance app )
	{
		final RenderingAttributes ra = app.getRenderingAttributes( );
		if( ra == null )
		{
			return true;
		}
		return ra.getVisible( );
	}
	
	public static boolean isVisible( Shape3D s3D )
	{
		final Appearance app = s3D.getAppearance( );
		if( app == null )
		{
			return true;
		}
		return isVisible( app );
	}
	
	public static void toggleVisibility( Shape3D s3D )
	{
		setVisible( s3D , !isVisible( s3D ) );
	}
	
	public static void toggleVisibilityRecursive( Node n )
	{
		for( final Node node : SceneGraphIterator.boundedIterable( n ) )
		{
			if( node instanceof Shape3D )
			{
				toggleVisibility( ( Shape3D ) node );
			}
		}
	}
	
	public static void setPolygonMode( Appearance app , int mode )
	{
		PolygonAttributes pa = app.getPolygonAttributes( );
		if( pa == null )
		{
			pa = new PolygonAttributes( );
			app.setPolygonAttributes( pa );
		}
		pa.setPolygonMode( mode );
	}
	
	public static void setPolygonMode( Shape3D s3D , int mode )
	{
		setPolygonMode( getAppearance( s3D ) , mode );
	}
	
	public static void setPolygonModeRecursive( Node n , int mode )
	{
		for( final Node node : SceneGraphIterator.boundedIterable( n ) )
		{
			if( node instanceof Shape3D )
			{
				setPolygonMode( ( Shape3D ) node , mode );
			}
		}
	}
	
	public static void setColor( Appearance app , Color3f color , boolean checkMaterial )
	{
		ColoringAttributes ca = app.getColoringAttributes( );
		if( ca == null )
		{
			ca = new ColoringAttributes( );
			ca.setShadeModel( ColoringAttributes.SHADE_FLAT );
			app.setColoringAttributes( ca );
		}
		ca.setColor( color.x , color.y , color.z );
		
		app.setTransparencyAttributes( null );
		
		if( checkMaterial )
		{
			final Material mat = app.getMaterial( );
			if( mat != null )
			{
				mat.setAmbientColor( color.x , color.y , color.z );
				mat.setDiffuseColor( color.x , color.y , color.z );
				mat.setSpecularColor( color.x , color.y , color.z );
			}
		}
	}
	
	public static void setColor( Appearance app , Color4f color , boolean checkMaterial )
	{
		ColoringAttributes ca = app.getColoringAttributes( );
		if( ca == null )
		{
			ca = new ColoringAttributes( );
			ca.setShadeModel( ColoringAttributes.SHADE_FLAT );
			app.setColoringAttributes( ca );
		}
		ca.setColor( color.x , color.y , color.z );
		
		if( color.w != 1f )
		{
			TransparencyAttributes ta = app.getTransparencyAttributes( );
			if( ta == null )
			{
				ta = new TransparencyAttributes( );
				app.setTransparencyAttributes( ta );
			}
			ta.setTransparencyMode( TransparencyAttributes.BLENDED );
			ta.setTransparency( 1f - color.w );
		}
		else
		{
			app.setTransparencyAttributes( null );
		}
		
		if( checkMaterial )
		{
			final Material mat = app.getMaterial( );
			if( mat != null )
			{
				mat.setAmbientColor( color.x , color.y , color.z );
				mat.setDiffuseColor( color.x , color.y , color.z );
				mat.setSpecularColor( color.x , color.y , color.z );
			}
		}
	}
	
	public static void setColor( Shape3D s3D , Color4f color , boolean checkMaterial )
	{
		setColor( getAppearance( s3D ) , color , checkMaterial );
	}
	
	public static void setColorRecursive( Node n , Color4f color , boolean checkMaterial )
	{
		for( final Node node : SceneGraphIterator.boundedIterable( n ) )
		{
			if( node instanceof Shape3D )
			{
				setColor( getAppearance( ( Shape3D ) node ) , color , checkMaterial );
			}
		}
	}
	
	public static void modifyColoringAttributesRecursive( Node n , Color3f color , int shadeModel )
	{
		for( final Node node : SceneGraphIterator.boundedIterable( n ) )
		{
			if( node instanceof Shape3D )
			{
				modifyColoringAttributes( ( Shape3D ) node , color , shadeModel );
			}
		}
	}
	
	public static void modifyColoringAttributes( Shape3D n , Color3f color , int shadeModel )
	{
		Appearance app = getAppearance( n );
		ColoringAttributes ca = app.getColoringAttributes( );
		if( ca == null )
		{
			ca = new ColoringAttributes( );
			app.setColoringAttributes( ca );
		}
		ca.setColor( color );
		ca.setShadeModel( shadeModel );
	}
}
