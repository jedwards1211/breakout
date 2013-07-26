package org.andork.torquescape.model2.section;

public interface ISectionFunction
{
	/**
	 * Passes the points of the section curve at a given parameter to an {@link IPointVisitor}, in sequence.
	 * 
	 * @param param
	 *            the param to evaluate this section function at.
	 * @param sectionPointVisitor
	 *            the visitor to which the section curve points at {@code param} will be passed.
	 */
	public void eval( float param , IPointVisitor sectionPointVisitor );
}
