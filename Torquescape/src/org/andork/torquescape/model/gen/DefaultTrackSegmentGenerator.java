package org.andork.torquescape.model.gen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.torquescape.model.Triangle;
import org.andork.torquescape.model.render.FoldType;
import org.andork.torquescape.model.render.TriangleRenderingInfo;
import org.andork.torquescape.model.section.ISectionFunction;
import org.andork.torquescape.model.section.SectionCurve;
import org.andork.torquescape.model.xform.IXformFunction;
import org.andork.vecmath.VecmathUtils;

public class DefaultTrackSegmentGenerator implements ITrackSegmentGenerator
{
	@Override
	public void generate( IXformFunction trackAxis , ISectionFunction crossSection , float start , float end , float step , J3DTempsPool pool , List<List<Triangle>> out )
	{
		ArrayList<SectionCurve> sectionCurves = crossSection.eval( start , pool , new ArrayList<SectionCurve>( ) );
		
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
		
		List<Triangle>[ ] triGroups = ( List<Triangle>[ ] ) new List[ sectionCurves.size( ) ];
		
		Point3f[ ][ ] prevSectionPoints = new Point3f[ sectionCurves.size( ) ][ ];
		boolean[ ][ ] prevSmoothFlags = new boolean[ sectionCurves.size( ) ][ ];
		Point3f[ ][ ] nextSectionPoints = new Point3f[ sectionCurves.size( ) ][ ];
		boolean[ ][ ] nextSmoothFlags = new boolean[ sectionCurves.size( ) ][ ];
		
		for( int c = 0 ; c < sectionCurves.size( ) ; c++ )
		{
			int pointCount = sectionCurves.get( c ).points.length;
			prevSectionPoints[ c ] = VecmathUtils.allocPoint3fArray( pointCount );
			prevSmoothFlags[ c ] = new boolean[ pointCount ];
			nextSectionPoints[ c ] = VecmathUtils.allocPoint3fArray( pointCount );
			nextSmoothFlags[ c ] = new boolean[ pointCount ];
			
			triGroups[ c ] = new LinkedList<Triangle>( );
			out.add( triGroups[ c ] );
		}
		
		Transform3D xform = new Transform3D( );
		
		trackAxis.eval( start , pool , xform );
		
		getSectionVertices( sectionCurves , prevSectionPoints , prevSmoothFlags , xform );
		
		for( param = start + step ; param < end + step ; param += step )
		{
			if( param > end )
			{
				param = end;
			}
			crossSection.eval( param , pool , sectionCurves );
			
			trackAxis.eval( param , pool , xform );
			
			getSectionVertices( sectionCurves , nextSectionPoints , nextSmoothFlags , xform );
			
			for( int c = 0 ; c < sectionCurves.size( ) ; c++ )
			{
				List<Triangle> triGroup = triGroups[ c ];
				
				int pointCount = nextSectionPoints[ c ].length;
				
				checkForBadValues( nextSectionPoints );
				
				for( int p = 0 ; p < pointCount ; p++ )
				{
					int nextP = ( p + 1 ) % pointCount;
					TriangleRenderingInfo ri = new TriangleRenderingInfo( );
					ri.folds[ 0 ] = prevSmoothFlags[ c ][ p ] ? FoldType.FOLDED : FoldType.NOT_FOLDED;
					ri.folds[ 1 ] = nextSmoothFlags[ c ][ p ] ? FoldType.FOLDED : FoldType.NOT_FOLDED;
					ri.folds[ 2 ] = ri.folds[ 3 ] = ri.folds[ 4 ] = ri.folds[ 5 ] = FoldType.ANGLE_DEPENDENT;
					triGroup.add( new Triangle( prevSectionPoints[ c ][ p ] , nextSectionPoints[ c ][ p ] , prevSectionPoints[ c ][ nextP ] , ri ) );
					
					ri = new TriangleRenderingInfo( );
					ri.folds[ 0 ] = ri.folds[ 1 ] = ri.folds[ 2 ] = ri.folds[ 3 ] = FoldType.ANGLE_DEPENDENT;
					ri.folds[ 4 ] = nextSmoothFlags[ c ][ nextP ] ? FoldType.FOLDED : FoldType.NOT_FOLDED;
					ri.folds[ 5 ] = prevSmoothFlags[ c ][ nextP ] ? FoldType.FOLDED : FoldType.NOT_FOLDED;
					triGroup.add( new Triangle( prevSectionPoints[ c ][ nextP ] , nextSectionPoints[ c ][ p ] , nextSectionPoints[ c ][ nextP ] , ri ) );
				}
			}
			
			Point3f[ ][ ] swapPoints = prevSectionPoints;
			prevSectionPoints = nextSectionPoints;
			nextSectionPoints = swapPoints;
			
			boolean[ ][ ] swapNextSmoothFlags = nextSmoothFlags;
			prevSmoothFlags = nextSmoothFlags;
			nextSmoothFlags = swapNextSmoothFlags;
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
	
	private void getSectionVertices( List<SectionCurve> curves , Point3f[ ][ ] points , boolean[ ][ ] smoothFlags , Transform3D xform )
	{
		for( int c = 0 ; c < curves.size( ) ; c++ )
		{
			SectionCurve curve = curves.get( c );
			Point3f[ ] curvePoints = points[ c ];
			boolean[ ] curveSmoothFlags = smoothFlags[ c ];
			
			for( int p = 0 ; p < curve.points.length ; p++ )
			{
				curvePoints[ p ].set( curve.points[ p ] );
				curveSmoothFlags[ p ] = curve.smoothFlags[ p ];
				
				xform.transform( curvePoints[ p ] );
			}
		}
	}
}
