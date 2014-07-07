package org.andork.breakout;

import java.util.Set;

import org.andork.breakout.model.Survey3dModel;
import org.andork.breakout.model.Survey3dModel.Shot3d;
import org.andork.func.FloatBinaryOperator;
import org.andork.math3d.Vecmath;
import org.andork.spatial.RTraversal;
import org.andork.spatial.Rectmath;
import org.omg.CORBA.FloatHolder;

public class FluidPerspectiveToOrtho
{
	public static float[ ] getOrthoBounds( Survey3dModel model , Set<Shot3d> shotsInView , float[ ] orthoRight , float[ ] orthoUp , float[ ] orthoForward )
	{
		float[ ] result = new float[ 4 ];
		
		float[ ] shotsInViewMbr = Rectmath.voidRectf( 3 );
		
		for( Shot3d shot : shotsInView )
		{
			shot.unionMbrInto( shotsInViewMbr );
		}
		
		FloatBinaryOperator minFunc = ( a , b ) -> Float.isNaN( a ) || b < a ? b : a;
		FloatBinaryOperator maxFunc = ( a , b ) -> Float.isNaN( a ) || b > a ? b : a;
		
		result[ 0 ] = getFarthestExtent( model , shotsInView , shotsInViewMbr , orthoRight , minFunc );
		result[ 1 ] = getFarthestExtent( model , shotsInView , shotsInViewMbr , orthoUp , minFunc );
		result[ 2 ] = getFarthestExtent( model , shotsInView , shotsInViewMbr , orthoForward , minFunc );
		result[ 3 ] = getFarthestExtent( model , shotsInView , shotsInViewMbr , orthoRight , maxFunc );
		result[ 4 ] = getFarthestExtent( model , shotsInView , shotsInViewMbr , orthoUp , maxFunc );
		result[ 5 ] = getFarthestExtent( model , shotsInView , shotsInViewMbr , orthoForward , maxFunc );
		
		return result;
	}
	
	private static float getFarthestExtent( Survey3dModel model , Set<Shot3d> shotsInView , float[ ] shotsInViewMbr , float[ ] direction , FloatBinaryOperator extentFunction )
	{
		FloatHolder farthest = new FloatHolder( Float.NaN );
		
		float[ ] testPoint = new float[ 3 ];
		
		RTraversal.traverse( model.getTree( ).getRoot( ) ,
				node -> {
					if( !Rectmath.intersects3( shotsInViewMbr , node.mbr( ) ) )
					{
						return false;
					}
					return Rectmath.findCorner3( node.mbr( ) , testPoint , corner -> {
						float dist = Vecmath.dot3( corner , direction );
						return farthest.value != extentFunction.applyAsFloat( farthest.value , dist ) ? true : null;
					} ) ? true : false; // make sure we don't return null!
				} ,
				leaf -> {
					if( shotsInView.contains( leaf.object( ) ) )
					{
						for( float[ ] coord : leaf.object( ).coordIterable( ) )
						{
							float dist = Vecmath.dot3( coord , direction );
							farthest.value = extentFunction.applyAsFloat( farthest.value , dist );
						}
					}
					return true;
				} );
		
		return farthest.value;
	}
}
