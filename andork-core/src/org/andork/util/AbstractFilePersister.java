package org.andork.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.event.SourcePath;
import org.andork.func.Bimapper;

public abstract class AbstractFilePersister<M> implements HierarchicalBasicPropertyChangeListener
{
	
	protected Batcher<M>			batcher;
	protected File					file;
	protected Bimapper<M, String>	format;
	
	public AbstractFilePersister( File file , Bimapper<M, String> format )
	{
		this.file = file;
		this.format = format;
		
		batcher = new Batcher<M>( )
		{
			@Override
			protected void handleLater( final LinkedList<M> batch )
			{
				saveInBackground( batch );
			}
		};
	}
	
	protected abstract void saveInBackground( final LinkedList<M> batch );
	
	protected void save( M model )
	{
		FileOutputStream fs = null;
		PrintStream ps = null;
		try
		{
			ps = new PrintStream( fs = new FileOutputStream( file ) );
			ps.print( format.map( model ) );
		}
		catch( Throwable t )
		{
			t.printStackTrace( );
		}
		finally
		{
			if( ps != null )
			{
				ps.close( );
			}
			if( fs != null )
			{
				try
				{
					fs.close( );
				}
				catch( IOException ex )
				{
					ex.printStackTrace( );
				}
			}
		}
	}
	
	public M load( ) throws Exception
	{
		ByteArrayOutputStream buffer = null;
		FileInputStream in = null;
		
		try
		{
			buffer = new ByteArrayOutputStream( );
			if( file.exists( ) )
			{
				in = new FileInputStream( file );
				int c;
				while( ( c = in.read( ) ) >= 0 )
				{
					buffer.write( c );
				}
			}
			
			return format.unmap( buffer.toString( ) );
		}
		finally
		{
			if( buffer != null )
			{
				buffer.close( );
			}
			if( in != null )
			{
				try
				{
					in.close( );
				}
				catch( IOException ex )
				{
					ex.printStackTrace( );
				}
			}
		}
	}
	
	private M getRootModel( Object source )
	{
		if( source instanceof SourcePath )
		{
			return getRootModel( ( M ) ( ( SourcePath ) source ).parent );
		}
		return ( M ) source;
	}
	
	@Override
	public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		batcher.add( getRootModel( source ) );
	}
	
	@Override
	public void childrenChanged( Object source , ChangeType changeType , Object child )
	{
		batcher.add( getRootModel( source ) );
	}
	
}