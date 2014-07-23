/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.func;

import java.util.function.BinaryOperator;

/**
 * Represents an operation upon two {@code float}-valued operands and producing a {@code float}-valued result. This is the primitive type specialization of
 * {@link BinaryOperator} for {@code float}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link #applyAsFloat(float, float)}.
 *
 * @see BinaryOperator
 * @see FloatUnaryOperator
 * @since 1.8
 */
@FunctionalInterface
public interface FloatBinaryOperator
{
	/**
	 * Applies this operator to the given operands.
	 *
	 * @param left
	 *        the first operand
	 * @param right
	 *        the second operand
	 * @return the operator result
	 */
	float applyAsFloat( float left , float right );
}
