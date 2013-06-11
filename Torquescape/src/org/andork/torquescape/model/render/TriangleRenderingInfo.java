package org.andork.torquescape.model.render;

import javax.vecmath.Vector3f;

import org.andork.vecmath.VecmathUtils;

public class TriangleRenderingInfo
{
	/**
	 * Array indicating where there are folds in the triangle edges. This will be used to compute the normals.<br>
	 * <br>
	 * The following diagram shows the edge locations corresponding to the array indices.<br>
	 * A {@code true} value at index {@code i} indicates the edge is folded at the endpoint nearest to {@code i} in the diagram below. An edge may be folded at
	 * only one endpoint.
	 * 
	 * <pre>
	 * p1---2----3---p2
	 *  \            /
	 *   1          4
	 *    \        /
	 *     \      /
	 *      0    5
	 *       \  /
	 *        p0
	 * </pre>
	 */
	public final FoldType[ ]	folds	= new FoldType[ 6 ];
	final Vector3f[ ]			normals	= VecmathUtils.allocVector3fArray( 6 );
}
