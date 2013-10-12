package org.andork.torquescape.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point3f;

import org.andork.util.CollectionUtils;

import com.google.common.collect.LinkedListMultimap;

public class Zone
{
	public static String								PRIMARY_VERT_BUFFER_KEY	= "_";
	
	ByteBuffer											vertBuffer;
	int													bytesPerVertex;
	
	CharBuffer											indexBuffer;
	
	public final Map<String, ByteBuffer>				vertBuffers				= CollectionUtils.newHashMap( );
	public final Map<String, Integer>					bytesPerVertexMap		= CollectionUtils.newHashMap( );
	
	final Set<Zone>										connectedZones			= new HashSet<Zone>( 0 );
	final List<ISlice>									slices					= new ArrayList<ISlice>( 0 );
	
	public final LinkedListMultimap<Edge, Character>	edgeToTriMap			= LinkedListMultimap.create( );
	public final LinkedListMultimap<Point3f, Character>	pointToTriMap			= LinkedListMultimap.create( );
	
	public void init( int vertexCount , int bytesPerVertex , int indexCount )
	{
		this.bytesPerVertex = bytesPerVertex;
		
		ByteBuffer bb = ByteBuffer.allocateDirect( vertexCount * bytesPerVertex );
		bb.order( ByteOrder.nativeOrder( ) );
		vertBuffer = bb;
		
		vertBuffers.put( PRIMARY_VERT_BUFFER_KEY , vertBuffer );
		bytesPerVertexMap.put( PRIMARY_VERT_BUFFER_KEY , bytesPerVertex );
		
		bb = ByteBuffer.allocateDirect( indexCount * 2 );
		bb.order( ByteOrder.nativeOrder( ) );
		indexBuffer = bb.asCharBuffer( );
	}
	
	public void rebuildMaps( )
	{
		edgeToTriMap.clear( );
		pointToTriMap.clear( );
		
		int prevIndexPosition = indexBuffer.position( );
		int prevVertPosition = vertBuffer.position( );
		
		indexBuffer.position( 0 );
		char triIndex = 0;
		while( indexBuffer.hasRemaining( ) )
		{
			vertBuffer.position( indexBuffer.get( ) * bytesPerVertex );
			Point3f p0 = new Point3f( vertBuffer.getFloat( ) , vertBuffer.getFloat( ) , vertBuffer.getFloat( ) );
			vertBuffer.position( indexBuffer.get( ) * bytesPerVertex );
			Point3f p1 = new Point3f( vertBuffer.getFloat( ) , vertBuffer.getFloat( ) , vertBuffer.getFloat( ) );
			vertBuffer.position( indexBuffer.get( ) * bytesPerVertex );
			Point3f p2 = new Point3f( vertBuffer.getFloat( ) , vertBuffer.getFloat( ) , vertBuffer.getFloat( ) );
			
			pointToTriMap.put( p0 , triIndex );
			pointToTriMap.put( p1 , triIndex );
			pointToTriMap.put( p2 , triIndex );
			
			edgeToTriMap.put( new Edge( p0 , p1 ).canonical( ) , triIndex );
			edgeToTriMap.put( new Edge( p1 , p2 ).canonical( ) , triIndex );
			edgeToTriMap.put( new Edge( p2 , p0 ).canonical( ) , triIndex );
			
			triIndex = ( char ) ( triIndex + 3 );
		}
		
		vertBuffer.position( prevVertPosition );
		indexBuffer.position( prevIndexPosition );
	}
	
	public void printTriangle( int index )
	{
		indexBuffer.position( 0 );
		vertBuffer.position( 0 );
		int i0 = indexBuffer.get( index );
		int i1 = indexBuffer.get( index + 1 );
		int i2 = indexBuffer.get( index + 2 );
		
		float x0 = vertBuffer.getFloat( i0 * bytesPerVertex );
		float y0 = vertBuffer.getFloat( i0 * bytesPerVertex + 4 );
		float z0 = vertBuffer.getFloat( i0 * bytesPerVertex + 8 );
		
		float x1 = vertBuffer.getFloat( i1 * bytesPerVertex );
		float y1 = vertBuffer.getFloat( i1 * bytesPerVertex + 4 );
		float z1 = vertBuffer.getFloat( i1 * bytesPerVertex + 8 );
		
		float x2 = vertBuffer.getFloat( i2 * bytesPerVertex );
		float y2 = vertBuffer.getFloat( i2 * bytesPerVertex + 4 );
		float z2 = vertBuffer.getFloat( i2 * bytesPerVertex + 8 );
		
		System.out.println( "(" + x0 + ", " + y0 + ", " + z0 + ")" );
		System.out.println( "(" + x1 + ", " + y1 + ", " + z1 + ")" );
		System.out.println( "(" + x2 + ", " + y2 + ", " + z2 + ")" );
		System.out.println( );
	}
	
	public ByteBuffer getVertBuffer( )
	{
		return vertBuffer;
	}
	
	public CharBuffer getIndexBuffer( )
	{
		return indexBuffer;
	}
	
	public int getBytesPerVertex( )
	{
		return bytesPerVertex;
	}
	
	public void addSlice( ISlice slice )
	{
		slices.add( slice );
	}
	
	public List<ISlice> getSlices( )
	{
		return Collections.unmodifiableList( slices );
	}
}
