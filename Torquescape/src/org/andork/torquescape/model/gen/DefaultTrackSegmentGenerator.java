package org.andork.torquescape.model.gen;

import java.util.List;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.math3d.curve.ICurve3f;
import org.andork.torquescape.model.Triangle;
import org.andork.torquescape.model.section.ICrossSectionCurve;
import org.andork.torquescape.model.section.ICrossSectionFunction;
import org.andork.vecmath.VecmathUtils;

public class DefaultTrackSegmentGenerator implements ITrackSegmentGenerator
{
	@Override
	public void generate( ICurve3f trackAxis , ICrossSectionFunction crossSection , float start , float end , float step , J3DTempsPool pool , List<GeometryArray> outGeom , List<Triangle> outTriangles )
	{
		ICrossSectionCurve[ ] sectionCurves = crossSection.eval( start , pool );
		
		int crossSectionCount = 0;
		
		float param;
		
		for( param = start ; param < end + step ; param += step )
		{
			if( param > end )
			{
				param = end;
			}
			crossSectionCount++ ;
		}
		
		GeometryArray[ ] geoms = new GeometryArray[ sectionCurves.length ];
		int coordIndices[] = new int[ sectionCurves.length ];
		
		Point3f[ ][ ] prevSectionPoints = new Point3f[ sectionCurves.length ][ ];
		Vector3f[ ][ ] prevSectionPrevNormals = new Vector3f[ sectionCurves.length ][ ];
		Vector3f[ ][ ] prevSectionNextNormals = new Vector3f[ sectionCurves.length ][ ];
		Point3f[ ][ ] nextSectionPoints = new Point3f[ sectionCurves.length ][ ];
		Vector3f[ ][ ] nextSectionPrevNormals = new Vector3f[ sectionCurves.length ][ ];
		Vector3f[ ][ ] nextSectionNextNormals = new Vector3f[ sectionCurves.length ][ ];
		
		for( int c = 0 ; c < sectionCurves.length ; c++ )
		{
			int pointCount = sectionCurves[ c ].getPointCount( );
			prevSectionPoints[ c ] = VecmathUtils.allocPoint3fArray( pointCount );
			prevSectionPrevNormals[ c ] = VecmathUtils.allocVector3fArray( pointCount );
			prevSectionNextNormals[ c ] = VecmathUtils.allocVector3fArray( pointCount );
			nextSectionPoints[ c ] = VecmathUtils.allocPoint3fArray( pointCount );
			nextSectionPrevNormals[ c ] = VecmathUtils.allocVector3fArray( pointCount );
			nextSectionNextNormals[ c ] = VecmathUtils.allocVector3fArray( pointCount );
			
			geoms[ c ] = new TriangleArray( pointCount * ( crossSectionCount - 1 ) * 6 , GeometryArray.COORDINATES | GeometryArray.NORMALS );
			outGeom.add( geoms[ c ] );
		}
		
		Transform3D xform = new Transform3D( );
		
		trackAxis.eval( start , pool , xform );
		
		getSectionVertices( sectionCurves , prevSectionPoints , prevSectionPrevNormals , prevSectionNextNormals , xform );
		
		for( param = start + step ; param < end + step ; param += step )
		{
			if( param > end )
			{
				param = end;
			}
			sectionCurves = crossSection.eval( param , pool );
			
			trackAxis.eval( param , pool , xform );
			
			getSectionVertices( sectionCurves , nextSectionPoints , nextSectionPrevNormals , nextSectionNextNormals , xform );
			
			for( int c = 0 ; c < sectionCurves.length ; c++ )
			{
				GeometryArray geom = geoms[ c ];
				int pointCount = nextSectionPoints[ c ].length;
				
				checkForBadValues( nextSectionPoints );
				checkForBadValues( nextSectionPrevNormals );
				checkForBadValues( nextSectionNextNormals );
				
				for( int p = 0 ; p < pointCount ; p++ )
				{
					int nextP = ( p + 1 ) % pointCount;
					geom.setCoordinate( coordIndices[ c ] , prevSectionPoints[ c ][ p ] );
					geom.setNormal( coordIndices[ c ] , prevSectionNextNormals[ c ][ p ] );
					coordIndices[ c ]++ ;
					geom.setCoordinate( coordIndices[ c ] , prevSectionPoints[ c ][ nextP ] );
					geom.setNormal( coordIndices[ c ] , prevSectionPrevNormals[ c ][ nextP ] );
					coordIndices[ c ]++ ;
					geom.setCoordinate( coordIndices[ c ] , nextSectionPoints[ c ][ p ] );
					geom.setNormal( coordIndices[ c ] , nextSectionNextNormals[ c ][ p ] );
					coordIndices[ c ]++ ;
					
					outTriangles.add( new Triangle( prevSectionPoints[ c ][ p ] , nextSectionPoints[ c ][ p ] , prevSectionPoints[ c ][ nextP ] , prevSectionNextNormals[ c ][ p ] , nextSectionNextNormals[ c ][ p ] , prevSectionPrevNormals[ c ][ nextP ] ) );
					
					geom.setCoordinate( coordIndices[ c ] , nextSectionPoints[ c ][ p ] );
					geom.setNormal( coordIndices[ c ] , nextSectionNextNormals[ c ][ p ] );
					coordIndices[ c ]++ ;
					geom.setCoordinate( coordIndices[ c ] , prevSectionPoints[ c ][ nextP ] );
					geom.setNormal( coordIndices[ c ] , prevSectionPrevNormals[ c ][ nextP ] );
					coordIndices[ c ]++ ;
					geom.setCoordinate( coordIndices[ c ] , nextSectionPoints[ c ][ nextP ] );
					geom.setNormal( coordIndices[ c ] , nextSectionPrevNormals[ c ][ nextP ] );
					coordIndices[ c ]++ ;
					
					outTriangles.add( new Triangle( nextSectionPoints[ c ][ p ] , prevSectionPoints[ c ][ nextP ] , nextSectionPoints[ c ][ nextP ] , nextSectionNextNormals[ c ][ p ] , prevSectionPrevNormals[ c ][ nextP ] , nextSectionPrevNormals[ c ][ nextP ] ) );
				}
			}
			
			Point3f[ ][ ] swapPoints = prevSectionPoints;
			prevSectionPoints = nextSectionPoints;
			nextSectionPoints = swapPoints;
			
			Vector3f[ ][ ] swapPrevNormals = prevSectionPrevNormals;
			prevSectionPrevNormals = nextSectionPrevNormals;
			nextSectionPrevNormals = swapPrevNormals;
			
			Vector3f[ ][ ] swapNextNormals = prevSectionNextNormals;
			prevSectionNextNormals = nextSectionNextNormals;
			nextSectionNextNormals = swapNextNormals;
		}
	}
	
