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
