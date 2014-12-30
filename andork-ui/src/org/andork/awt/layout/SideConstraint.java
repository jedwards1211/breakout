package org.andork.awt.layout;

import java.awt.Component;

/**
 * Info for constraining to a fixed offset from a given side of a given component.
 * 
 * @author James
 */
public class SideConstraint
{
	/**
	 * the component whose side to constrain to.
	 */
	final Component	targetComponent;
	/**
	 * the side of {@link #targetComponent} to constrain to.
	 */
	final Side		targetSide;
	/**
	 * the offset from {@link #targetSide}; positive values are downward (if {@link #targetSide} is horizontal) or
	 * to the right (if {@link #targetSide} is vertical).
	 */
	final int		offset;

	/**
	 * Creates a {@code SideConstraint}.
	 * 
	 * @param targetComponent
	 *            the component whose side to constrain to.
	 * @param targetSide
	 *            the side of {@code targetComponent} to constrain to.
	 * @param offset
	 *            the offset from {@code targetSide}; positive values are downward (if {@code targetSide} is horizontal)
	 *            or
	 *            to the right (if {@code targetSide} is vertical).
	 */
	public SideConstraint( Component targetComponent , Side targetSide , int offset )
	{
		super( );
		this.targetComponent = targetComponent;
		this.targetSide = targetSide;
		this.offset = offset;
	}

	/**
	 * @return the current location of this {@link SideConstraint}, which is {@link #offset} pixels away from
	 *         the current {@link Side#location(Component)} location of {@link #targetSide} of {@link #targetComponent}.
	 */
	public int location( )
	{
		return targetSide.location( targetComponent ) + offset;
	}
}