	public void checkForBadValues( Tuple3f[ ][ ] t )
	{
		for( int i = 0 ; i < t.length ; i++ )
		{
			for( int j = 0 ; j < t[ i ].length ; j++ )
			{
				Tuple3f t3f = t[ i ][ j ];
				VecmathUtils.checkReal( t3f );
			}
		}
	}
	
	private void getSectionVertices( ICrossSectionCurve[ ] curves , Point3f[ ][ ] points , Vector3f[ ][ ] prevNormals , Vector3f[ ][ ] nextNormals , Transform3D xform )
	{
		for( int c = 0 ; c < curves.length ; c++ )
		{
			ICrossSectionCurve curve = curves[ c ];
			Point3f[ ] curvePoints = points[ c ];
			Vector3f[ ] curvePrevNormals = prevNormals[ c ];
			Vector3f[ ] curveNextNormals = nextNormals[ c ];
			
			for( int p = 0 ; p < curve.getPointCount( ) ; p++ )
			{
				curve.getPoint( p , curvePoints[ p ] );
				curve.getPrevSegmentNormal( p , curvePrevNormals[ p ] );
				curve.getNextSegmentNormal( p , curveNextNormals[ p ] );
				;
				
				xform.transform( curvePoints[ p ] );
				xform.transform( curvePrevNormals[ p ] );
				xform.transform( curveNextNormals[ p ] );
				
				curvePrevNormals[ p ].normalize( );
				curveNextNormals[ p ].normalize( );
			}
		}
	}
}
