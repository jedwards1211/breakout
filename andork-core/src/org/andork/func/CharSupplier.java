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

import java.util.function.Supplier;

/**
 * Represents a supplier of {@code char}-valued results. This is the
 * {@code char}-producing primitive specialization of {@link Supplier}.
 *
 * <p>
 * There is no requirement that a distinct result be returned each time the
 * supplier is invoked.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #getAsChar()}.
 *
 * @see Supplier
 * @since 1.8
 */
@FunctionalInterface
public interface CharSupplier {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	char getAsChar();
}
