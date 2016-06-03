/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.func;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Represents an operation on a single {@code float}-valued operand that
 * produces a {@code float}-valued result. This is the primitive type
 * specialization of {@link UnaryOperator} for {@code float}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #applyAsFloat(float)}.
 *
 * @see UnaryOperator
 * @since 1.8
 */
@FunctionalInterface
public interface FloatUnaryOperator {

	/**
	 * Returns a unary operator that always returns its input argument.
	 *
	 * @return a unary operator that always returns its input argument
	 */
	static FloatUnaryOperator identity() {
		return t -> t;
	}

	/**
	 * Returns a composed operator that first applies this operator to its
	 * input, and then applies the {@code after} operator to the result. If
	 * evaluation of either operator throws an exception, it is relayed to the
	 * caller of the composed operator.
	 *
	 * @param after
	 *            the operator to apply after this operator is applied
	 * @return a composed operator that first applies this operator and then
	 *         applies the {@code after} operator
	 * @throws NullPointerException
	 *             if after is null
	 *
	 * @see #compose(FloatUnaryOperator)
	 */
	default FloatUnaryOperator andThen(FloatUnaryOperator after) {
		Objects.requireNonNull(after);
		return (float t) -> after.applyAsFloat(applyAsFloat(t));
	}

	/**
	 * Applies this operator to the given operand.
	 *
	 * @param operand
	 *            the operand
	 * @return the operator result
	 */
	float applyAsFloat(float operand);

	/**
	 * Returns a composed operator that first applies the {@code before}
	 * operator to its input, and then applies this operator to the result. If
	 * evaluation of either operator throws an exception, it is relayed to the
	 * caller of the composed operator.
	 *
	 * @param before
	 *            the operator to apply before this operator is applied
	 * @return a composed operator that first applies the {@code before}
	 *         operator and then applies this operator
	 * @throws NullPointerException
	 *             if before is null
	 *
	 * @see #andThen(FloatUnaryOperator)
	 */
	default FloatUnaryOperator compose(FloatUnaryOperator before) {
		Objects.requireNonNull(before);
		return (float v) -> applyAsFloat(before.applyAsFloat(v));
	}
}
