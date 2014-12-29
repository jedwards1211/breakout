package org.andork.swing;


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

import java.math.BigDecimal;

import javax.swing.AbstractSpinnerModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.andork.util.Java7.Objects;

/**
 * A better version of {@link SpinnerNumberModel} that is generic and allows all values to be {@code null}.
 * 
 * @author andy.edwards
 * 
 * @param <N>
 *            the number type (doesn't necessarily have to be a {@link Number}).
 */
public class BetterSpinnerNumberModel<N> extends AbstractSpinnerModel
{
	private Class<N>	numberClass;
	private N			stepSize, value, defaultValue;
	private Comparable<N>	minimum, maximum;
	private Arithmetic<N>	arithmetic;

	/**
	 * Constructs a <code>SpinnerModel</code> that represents
	 * a closed sequence of
	 * numbers from <code>minimum</code> to <code>maximum</code>. The <code>nextValue</code> and <code>previousValue</code> methods
	 * compute elements of the sequence by adding or subtracting <code>stepSize</code> respectively. All of the parameters
	 * must be mutually <code>Comparable</code>, <code>value</code> and <code>stepSize</code> must be instances of <code>Integer</code> <code>Long</code>, <code>Float</code>, or <code>Double</code>.
	 * <p>
	 * The <code>minimum</code> and <code>maximum</code> parameters can be <code>null</code> to indicate that the range doesn't have an upper or lower bound. If <code>value</code> or <code>stepSize</code> is <code>null</code>, or if both <code>minimum</code> and <code>maximum</code> are specified and <code>mininum &gt; maximum</code> then an <code>IllegalArgumentException</code> is thrown. Similarly if <code>(minimum &lt;= value &lt;= maximum</code>) is false, an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param value
	 *            the current (non <code>null</code>) value of the model
	 * @param minimum
	 *            the first number in the sequence or <code>null</code>
	 * @param maximum
	 *            the last number in the sequence or <code>null</code>
	 * @param stepSize
	 *            the difference between elements of the sequence
	 * 
	 * @throws IllegalArgumentException
	 *             if stepSize or value is <code>null</code> or if the following expression is false: <code>minimum &lt;= value &lt;= maximum</code>
	 */
	public BetterSpinnerNumberModel(Class<N> numberClass, N value, Comparable<N> minimum, Comparable<N> maximum, N stepSize, Arithmetic<N> arithmetic) {
		if (value != null && !(((minimum == null) || (minimum.compareTo(value) <= 0)) && ((maximum == null) || (maximum.compareTo(value) >= 0)))) {
			throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
		}
		this.numberClass = numberClass;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.stepSize = stepSize;
		this.arithmetic = arithmetic;
	}

	public static BetterSpinnerNumberModel<Integer> newInstance(Integer value, Integer minimum, Integer maximum, Integer stepSize) {
		return new BetterSpinnerNumberModel<Integer>(Integer.class, value, minimum, maximum, stepSize, IntegerArithmetic.instance);
	}

	public static BetterSpinnerNumberModel<Double> newInstance(Double value, Double minimum, Double maximum, Double stepSize) {
		return new BetterSpinnerNumberModel<Double>(Double.class, value, minimum, maximum, stepSize, DoubleArithmetic.instance);
	}

	public static BetterSpinnerNumberModel<BigDecimal> newInstance(BigDecimal value, BigDecimal minimum, BigDecimal maximum, BigDecimal stepSize) {
		return new BetterSpinnerNumberModel<BigDecimal>(BigDecimal.class, value, minimum, maximum, stepSize, BigDecimalArithmetic.instance);
	}

