package org.andork.torquescape.model.gen;

import org.andork.torquescape.model.list.CharList;
import org.andork.torquescape.model.list.PointList;
import org.andork.torquescape.model.meshing.IIndexVisitor;
import org.andork.torquescape.model.meshing.IMeshingFunction;
import org.andork.torquescape.model.section.IPointVisitor;
import org.andork.torquescape.model.section.ISectionFunction;
import org.andork.torquescape.model.xform.IXformFunction;

public class DefaultTrackGenerator implements IPointVisitor , IIndexVisitor
{
	PointList			verts	= new PointList( );
	CharList			indices	= new CharList( );
	
	int					paramStartIndex;
	private float[ ]	matrix	= new float[ 16 ];
	
	public void add( IXformFunction xform , ISectionFunction section , IMeshingFunction mesh , float startParam , float endParam , float step )
	{
		for( float param = startParam ; param < endParam ; param += step )
		{
			paramStartIndex = verts.size( ) / 2;
			xform.eval( param , matrix );
			section.eval( param , this );
			mesh.eval( param , this );
		}
		
		paramStartIndex = verts.size( ) / 2;
		xform.eval( endParam , matrix );
		section.eval( endParam , this );
	}
	
	public float[ ] getVertices( )
	{
		return verts.toArray( );
	}
	
	public char[ ] getIndices( )
	{
		return indices.toArray( );
	}
	
	@Override
	public void visit( char index )
	{
		indices.add( ( char ) ( paramStartIndex + index ) );
	}
	
	@Override
	public void visit( float x , float y , float z )
	{
		float x2 = matrix[ 0 ] * x + matrix[ 1 ] * y + matrix[ 2 ] * z + matrix[ 3 ];
		float y2 = matrix[ 4 ] * x + matrix[ 5 ] * y + matrix[ 6 ] * z + matrix[ 7 ];
		float z2 = matrix[ 8 ] * x + matrix[ 9 ] * y + matrix[ 10 ] * z + matrix[ 11 ];
		verts.add( x2 , y2 , z2 );
		verts.add( 0 , 0 , 0 );
	}
}