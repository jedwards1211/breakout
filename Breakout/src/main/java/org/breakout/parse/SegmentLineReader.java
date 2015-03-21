package org.breakout.parse;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SegmentLineReader implements AutoCloseable
{
	private Object				source;
	private LineNumberReader	reader;

	public SegmentLineReader( Object source , Reader reader )
	{
		super( );
		this.source = source;
		if( reader instanceof LineNumberReader )
		{
			this.reader = ( LineNumberReader ) reader;
		}
		else
		{
			this.reader = new LineNumberReader( reader );
		}
	}

	public SegmentLineReader( String fileName ) throws FileNotFoundException
	{
		this( new File( fileName ) , new FileReader( fileName ) );
	}

	public SegmentLineReader( File file ) throws FileNotFoundException
	{
		this( file , new FileReader( file ) );
	}

	public SegmentLineReader( FileDescriptor fd ) throws FileNotFoundException
	{
		this( fd , new FileReader( fd ) );
	}

	public SegmentLineReader( URL url ) throws IOException
	{
		this( url , new InputStreamReader( url.openStream( ) ) );
	}

	public Segment readLine( ) throws IOException
	{
		int lineNumber = reader.getLineNumber( );
		String line = reader.readLine( );
		return line == null ? null : new Segment( line , source , lineNumber , 0 );
	}

	@Override
	public void close( ) throws IOException
	{
		reader.close( );
	}

	public Stream<Segment> lines( )
	{
		Iterator<Segment> iter = new Iterator<Segment>( ) {
			Segment	nextLine	= null;

			@Override
			public boolean hasNext( )
			{
				if( nextLine != null )
				{
					return true;
				}
				else
				{
					try
					{
						nextLine = readLine( );
						return ( nextLine != null );
					}
					catch( IOException e )
					{
						throw new UncheckedIOException( e );
					}
				}
			}

			@Override
			public Segment next( )
			{
				if( nextLine != null || hasNext( ) )
				{
					Segment line = nextLine;
					nextLine = null;
					return line;
				}
				else
				{
					throw new NoSuchElementException( );
				}
			}
		};

		return StreamSupport.stream( Spliterators.spliteratorUnknownSize(
			iter , Spliterator.ORDERED | Spliterator.NONNULL ) , false );
	}
}
