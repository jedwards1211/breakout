package org.andork.torquescape.model.meshing;

public interface IMeshingFunction
{
	/**
	 * Passes the vertex indices for the mesh at a given parameter to an {@link IIndexVisitor} in sequence. Index 0 corresponds to the first vertex from the
	 * section function at {@code param}, 1 corresponds to the second, and indices less than 0 / greater than last index in the section at {@code param}
	 * correspond to the previous / next section, respectively.
	 * 
	 * @param param
	 *            the param to evaluate at.
	 * @param indexVisitor
	 *            the visitor to pass the vertex indices at {@code param} to.
	 */
	public void eval( float param , IIndexVisitor indexVisitor );
}