	/**
	 * Changes the lower bound for numbers in this sequence.
	 * If <code>minimum</code> is <code>null</code>,
	 * then there is no lower bound. No bounds checking is done here;
	 * the new <code>minimum</code> value may invalidate the <code>(minimum &lt;= value &lt= maximum)</code> invariant enforced by the constructors. This is to simplify updating
	 * the model, naturally one should ensure that the invariant is true
	 * before calling the <code>getNextValue</code>, <code>getPreviousValue</code>, or <code>setValue</code> methods.
	 * <p>
	 * Typically this property is a <code>Number</code> of the same type as the <code>value</code> however it's possible to use any <code>Comparable</code> with a <code>compareTo</code> method for a <code>Number</code> with the same type as the value. For example if value was a <code>Long</code>, <code>minimum</code> might be a Date subclass defined like this:
	 * 
	 * <pre>
	 * MyDate extends Date {  // Date already implements Comparable
	 *     public int compareTo(Long o) {
	 *         long t = getTime();
	 *         return (t < o.longValue() ? -1 : (t == o.longValue() ? 0 : 1));
	 *     }
	 * }
	 * </pre>
	 * <p>
	 * This method fires a <code>ChangeEvent</code> if the <code>minimum</code> has changed.
	 * 
	 * @param minimum
	 *            a <code>Comparable</code> that has a <code>compareTo</code> method for <code>Number</code>s with
	 *            the same type as <code>value</code>
	 * @see #getMinimum
	 * @see #setMaximum
	 * @see SpinnerModel#addChangeListener
	 */
	public void setMinimum(Comparable<N> minimum) {
		if ((minimum == null) ? (this.minimum != null) : !minimum.equals(this.minimum)) {
			this.minimum = minimum;
			fireStateChanged();
		}
	}

	/**
	 * Returns the first number in this sequence.
	 * 
	 * @return the value of the <code>minimum</code> property
	 * @see #setMinimum
	 */
	public Comparable<N> getMinimum() {
		return minimum;
	}

	/**
	 * Changes the upper bound for numbers in this sequence.
	 * If <code>maximum</code> is <code>null</code>, then there
	 * is no upper bound. No bounds checking is done here; the new <code>maximum</code> value may invalidate the <code>(minimum <= value < maximum)</code> invariant enforced by the constructors. This is to simplify updating
	 * the model, naturally one should ensure that the invariant is true
	 * before calling the <code>next</code>, <code>previous</code>,
	 * or <code>setValue</code> methods.
	 * <p>
	 * Typically this property is a <code>Number</code> of the same type as the <code>value</code> however it's possible to use any <code>Comparable</code> with a <code>compareTo</code> method for a <code>Number</code> with the same type as the value. See <a href="#setMinimum(java.lang.Comparable)"> <code>setMinimum</code></a> for an example.
	 * <p>
	 * This method fires a <code>ChangeEvent</code> if the <code>maximum</code> has changed.
	 * 
	 * @param maximum
	 *            a <code>Comparable</code> that has a <code>compareTo</code> method for <code>Number</code>s with
	 *            the same type as <code>value</code>
	 * @see #getMaximum
	 * @see #setMinimum
	 * @see SpinnerModel#addChangeListener
	 */
	public void setMaximum(Comparable<N> maximum) {
		if ((maximum == null) ? (this.maximum != null) : !maximum.equals(this.maximum)) {
			this.maximum = maximum;
			fireStateChanged();
		}
	}

	/**
	 * Returns the last number in the sequence.
	 * 
	 * @return the value of the <code>maximum</code> property
	 * @see #setMaximum
	 */
	public Comparable<N> getMaximum() {
		return maximum;
	}

	/**
	 * Changes the size of the value change computed by the <code>getNextValue</code> and <code>getPreviousValue</code> methods. An <code>IllegalArgumentException</code> is thrown if <code>stepSize</code> is <code>null</code>.
	 * <p>
	 * This method fires a <code>ChangeEvent</code> if the <code>stepSize</code> has changed.
	 * 
	 * @param stepSize
	 *            the size of the value change computed by the <code>getNextValue</code> and <code>getPreviousValue</code> methods
	 * @see #getNextValue
	 * @see #getPreviousValue
	 * @see #getStepSize
	 * @see SpinnerModel#addChangeListener
	 */
	public void setStepSize(N stepSize) {
		if (Objects.equals(this.stepSize, stepSize)) {
			this.stepSize = stepSize;
			fireStateChanged();
		}
	}

	/**
	 * Returns the size of the value change computed by the <code>getNextValue</code> and <code>getPreviousValue</code> methods.
	 * 
	 * @return the value of the <code>stepSize</code> property
	 * @see #setStepSize
	 */
	public N getStepSize() {
		return stepSize;
	}

