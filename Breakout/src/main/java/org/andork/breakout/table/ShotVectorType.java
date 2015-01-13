package org.andork.breakout.table;

/**
 * Represents a type of {@link ShotVector} the user can select in a dropdown.
 * 
 * @author James
 */
public enum ShotVectorType
{
	/**
	 * Indicates {@link DaiShotVector} with {@linkplain Dai#areBacksightsCorrected() corrected backsights}.
	 */
	DAIc,
	/**
	 * Indicates {@link DaiShotVector} with {@linkplain Dai#areBacksightsCorrected() uncorrected backsights}.
	 */
	DAIu,
	/**
	 * Indicates {@link NevShotVector} where downward is {@linkplain Nev#isDownwardPositive() positive}.
	 */
	NED,
	/**
	 * Indicates {@link NevShotVector} where downward is {@linkplain Nev#isDownwardPositive() negative}.
	 */
	NEEl;
}