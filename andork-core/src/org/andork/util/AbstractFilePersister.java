package org.andork.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.event.SourcePath;
import org.andork.func.Bimapper;
import org.andork.func.BimapperStreamBimapper;
import org.andork.func.StreamBimapper;

public abstract class AbstractFilePersister<M> implements HierarchicalBasicPropertyChangeListener
{
	protected StreamBimapper<M>	bimapper;
	protected Batcher<M>		batcher;
	protected File				file;
	
	public AbstractFilePersister( File file , Bimapper<M, String> format )
	{
		this.file = file;
		this.bimapper = new BimapperStreamBimapper<M>( format );
		
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
		try
		{
			if( !file.getParentFile( ).exists( ) )
			{
				file.getParentFile( ).mkdirs( );
			}
			bimapper.write( model , new FileOutputStream( file ) );
		}
		catch( Throwable t )
		{
			t.printStackTrace( );
		}
	}
	
	public M load( ) throws Exception
	{
		if( !file.exists( ) )
		{
			return null;
		}
		return bimapper.read( new FileInputStream( file ) );
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
	public void childrenChanged( Object source , ChangeType changeType , Object ... children )
	{
		batcher.add( getRootModel( source ) );
	}
	
}