	private N incrValue(int dir)
	{
		N newValue;
		N value = this.value == null ? defaultValue : this.value;
		if (value == null || stepSize == null) {
			return null;
		}
		newValue = arithmetic.addMultiplied(value, stepSize, dir);

		if ((maximum != null) && (maximum.compareTo(newValue) < 0)) {
			return null;
		}
		if ((minimum != null) && (minimum.compareTo(newValue) > 0)) {
			return null;
		}
		else {
			return newValue;
		}
	}

	/**
	 * Returns the next number in the sequence.
	 * 
	 * @return <code>value + stepSize</code> or <code>null</code> if the sum
	 *         exceeds <code>maximum</code>.
	 * 
	 * @see SpinnerModel#getNextValue
	 * @see #getPreviousValue
	 * @see #setStepSize
	 */
	public Object getNextValue() {
		return incrValue(+1);
	}

	/**
	 * Returns the previous number in the sequence.
	 * 
	 * @return <code>value - stepSize</code>, or <code>null</code> if the sum is less
	 *         than <code>minimum</code>.
	 * 
	 * @see SpinnerModel#getPreviousValue
	 * @see #getNextValue
	 * @see #setStepSize
	 */
	public Object getPreviousValue() {
		return incrValue(-1);
	}

	/**
	 * Returns the value of the current element of the sequence.
	 * 
	 * @return the value property
	 * @see #setValue
	 */
	public N getNumber() {
		return value;
	}

	/**
	 * Returns the value of the current element of the sequence.
	 * 
	 * @return the value property
	 * @see #setValue
	 * @see #getNumber
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the current value for this sequence. If <code>value</code> is <code>null</code>, or not a <code>Number</code>, an <code>IllegalArgumentException</code> is thrown. No
	 * bounds checking is done here; the new value may invalidate the <code>(minimum &lt;= value &lt;= maximum)</code> invariant enforced by the constructors. It's also possible to set
	 * the value to be something that wouldn't naturally occur in the sequence,
	 * i.e. a value that's not modulo the <code>stepSize</code>.
	 * This is to simplify updating the model, and to accommodate
	 * spinners that don't want to restrict values that have been
	 * directly entered by the user. Naturally, one should ensure that the <code>(minimum &lt;= value &lt;= maximum)</code> invariant is true
	 * before calling the <code>next</code>, <code>previous</code>, or <code>setValue</code> methods.
	 * <p>
	 * This method fires a <code>ChangeEvent</code> if the value has changed.
	 * 
	 * @param value
	 *            the current (non <code>null</code>) <code>Number</code> for this sequence
	 * @throws IllegalArgumentException
	 *             if <code>value</code> is <code>null</code> or not a <code>Number</code>
	 * @see #getNumber
	 * @see #getValue
	 * @see SpinnerModel#addChangeListener
	 */
	public void setValue(Object value) {
		if ((value != null) && !(numberClass.isInstance(value))) {
			throw new IllegalArgumentException("illegal value");
		}
		if (!Objects.equals(this.value, value)) {
			this.value = value == null ? null : numberClass.cast(value);
			fireStateChanged();
		}
	}

	public static interface Arithmetic<N> {
		/**
		 * @return {@code n0 + n1 * n1f}.
		 */
		public N addMultiplied(N n0, N n1, int n1f);
	}

	public static class IntegerArithmetic implements Arithmetic<Integer> {
		private IntegerArithmetic() {

		}

		public static final IntegerArithmetic	instance	= new IntegerArithmetic();

		public Integer addMultiplied(Integer n0, Integer n1, int n1f) {
			return n0 + n1 * n1f;
		}
	}

	public static class DoubleArithmetic implements Arithmetic<Double> {
		private DoubleArithmetic() {

		}

		public static final DoubleArithmetic	instance	= new DoubleArithmetic();

		public Double addMultiplied(Double n0, Double n1, int n1f) {
			return n0 + n1 * n1f;
		}
	}

	public static class BigDecimalArithmetic implements Arithmetic<BigDecimal> {
		private BigDecimalArithmetic() {

		}

		public static final BigDecimalArithmetic	instance	= new BigDecimalArithmetic();

		public BigDecimal addMultiplied(BigDecimal n0, BigDecimal n1, int n1f) {
			return n0.add(n1.multiply(new BigDecimal(n1f)));
		}
	}
}
