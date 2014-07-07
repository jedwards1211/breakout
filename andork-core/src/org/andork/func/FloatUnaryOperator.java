/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.andork.func;

import java.util.Objects;

/**
 * Represents an operation on a single {@code float}-valued operand that produces
 * a {@code float}-valued result. This is the primitive type specialization of {@link UnaryOperator} for {@code float}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link #applyAsFloat(float)}.
 *
 * @see UnaryOperator
 * @since 1.8
 */
@FunctionalInterface
public interface FloatUnaryOperator
{
	
	/**
	 * Applies this operator to the given operand.
	 *
	 * @param operand
	 *        the operand
	 * @return the operator result
	 */
	float applyAsFloat( float operand );
	
	/**
	 * Returns a composed operator that first applies the {@code before} operator to its input, and then applies this operator to the result.
	 * If evaluation of either operator throws an exception, it is relayed to
	 * the caller of the composed operator.
	 *
	 * @param before
	 *        the operator to apply before this operator is applied
	 * @return a composed operator that first applies the {@code before} operator and then applies this operator
	 * @throws NullPointerException
	 *         if before is null
	 *
	 * @see #andThen(FloatUnaryOperator)
	 */
	default FloatUnaryOperator compose( FloatUnaryOperator before )
	{
		Objects.requireNonNull( before );
		return ( float v ) -> applyAsFloat( before.applyAsFloat( v ) );
	}
	
	/**
	 * Returns a composed operator that first applies this operator to
	 * its input, and then applies the {@code after} operator to the result.
	 * If evaluation of either operator throws an exception, it is relayed to
	 * the caller of the composed operator.
	 *
	 * @param after
	 *        the operator to apply after this operator is applied
	 * @return a composed operator that first applies this operator and then
	 *         applies the {@code after} operator
	 * @throws NullPointerException
	 *         if after is null
	 *
	 * @see #compose(FloatUnaryOperator)
	 */
	default FloatUnaryOperator andThen( FloatUnaryOperator after )
	{
		Objects.requireNonNull( after );
		return ( float t ) -> after.applyAsFloat( applyAsFloat( t ) );
	}
	
	/**
	 * Returns a unary operator that always returns its input argument.
	 *
	 * @return a unary operator that always returns its input argument
	 */
	static FloatUnaryOperator identity( )
	{
		return t -> t;
	}
}
