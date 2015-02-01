package org.breakout.wallsimport;

import org.breakout.wallsimport.WallsImportMessage.Severity;

/**
 * A {@link FunctionalInterface} for logging errors, warnings, and other messages during parsing.
 * 
 * @author James
 */
@FunctionalInterface
public interface ParseLogger
{
	/**
	 * Logs a parse message.
	 * 
	 * @param severity
	 *            the severity of the message.
	 * @param message
	 *            the message text.
	 * @param line
	 *            the line on which the parse message begins.
	 * @param column
	 *            the column on which the parse message begins.
	 */
	public void log( Severity severity , String message , int line , int column );
}