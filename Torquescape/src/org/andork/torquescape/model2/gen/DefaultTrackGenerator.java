package org.andork.torquescape.model2.gen;

import org.andork.torquescape.model2.section.ISectionFunction;
import org.andork.torquescape.model.xform.IXformFunction;
import org.andork.torquescape.model2.list.IntList;
import org.andork.torquescape.model2.list.PointList;
import org.andork.torquescape.model2.meshing.IIntVisitor;
import org.andork.torquescape.model2.meshing.IMeshingFunction;
import org.andork.torquescape.model2.section.IPointVisitor;

public class DefaultTrackGenerator implements IPointVisitor , IIntVisitor
{
	PointList			verts	= new PointList( );
	IntList				indices	= new IntList( );
	
	int					paramStartIndex;
	private float[ ]	matrix	= new float[ 16 ];
	
	public void add( IXformFunction xform , ISectionFunction section , IMeshingFunction mesh , float startParam , float endParam , float step )
	{
		for( float param = startParam ; param < endParam ; param += step )
		{
			paramStartIndex = verts.size( ) * 3;
			xform.eval( param , matrix );
			section.eval( param , this );
			mesh.eval( param , this );
		}
		
		paramStartIndex = verts.size( ) * 3;
		xform.eval( endParam , matrix );
		section.eval( endParam , this );
	}
	
	public float[ ] getVertices( )
	{
		return verts.toArray( );
	}
	
	public int[ ] getIndices( )
	{
		return indices.toArray( );
	}
	
	@Override
	public void visit( int value )
	{
		indices.add( paramStartIndex + value * 6 );
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
