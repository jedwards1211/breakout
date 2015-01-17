package org.andork.breakout.table;

import org.andork.breakout.table.LrudXSection.FacingAzimuth;

/**
 * Represents a type of {@link XSection} the user can select in a dropdown.
 * 
 * @author James
 */
public enum XSectionType
{
	/**
	 * An {@link LrudXSection} that is {@linkplain Lrud.Angle#PERPENDICULAR perpendicular} to its survey shot.
	 */
	PERPENDICULAR_LRUD,
	/**
	 * An {@link LrudXSection} that {@linkplain Lrud.Angle#BISECTOR bisects} the angle between two survey shots.
	 */
	BISECTOR_LRUD,
	/**
	 * An {@link LrudXSection} oriented to a {@link FacingAzimuth}.
	 */
	LRUD_WITH_FACING_AZIMUTH,
	/**
	 * An {@link LlrrudXSection}.
	 */
	LLRRUD,
	/**
	 * A {@link NsewXSection}.
	 */
	NSEW;
}