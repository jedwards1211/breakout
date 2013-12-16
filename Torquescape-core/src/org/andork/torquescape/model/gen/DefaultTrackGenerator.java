package org.andork.torquescape.model.gen;

import org.andork.torquescape.model.coord.ICoordFn;
import org.andork.torquescape.model.index.IIndexFn;
import org.andork.torquescape.model.list.CharList;
import org.andork.torquescape.model.list.FloatList;

public class DefaultTrackGenerator
{
	int					vertexCount	= 0;
	FloatList			verts		= new FloatList( );
	CharList			indices		= new CharList( );
	
	int					paramStartIndex;
	private float[ ]	matrix		= new float[ 16 ];
	
	float[ ]			coord		= new float[ 3 ];
	
	public void add( ICoordFn section , IIndexFn mesh , float startParam , float endParam , float step )
	{
		vertexCount = 0;
		
		for( float param = startParam ; param < endParam ; param += step )
		{
			paramStartIndex = vertexCount;
			eval( section , param );
			eval( mesh , param );
		}
		
		paramStartIndex = vertexCount;
		eval( section , endParam );
	}
	
	private void eval( ICoordFn section , float param )
	{
		for( int i = 0 ; i < section.getCoordCount( param ) ; i++ )
		{
			section.eval( param , i , coord );
			verts.add( coord[ 0 ] , coord[ 1 ] , coord[ 2 ] );
			verts.add( 0 , 0 , 0 );
		}
		vertexCount += section.getCoordCount( param );
	}
	
	private void eval( IIndexFn mesh , float param )
	{
		for( int i = 0 ; i < mesh.getIndexCount( param ) ; i++ )
		{
			indices.add( ( char ) ( paramStartIndex + mesh.eval( param , i ) ) );
		}
	}
	
	public float[ ] getVertices( )
	{
		return verts.drain( );
	}
	
	public char[ ] getIndices( )
	{
		return indices.drain( );
	}
}
