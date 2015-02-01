package org.breakout.wallsimport;

import java.nio.file.Path;

/**
 * Provides errors, warnings, or other information about the results of importing data in Walls format.
 * 
 * @author James
 */
public class WallsImportMessage
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
	 * The path to the file the message regards (may be {@code null})
	 */
	private final Path		pathToSourceFile;
	/**
	 * The original line (in which no macro references have been replaced) from the file which the message regards (may
	 * be {@code null})
	 */
	private final String	line;
	/**
	 * The line from the file which the message regards, in which macro references have been replaced with macro values.
	 */
	private final String	preprocessedLine;
	/**
	 * The line number of {@code file} the message regards
	 */
	private final int		lineNumber;
	/**
	 * The column number within {@code preprocessedLine} (or {@code line} if {@code preprocessedLine} is null)
	 */
	private final int		columnNumber;

	public WallsImportMessage( String message , Severity severity )
	{
		this( message , severity , null , -1 , null , null , -1 );
	}

	public WallsImportMessage( String message , Severity severity , Path file , int lineNumber , String line ,
		int columnNumber )
	{
		this( message , severity , file , lineNumber , line , null , columnNumber );
	}

	public WallsImportMessage( String message , Severity severity , Path file , int lineNumber , String line ,
		String preprocessedLine , int columnNumber )
	{
		super( );
		this.severity = severity;
		this.message = message;
		this.pathToSourceFile = file;
		this.lineNumber = lineNumber;
		this.line = line;
		this.preprocessedLine = preprocessedLine;
		this.columnNumber = columnNumber;
	}

	public Severity getSeverity( )
	{
		return severity;
	}

	public String getMessage( )
	{
		return message;
	}

	public Path getPathToSourceFile( )
	{
		return pathToSourceFile;
	}

	public String getLine( )
	{
		return line;
	}

	public String getPreprocessedLine( )
	{
		return preprocessedLine;
	}

	public int getLineNumber( )
	{
		return lineNumber;
	}

	public int getColumnNumber( )
	{
		return columnNumber;
	}

	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + columnNumber;
		result = prime * result + ( ( line == null ) ? 0 : line.hashCode( ) );
		result = prime * result + lineNumber;
		result = prime * result + ( ( message == null ) ? 0 : message.hashCode( ) );
		result = prime * result + ( ( pathToSourceFile == null ) ? 0 : pathToSourceFile.hashCode( ) );
		result = prime * result + ( ( preprocessedLine == null ) ? 0 : preprocessedLine.hashCode( ) );
		result = prime * result + ( ( severity == null ) ? 0 : severity.hashCode( ) );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass( ) != obj.getClass( ) )
			return false;
		WallsImportMessage other = ( WallsImportMessage ) obj;
		if( columnNumber != other.columnNumber )
			return false;
		if( line == null )
		{
			if( other.line != null )
				return false;
		}
		else if( !line.equals( other.line ) )
			return false;
		if( lineNumber != other.lineNumber )
			return false;
		if( message == null )
		{
			if( other.message != null )
				return false;
		}
		else if( !message.equals( other.message ) )
			return false;
		if( pathToSourceFile == null )
		{
			if( other.pathToSourceFile != null )
				return false;
		}
		else if( !pathToSourceFile.equals( other.pathToSourceFile ) )
			return false;
		if( preprocessedLine == null )
		{
			if( other.preprocessedLine != null )
				return false;
		}
		else if( !preprocessedLine.equals( other.preprocessedLine ) )
			return false;
		if( severity != other.severity )
			return false;
		return true;
	}

	@Override
	public String toString( )
	{
		return "WallsImportMessage [severity=" + severity + ", message=" + message + ", pathToSourceFile="
			+ pathToSourceFile + ", line=" + line + ", preprocessedLine=" + preprocessedLine + ", lineNumber="
			+ lineNumber + ", columnNumber=" + columnNumber + "]";
	}

	public static enum Severity
	{
		INFO, WARNING, ERROR, FATAL;
	}
}
