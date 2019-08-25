package org.andork.scrape;

public interface ScrapeMatcher<T> {
	/**
	 * Returns the pattern that is interpreted by this matcher.
	 *
	 * @return The pattern for which this matcher was created
	 */
	ScrapePattern<T> pattern();

	/**
	 * Resets this matcher.
	 *
	 * <p>
	 * Resetting a matcher discards all of its explicit state information. The
	 * matcher's region is set to the default region, which is its entire character
	 * sequence.
	 *
	 * @return This matcher
	 */
	ScrapeMatcher<T> reset();

	/**
	 * Resets this matcher with a new input sequence.
	 *
	 * <p>
	 * Resetting a matcher discards all of its explicit state information. The
	 * matcher's region is set to the default region, which is its entire character
	 * sequence.
	 * 
	 * @param input The new input character sequence
	 *
	 * @return This matcher
	 */
	ScrapeMatcher<T> reset(CharSequence input);

	/**
	 * Returns the start index of the previous match.
	 *
	 * @return The index of the first character matched
	 *
	 * @throws IllegalStateException If no match has yet been attempted, or if the
	 *                               previous match operation failed
	 */
	int start();

	/**
	 * Returns the offset after the last character matched.
	 *
	 * @return The offset after the last character matched
	 *
	 * @throws IllegalStateException If no match has yet been attempted, or if the
	 *                               previous match operation failed
	 */
	int end();

	/**
	 * @return the parsed value that was matched.
	 */
	T match();

	/**
	 * @return the text that was matched.
	 */
	String matchText();

	/**
	 * Attempts to match the entire region against the pattern.
	 *
	 * <p>
	 * If the match succeeds then more information can be obtained via the
	 * <tt>start</tt>, <tt>end</tt>, and <tt>group</tt> methods.
	 * </p>
	 *
	 * @return <tt>true</tt> if, and only if, the entire region sequence matches
	 *         this matcher's pattern
	 */
	boolean matches();

	/**
	 * Attempts to find the next subsequence of the input sequence that matches.
	 *
	 * <p>
	 * This method starts at the beginning of this matcher's region, or, if a
	 * previous invocation of the method was successful and the matcher has not
	 * since been reset, at the first character not matched by the previous match.
	 *
	 * <p>
	 * If the match succeeds then more information can be obtained via the
	 * <tt>start</tt>, <tt>end</tt>, <tt>match</tt>, and <tt>matchText</tt> methods.
	 * </p>
	 *
	 * @return <tt>true</tt> if, and only if, a subsequence of the input sequence
	 *         matches this matcher's pattern
	 */
	boolean find();

	/**
	 * Resets this matcher and then attempts to find the next subsequence of the
	 * input sequence that matches the pattern, starting at the specified index.
	 *
	 * <p>
	 * If the match succeeds then more information can be obtained via the
	 * <tt>start</tt>, <tt>end</tt>, and <tt>match</tt>, and <tt>matchText</tt>
	 * methods, and subsequent invocations of the {@link #find()} method will start
	 * at the first character not matched by this match.
	 * </p>
	 *
	 * @param start the index to start searching for a match
	 * @throws IndexOutOfBoundsException If start is less than zero or if start is
	 *                                   greater than the length of the input
	 *                                   sequence.
	 *
	 * @return <tt>true</tt> if, and only if, a subsequence of the input sequence
	 *         starting at the given index matches this matcher's pattern
	 */
	boolean find(int start);

	/**
	 * Attempts to match the input sequence, starting at the beginning of the
	 * region, against the pattern.
	 *
	 * <p>
	 * Like the {@link #matches matches} method, this method always starts at the
	 * beginning of the region; unlike that method, it does not require that the
	 * entire region be matched.
	 *
	 * <p>
	 * If the match succeeds then more information can be obtained via the
	 * <tt>start</tt>, <tt>end</tt>, and <tt>group</tt> methods.
	 * </p>
	 *
	 * @return <tt>true</tt> if, and only if, a prefix of the input sequence matches
	 *         this matcher's pattern
	 */
	boolean lookingAt();

	/**
	 * Sets the limits of this matcher's region. The region is the part of the input
	 * sequence that will be searched to find a match. Invoking this method resets
	 * the matcher, and then sets the region to start at the index specified by the
	 * <code>start</code> parameter and end at the index specified by the
	 * <code>end</code> parameter.
	 *
	 * @param start The index to start searching at (inclusive)
	 * @param end   The index to end searching at (exclusive)
	 * @throws IndexOutOfBoundsException If start or end is less than zero, if start
	 *                                   is greater than the length of the input
	 *                                   sequence, if end is greater than the length
	 *                                   of the input sequence, or if start is
	 *                                   greater than end.
	 * @return this matcher
	 */
	ScrapeMatcher<T> region(int start, int end);

	/**
	 * Reports the start index of this matcher's region. The searches this matcher
	 * conducts are limited to finding matches within {@link #regionStart
	 * regionStart} (inclusive) and {@link #regionEnd regionEnd} (exclusive).
	 *
	 * @return The starting point of this matcher's region
	 */
	int regionStart();

	/**
	 * Reports the end index (exclusive) of this matcher's region. The searches this
	 * matcher conducts are limited to finding matches within {@link #regionStart
	 * regionStart} (inclusive) and {@link #regionEnd regionEnd} (exclusive).
	 *
	 * @return the ending point of this matcher's region
	 */
	int regionEnd();

	/**
	 * <p>
	 * Returns true if the end of input was hit by the search engine in the last
	 * match operation performed by this matcher.
	 *
	 * <p>
	 * When this method returns true, then it is possible that more input would have
	 * changed the result of the last search.
	 *
	 * @return true iff the end of input was hit in the last match; false otherwise
	 */
	boolean hitEnd();
}
