package org.andork.breakout.wallsimport;

import java.nio.file.Path;

import javax.swing.JOptionPane;

/**
 * Provides errors, warnings, or other information about the results of importing data in Walls format.
 * 
 * @author James
 */
public class WallsImportStatusMessage
{
	/**
	 * The severity.
	 */
	private final Severity	severity;
	/**
	 * The message text
	 */
	private final String	message;
	/**
	 * The path to the file the message regards (may be {@code null}
	 */
	private final Path		file;
	/**
	 * The line number of {@code file} the message regards
	 */
	private final int		line;
	/**
	 * The column number of {@code line} in {@code file} the message regards
	 */
	private final int		column;

	public WallsImportStatusMessage( String message , Severity severity )
	{
		this( message , severity , null , -1 , -1 );
	}

	public WallsImportStatusMessage( String message , Severity severity , Path file , int line , int column )
	{
		super( );
		this.severity = severity;
		this.message = message;
		this.file = file;
		this.line = line;
		this.column = column;
	}

	public Severity getSeverity( )
	{
		return severity;
	}

	public String getMessage( )
	{
		return message;
	}

	public Path getFile( )
	{
		return file;
	}

	public int getLine( )
	{
		return line;
	}

	public int getColumn( )
	{
		return column;
	}

	public static enum Severity
	{
		INFO, WARNING, ERROR, FATAL;
	}
}
